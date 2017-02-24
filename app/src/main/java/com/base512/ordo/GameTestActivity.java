package com.base512.ordo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.UserGameGuesses;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.ui.CounterView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

import java.util.ArrayList;

public class GameTestActivity extends OrdoActivity {

    private Button mNextButton;
    private ImageView mGuessButton;
    private EditText mGuessField;
    private ImageView mReturnToMenu;

    private LinearLayout mGuessContainer;
    private CounterView mGuessCounter;

    private ArrayList<String> mGuesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_test);
        setupViews();
        setupTest();
    }

    private void setupViews() {
        mNextButton = (Button) findViewById(R.id.gameTestResultsButton);
        mGuessButton = (ImageView) findViewById(R.id.gameTestGuessButton);
        mGuessField = (EditText) findViewById(R.id.gameTestGuessField);
        mReturnToMenu = (ImageView) findViewById(R.id.logoImage);
        mGuessCounter = (CounterView) findViewById(R.id.testCounterView);
        mGuessContainer = (LinearLayout) findViewById(R.id.gameTestGuessContainer);

        mReturnToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMenu();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToResults();
            }
        });
        mGuessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guess();
            }
        });

        mGuessField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    guess();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void setupTest() {
        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game data) {
                mGuessCounter.setDenominator(data.getGameObjects().length);
                mGuesses = new ArrayList<>();
            }

            @Override
            public void onDataError() {

            }
        });
    }

    private void guess() {
        final String guess = mGuessField.getText().toString();

        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game data) {
                boolean isCorrect = false;
                for(GameObject object : data.getGameObjects()) {
                    if(object.getName().equals(guess)) {
                        isCorrect = true;
                    }
                }
                /*Toast.makeText(GameTestActivity.this, String.valueOf(isCorrect), Toast.LENGTH_LONG).show();*/

                mGuesses.add(guess);

                TextView guessLabel = new TextView(GameTestActivity.this, null, R.style.GuessLabelStyle, R.style.GuessLabelStyle);
                guessLabel.setText(guess);
                CalligraphyUtils.applyFontToTextView(GameTestActivity.this, guessLabel, CalligraphyConfig.get().getFontPath());

                mGuessContainer.addView(guessLabel);
                mGuessCounter.setNumerator(mGuesses.size());
                mGuessField.setText("");

                if(mGuesses.size() == data.getGameObjects().length) {
                    mGuessButton.setEnabled(false);
                    mGuessField.setEnabled(false);
                    mGuessField.clearFocus();

                    String userId = DataModel.getDataModel().getUser().getId();
                    String gameId = data.getId();

                    DataModel.getDataModel().setGuesses(new UserGameGuesses(userId, gameId, mGuesses), new BaseDataSource.UpdateDataCallback() {
                        @Override
                        public void onDataUpdated(String id) {
                            sendToResults();
                        }

                        @Override
                        public void onDataError() {

                        }
                    });
                }
            }

            @Override
            public void onDataError() {

            }
        });
    }

    private void sendToResults() {
        Intent intent = new Intent(this, GameResultsActivity.class);
        startActivity(intent);
    }

    private void returnToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
