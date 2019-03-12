package com.base512.ordo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.UserGameGuesses;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;

/**
 * Screen that shows personal score for completed game, along with high score
 */
public class GameResultsActivity extends BaseGameActivity {

    private Button mMenuButton;
    private Button mReplayButton;
    private TextView mCorrectGuessesLabel;
    private TextView mCorrectDescriptionLabel;
    private TextView mHighScoreLabel;
    private TextView mHighScoreDescriptionLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);
        setupViews();
        loadResults();
    }

    private void setupViews() {
        mMenuButton = (Button) findViewById(R.id.sendToMenu);
        mReplayButton = (Button) findViewById(R.id.replayButton);
        mCorrectGuessesLabel = (TextView) findViewById(R.id.resultsCorrectGuessesLabel);
        mCorrectDescriptionLabel = (TextView) findViewById(R.id.correctDescriptionLabel);
        mHighScoreLabel = (TextView) findViewById(R.id.resultsHighScoreLabel);
        mHighScoreDescriptionLabel = (TextView) findViewById(R.id.highScoreDescriptionLabel) ;

        mReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replayGame();
            }
        });
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMenu();
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
                                for(String name : object.getNames()) {
                                    if (name.equals(guess.toLowerCase())) {
                                        correct = true;
                                    }
                                }
                            }
                            correctGuesses = correct ? correctGuesses + 1 : correctGuesses;
                        }

                        int lastHighScore = DataModel.getDataModel().getUser().getHighScore();

                        mCorrectGuessesLabel.setText(String.valueOf(correctGuesses));
                        mHighScoreLabel.setText(String.valueOf(lastHighScore));

                        if (correctGuesses > lastHighScore) {
                            DataModel.getDataModel().setHighScore(correctGuesses, null);

                            mCorrectDescriptionLabel.setText("new high score");
                            mHighScoreDescriptionLabel.setText("last high score");
                        }

                        DataModel.getDataModel().incrementGamesPlayed(null);
                        DataModel.getDataModel().setGameState(Game.State.FINISHED, null);
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
        requestFinish();
    }

    @Override
    protected void exit() {
        goToMenu();
    }

}
