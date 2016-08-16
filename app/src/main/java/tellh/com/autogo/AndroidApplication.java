package tellh.com.autogo;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by tlh on 2016/8/16 :)
 */
public class AndroidApplication extends Application {
    private static AndroidApplication instance;

    public static AndroidApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LeakCanary.install(this);
    }
}