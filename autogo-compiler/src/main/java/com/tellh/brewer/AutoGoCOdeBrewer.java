package com.tellh.brewer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.tellh.utils.ClassNames;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

/**
 * Created by tlh on 2016/8/2.
 */
public class AutoGoCodeBrewer {
    public static Builder builder(Filer filer) {
        return new Builder(filer);
    }

    public static class Builder {
        private Filer mFileUtils;
        List<MethodSpec> launchActivityMethods;

        public Builder(Filer filer) {
            mFileUtils = filer;
        }

        public Builder launchActivityMethods(List<MethodSpec> methods) {
            launchActivityMethods = methods;
            return this;
        }

        public void brew() throws IOException {
            TypeSpec.Builder builder = TypeSpec.classBuilder("AutoGo")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
            if (launchActivityMethods != null)
                brewAutoGo(builder);
            TypeSpec AutoGo = builder.build();
            JavaFile javaFile = JavaFile.builder("autogo", AutoGo)
                    .build();
            javaFile.writeTo(mFileUtils);
        }

        private void brewAutoGo(TypeSpec.Builder classBuilder) throws IOException {
            FieldSpec autoAssignerMap = FieldSpec.builder(ClassNames.MAP_ASSIGNER, "autoAssignerMap")
                    .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
                    .initializer("new $T<>()", HashMap.class)
                    .build();
            TypeSpec ActivityLauncherManager = TypeSpec.classBuilder("ActivityLauncherManager")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .addField(FieldSpec.builder(ClassNames.ACTIVITY, "srcActivity")
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build()
                    )
                    .addMethod(MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ClassNames.ACTIVITY, "activity")
                            .addStatement("this.srcActivity = activity")
                            .build()
                    )
                    .addMethod(MethodSpec.methodBuilder("check")
                            .addModifiers(Modifier.PRIVATE)
                            .beginControlFlow("if (autoAssignerMap == null)")
                            .addStatement("autoAssignerMap = new $T<>()", HashMap.class)
                            .endControlFlow()
                            .build()
                    )
                    .addMethods(launchActivityMethods)
                    .build();

            MethodSpec from = MethodSpec.methodBuilder("from")
                    .returns(ClassName.get("", "ActivityLauncherManager"))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ClassNames.ACTIVITY, "activity")
                    .addStatement("return new ActivityLauncherManager(activity)")
                    .build();
            MethodSpec assign = MethodSpec.methodBuilder("assign")
                    .returns(boolean.class)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ClassNames.ACTIVITY, "activity")
                    .beginControlFlow("try")
                    .addStatement("getAssigner(activity).assign()")
                    .endControlFlow()
                    .beginControlFlow("catch (IllegalArgumentException e)")
                    .addStatement("$T.e($S, e.getMessage())", ClassNames.LOG, "AutoGo")
                    .addStatement("return false")
                    .endControlFlow()
                    .addStatement("return true")
                    .build();
            MethodSpec getAssigner = MethodSpec.methodBuilder("getAssigner")
                    .returns(ClassNames.AUTO_ASSIGNER)
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                    .addParameter(ClassNames.ACTIVITY, "activity")
                    .addException(IllegalArgumentException.class)
                    .addStatement("String className = activity.getClass().getName()")
                    .addStatement("$T autoAssigner = autoAssignerMap.get(className)", ClassNames.AUTO_ASSIGNER)
                    .beginControlFlow("if (autoAssigner == null)")
                    .addStatement("throw new IllegalArgumentException($S)", "your target activity haven't been started.")
                    .endControlFlow()
                    .addStatement("autoAssigner.setTarget(activity)")
                    .addStatement("return autoAssigner")
                    .build();
            classBuilder.addField(autoAssignerMap)
                    .addMethod(from)
                    .addMethod(assign)
                    .addMethod(getAssigner)
                    .addType(ActivityLauncherManager)
                    .build();
        }
    }
}
