package autogo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by tlh on 2016/8/2.
 */
public class SharedPreferencesUtils {
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferencesUtils instance;
    private static String TAG = "AutoGo_SharedPreferencesUtils";

    public EditorWrapper editor(){
        return new EditorWrapper(mSharedPreferences.edit());
    }
    public class EditorWrapper{
        SharedPreferences.Editor editor;
        private EditorWrapper(SharedPreferences.Editor editor) {
            this.editor = editor;
        }
        public void commit(){
            editor.commit();
        }
        public EditorWrapper saveObject(String key, Object obj) {
            String strObject = null;
            try {
                strObject = serialize(obj);
                editor.putString(key, strObject);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }
        public EditorWrapper saveData(String key, Object data) {
            String type = data.getClass().getSimpleName();
            if ("Integer".equals(type)) {
                editor.putInt(key, (Integer) data);
            } else if ("Boolean".equals(type)) {
                editor.putBoolean(key, (Boolean) data);
            } else if ("String".equals(type)) {
                editor.putString(key, (String) data);
            } else if ("Float".equals(type)) {
                editor.putFloat(key, (Float) data);
            } else if ("Long".equals(type)) {
                editor.putLong(key, (Long) data);
            }
            return this;
        }

    }
    public Object getData(String key, Object defValue) {
        return getData(key,defValue.getClass(),defValue);
    }
    public Object getData(String key,Class clz, Object defValue) {
        String type = clz.getSimpleName();
        if ("Integer".equals(type)) {
            return mSharedPreferences.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return mSharedPreferences.getBoolean(key, (Boolean) defValue);
        } else if ("String".equals(type)) {
            return mSharedPreferences.getString(key, (String) defValue);
        } else if ("Float".equals(type)) {
            return mSharedPreferences.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return mSharedPreferences.getLong(key, (Long) defValue);
        }
        return null;
    }
    public Object getObject(String key, Object def) {
        String strObj = mSharedPreferences.getString(key, "");
        if (TextUtils.isEmpty(strObj)) {
            return def;
        }
        try {
            return deSerialization(strObj);
        } catch (IOException e) {
            e.printStackTrace();
            return def;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return def;
        }
    }
    public static void setSaveTag(String tag, Context context) {
        if (TextUtils.isEmpty(tag))
            return;
        TAG = tag;
        mSharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    private SharedPreferencesUtils(Context context) {
        mSharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesUtils(context);
        }
    }

    public static SharedPreferencesUtils getInstance() throws RuntimeException {
        if (instance == null) {
            throw new RuntimeException("class should init!");
        }
        return instance;
    }

    public static SharedPreferencesUtils getInstance(Context context) throws RuntimeException {
        if (instance == null) {
            instance = new SharedPreferencesUtils(context);
        }
        return instance;
    }



    private String serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return serStr;
    }

    private Object deSerialization(String str) throws IOException,
            ClassNotFoundException {
        String redStr = java.net.URLDecoder.decode(str, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return obj;
    }



}
