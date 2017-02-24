package com.base512.ordo;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.UserGameGuesses;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class GameResultsActivity extends OrdoActivity {

    private Button mMenuButton;
    private Button mReplayButton;
    private ImageView mReturnToMenu;
    private TextView mCorrectGuessesLabel;
    private TextView mCorrectDescriptionLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);
        setupViews();
        loadResults();
    }

    private void setupViews() {
        mMenuButton = (Button) findViewById(R.id.sendToMenu);
        mReturnToMenu = (ImageView) findViewById(R.id.logoImage);
        mReplayButton = (Button) findViewById(R.id.replayButton);
        mCorrectGuessesLabel = (TextView) findViewById(R.id.resultsCorrectGuessesLabel);
        mCorrectDescriptionLabel = (TextView) findViewById(R.id.correctDescriptionLabel);

        mReturnToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMenu();
            }
        });
        mReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayGame();
            }
        });
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToMenu();
            }
        });
    }

    private void loadResults() {
        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(final Game game) {
                DataModel.getDataModel().getGuesses(new BaseDataSource.GetDataCallback<UserGameGuesses>() {
                    @Override
                    public void onDataLoaded(UserGameGuesses guesses) {
                        int correctGuesses = 0;
                        for(String guess : guesses.getGuesses()) {
                            boolean correct = false;
                            for(GameObject object : game.getGameObjects()) {
                                if(object.getName().equals(guess.toLowerCase())) {
                                    correct = true;
                                }
                            }
                            correctGuesses = correct ? correctGuesses + 1 : correctGuesses;
                        }
                        mCorrectGuessesLabel.setText(String.valueOf(correctGuesses));

                        if (correctGuesses > DataModel.getDataModel().getUser().getHighScore()) {
                            DataModel.getDataModel().setHighScore(correctGuesses);

                            mCorrectDescriptionLabel.setText("new high score");
                        } else {

                        }
                    }

                    @Override
                    public void onDataError() {

                    }
                });
            }

            @Override
            public void onDataError() {

            }
        });
    }

    private void replayGame() {
        Intent intent = new Intent(this, GameCreateActivity.class);
        startActivity(intent);
    }

    private void sendToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

}
