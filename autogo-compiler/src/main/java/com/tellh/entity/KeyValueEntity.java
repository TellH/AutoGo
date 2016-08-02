package com.tellh.entity;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by tlh on 2016/7/31.
 */
public abstract class KeyValueEntity {
    protected String mKey;
    protected VariableElement mElement;
    protected TypeElement mClassElement;//The class which the field belongs to
    protected TypeName fieldType;
    protected String fieldName;

    public TypeName getFieldType() {
        return fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public KeyValueEntity(VariableElement element) {
        this.mElement = element;
        initKey();
        mClassElement = (TypeElement) element.getEnclosingElement();
        fieldType = ClassName.get(element.asType());
        fieldName = element.getSimpleName().toString();
    }

    protected abstract void initKey();

    public String getKey() {
        return mKey;
    }

    public VariableElement getElement() {
        return mElement;
    }

    public TypeElement getClassElement() {
        return mClassElement;
    }
}
