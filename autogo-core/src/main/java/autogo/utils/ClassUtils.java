package autogo.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tlh on 2016/8/2.
 */
public class ClassUtils {

    public static boolean checkNull(Object o) {
        if (o == null)
            return true;
        Class<?> type = o.getClass();
        if (isPrimitiveOrWrapper(type))
            return false;
        if (type == String.class&&!stringIsEmpty((CharSequence) o)) {
            return false;
        }
        return true;

    }
    public static boolean stringIsEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    public static boolean isPrimitiveOrWrapper(final Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isPrimitive() || isPrimitiveWrapper(type);
    }

    public static boolean isPrimitiveWrapper(final Class<?> type) {
        return wrapperPrimitiveMap.containsKey(type);
    }

    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }

    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<Class<?>, Class<?>>();

    static {
        for (final Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperMap.entrySet()) {
            final Class<?> primitiveClass = entry.getKey();
            final Class<?> wrapperClass = entry.getValue();
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
    }
}
