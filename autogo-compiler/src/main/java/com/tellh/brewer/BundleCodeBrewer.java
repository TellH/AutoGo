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

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

/**
 * Created by tlh on 2016/8/2.
 */
public class BundleCodeBrewer extends CodeBrewer {

    public BundleCodeBrewer(Filer mFileUtils, Elements mElementUtils, Messager mMessager) {
        super(mFileUtils, mElementUtils, mMessager);
    }

    @Override
    public void brewCode(KeyValueGroup group) throws IOException {
        ClassName assistantName = ClassName.get(group.getPackageName(), group.getSimpleClassName() + "_AutoBundleAssistant");
        ClassName targetType = ClassName.get(group.getPackageName(), group.getSimpleClassName());
        FieldSpec target = FieldSpec.builder(targetType, "target")
                .addModifiers(Modifier.PRIVATE)
                .build();
        FieldSpec bundle = FieldSpec.builder(ClassNames.BUNDLE, "bundle")
                .addModifiers(Modifier.PRIVATE)
                .build();
        MethodSpec setTarget = MethodSpec.methodBuilder("setTarget")
                .addParameter(ClassNames.CONTEXT, "context")
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("this.target = ($T)context", targetType)
                .build();
        MethodSpec setBundle = MethodSpec.methodBuilder("setBundle")
                .addParameter(ClassNames.BUNDLE, "bundle")
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(Override.class)
                .addStatement("this.bundle = bundle")
                .build();
        CodeBlock restoreBlock = getBundleUtilCodeBlock("get", group);
        CodeBlock saveBlock = getBundleUtilCodeBlock("save", group);
        MethodSpec restore = MethodSpec.methodBuilder("restore")
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .beginControlFlow("if (target == null)")
                .addStatement("return")
                .endControlFlow()
                .addCode(restoreBlock)
                .addStatement("target = null")
                .addStatement("bundle = null")
                .build();
        MethodSpec save = MethodSpec.methodBuilder("save")
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .beginControlFlow("if (target == null)")
                .addStatement("return")
                .endControlFlow()
                .addCode(saveBlock)
                .addStatement("target = null")
                .addStatement("bundle = null")
                .build();
        TypeSpec AutoSharePrefsAssistant = TypeSpec.classBuilder(assistantName)
                .addSuperinterface(ClassNames.AUTO_STORAGE_ASSISTANT)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(target)
                .addField(bundle)
                .addMethod(setTarget)
                .addMethod(setBundle)
                .addMethod(save)
                .addMethod(restore)
                .build();
        brewJavaFile(group.getPackageName(), AutoSharePrefsAssistant, mFileUtils);
    }

    private CodeBlock getBundleUtilCodeBlock(String type, KeyValueGroup group) {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (KeyValueEntity valueEntity : group.getIntentValues()) {
            TypeName fieldType = valueEntity.getFieldType();
            if (type.equals("get")) {
                builder.addStatement(String.format("target.$L = ($T)$T.%sData(bundle, $S, target.$L)",type),valueEntity.getFieldName(),
                        valueEntity.getFieldType(), ClassNames.BUNDLE_UTILS, valueEntity.getKey(), valueEntity.getFieldName());
            } else {

                builder.addStatement(String.format("$T.%sData(bundle, $S, target.$L)", type),
                        ClassNames.BUNDLE_UTILS, valueEntity.getKey(), valueEntity.getFieldName());
            }
        }
        return builder.build();
    }
}
