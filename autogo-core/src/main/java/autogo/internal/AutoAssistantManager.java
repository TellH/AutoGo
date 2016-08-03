package autogo.internal;

import android.content.Context;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tlh on 2016/8/3.
 */
public final class AutoAssistantManager {
    private Map<String, AutoStorageAssistant> sharedPrefsAssistants;
    private Map<String, AutoStorageAssistant> bundleAssistants;
    public static AutoAssistantManager manager;

    public static AutoAssistantManager getInstance() {
        if (manager == null)
            manager = new AutoAssistantManager();
        return manager;
    }

    private AutoAssistantManager() {
        sharedPrefsAssistants = new HashMap<>();
        bundleAssistants = new HashMap<>();
    }

    public void addSharePrefsAssistant(String key, AutoStorageAssistant assistant) {
        sharedPrefsAssistants.put(key, assistant);
    }

    public void addBundleAssistant(String key, AutoStorageAssistant assistant) {
        bundleAssistants.put(key, assistant);
    }

    public void restore(Context context) throws RuntimeException {
        if (context == null)
            return;
        String className = context.getClass().getName();
        AutoStorageAssistant handler = sharedPrefsAssistants.get(className);
        if (handler == null) {
            throw new RuntimeException("your target field members don't have any @SharePerfs.");
        }
        handler.setTarget(context);
        handler.restore();
    }

    public void save(Context context) throws RuntimeException {
        if (context == null)
            return;
        String className = context.getClass().getName();
        AutoStorageAssistant handler = sharedPrefsAssistants.get(className);
        if (handler == null) {
            throw new RuntimeException("your target field members don't have any @SharePerfs.");
        }
        handler.setTarget(context);
        handler.save();
    }

    public void save(Context context, Bundle bundle) throws RuntimeException{
        if (context == null)
            return;
        if (bundle == null)
            save(context);
        String className = context.getClass().getName();
        AutoStorageAssistant handler = bundleAssistants.get(className);
        if (handler == null) {
            throw new RuntimeException("your target field members don't have any @Bundle.");
        }
        handler.setTarget(context);
        handler.setBundle(bundle);
        handler.save();
    }

    public void restore(Context context, Bundle bundle) throws RuntimeException {
        if (context == null)
            return;
        if (bundle == null)
            restore(context);
        String className = context.getClass().getName();
        AutoStorageAssistant handler = bundleAssistants.get(className);
        if (handler == null) {
            throw new RuntimeException("your target field members don't have any @Bundle.");
        }
        handler.setTarget(context);
        handler.setBundle(bundle);
        handler.restore();
    }
}
