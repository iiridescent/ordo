package com.base512.ordo;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.util.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    TextView mActivityLabel;

    ImageView mLogoView;

    AnimatedVectorDrawableCompat mAnimatedLogoLoaderDrawable;
    AnimatedVectorDrawableCompat mAnimatedLogoEnterDrawable;
    AnimatedVectorDrawableCompat mAnimatedLogoExitDrawable;

    /**
     * Wraps child content with parent layout containing logo and label
     * @param contentLayout
     */
    @Override
    public void setContentView(int contentLayout) {
        super.setContentView(R.layout.activity_game_base);

        setupViews();
        getLayoutInflater().inflate(contentLayout, mChildContainer);

        setLabel(getDefaultLabel());
    }

    private void setupViews() {
        mChildContainer = findViewById(R.id.gameBaseChildContainer);
        mActivityLabel = findViewById(R.id.activityLabel);
        mLogoView = findViewById(R.id.logoImage);

        // Setup animated drawables for cool animated logo
        mAnimatedLogoLoaderDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.ic_memory_animated);
        mAnimatedLogoEnterDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.ic_memory_from_thin_to_thick);
        mAnimatedLogoExitDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.ic_memory_from_thick_to_thin);

        mLogoView.setImageDrawable(mAnimatedLogoLoaderDrawable);

        // Logo doubles as home button, which is pretty slick
        mLogoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });
    }

    /**
     * Show exit confirmation dialog so that user
     * doesn't accidentally quit a game by clicking back button
     */
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

    /**
     * @return default name for current activity
     */
    protected String getDefaultLabel() {
        try {
            return getString(getPackageManager().getActivityInfo(getComponentName(), 0).labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set screen label text
     * @param label
     */
    protected void setLabel(String label) {
        mActivityLabel.setText(label);
    }

    /**
     * Make logo do cool pulsing animation to indicate that
     * something is happening in the background
     * @param isLoading
     */
    protected void setLoadingState(boolean isLoading) {
        mLogoView.setImageDrawable(mAnimatedLogoLoaderDrawable);
        if(isLoading) {
            mAnimatedLogoLoaderDrawable.start();
        } else {
            mAnimatedLogoLoaderDrawable.stop();
        }
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

    /**
     * Make sure that back key is captured so game quit confirmation dialog
     * can be shown
     */
    @Override
    public void onBackPressed() {
        exit();
    }
}
