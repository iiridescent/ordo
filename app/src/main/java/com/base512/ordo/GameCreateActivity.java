package com.base512.ordo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.ui.NumberConfigView;
import com.base512.ordo.util.ActivityUtils;

public class GameCreateActivity extends BaseGameActivity {

    private NumberConfigView mGameObjectsField;
    private NumberConfigView mGameDurationField;

    private Button mCreateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);

        setupViews();
    }

    public void setupViews() {
        mCreateButton = (Button) findViewById(R.id.createButton);
        mGameObjectsField = (NumberConfigView) findViewById(R.id.gameCreateObjectsField);
        mGameDurationField = (NumberConfigView) findViewById(R.id.gameCreateDurationField);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupGameAndGoToLobby();
            }
        });
    }

    private void setupGameAndGoToLobby() {
        int numberOfObjects = mGameObjectsField.getValue();
        int studyDuration = mGameDurationField.getValue();

        Game.Config gameConfig = new Game.Config(numberOfObjects, studyDuration);
        setLoadingState(true);
        DataModel.getDataModel().createGame(gameConfig, new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game data) {
                setLoadingState(false);
                goToLobby();
            }

            @Override
            public void onDataError() {

            }
        });
    }

    @Override
    protected void exit() {
        goToMenu();
    }

    private void goToLobby() {
        Intent intent = new Intent(this, GameLobbyActivity.class);
        startActivity(intent);
        requestFinish();
    }
}
