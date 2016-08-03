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

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

/**
 * Created by tlh on 2016/8/2.
 */
public class SharePrefsCodeBrewer extends CodeBrewer {

    public SharePrefsCodeBrewer(Filer mFileUtils, Elements mElementUtils, Messager mMessager) {
        super(mFileUtils, mElementUtils, mMessager);
    }

    @Override
    public void brewCode(KeyValueGroup group) throws IOException {
        ClassName assistantName = ClassName.get(group.getPackageName(), group.getSimpleClassName() + "_AutoSharePrefsAssistant");
        ClassName targetType = ClassName.get(group.getPackageName(), group.getSimpleClassName());
        FieldSpec target = FieldSpec.builder(targetType, "target")
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
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addAnnotation(Override.class)
                .build();
        CodeBlock.Builder builder = CodeBlock.builder();
        StringBuilder savaDataStatements=new StringBuilder("SharedPreferencesUtils.getInstance(target).editor()");
        for (KeyValueEntity valueEntity : group.getIntentValues()) {
            TypeName fieldType = valueEntity.getFieldType();
            if (Utils.isData(fieldType)){
                makeGetDataBlock(valueEntity,builder);
            }else {
                if (fieldType.isPrimitive())
                    continue;
                builder.addStatement("target.$L = ($T) utils.getObject($S, target.$L)",
                        valueEntity.getFieldName(),valueEntity.getFieldType(),valueEntity.getKey(),valueEntity.getFieldName());
            }
            savaDataStatements.append(String.format(".saveData(\"%s\", target.%s)",valueEntity.getKey(),valueEntity.getFieldName()));
        }
        savaDataStatements.append(".commit()");
        MethodSpec restore = MethodSpec.methodBuilder("restore")
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .beginControlFlow("if (target == null)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T utils = $T.getInstance(target)",ClassNames.SHARE_PREFS_UTILS,ClassNames.SHARE_PREFS_UTILS)
                .addCode(builder.build())
                .addStatement("target = null")
                .build();
        MethodSpec save = MethodSpec.methodBuilder("save")
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .beginControlFlow("if (target == null)")
                .addStatement("return")
                .endControlFlow()
                .addStatement(savaDataStatements.toString())
                .addStatement("target = null")
                .build();
        TypeSpec AutoSharePrefsAssistant = TypeSpec.classBuilder(assistantName)
                .addSuperinterface(ClassNames.AUTO_STORAGE_ASSISTANT)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(target)
                .addMethod(setTarget)
                .addMethod(setBundle)
                .addMethod(save)
                .addMethod(restore)
                .build();
        brewJavaFile(group.getPackageName(), AutoSharePrefsAssistant, mFileUtils);
    }
    private CodeBlock.Builder makeGetDataBlock(KeyValueEntity entity,CodeBlock.Builder builder){
        builder.beginControlFlow("if (target.$L==null)",entity.getFieldName())
                .addStatement("target.$L = ($T) utils.getData($S,$L.class, target.$L)",
                        entity.getFieldName(),entity.getFieldType(),entity.getKey(),
                        Utils.withoutGenericType(entity.getFieldType().toString()),
                        entity.getFieldName())
                .endControlFlow()
                .beginControlFlow("else")
                .addStatement("target.$L = ($T) utils.getData($S, target.$L)",entity.getFieldName(),
                        entity.getFieldType(),entity.getKey(),entity.getFieldName())
                .endControlFlow();
        return builder;
    }
}
