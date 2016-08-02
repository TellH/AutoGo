package com.tellh.utils;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.tellh.entity.KeyValueEntity;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Created by tlh on 2016/7/31.
 */
public class Utils {
    private static Set<TypeName> typeSupportedSet;

    static {
        typeSupportedSet = new HashSet<>();
        typeSupportedSet.add(ArrayTypeName.of(TypeName.BOOLEAN));
        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.INT));
        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.BYTE));
        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.CHAR));
        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.LONG));
        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.DOUBLE));
        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.FLOAT));
        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.SHORT));
        typeSupportedSet.add(ArrayTypeName.get(String.class));
        typeSupportedSet.add(ArrayTypeName.get(CharSequence.class));
        typeSupportedSet.add(ClassName.get(String.class));
        typeSupportedSet.add(ClassNames.STRING_ARRAY_LIST);
        typeSupportedSet.add(ClassNames.CHAR_SEQUENCE_ARRAY_LIST);
        typeSupportedSet.add(ClassNames.INT_ARRAY_LIST);
        typeSupportedSet.add(ClassNames.PARCELABLE_ARRAY_LIST);
    }

    public static void error(Messager messager, Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    public static void error(Messager messager, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args));
    }

    public static String getFormatForExtra(final KeyValueEntity valueEntity) throws IllegalArgumentException {
//        "target.$L = intent.get%sExtra($S)"
//        "$T $L = intent.get%sExtra($S)"
        TypeName fieldType = valueEntity.getFieldType();
        String key = valueEntity.getKey();
        if (fieldType.equals(TypeName.BOOLEAN)) {
            return String.format("target.$L = intent.get%sExtra($S,target.$L)", "Boolean");
        } else if (fieldType.equals(TypeName.DOUBLE)) {
            return String.format("target.$L = intent.get%sExtra($S,target.$L)", "Double");
        } else if (fieldType.equals(TypeName.INT)) {
            return String.format("target.$L = intent.get%sExtra($S,target.$L)", "Int");
        } else if (fieldType.equals(TypeName.CHAR)) {
            return String.format("target.$L = intent.get%sExtra($S,target.$L)", "Char");
        } else if (fieldType.equals(TypeName.FLOAT)) {
            return String.format("target.$L = intent.get%sExtra($S,target.$L)", "Float");
        } else if (fieldType.equals(TypeName.BYTE)) {
            return String.format("target.$L = intent.get%sExtra($S,target.$L)", "Byte");
        } else if (fieldType.equals(TypeName.LONG)) {
            return String.format("target.$L = intent.get%sExtra($S,target.$L)", "Long");
        } else if (fieldType.equals(TypeName.SHORT)) {
            return String.format("target.$L = intent.get%sExtra($S,target.$L)", "Short");
        } else if (fieldType.equals(ClassName.get(String.class))) {
            return String.format("$T $L = intent.get%sExtra($S)", "String");
        } else if (fieldType.equals(ClassName.get(CharSequence.class))) {
            return String.format("$T $L = intent.get%sExtra($S)", "CharSequence");
        } else if (fieldType.equals(ClassNames.BUNDLE)) {
            return String.format("$T $L = intent.get%sExtra($S)", "Bundle");
        } else if (fieldType.equals(ArrayTypeName.of(TypeName.BOOLEAN))) {
            return String.format("$T $L = intent.get%sExtra($S)", "BooleanArray");
        } else if (fieldType.equals(ArrayTypeName.of(TypeName.DOUBLE))) {
            return String.format("$T $L = intent.get%sExtra($S)", "DoubleArray");
        } else if (fieldType.equals(ArrayTypeName.of(TypeName.INT))) {
            return String.format("$T $L = intent.get%sExtra($S)", "IntArray");
        } else if (fieldType.equals(ArrayTypeName.of(TypeName.CHAR))) {
            return String.format("$T $L = intent.get%sExtra($S)", "CharArray");
        } else if (fieldType.equals(ArrayTypeName.of(TypeName.FLOAT))) {
            return String.format("$T $L = intent.get%sExtra($S)", "FloatArray");
        } else if (fieldType.equals(ArrayTypeName.of(TypeName.BYTE))) {
            return String.format("$T $L = intent.get%sExtra($S)", "ByteArray");
        } else if (fieldType.equals(ArrayTypeName.of(TypeName.LONG))) {
            return String.format("$T $L = intent.get%sExtra($S)", "LongArray");
        } else if (fieldType.equals(ArrayTypeName.of(TypeName.SHORT))) {
            return String.format("$T $L = intent.get%sExtra($S)", "ShortArray");
        } else if (fieldType.equals(ArrayTypeName.get(String.class))) {
            return String.format("$T $L = intent.get%sExtra($S)", "StringArray");
        } else if (fieldType.equals(ArrayTypeName.get(CharSequence.class))) {
            return String.format("$T $L = intent.get%sExtra($S)", "CharSequenceArray");
        } else if (fieldType.equals(ClassNames.STRING_ARRAY_LIST)) {
            return String.format("$T $L = intent.get%sExtra($S)", "StringArrayList");
        } else if (fieldType.equals(ClassNames.CHAR_SEQUENCE_ARRAY_LIST)) {
            return String.format("$T $L = intent.get%sExtra($S)", "CharSequenceArrayList");
        } else if (fieldType.equals(ClassNames.INT_ARRAY_LIST)) {
            return String.format("$T $L = intent.get%sExtra($S)", "IntArrayList");
        } else if (fieldType.equals(ClassNames.PARCELABLE_ARRAY_LIST)) {
            return String.format("$T $L = intent.get%sExtra($S)", "ParcelableArrayList");
        }
        throw new IllegalArgumentException("your type " + fieldType.toString() + " can not be used to get extra from an intent");
    }

    public static boolean checkFieldType(TypeName fieldType) {
        if (fieldType.isPrimitive() || typeSupportedSet.contains(fieldType))
            return true;
        return false;
    }

    public static boolean isData(TypeName typeName) {
        if (typeName.equals(TypeName.INT) ||
                typeName.equals(TypeName.BOOLEAN) ||
                typeName.equals(TypeName.FLOAT) ||
                typeName.equals(TypeName.LONG) ||
                typeName.equals(ClassName.get(String.class)))
            return true;
        return false;
    }
}
