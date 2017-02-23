package com.base512.ordo;

import com.base512.ordo.ui.CounterView;
import com.google.android.flexbox.FlexboxLayout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.util.ActivityUtils;
import com.bumptech.glide.Glide;

import java.sql.Time;

public class GameStudyActivity extends OrdoActivity {

    // Game object container layout
    private FlexboxLayout mContainerLayout;

    // This label shows the keyCode of the game creator
    private TextView mCreatorLabel;

    private CounterView mTimerView;

    private final TimeUpdateRunnable mTimeUpdateRunnable = new TimeUpdateRunnable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setupViews();
        setupGame();
        displayRows();
    }

    public void setupViews() {
        mContainerLayout = (FlexboxLayout) findViewById(R.id.study_row_container);
        mCreatorLabel = (TextView) findViewById(R.id.creatorLabel);

        mCreatorLabel.setText(DataModel.getDataModel().getUser().getId());

        mTimerView = (CounterView) findViewById(R.id.gameTimerView);
    }

    public void setupGame() {
        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game game) {
                mCreatorLabel.setText(game.getCreator());
                updateTimingRunnable(mTimeUpdateRunnable, 0);
            }

            @Override
            public void onDataError() {

            }
        });
    }

    public void displayRows() {
        final AnimatedVectorDrawableCompat loadingAnimation = AnimatedVectorDrawableCompat.create(GameStudyActivity.this, R.drawable.ic_memory_animated);
        loadingAnimation.start();

        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game data) {
                GameObject[] gameObjects = data.getGameObjects();

                int rowSize = 3;

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int imageWidth = displayMetrics.widthPixels / rowSize;

                for(int i = 0; i < gameObjects.length; i++) {

                    final ImageView imageView = new ImageView(GameStudyActivity.this, null, R.style.StudyImage);
                    DataModel.getDataModel().getGameObjectDrawable(gameObjects[i].getImageName(), new BaseDataSource.GetDataCallback<Integer>() {
                        @Override
                        public void onDataLoaded(Integer integer) {

                            imageView.setScaleType(ImageView.ScaleType.CENTER);
                            imageView.setAdjustViewBounds(false);
                            imageView.setImageDrawable(loadingAnimation);

                            Glide.with(getApplicationContext()).load(integer).fitCenter().crossFade().placeholder(R.drawable.ic_memory_animated).into(imageView);
                        }

                        @Override
                        public void onDataError() {
                            Log.d(GameStudyActivity.class.getSimpleName(), "Failed to load image");
                        }
                    });
                    imageView.setAdjustViewBounds(true);
                    FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.ALIGN_SELF_AUTO,
                            FlexboxLayout.LayoutParams.ALIGN_SELF_AUTO
                    );

                    params.width = imageWidth;
                    imageView.setLayoutParams(params);

                    mContainerLayout.addView(imageView);
                }
            }

            @Override
            public void onDataError() {

            }
        });
    }

    private void updateTime(long startTime) {
        long timeElapsed = System.currentTimeMillis() - startTime;
        int timeElapsedSeconds = (int) timeElapsed / 1000;

        if(timeElapsedSeconds >= 5) {
            removeTimingRunnable(mTimeUpdateRunnable);
            goToTest();
        }

        mTimerView.setDenominator(timeElapsedSeconds);
    }

    private void updateTimingRunnable(TimeUpdateRunnable timeUpdateRunnable, long delay) {
        if(delay > 0) {
            mTimerView.postDelayed(timeUpdateRunnable, delay);
        } else {
            removeTimingRunnable(timeUpdateRunnable);
            mTimerView.post(timeUpdateRunnable);
        }
    }

    private void removeTimingRunnable(TimeUpdateRunnable timeUpdateRunnable) {
        mTimerView.removeCallbacks(timeUpdateRunnable);
    }

    private void goToTest() {
        DataModel.getDataModel().setGameState(Game.State.TEST, new BaseDataSource.UpdateDataCallback() {
            @Override
            public void onDataUpdated(String id) {
                ActivityUtils.openActivity(GameStudyActivity.this, GameTestActivity.class);
            }

            @Override
            public void onDataError() {

            }
        });
    }

    private final class TimeUpdateRunnable implements Runnable {
        @Override
        public void run() {
            final long startTime = SystemClock.elapsedRealtime();

            DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
                @Override
                public void onDataLoaded(Game data) {
                    if (data.getState() == Game.State.STUDY) {
                        updateTime(data.getStartTime());

                        final int period = 25;

                        final long endTime = SystemClock.elapsedRealtime();
                        final long delay = Math.max(0, startTime + period - endTime);

                        updateTimingRunnable(TimeUpdateRunnable.this, delay);
                    }
                }

                @Override
                public void onDataError() {
                    // TODO Idk what to do here
                }
            });
        }
    }

}
