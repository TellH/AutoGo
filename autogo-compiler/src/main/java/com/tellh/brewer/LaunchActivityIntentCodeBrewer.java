package com.tellh.brewer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tellh.entity.KeyValueEntity;
import com.tellh.entity.KeyValueGroup;
import com.tellh.utils.ClassNames;
import com.tellh.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

/**
 * Created by tlh on 2016/8/2.
 */
public class LaunchActivityIntentCodeBrewer extends CodeBrewer {

    public LaunchActivityIntentCodeBrewer(Filer mFileUtils, Elements mElementUtils, Messager mMessager) {
        super(mFileUtils, mElementUtils, mMessager);
    }

    @Override
    public void brewCode(KeyValueGroup group) throws IOException {
        brewAutoLauncher(group);
        brewAutoAssigner(group);
    }

    private void brewAutoAssigner(KeyValueGroup group) throws IOException, IllegalArgumentException {
        ClassName assignerName = ClassName.get(group.getPackageName(), group.getSimpleClassName() + "_AutoAssigner");
        ClassName activity = ClassName.get(group.getPackageName(), group.getSimpleClassName());
        FieldSpec srcActivity = FieldSpec.builder(activity, "target")
                .addModifiers(Modifier.PRIVATE)
                .build();
        FieldSpec intent = FieldSpec.builder(ClassNames.INTENT, "intent")
                .addModifiers(Modifier.PRIVATE)
                .build();
        MethodSpec setTarget = MethodSpec.methodBuilder("setTarget")
                .addParameter(ClassNames.ACTIVITY, "target")
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("this.target = ($T)target", activity)
                .build();
        CodeBlock.Builder builder = CodeBlock.builder();
        for (KeyValueEntity valueEntity : group.getIntentValues()) {
            makeGetDataBlock(valueEntity, builder);
        }
        MethodSpec assign = MethodSpec.methodBuilder("assign")
                .returns(ClassNames.INTENT)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("intent = target.getIntent()")
                .addStatement("this.target = ($T)target", activity)
                .addCode(builder.build())
                .addStatement("target = null")
                .addStatement("return intent")
                .build();
        TypeSpec AutoAssigner = TypeSpec.classBuilder(assignerName)
                .addSuperinterface(ClassNames.AUTO_ASSIGNER)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(srcActivity)
                .addField(intent)
                .addMethod(setTarget)
                .addMethod(assign)
                .build();
        brewJavaFile(group.getPackageName(), AutoAssigner, mFileUtils);
    }

    private CodeBlock.Builder makeGetDataBlock(KeyValueEntity entity, CodeBlock.Builder builder) {
        if (entity.getFieldType().isPrimitive()) {
            builder.addStatement("target.$L = ($T) $T.getData(intent,$S, target.$L)", entity.getFieldName(),
                    entity.getFieldType(), ClassNames.INTENT_UTILS, entity.getKey(), entity.getFieldName());
            return builder;
        }
        builder.beginControlFlow("if (target.$L==null)", entity.getFieldName())
                .addStatement("target.$L = ($T) $T.getData(intent,$S,$L.class, target.$L)",
                        entity.getFieldName(), entity.getFieldType(), ClassNames.INTENT_UTILS,
                        entity.getKey(), Utils.withoutGenericType(entity.getFieldType().toString()), entity.getFieldName())
                .endControlFlow()
                .beginControlFlow("else")
                .addStatement("target.$L = ($T) $T.getData(intent,$S, target.$L)", entity.getFieldName(),
                        entity.getFieldType(), ClassNames.INTENT_UTILS, entity.getKey(), entity.getFieldName())
                .endControlFlow();
        return builder;
    }

    private void brewAutoLauncher(KeyValueGroup group) throws IOException, IllegalArgumentException {
        ClassName activity = ClassName.get(group.getPackageName(), group.getSimpleClassName());
        ClassName launcherName = ClassName.get(group.getPackageName(), group.getSimpleClassName() + "_AutoLauncher");
        FieldSpec srcActivity = FieldSpec.builder(ClassNames.ACTIVITY, "srcActivity")
                .addModifiers(Modifier.PRIVATE)
                .build();
        FieldSpec intent = FieldSpec.builder(ClassNames.INTENT, "intent")
                .addModifiers(Modifier.PRIVATE)
                .build();
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassNames.ACTIVITY, "srcActivity")
                .addStatement("this.srcActivity = srcActivity")
                .addStatement("intent = new $T(srcActivity,$T.class)", ClassNames.INTENT, activity)
                .build();
        MethodSpec go = MethodSpec.methodBuilder("go")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.VOID)
                .addStatement("srcActivity.startActivity(intent)")
                .addStatement("intent = null")
                .addStatement("srcActivity = null")
                .build();
        List<MethodSpec> valueSetters = new ArrayList<>();

        for (KeyValueEntity valueEntity : group.getIntentValues()) {
            TypeName fieldType = valueEntity.getFieldType();
            MethodSpec.Builder builder = MethodSpec.methodBuilder(valueEntity.getKey())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(launcherName)
                    .addParameter(fieldType, "value");
            if (fieldType.equals(ClassNames.INT_ARRAY_LIST)) {
                builder.addStatement("intent.putIntegerArrayListExtra($S,value)", valueEntity.getKey());
            } else if (fieldType.equals(ClassNames.STRING_ARRAY_LIST)) {
                builder.addStatement("intent.putStringArrayListExtra($S,value)", valueEntity.getKey());
            } else if (fieldType.equals(ClassNames.CHAR_SEQUENCE_ARRAY_LIST)) {
                builder.addStatement("intent.putCharSequenceArrayListExtra($S,value)", valueEntity.getKey());
            } else if (fieldType.equals(ClassNames.PARCELABLE_ARRAY_LIST)) {
                builder.addStatement("intent.putParcelableArrayListExtra($S,value)", valueEntity.getKey());
            } else {
//                if (!Utils.checkFieldType(fieldType)) {
//                    Utils.error(mMessager, "your value type :" + fieldType + " do not allow to put into an intent object.");
//                    continue;
//                }
                builder.addStatement("intent.putExtra($S,value)", valueEntity.getKey());
            }
            builder.addStatement("return this");
            MethodSpec setter = builder.build();
            valueSetters.add(setter);
        }
        TypeSpec AutoLauncher = TypeSpec.classBuilder(launcherName)
                .addSuperinterface(ClassNames.AUTO_LAUNCHER)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(srcActivity)
                .addField(intent)
                .addMethod(constructor)
                .addMethod(go)
                .addMethods(valueSetters)
                .build();
        brewJavaFile(group.getPackageName(), AutoLauncher, mFileUtils);
    }
}
