package com.base512.ordo;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.ui.EasingType;
import com.base512.ordo.ui.SineInterpolator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameLobbyActivity extends OrdoActivity {

    @BindView(R.id.gameStartButton)
    Button mStartButton;

    @BindView(R.id.gameCodeLabel)
    TextView mGameCodeLabel;

    @BindView(R.id.gameCodeContainer)
    LinearLayout mGameCodeContainer;

    private ImageView mReturnToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);
        ButterKnife.bind(this);

        setupViews();
        setupAnimation();
    }

    private void setupViews() {
        mReturnToMenu = (ImageView) findViewById(R.id.logoImage);

        mReturnToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMenu();
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game data) {
                mGameCodeLabel.setText(data.getId());
            }

            @Override
            public void onDataError() {

            }
        });
    }

    private void setupAnimation() {
        int accentColor = ContextCompat.getColor(this, R.color.colorAccent);

        ObjectAnimator[] objectAnimators = new ObjectAnimator[2];

        objectAnimators[0] = ObjectAnimator.ofInt(mGameCodeLabel, "textColor", Color.BLACK, accentColor);

        objectAnimators[1] = ObjectAnimator.ofInt(mGameCodeContainer, "backgroundTint", Color.BLACK, accentColor);
        objectAnimators[1].addUpdateListener(new ObjectAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mGameCodeContainer.setBackgroundTintList(ColorStateList.valueOf(animatedValue));
            }
        });

        for(ObjectAnimator animator : objectAnimators) {
            animator.setEvaluator(new ArgbEvaluator());
            animator.setRepeatMode(ObjectAnimator.REVERSE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new SineInterpolator(EasingType.Type.INOUT));
            animator.setDuration(1500);
        }

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playTogether(objectAnimators);
        animationSet.start();
    }

    private void startGame() {
        DataModel.getDataModel().setGameState(Game.State.STUDY, new BaseDataSource.UpdateDataCallback() {
            @Override
            public void onDataUpdated(String id) {
                Intent intent = new Intent(GameLobbyActivity.this, GameStudyActivity.class);
                startActivity(intent);
            }

            @Override
            public void onDataError() {
                Intent intent = new Intent(GameLobbyActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }

    private void returnToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

}
