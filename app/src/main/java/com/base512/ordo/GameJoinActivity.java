package com.base512.ordo;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.util.ActivityUtils;

public class GameJoinActivity extends BaseGameActivity {

    private Button mExitButton;

    private Button mJoinButton;

    private EditText mGameCodeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_join);
        setupViews();
    }

    private void setupViews() {
        mExitButton = (Button) findViewById(R.id.gameExitButton);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMenu();
            }
        });

        mJoinButton = (Button) findViewById(R.id.gameJoinButton);
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptToJoinGame();
            }
        });

        mGameCodeField = (EditText) findViewById(R.id.joinGameCodeLabel);
        mGameCodeField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(!mGameCodeField.getText().toString().isEmpty() && !mGameCodeField.getText().toString().equals(" ")) {
                        attemptToJoinGame();
                    }
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void attemptToJoinGame() {
        String id = mGameCodeField.getText().toString();
        setLoadingState(true);
        DataModel.getDataModel().getGame(id, new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game data) {
                Toast.makeText(GameJoinActivity.this, data.getCreator(), Toast.LENGTH_SHORT).show();
                DataModel.getDataModel().joinGame(data, new BaseDataSource.UpdateDataCallback() {
                    @Override
                    public void onDataUpdated(String id) {
                        setLoadingState(false);
                        goToLobby();
                    }

                    @Override
                    public void onDataError() {

                    }
                });
            }

            @Override
            public void onDataError() {
                Toast.makeText(GameJoinActivity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToLobby() {
        ActivityUtils.openActivity(this, GameLobbyActivity.class);
        requestFinish();
    }

    @Override
    protected void exit() {
        goToMenu();
    }

}
