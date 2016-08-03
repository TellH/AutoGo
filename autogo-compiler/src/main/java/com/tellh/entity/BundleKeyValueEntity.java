package com.tellh.entity;

import com.autogo.annotation.BundleValue;

import javax.lang.model.element.VariableElement;

/**
 * Created by tlh on 2016/8/2.
 */
public class BundleKeyValueEntity extends KeyValueEntity {
    public BundleKeyValueEntity(VariableElement element) {
        super(element);
    }

    @Override
    protected void initKey() {
        BundleValue annotation = mElement.getAnnotation(BundleValue.class);
        mKey = annotation.value();
        if (mKey == null || mKey.equals(""))
            mKey = mElement.getSimpleName().toString();
    }
}
