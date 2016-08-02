package com.tellh.processor;

import com.autogo.annotation.SharePrefs;
import com.tellh.brewer.AutoGoCodeBrewer;
import com.tellh.brewer.CodeBrewer;
import com.tellh.brewer.SharePrefsCodeBrewer;
import com.tellh.entity.KeyValueEntity;
import com.tellh.entity.KeyValueGroup;
import com.tellh.entity.SharePrefsKeyValueEntity;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;

/**
 * Created by tlh on 2016/8/2.
 */
public class SharePrefsHandler extends BaseAnnotationHandler {

    public SharePrefsHandler(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    protected CodeBrewer newCodeBrewer() {
        return new SharePrefsCodeBrewer(mFileUtils,mElementUtils,mMessager);
    }

    @Override
    protected void buildAutoGoBrewer(AutoGoCodeBrewer.Builder autoGoBrewerBuilder, List<KeyValueGroup> targets) {
        autoGoBrewerBuilder.sharePrefsTargets(targets);
    }

    @Override
    protected KeyValueEntity getKeyValueEntity(VariableElement variableElement) {
        return new SharePrefsKeyValueEntity(variableElement);
    }

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return SharePrefs.class;
    }
}
