package com.base512.ordo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.text.Editable;
import android.text.TextWatcher;
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

public class GameTestActivity extends BaseGameActivity {

    private Button mNextButton;
    private ImageView mGuessButton;
    private EditText mGuessField;

    private LinearLayout mGuessContainer;
    private CounterView mGuessCounter;

    private ArrayList<String> mGuesses;

    private boolean mWasEdited = false;

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
        mGuessCounter = (CounterView) findViewById(R.id.testCounterView);
        mGuessContainer = (LinearLayout) findViewById(R.id.gameTestGuessContainer);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishTest();
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
                    if(!mGuessField.getText().toString().isEmpty() && !mGuessField.getText().toString().equals(" ")) {
                        guess();
                    }
                    handled = true;
                }
                return handled;
            }
        });

        mGuessField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty()) {
                    mGuessButton.setEnabled(false);
                    mGuessField.setImeOptions(EditorInfo.IME_ACTION_NONE);
                } else {
                    mGuessButton.setEnabled(true);
                    mGuessField.setImeOptions(EditorInfo.IME_ACTION_DONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mWasEdited){
                    mWasEdited = false;
                    return;
                }

                if(editable.toString().equals(" ")) {
                    mGuessField.setText("");
                }

                mWasEdited = true;
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
                    finishTest();
                }
            }

            @Override
            public void onDataError() {

            }
        });
    }

    private void finishTest() {
        mGuessButton.setEnabled(false);
        mGuessField.setEnabled(false);
        mNextButton.setEnabled(false);
        mGuessField.clearFocus();

        setLoadingState(true);

        final String userId = DataModel.getDataModel().getUser().getId();
        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game game) {
                DataModel.getDataModel().setGuesses(new UserGameGuesses(userId, game.getId(), mGuesses), new BaseDataSource.UpdateDataCallback() {
                    @Override
                    public void onDataUpdated(String id) {
                        setLoadingState(false);
                        sendToResults();
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

    private void sendToResults() {
        Intent intent = new Intent(this, GameResultsActivity.class);
        startActivity(intent);
        requestFinish();
    }
}
