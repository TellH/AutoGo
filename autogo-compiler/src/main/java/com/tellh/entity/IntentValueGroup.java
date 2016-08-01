package com.tellh.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by tlh on 2016/7/31.
 */
public final class IntentValueGroup {
    private Set<IntentValueEntity> intentValues;
    private TypeElement mClassElement;
    private PackageElement mPackageElement;
    private String packageName;
    private String simpleClassName;
    private String className;

    public IntentValueGroup(TypeElement typeElement,Elements elementUtils) {
        intentValues=new LinkedHashSet<>();
        mClassElement=typeElement;
        mPackageElement=elementUtils.getPackageOf(mClassElement);
        packageName=mPackageElement.getQualifiedName().toString();
        simpleClassName=mClassElement.getSimpleName().toString();
        className=mClassElement.getQualifiedName().toString();
    }

    public void add(IntentValueEntity intentValue){
        intentValues.add(intentValue);
    }

    public Set<IntentValueEntity> getIntentValues() {
        return intentValues;
    }

    public TypeElement getmClassElement() {
        return mClassElement;
    }

    public PackageElement getmPackageElement() {
        return mPackageElement;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public String getClassName() {
        return className;
    }
}
