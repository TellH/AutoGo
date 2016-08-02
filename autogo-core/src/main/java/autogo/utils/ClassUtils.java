package autogo.utils;

/**
 * Created by tlh on 2016/8/2.
 */
public class ClassUtils {
    public static boolean checkNull(Object o) {
        if (o == null)
            return true;
        Class<?> type = o.getClass();
        if (type == String.class&&stringIsEmpty((CharSequence) o)) {
            return true;
        }
        return false;
    }
    public static boolean stringIsEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
