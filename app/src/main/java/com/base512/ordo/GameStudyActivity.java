package com.base512.ordo;

import com.google.android.flexbox.FlexboxLayout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.bumptech.glide.Glide;

public class GameStudyActivity extends AppCompatActivity {

    // Game object container layout
    private FlexboxLayout mContainerLayout;

    // This label shows the keyCode of the game creator
    private TextView mCreatorLabel;

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
    }

    public void setupGame() {
        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game game) {
                Toast.makeText(GameStudyActivity.this, "Number of game objects "+game.getGameObjects().length, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataError() {

            }
        });
    }

    public void displayRows() {

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
                            Glide.with(getApplicationContext()).load(integer).fitCenter().crossFade().placeholder(R.drawable.ic_memory_black_24dp).into(imageView);
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
}
