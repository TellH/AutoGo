package com.tellh.entity;

import com.autogo.annotation.IntentValue;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by tlh on 2016/7/31.
 */
public class IntentValueEntity {
    private String mKey;
    private VariableElement mElement;
    private TypeElement mClassElement;//The class which the field belongs to
    private TypeName fieldType;
    private String fieldName;

    public TypeName getFieldType() {
        return fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public IntentValueEntity(VariableElement element) {
        this.mElement = element;
        IntentValue annotation = mElement.getAnnotation(IntentValue.class);
        mKey = annotation.value();
        if (mKey == null || mKey.equals(""))
            mKey = mElement.getSimpleName().toString();
        mClassElement = (TypeElement) element.getEnclosingElement();
        fieldType = ClassName.get(element.asType());

        fieldName = element.getSimpleName().toString();
    }

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
