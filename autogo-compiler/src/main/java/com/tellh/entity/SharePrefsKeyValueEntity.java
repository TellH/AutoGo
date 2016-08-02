package com.tellh.entity;

import com.autogo.annotation.SharePrefs;

import javax.lang.model.element.VariableElement;

/**
 * Created by tlh on 2016/8/2.
 */
public class SharePrefsKeyValueEntity extends KeyValueEntity {
    public SharePrefsKeyValueEntity(VariableElement element) {
        super(element);
    }

    @Override
    protected void initKey() {
        SharePrefs annotation = mElement.getAnnotation(SharePrefs.class);
        mKey = annotation.value();
        if (mKey == null || mKey.equals(""))
            mKey = mElement.getSimpleName().toString();
    }
}
