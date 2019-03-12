package com.base512.ordo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Thomas on 2/23/2017.
 */

/**
 * Base class for all Ordo activities
 *
 * Modifies default animation transition (there could be a better way to do this) and
 * wraps Calligraphy
 */

public class OrdoActivity extends AppCompatActivity {

    private boolean mShouldFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mShouldFinish) {
            finish();
        }
    }

    protected void requestFinish() {
        mShouldFinish = true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
