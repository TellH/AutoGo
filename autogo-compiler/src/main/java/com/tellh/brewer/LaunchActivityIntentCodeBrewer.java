package com.tellh.brewer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tellh.entity.IntentValueEntity;
import com.tellh.entity.IntentValueGroup;
import com.tellh.utils.ClassNames;
import com.tellh.utils.ProcessorUtils;

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
public class LaunchActivityIntentCodeBrewer {
    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;
    public LaunchActivityIntentCodeBrewer(Filer mFileUtils, Elements mElementUtils, Messager mMessager) {
        this.mFileUtils = mFileUtils;
        this.mElementUtils = mElementUtils;
        this.mMessager = mMessager;
    }

    public void brewCode(IntentValueGroup group) throws IOException {
        brewAutoLauncher(group);
        brewAutoAssigner(group);
    }

    /**
     * public final class TestActivity_AutoAssigner implements AutoAssigner {
     * private TestActivity target;
     * private Intent intent;
     *
     * @param group
     * @throws IOException
     * @throws IllegalArgumentException
     * @Override public void setTarget(Activity target) {
     * this.target = (TestActivity) target;
     * intent = target.getIntent();
     * }
     * @Override public void assign() {
     * target.name = intent.getStringExtra("name");
     * target.age = intent.getIntExtra("age", 0);
     * }
     * }
     */
    private void brewAutoAssigner(IntentValueGroup group) throws IOException, IllegalArgumentException {
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
        for (IntentValueEntity valueEntity : group.getIntentValues()) {
//            builder.addStatement("$T $L = intent.get%sExtra($S)",
            try {
                if (valueEntity.getFieldType().isPrimitive()){
                    builder.addStatement(ProcessorUtils.getFormatForExtra(valueEntity),
                            valueEntity.getFieldType(),
                            valueEntity.getFieldName(),
                            valueEntity.getKey(),
                            valueEntity.getFieldName());
                }else {
                    builder.addStatement(ProcessorUtils.getFormatForExtra(valueEntity),
                            valueEntity.getFieldType(),
                            valueEntity.getFieldName(),
                            valueEntity.getKey());
                }
                builder.beginControlFlow("if (!$T.checkNull($L))",ClassNames.CLASSUTILS,valueEntity.getFieldName())
                        .addStatement("target.$L = $L",valueEntity.getFieldName(),valueEntity.getFieldName())
                        .endControlFlow();
            } catch (IllegalArgumentException e) {
                ProcessorUtils.error(mMessager, e.getMessage().toString());
                continue;
            }
        }
        MethodSpec assign = MethodSpec.methodBuilder("assign")
                .returns(ClassNames.INTENT)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("intent = target.getIntent()")
                .addStatement("this.target = ($T)target", activity)
                .addCode(builder.build())
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
        JavaFile javaFile = JavaFile.builder(group.getPackageName(), AutoAssigner)
                .build();
        javaFile.writeTo(mFileUtils);
    }

    /**
     * code template
     * public class TestActivity_AutoLauncher implements AutoLauncher {
     * private final Activity srcActivity;
     * private Intent intent;
     * <p/>
     * public TestActivity_AutoLauncher name(String value) {
     * intent.putExtra("name", value);
     * return this;
     * }
     * <p/>
     * public TestActivity_AutoLauncher age(int value) {
     * intent.putExtra("age", value);
     * //        intent.putCharSequenceArrayListExtra()
     * //        intent.putIntegerArrayListExtra()
     * //        intent.putParcelableArrayListExtra()
     * //        intent.putStringArrayListExtra()
     * return this;
     * }
     * <p/>
     * public TestActivity_AutoLauncher(Activity srcActivity) {
     * intent = new Intent(srcActivity, TestActivity.class);
     * this.srcActivity = srcActivity;
     * }
     *
     * @param group
     * @throws IOException
     * @throws IllegalArgumentException
     * @Override public void go() {
     * srcActivity.startActivity(intent);
     * }
     * }
     */
    private void brewAutoLauncher(IntentValueGroup group) throws IOException, IllegalArgumentException {
        ClassName activity = ClassName.get(group.getPackageName(), group.getSimpleClassName());
        ClassName launcherName = ClassName.get(group.getPackageName(), group.getSimpleClassName() + "_AutoLauncher");
        FieldSpec srcActivity = FieldSpec.builder(ClassNames.ACTIVITY, "srcActivity")
                .addModifiers(Modifier.FINAL, Modifier.PRIVATE)
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
                .returns(TypeName.VOID)
                .addStatement("srcActivity.startActivity(intent)")
                .addAnnotation(Override.class)
                .build();
        List<MethodSpec> valueSetters = new ArrayList<>();

        for (IntentValueEntity valueEntity : group.getIntentValues()) {
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
                if (!ProcessorUtils.checkFieldType(fieldType)) {
                    ProcessorUtils.error(mMessager, "your value type :" + fieldType + " do not allow to put into an intent object.");
                    continue;
                }
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
        JavaFile javaFile = JavaFile.builder(group.getPackageName(), AutoLauncher)
                .build();
        javaFile.writeTo(mFileUtils);
    }
}
