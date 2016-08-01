package com.tellh.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;

/**
 * Created by tlh on 2016/7/31.
 */
public class ClassNames {
    public static final ClassName ACTIVITY = ClassName.get("android.app", "Activity");
    public static final ClassName LOG = ClassName.get("android.util", "Log");
    public static final ClassName AUTO_ASSIGNER = ClassName.get("autogo.internal", "AutoAssigner");
    public static final ClassName AUTO_LAUNCHER = ClassName.get("autogo.internal", "AutoLauncher");
    public static final ClassName INTENT = ClassName.get("android.content", "Intent");
    public static final ClassName ARRAY_LIST = ClassName.get("java.util", "ArrayList");
    public static final ClassName MAP = ClassName.get("java.util", "Map");
    public static final ClassName BUNDLE = ClassName.get("android.os", "Bundle");
    public static final ParameterizedTypeName STRING_ARRAY_LIST = ParameterizedTypeName.get(ARRAY_LIST, ClassName.get(String.class));
    public static final ParameterizedTypeName INT_ARRAY_LIST = ParameterizedTypeName.get(ARRAY_LIST, ClassName.get(Integer.class));
    public static final ParameterizedTypeName CHAR_SEQUENCE_ARRAY_LIST = ParameterizedTypeName.get(ARRAY_LIST, ClassName.get(CharSequence.class));
    public static final ParameterizedTypeName PARCELABLE_ARRAY_LIST = ParameterizedTypeName.get(ARRAY_LIST, ClassName.get("android.os", "Parcelable"));
    public static final ParameterizedTypeName MAP_ASSIGNER = ParameterizedTypeName.get(MAP, ClassName.get(String.class), AUTO_ASSIGNER);
}
