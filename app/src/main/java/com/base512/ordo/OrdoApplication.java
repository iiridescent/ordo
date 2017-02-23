package com.base512.ordo;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.base512.ordo.data.source.DataModel;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class OrdoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DataModel.getDataModel().setContext(getApplicationContext());
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
