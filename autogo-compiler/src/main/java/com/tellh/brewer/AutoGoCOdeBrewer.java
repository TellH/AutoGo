package com.tellh.brewer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.tellh.entity.KeyValueGroup;
import com.tellh.utils.ClassNames;

import java.io.IOException;
import java.util.ArrayList;
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
        List<KeyValueGroup> launchTargets;
        List<KeyValueGroup> sharePrefsTargets;

        public Builder(Filer filer) {
            mFileUtils = filer;
        }

        public Builder launchActivityMethods(List<KeyValueGroup> targets) {
            this.launchTargets = targets;
            return this;
        }

        public Builder sharePrefsTargets(List<KeyValueGroup> targets) {
            this.sharePrefsTargets = targets;
            return this;
        }

        public void brewCode() throws IOException {
            TypeSpec.Builder builder = TypeSpec.classBuilder("AutoGo")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
            brewAutoGo(builder);
            TypeSpec AutoGo = builder.build();
            CodeBrewer.brewJavaFile("autogo", AutoGo, mFileUtils);
        }

        private void brewAutoGo(TypeSpec.Builder classBuilder) throws IOException {
            List<MethodSpec> launchActivityMethods = new ArrayList<>();
            if (launchTargets != null) {
                for (KeyValueGroup group : launchTargets) {
                    ClassName launcherClassName = ClassName.get(group.getPackageName(), group.getSimpleClassName() + "_AutoLauncher");
                    MethodSpec launchMethod = MethodSpec.methodBuilder("goto" + group.getSimpleClassName())
                            .addModifiers(Modifier.PUBLIC)
                            .returns(launcherClassName)
                            .addStatement("check()")
                            .addStatement("autoAssignerMap.put($S, new $T())",
                                    group.getClassName(),
                                    ClassName.get(group.getPackageName(), group.getSimpleClassName() + "_AutoAssigner"))
                            .addStatement("return new $T(srcActivity)", launcherClassName)
                            .build();
                    launchActivityMethods.add(launchMethod);
                }
            }
            CodeBlock.Builder putToSharedPrefsAssistantMapBuilder = CodeBlock.builder();
            if (sharePrefsTargets != null) {
                for (KeyValueGroup sharePrefsTarget : sharePrefsTargets) {
                    putToSharedPrefsAssistantMapBuilder
                            .addStatement("sharedPrefsAssistantMap.put($S, new $T())",
                                    sharePrefsTarget.getPackageName(),
                                    ClassName.get(sharePrefsTarget.getPackageName(),sharePrefsTarget.getSimpleClassName()+"_AutoSharePrefsAssistant"));
                }
            }
            MethodSpec save = MethodSpec.methodBuilder("save")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ClassNames.CONTEXT, "context")
                    .beginControlFlow("if (context == null)")
                    .addStatement("return")
                    .endControlFlow()
                    .addStatement("initSharedPrefsAssistantMap()")
                    .beginControlFlow("try")
                    .addStatement("getSharedPrefsAssistant(context).save()")
                    .endControlFlow()
                    .beginControlFlow("catch ($T e)", Exception.class)
                    .addStatement("Log.d($S, e.getMessage())", "AutoGo")
                    .endControlFlow()
                    .build();
            MethodSpec restore = MethodSpec.methodBuilder("restore")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ClassNames.CONTEXT, "context")
                    .beginControlFlow("if (context == null)")
                    .addStatement("return")
                    .endControlFlow()
                    .addStatement("initSharedPrefsAssistantMap()")
                    .beginControlFlow("try")
                    .addStatement("getSharedPrefsAssistant(context).restore()")
                    .endControlFlow()
                    .beginControlFlow("catch ($T e)", Exception.class)
                    .addStatement("Log.d($S, e.getMessage())", "AutoGo")
                    .endControlFlow()
                    .build();
            MethodSpec initSharedPrefsAssistantMap = MethodSpec.methodBuilder("initSharedPrefsAssistantMap")
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                    .beginControlFlow("if (sharedPrefsAssistantMap == null)")
                    .addStatement("sharedPrefsAssistantMap = new HashMap<>()")
                    .addCode(putToSharedPrefsAssistantMapBuilder.build())
                    .endControlFlow()
                    .build();
            MethodSpec getSharedPrefsAssistant = MethodSpec.methodBuilder("getSharedPrefsAssistant")
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                    .addParameter(ClassNames.CONTEXT, "context")
                    .returns(ClassNames.AUTO_SHARE_PREFS_ASSISTANT)
                    .addStatement("String className = context.getClass().getName()")
                    .addStatement("$T handler = sharedPrefsAssistantMap.get(className)", ClassNames.AUTO_SHARE_PREFS_ASSISTANT)
                    .beginControlFlow("if (handler == null)")
                    .addStatement("throw new $T($S)", NullPointerException.class, "your target field members don't have any @SharePerfs.")
                    .endControlFlow()
                    .addStatement("handler.setTarget(context)")
                    .addStatement("return handler")
                    .build();
            FieldSpec autoAssignerMap = FieldSpec.builder(ClassNames.MAP_ASSIGNER, "autoAssignerMap")
                    .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
                    .initializer("new $T<>()", HashMap.class)
                    .build();
            FieldSpec sharedPrefsAssistantMap = FieldSpec.builder(ClassNames.MAP_SHARE_PREFS_ASSISTANT, "sharedPrefsAssistantMap")
                    .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
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
            classBuilder
                    .addField(autoAssignerMap)
                    .addField(sharedPrefsAssistantMap)
                    .addMethod(from)
                    .addMethod(assign)
                    .addMethod(getAssigner)
                    .addMethod(initSharedPrefsAssistantMap)
                    .addMethod(save)
                    .addMethod(restore)
                    .addMethod(getSharedPrefsAssistant)
                    .addType(ActivityLauncherManager)
                    .build();
        }
    }
}
