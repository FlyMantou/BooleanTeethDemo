package com.booleanteeth.demo;

import android.app.Application;



/**
 * Created by huang on 2017/5/31.
 */

public class App extends Application {
    //JLog实例
    private static App app;



    public static App getApp() {
        return app;
    }

    private static int i = 20;

    @Override
    public void onCreate() {
        super.onCreate();
        this.app = this;

    }





}
