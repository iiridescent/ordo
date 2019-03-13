package com.base512.ordo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.ui.NumberConfigView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Configuration screen for creating a new game
 *
 * Provides three options:
 * <li>object count
 * <li>game duration
 * <li>object type
 */
public class GameCreateActivity extends BaseGameActivity {

    @BindView(R.id.gameCreateObjectsField)
    NumberConfigView mGameObjectsField;
    @BindView(R.id.gameCreateDurationField)
    NumberConfigView mGameDurationField;
    @BindView(R.id.gameCreateTypeSelector)
    RadioGroup mTypeSelector;
    @BindView(R.id.gameCreateButton)
    Button mCreateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);

        setupViews();
    }

    public void setupViews() {
        ButterKnife.bind(this);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupGameAndGoToLobby();
            }
        });
    }

    /**
     * Create new game with specified configuration, update data model, and go to lobby for game
     */
    private void setupGameAndGoToLobby() {
        int numberOfObjects = mGameObjectsField.getValue();
        int studyDuration = mGameDurationField.getValue();
        GameObject.Type type;
        switch (mTypeSelector.getCheckedRadioButtonId()) {
            case R.id.gameCreateTypeItems:
                type = GameObject.Type.ITEM;
                break;
            case R.id.gameCreateTypeUsLicensePlates:
                type = GameObject.Type.US_LICENSE_PLATE;
                break;
            default:
                type = GameObject.Type.ITEM;
                break;
        }

        Game.Config gameConfig = new Game.Config(numberOfObjects, studyDuration, type);
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

    /**
     * Return to menu because exiting this screen doesn't abort a running game
     */
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
