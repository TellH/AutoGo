package com.tellh.processor;

import com.autogo.annotation.IntentValue;
import com.google.auto.service.AutoService;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by tlh on 2016/7/31.
 */
@AutoService(Processor.class)
public class IntentValueProcessor extends AbstractProcessor {
    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;
    private Map<String, IntentValueGroup> mClassifyMap = new LinkedHashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFileUtils = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(IntentValue.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mClassifyMap.clear();
        if (gatherInformation(roundEnv)) return true;
        if (!mClassifyMap.isEmpty())
            brewJavaCode();
        return true;
    }

    private boolean gatherInformation(RoundEnvironment roundEnv) {
        //traverse all elements annotated with @IntentValue
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(IntentValue.class)) {
            if (!isValid(annotatedElement)) {
                return true;
            }
            VariableElement variableElement = (VariableElement) annotatedElement;
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            String className = classElement.getQualifiedName().toString();
            IntentValueGroup intentValueGroup = mClassifyMap.get(className);
            if (intentValueGroup == null) {
                intentValueGroup = new IntentValueGroup(classElement, mElementUtils);
                mClassifyMap.put(className, intentValueGroup);
            }
            intentValueGroup.add(new IntentValueEntity(variableElement));
        }
        return false;
    }

    /**
     * public abstract class AutoGo {
     * private static Map<String, AutoAssigner> autoAssignerMap = new HashMap<>();
     * public static ActivityLauncherManager from(Activity activity) {
     * return new ActivityLauncherManager(activity);
     * }
     * <p/>
     * public static class ActivityLauncherManager {
     * private final Activity srcActivity;
     * <p/>
     * public ActivityLauncherManager(Activity activity) {
     * this.srcActivity = activity;
     * }
     * <p/>
     * public TestActivity_AutoLauncher gotoTestActivity() {
     * check();
     * autoAssignerMap.put("tellh.com.autogo.TestActivity", new TestActivity_AutoAssigner());
     * return new TestActivity_AutoLauncher(srcActivity);
     * }
     * <p/>
     * private void check() {
     * if (autoAssignerMap == null) {
     * autoAssignerMap = new HashMap<>();
     * }
     * }
     * }
     * <p/>
     * public static boolean assign(Activity activity) {
     * try {
     * getAssigner(activity).assign();
     * } catch (IllegalArgumentException e) {
     * Log.e("AutoGo", e.getMessage());
     * return false;
     * }
     * return true;
     * }
     * <p/>
     * private static AutoAssigner getAssigner(Activity activity) throws IllegalArgumentException {
     * String className = activity.getClass().getName();
     * AutoAssigner autoAssigner = autoAssignerMap.get(className);
     * if (autoAssigner == null)
     * throw new IllegalArgumentException("your target activity haven't been started.");
     * autoAssigner.setTarget(activity);
     * return autoAssigner;
     * }
     * }
     */
    private void brewJavaCode() {
        List<MethodSpec> launchMethods = new ArrayList<>();
        try {
            for (Map.Entry<String, IntentValueGroup> groupEntry : mClassifyMap.entrySet()) {
                String key = groupEntry.getKey();
                IntentValueGroup group = groupEntry.getValue();
                brewAutoLauncher(group);
                brewAutoAssigner(group);
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
                launchMethods.add(launchMethod);
            }
            brewAutoGo(launchMethods);
        } catch (Exception e) {
            ProcessorUtils.error(mMessager, e.getMessage().toString());
        }
    }

    private void brewAutoGo(List<MethodSpec> launchMethods) throws IOException {
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
                .addMethods(launchMethods)
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
        TypeSpec AutoGo = TypeSpec.classBuilder("AutoGo")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addField(autoAssignerMap)
                .addMethod(from)
                .addMethod(assign)
                .addMethod(getAssigner)
                .addType(ActivityLauncherManager)
                .build();
        JavaFile javaFile = JavaFile.builder("autogo", AutoGo)
                .build();
        javaFile.writeTo(mFileUtils);
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
                .addStatement("intent = target.getIntent()")
                .addStatement("this.target = ($T)target", activity)
                .build();
        CodeBlock.Builder builder = CodeBlock.builder();
        for (IntentValueEntity valueEntity : group.getIntentValues()) {
//            builder.addStatement("target.$L = intent.get%sExtra($S)",
            try {
                builder.addStatement(ProcessorUtils.getFormatForExtra(valueEntity),
                        valueEntity.getFieldName(),
                        valueEntity.getKey());
            } catch (IllegalArgumentException e) {
                ProcessorUtils.error(mMessager, e.getMessage().toString());
                continue;
            }
        }
        MethodSpec assign = MethodSpec.methodBuilder("assign")
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("intent=target.getIntent()")
                .addStatement("this.target=($T)target", activity)
                .addCode(builder.build())
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

    private boolean isValid(Element annotatedElement) {
        if (annotatedElement.getKind() != ElementKind.FIELD) {
            ProcessorUtils.error(mMessager, annotatedElement, "Only field can be annotated with @%s",
                    IntentValue.class);
            return false;
        }
        return true;
    }
}
