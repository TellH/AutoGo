package com.tellh.processor;

import com.autogo.annotation.Bundle;
import com.tellh.brewer.AutoGoClassCodeBrewer;
import com.tellh.brewer.CodeBrewer;
import com.tellh.brewer.BundleCodeBrewer;
import com.tellh.entity.BundleKeyValueEntity;
import com.tellh.entity.KeyValueEntity;
import com.tellh.entity.KeyValueGroup;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;

/**
 * Created by tlh on 2016/8/2.
 */
public class BundleHandler extends BaseAnnotationHandler {

    public BundleHandler(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    protected CodeBrewer newCodeBrewer() {
        return new BundleCodeBrewer(mFileUtils,mElementUtils,mMessager);
    }

    @Override
    protected void buildAutoGoBrewer(AutoGoClassCodeBrewer.Builder autoGoBrewerBuilder, List<KeyValueGroup> targets) {
        autoGoBrewerBuilder.instancceStateTargets(targets);
    }

    @Override
    protected KeyValueEntity getKeyValueEntity(VariableElement variableElement) {
        return new BundleKeyValueEntity(variableElement);
    }

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return Bundle.class;
    }
}
