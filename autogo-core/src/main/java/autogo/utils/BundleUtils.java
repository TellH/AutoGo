package autogo.utils;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by tlh on 2016/8/3.
 */
public class BundleUtils {
    public static void saveData(Bundle bundle, String key, Object data) {
        String type = data.getClass().getSimpleName();
        if ("Integer".equals(type)) {
            bundle.putInt(key, (Integer) data);
        } else if ("Boolean".equals(type)) {
            bundle.putBoolean(key, (Boolean) data);
        } else if ("String".equals(type)) {
            bundle.putString(key, (String) data);
        } else if ("Float".equals(type)) {
            bundle.putFloat(key, (Float) data);
        } else if ("Long".equals(type)) {
            bundle.putLong(key, (Long) data);
        } else if ("Int[]".equals(type)) {
            bundle.putIntArray(key, (int[]) data);
        } else if ("boolean[]".equals(type)) {
            bundle.putBooleanArray(key, (boolean[]) data);
        } else if ("String[]".equals(type)) {
            bundle.putStringArray(key, (String[]) data);
        } else if ("float[]".equals(type)) {
            bundle.putFloatArray(key, (float[]) data);
        } else if ("long[]".equals(type)) {
            bundle.putLongArray(key, (long[]) data);
        } else if ("ArrayList".equals(type)) {
            //I don't want to get the generic type via reflection, so I use String.class as the default type.
            bundle.putStringArrayList(key, (ArrayList<String>) data);
        }
    }

    public static Object getData(Bundle bundle, String key, Object defValue) {
        return getData(bundle, key, defValue.getClass(),defValue);
    }

    public static Object getData(Bundle bundle, String key, Class clz, Object defValue) {
        String type = clz.getSimpleName();
        if ("Integer".equals(type)) {
            return bundle.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return bundle.getBoolean(key, (Boolean) defValue);
        } else if ("String".equals(type)) {
            return bundle.getString(key, (String) defValue);
        } else if ("Float".equals(type)) {
            return bundle.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return bundle.getLong(key, (Long) defValue);
        } else if ("Int[]".equals(type)) {
            return checkNull(bundle.getIntArray(key),defValue);
        } else if ("boolean[]".equals(type)) {
            return checkNull(bundle.getBooleanArray(key),defValue);
        } else if ("String[]".equals(type)) {
            return checkNull(bundle.getStringArray(key),defValue);
        } else if ("float[]".equals(type)) {
            return checkNull(bundle.getFloatArray(key),defValue);
        } else if ("long[]".equals(type)) {
            return checkNull(bundle.getLongArray(key),defValue);
        } else if ("ArrayList".equals(type)) {
            //I don't want to get the generic type via reflection, so I use String.class as the default type.
            return checkNull(bundle.getStringArrayList(key),defValue);
        }
        return null;
    }
    private static Object checkNull(Object extra, Object defValue) {
        if (extra == null)
            return defValue;
        return extra;
    }
}
