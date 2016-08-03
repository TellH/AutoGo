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
        String type = defValue.getClass().getSimpleName();
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
            return bundle.getIntArray(key);
        } else if ("boolean[]".equals(type)) {
            return bundle.getBooleanArray(key);
        } else if ("String[]".equals(type)) {
            return bundle.getStringArray(key);
        } else if ("float[]".equals(type)) {
            return bundle.getFloatArray(key);
        } else if ("long[]".equals(type)) {
            return bundle.getLongArray(key);
        } else if ("ArrayList".equals(type)) {
            //I don't want to get the generic type via reflection, so I use String.class as the default type.
            return bundle.getStringArrayList(key);
        }
        return null;
    }
}
