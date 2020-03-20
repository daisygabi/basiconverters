package com.gra.converters.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Common operations with SharedPref. This class should have only one instance in the entire app
 */
public class SharedPrefsUtils {

    private SharedPreferences sharedPreferences;
    private static SharedPrefsUtils prefInstance = null;

    private SharedPrefsUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static void init(Context context) {
        prefInstance = new SharedPrefsUtils(context);
    }

    public static SharedPrefsUtils getInstance() {
        if (prefInstance == null) {
            throw new RuntimeException("Must run init(Application class) before an instance can be obtained");
        }
        return prefInstance;
    }

    public boolean addIntValue(String key, int value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.commit();

            return true;
        }
        return false;
    }

    public boolean addLongValue(String key, long value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(key, value);
            editor.commit();

            return true;
        }
        return false;
    }

    public int getIntValue(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public long getLongValue(String key) {
        return sharedPreferences.getLong(key, -1);
    }

    public boolean isValueInMemory(String key) {
        return key != null && sharedPreferences.contains(key);
    }
}
