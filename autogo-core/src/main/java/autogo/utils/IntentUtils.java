package autogo.utils;

import android.content.Intent;
import android.os.Parcelable;

/**
 * Created by tlh on 2016/8/3.
 */
public class IntentUtils {
    public static Object getData(Intent intent, String key, Object defValue) {
        String type = defValue.getClass().getSimpleName();
        if ("Integer".equals(type)) {
            return intent.getIntExtra(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return intent.getBooleanExtra(key, (Boolean) defValue);
        } else if ("Byte".equals(type)) {
            return intent.getByteExtra(key, (Byte) defValue);
        } else if ("Char".equals(type)) {
            return intent.getCharExtra(key, (Character) defValue);
        } else if ("String".equals(type)) {
            return checkNull(intent.getStringExtra(key), defValue);
        } else if ("Float".equals(type)) {
            return intent.getFloatExtra(key, (Float) defValue);
        } else if ("Double".equals(type)) {
            return intent.getDoubleExtra(key, (Double) defValue);
        } else if ("Short".equals(type)) {
            return intent.getShortExtra(key, (Short) defValue);
        } else if ("Long".equals(type)) {
            return intent.getLongExtra(key, (Long) defValue);
        } else if (defValue instanceof Parcelable) {
            return checkNull(intent.getParcelableExtra(key), defValue);
        } else if ("Int[]".equals(type)) {
            return checkNull(intent.getIntArrayExtra(key), defValue);
        } else if ("boolean[]".equals(type)) {
            return checkNull(intent.getBooleanArrayExtra(key), defValue);
        } else if ("char[]".equals(type)) {
            return checkNull(intent.getCharArrayExtra(key), defValue);
        } else if ("short[]".equals(type)) {
            return checkNull(intent.getShortArrayExtra(key), defValue);
        } else if ("long[]".equals(type)) {
            return checkNull(intent.getLongArrayExtra(key), defValue);
        } else if ("double[]".equals(type)) {
            return checkNull(intent.getDoubleArrayExtra(key), defValue);
        } else if ("byte[]".equals(type)) {
            return checkNull(intent.getByteArrayExtra(key), defValue);
        } else if ("float[]".equals(type)) {
            return checkNull(intent.getFloatArrayExtra(key), defValue);
        } else if ("String[]".equals(type)) {
            return checkNull(intent.getStringArrayExtra(key), defValue);
        } else if (defValue instanceof Parcelable[]) {
            return checkNull(intent.getParcelableArrayExtra(key), defValue);
        } else if ("ArrayList".equals(type)) {
            //I don't want to get the generic type via reflection, so I use String.class as the default type.
            return checkNull(intent.getStringArrayListExtra(key), defValue);
        }
        return null;
    }

    private static Object checkNull(Object extra, Object defValue) {
        if (extra == null)
            return defValue;
        return extra;
    }

}
