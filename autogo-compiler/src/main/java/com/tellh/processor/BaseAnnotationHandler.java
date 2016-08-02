package com.tellh.processor;

import com.autogo.annotation.IntentValue;
import com.tellh.brewer.AutoGoCodeBrewer;
import com.tellh.brewer.CodeBrewer;
import com.tellh.entity.KeyValueEntity;
import com.tellh.entity.KeyValueGroup;
import com.tellh.utils.Utils;

import java.lang.annotation.Annotation;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by tlh on 2016/8/2.
 */
public abstract class BaseAnnotationHandler {
    protected Filer mFileUtils;
    protected Elements mElementUtils;
    protected Messager mMessager;
    protected Map<String, KeyValueGroup> mClassifyMap;
    protected AutoGoCodeBrewer.Builder autoGoBrewerBuilder;
    protected CodeBrewer codeBrewer;

    public BaseAnnotationHandler(ProcessingEnvironment processingEnv) {
        mFileUtils = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        codeBrewer = newCodeBrewer();
        mClassifyMap = new LinkedHashMap<>();
    }

    protected abstract CodeBrewer newCodeBrewer();

    public boolean handle(RoundEnvironment roundEnv, AutoGoCodeBrewer.Builder autoGoBrewerBuilder) {
        mClassifyMap.clear();
        if (!gatherInformation(roundEnv)) return false;
        if (!mClassifyMap.isEmpty()) {
            this.autoGoBrewerBuilder = autoGoBrewerBuilder;
            List<KeyValueGroup> targets = new ArrayList<>();
            try {
                for (Map.Entry<String, KeyValueGroup> groupEntry : mClassifyMap.entrySet()) {
                    KeyValueGroup group = groupEntry.getValue();
                    codeBrewer.brewCode(group);
                    targets.add(group);
                }
                buildAutoGoBrewer(autoGoBrewerBuilder, targets);
            } catch (Exception e) {
                Utils.error(mMessager, e.getMessage().toString());
            }
            return true;
        }
        return false;
    }

    public boolean gatherInformation(RoundEnvironment roundEnv) {
        //traverse all elements annotated with
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(getAnnotationClass())) {
            if (!isValid(annotatedElement)) {
                return false;
            }
            VariableElement variableElement = (VariableElement) annotatedElement;
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            String className = classElement.getQualifiedName().toString();
            KeyValueGroup keyValueGroup = mClassifyMap.get(className);
            if (keyValueGroup == null) {
                keyValueGroup = new KeyValueGroup(classElement, mElementUtils);
                mClassifyMap.put(className, keyValueGroup);
            }
            keyValueGroup.add(getKeyValueEntity(variableElement));
        }
        return true;
    }

    protected abstract void buildAutoGoBrewer(AutoGoCodeBrewer.Builder autoGoBrewerBuilder, List<KeyValueGroup> targets);

    protected abstract KeyValueEntity getKeyValueEntity(VariableElement variableElement);

    protected abstract Class<? extends Annotation> getAnnotationClass();

    private boolean isValid(Element annotatedElement) {
        if (annotatedElement.getKind() != ElementKind.FIELD) {
            Utils.error(mMessager, annotatedElement, "Only field can be annotated with @%s",
                    IntentValue.class);
            return false;
        }
        return true;
    }

}
