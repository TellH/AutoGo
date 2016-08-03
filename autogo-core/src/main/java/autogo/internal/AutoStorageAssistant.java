package autogo.internal;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by tlh on 2016/8/2.
 */
public interface AutoStorageAssistant {
    void save();
    void restore();

    void setBundle(Bundle bundle);

    void setTarget(Context context);
}
