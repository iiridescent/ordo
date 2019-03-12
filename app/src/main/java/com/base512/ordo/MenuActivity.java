package com.base512.ordo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.util.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main game options screen
 */
public class MenuActivity extends OrdoActivity {

    @BindView(R.id.newGameButton)
    Button mNewGameButton;

    @BindView(R.id.joinGameButton)
    Button mJoinGameButton;

    @BindView(R.id.uploadGameObjectButton)
    Button mGameObjectUploadButton;

    @BindView(R.id.logoImage)
    ImageView mLogoImage;

    AnimatedVectorDrawableCompat mAnimatedLogoExitDrawable;
    AnimatedVectorDrawableCompat mAnimatedLogoEnterDrawable;
    Drawable mLogoEnterDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        checkAuthentication();
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupViews() {
        mNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitToActivity(GameCreateActivity.class);
            }
        });
        mJoinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitToActivity(GameJoinActivity.class);
            }
        });
        mGameObjectUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitToActivity(GameObjectUploadActivity.class);
            }
        });

        mAnimatedLogoExitDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.ic_memory_from_thin_to_thick);
        mAnimatedLogoEnterDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.ic_memory_from_thick_to_thin);
        mLogoEnterDrawable = getDrawable(R.drawable.ic_memory_black_thin_24px);

        mLogoImage.setImageDrawable(mLogoEnterDrawable);
    }

    private void checkAuthentication() {
        if (!DataModel.getDataModel().isUserAuthenticated()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            requestFinish();
        }
    }


    private void exitToActivity(@NonNull final Class<?> activityToOpen) {
        mLogoImage.setImageDrawable(mAnimatedLogoExitDrawable);
        mAnimatedLogoExitDrawable.start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityUtils.openActivityWithTransition(MenuActivity.this, activityToOpen, null, mLogoImage, getString(R.string.logo_transition_name));
                requestFinish();
            }
        }, 200);
    }
}
