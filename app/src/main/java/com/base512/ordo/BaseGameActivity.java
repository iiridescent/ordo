package com.base512.ordo;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AlertDialog;
import android.transition.Transition;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.util.ActivityUtils;

/**
 * Base class for all activities in game-play flow
 *
 * Wraps child content with header and home button and provides an exit confirmation dialog
 * when either the home button is clicked or on system back navigation event.
 *
 * When dialog is confirmed, BaseGameActivity handles calling the data model to finish current game.
 */
public class BaseGameActivity extends OrdoActivity {

    FrameLayout mChildContainer;

    ImageView mLogoView;

    TextView mActivityLabel;

    AnimatedVectorDrawableCompat mAnimatedLogoLoaderDrawable;
    AnimatedVectorDrawableCompat mAnimatedLogoEnterDrawable;
    AnimatedVectorDrawableCompat mAnimatedLogoExitDrawable;

    @Override
    public void setContentView(int contentLayout) {
        super.setContentView(R.layout.activity_game_base);
        setupViews();
        getLayoutInflater().inflate(contentLayout, mChildContainer);

        mActivityLabel.setText(getLabel());
    }

    private void setupViews() {
        mChildContainer = (FrameLayout) findViewById(R.id.gameBaseChildContainer);
        mActivityLabel = (TextView) findViewById(R.id.activityLabel);
        mLogoView = (ImageView) findViewById(R.id.logoImage);

        mAnimatedLogoLoaderDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.ic_memory_animated);
        mAnimatedLogoEnterDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.ic_memory_from_thin_to_thick);
        mAnimatedLogoExitDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.ic_memory_from_thick_to_thin);
        mLogoView.setImageDrawable(mAnimatedLogoLoaderDrawable);

        mLogoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });
    }

    protected void exit() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        DataModel.getDataModel().setGameState(Game.State.FINISHED, null);
                        goToMenu();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("are you sure you want to quit the game?").setPositiveButton("quit", dialogClickListener)
                .setNegativeButton("cancel", dialogClickListener).show();
    }

    protected void goToMenu() {
        mLogoView.setImageDrawable(mAnimatedLogoExitDrawable);
        mAnimatedLogoExitDrawable.start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityUtils.openActivityWithTransition(BaseGameActivity.this, MenuActivity.class, null, mLogoView, getString(R.string.logo_transition_name));
                requestFinish();
            }
        }, 200);
    }

    protected String getLabel() {
        try {
            return getString(getPackageManager().getActivityInfo(getComponentName(), 0).labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void setLoadingState(boolean isLoading) {
        mLogoView.setImageDrawable(mAnimatedLogoLoaderDrawable);
        if(isLoading) {
            mAnimatedLogoLoaderDrawable.start();
        } else {
            mAnimatedLogoLoaderDrawable.stop();
        }
    }

    protected void setLabel(String label) {
        mActivityLabel.setText(label);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        exit();
    }
}
