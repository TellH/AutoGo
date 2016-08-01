package autogo.internal;

import android.app.Activity;

/**
 * Created by tlh on 2016/8/1.
 */
public interface AutoAssigner {
    void setTarget(Activity activity);
    void assign();
}
