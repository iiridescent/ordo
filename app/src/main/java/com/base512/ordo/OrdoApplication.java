package com.base512.ordo;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.base512.ordo.data.source.DataModel;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Base point for Ordo application
 */
public class OrdoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DataModel.getDataModel().setContext(getApplicationContext());

        // Initialize font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
