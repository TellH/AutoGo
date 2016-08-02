package autogo.internal;

import android.content.Context;

/**
 * Created by tlh on 2016/8/2.
 */
public interface AutoSharePrefsAssistant {
    void save();
    void restore();
    void setTarget(Context context);
}
