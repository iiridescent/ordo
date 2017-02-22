package com.base512.ordo;

import android.app.Application;

import com.base512.ordo.data.source.DataModel;

public class OrdoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DataModel.getDataModel().setContext(getApplicationContext());
    }
}
