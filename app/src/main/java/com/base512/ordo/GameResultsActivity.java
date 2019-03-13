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
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Screen that shows personal score for completed game, along with high score
 */
public class GameResultsActivity extends BaseGameActivity {

    @BindView(R.id.resultsMainMenuButton)
    Button mMenuButton;
    @BindView(R.id.resultsRestartButton)
    Button mReplayButton;
    @BindView(R.id.resultsCorrectGuessesLabel)
    TextView mCorrectGuessesLabel;
    @BindView(R.id.resultsCorrectDescriptionLabel)
    TextView mCorrectDescriptionLabel;
    @BindView(R.id.resultsHighScoreLabel)
    TextView mHighScoreLabel;
    @BindView(R.id.resultsHighScoreDescriptionLabel)
    TextView mHighScoreDescriptionLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);
        setupViews();
        loadResults();
    }

    private void setupViews() {
        ButterKnife.bind(this);

        mReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartGame();
            }
        });
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMenu();
            }
        });
    }

    /**
     * Compare guesses to object names and generate score
     */
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

                            // TODO: Localize these
                            mCorrectDescriptionLabel.setText("new high score");
                            mHighScoreDescriptionLabel.setText("last high score");
                        }

                        DataModel.getDataModel().incrementGamesPlayed(null);
                        DataModel.getDataModel().setGameState(Game.State.FINISHED, null);
                    }

                    @Override
                    public void onDataError() {
                        // TODO: Handle data error
                    }
                });
            }

            @Override
            public void onDataError() {
                // TODO: Handle data error
            }
        });
    }

    /**
     * Shortcut to go back to game create activity
     */
    private void restartGame() {
        Intent intent = new Intent(this, GameCreateActivity.class);
        startActivity(intent);
        requestFinish();
    }

    /**
     * Returns to menu because exiting this screen doesn't abort a running game
     */
    @Override
    protected void exit() {
        goToMenu();
    }

}
