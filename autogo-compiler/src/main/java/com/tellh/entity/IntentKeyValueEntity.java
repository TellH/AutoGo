package com.tellh.entity;

import com.autogo.annotation.IntentValue;

import javax.lang.model.element.VariableElement;

/**
 * Created by tlh on 2016/8/2.
 */
public class IntentKeyValueEntity extends KeyValueEntity {
    public IntentKeyValueEntity(VariableElement element) {
        super(element);
    }

    @Override
    protected void initKey() {
        IntentValue annotation = mElement.getAnnotation(IntentValue.class);
        mKey = annotation.value();
        if (mKey == null || mKey.equals(""))
            mKey = mElement.getSimpleName().toString();
    }
}
