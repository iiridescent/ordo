package com.base512.ordo;

import android.content.Intent;
import android.os.Bundle;
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
import com.base512.ordo.data.UserGameGuesses;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.ui.CounterView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Screen shown after timer completes on study portion of game
 *
 * User attempts to correctly guess the names of as many objects as possible.
 * Because many objects have multiple names, each guess is compared against
 * every name for any given object.
 */
public class GameTestActivity extends BaseGameActivity {

    @BindView(R.id.gameTestResultsButton)
    Button mResultsButton;
    @BindView(R.id.gameTestGuessButton)
    ImageView mGuessButton;
    @BindView(R.id.gameTestGuessField)
    EditText mGuessField;

    @BindView(R.id.gameTestGuessContainer)
    LinearLayout mGuessContainer;
    @BindView(R.id.gameTestCounterView)
    CounterView mGuessCounter;

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
        ButterKnife.bind(this);

        mResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishTest();
            }
        });
        mGuessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitGuess();
            }
        });

        mGuessField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(!mGuessField.getText().toString().isEmpty() && !mGuessField.getText().toString().equals(" ")) {
                        submitGuess();
                    }
                    return true;
                }
                return false;
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

    /**
     * Load number of objects to guess from game model
     */
    private void setupTest() {
        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game data) {
                mGuessCounter.setTotal(data.getGameObjects().length);
                mGuesses = new ArrayList<>();
            }

            @Override
            public void onDataError() {
                // TODO: Stub for failure to access data model
            }
        });
    }

    /**
     * Adds guess to local guess list and finishes test portion
     */
    private void submitGuess() {
        final String guess = mGuessField.getText().toString();

        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game data) {
                mGuesses.add(guess);

                TextView guessLabel = new TextView(GameTestActivity.this, null, R.style.GuessLabelStyle, R.style.GuessLabelStyle);
                guessLabel.setText(guess);
                CalligraphyUtils.applyFontToTextView(GameTestActivity.this, guessLabel, CalligraphyConfig.get().getFontPath());

                mGuessContainer.addView(guessLabel);
                mGuessCounter.setCount(mGuesses.size());
                mGuessField.setText("");

                // No more guesses left, finish game and show results
                if(mGuesses.size() == data.getGameObjects().length) {
                    finishTest();
                }
            }

            @Override
            public void onDataError() {

            }
        });
    }

    /**
     * Disables UI and updates game model with guesses, then sends user to next activity
     */
    private void finishTest() {
        mGuessButton.setEnabled(false);
        mGuessField.setEnabled(false);
        mResultsButton.setEnabled(false);
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
                        setLoadingState(false);
                        Toast.makeText(GameTestActivity.this, "Failed to save guesses", Toast.LENGTH_SHORT);
                    }
                });
            }

            @Override
            public void onDataError() {
                setLoadingState(false);
                Toast.makeText(GameTestActivity.this, "Failed to get game object", Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * Open results activity
     */
    private void sendToResults() {
        Intent intent = new Intent(this, GameResultsActivity.class);
        startActivity(intent);
        requestFinish();
    }
}
