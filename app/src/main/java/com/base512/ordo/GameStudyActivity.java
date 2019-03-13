package com.base512.ordo;

import com.base512.ordo.ui.CounterView;
import com.google.android.flexbox.FlexboxLayout;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.util.ActivityUtils;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Screen with grid of objects to memorize before timer runs out
 */
public class GameStudyActivity extends BaseGameActivity {

    // Game object container layout
    @BindView(R.id.gameFlexbox)
    FlexboxLayout mContainerLayout;

    @BindView(R.id.gameTimerView)
    CounterView mTimerView;

    private final TimeUpdateRunnable mTimeUpdateRunnable = new TimeUpdateRunnable();

    // Milliseconds between each time elapsed event
    final static int TICK_DELAY_MS = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_study);
        ButterKnife.bind(this);
        setupGame();
        displayRows();
    }

    /**
     * Set up timer view and update label ({@link BaseGameActivity#setLabel(String)}) if
     * game is multi-player and current user is not owner
     */
    public void setupGame() {
        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game game) {
                if(!DataModel.getDataModel().getUser().getId().equals(game.getCreator())) {
                    setLabel("game by "+game.getCreator());
                }
                updateTimingRunnable(mTimeUpdateRunnable, 0);
            }

            @Override
            public void onDataError() {

            }
        });
    }

    /**
     * Load images from URLs (or cache, using Glide) into Flexbox view
     */
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
                int imageDivisor = 16;
                int imageWidth = displayMetrics.widthPixels / rowSize;
                int imageRealWidth = (imageWidth / imageDivisor) * (imageDivisor-2);
                int imagePadding = (imageWidth / imageDivisor);

                for(int i = 0; i < gameObjects.length; i++) {

                    final ImageView imageView = new ImageView(GameStudyActivity.this, null, R.style.StudyImage);

                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setAdjustViewBounds(false);
                    imageView.setImageDrawable(loadingAnimation);

                    Glide.with(getApplicationContext()).load(gameObjects[i].getImageUrl()).fitCenter().crossFade().placeholder(R.drawable.ic_memory_animated).into(imageView);
                    imageView.setAdjustViewBounds(true);
                    FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.ALIGN_SELF_AUTO,
                            FlexboxLayout.LayoutParams.ALIGN_SELF_AUTO
                    );

                    params.width = imageRealWidth;
                    params.height = imageRealWidth;
                    params.setMargins(imagePadding, imagePadding, imagePadding, imagePadding);
                    imageView.setLayoutParams(params);

                    mContainerLayout.addView(imageView);
                }
            }

            @Override
            public void onDataError() {

            }
        });
    }

    /**
     * Update timer view while study time remains,
     * then stop timer runnable and open test screen.
     * @param startTime epoch timestamp at beginning of game
     */
    private void updateTime(long startTime) {
        long timeElapsed = System.currentTimeMillis() - startTime;
        final int timeElapsedSeconds = (int) timeElapsed / 1000;

        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game data) {
                if(timeElapsedSeconds >= data.getStudyDuration()) {
                    // Time's up, go to test portion of game
                    removeTimingRunnable(mTimeUpdateRunnable);
                    goToTest();
                }
            }

            @Override
            public void onDataError() {

            }
        });

        mTimerView.setTotal(timeElapsedSeconds);
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
                requestFinish();
            }

            @Override
            public void onDataError() {

            }
        });
    }

    private final class TimeUpdateRunnable implements Runnable {
        @Override
        public void run() {
            // Time at beginning of update
            final long startTime = SystemClock.elapsedRealtime();

            DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
                @Override
                public void onDataLoaded(Game data) {
                    if (data.getState() == Game.State.STUDY) {
                        // Update timer view
                        updateTime(data.getStartTime());

                        // Time at end of update
                        final long endTime = SystemClock.elapsedRealtime();
                        // Correct for difference between update start and end
                        final long delay = Math.max(0, startTime + TICK_DELAY_MS - endTime);

                        // Perform another tick after {delay} milliseconds
                        updateTimingRunnable(TimeUpdateRunnable.this, delay);
                    }
                }

                @Override
                public void onDataError() {
                    // TODO: Handle game model unable to load during time update runnable
                }
            });
        }
    }
}
