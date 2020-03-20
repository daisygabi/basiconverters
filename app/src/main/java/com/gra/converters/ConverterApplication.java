package com.gra.converters;

import android.app.Application;

import com.gra.converters.utils.SharedPrefsUtils;

public class ConverterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPrefsUtils.init(this);
    }
}
