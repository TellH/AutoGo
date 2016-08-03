package com.tellh.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Created by tlh on 2016/7/31.
 */
public class Utils {
//    private static Set<TypeName> typeSupportedSet;
//
//    static {
//        typeSupportedSet = new HashSet<>();
//        typeSupportedSet.add(ArrayTypeName.of(TypeName.BOOLEAN));
//        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.INT));
//        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.BYTE));
//        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.CHAR));
//        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.LONG));
//        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.DOUBLE));
//        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.FLOAT));
//        typeSupportedSet.add(ArrayTypeName.of(ArrayTypeName.SHORT));
//        typeSupportedSet.add(ArrayTypeName.get(String.class));
//        typeSupportedSet.add(ArrayTypeName.get(CharSequence.class));
//        typeSupportedSet.add(ClassName.get(String.class));
//        typeSupportedSet.add(ClassNames.STRING_ARRAY_LIST);
//        typeSupportedSet.add(ClassNames.CHAR_SEQUENCE_ARRAY_LIST);
//        typeSupportedSet.add(ClassNames.INT_ARRAY_LIST);
//        typeSupportedSet.add(ClassNames.PARCELABLE_ARRAY_LIST);
//    }

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

//    public static boolean checkFieldType(TypeName fieldType) {
//        if (fieldType.isPrimitive() || typeSupportedSet.contains(fieldType))
//            return true;
//        return false;
//    }

    public static boolean isData(TypeName typeName) {
        if (typeName.equals(TypeName.INT) ||
                typeName.equals(TypeName.BOOLEAN) ||
                typeName.equals(TypeName.FLOAT) ||
                typeName.equals(TypeName.LONG) ||
                typeName.equals(ClassName.get(String.class)))
            return true;
        return false;
    }

    public static String withoutGenericType(String src) {
        StringBuilder builder = new StringBuilder(src);
        int left = builder.indexOf("<");
        if (left == -1)
            return builder.toString();
        int right = builder.indexOf(">", left);
        return builder.delete(left, right + 1).toString();
    }
}
