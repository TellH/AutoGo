package com.tellh.processor;

import com.autogo.annotation.IntentValue;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.tellh.brewer.AutoGoCodeBrewer;
import com.tellh.brewer.LaunchActivityIntentCodeBrewer;
import com.tellh.entity.IntentValueEntity;
import com.tellh.entity.IntentValueGroup;
import com.tellh.utils.ProcessorUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by tlh on 2016/8/2.
 */
public class IntentValueHandler {
    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;
    private Map<String, IntentValueGroup> mClassifyMap;
    private LaunchActivityIntentCodeBrewer intentCodeBrewer;
    private AutoGoCodeBrewer.Builder autoGoBrewerBuilder;

    public IntentValueHandler(ProcessingEnvironment processingEnv) {
        mFileUtils = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        intentCodeBrewer = new LaunchActivityIntentCodeBrewer(mFileUtils, mElementUtils, mMessager);
        mClassifyMap = new LinkedHashMap<>();
    }

    public boolean handle(RoundEnvironment roundEnv, AutoGoCodeBrewer.Builder autoGoBrewerBuilder) {
        mClassifyMap.clear();
        if (!gatherInformation(roundEnv)) return false;
        if (!mClassifyMap.isEmpty()) {
            this.autoGoBrewerBuilder = autoGoBrewerBuilder;
            brewJavaCode();
            return true;
        }
        return false;
    }

    private boolean gatherInformation(RoundEnvironment roundEnv) {
        //traverse all elements annotated with @IntentValue
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(IntentValue.class)) {
            if (!isValid(annotatedElement)) {
                return false;
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
        return true;
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
                IntentValueGroup group = groupEntry.getValue();
                intentCodeBrewer.brewCode(group);
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
            autoGoBrewerBuilder.launchActivityMethods(launchMethods);
        } catch (Exception e) {
            ProcessorUtils.error(mMessager, e.getMessage().toString());
        }
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
