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

public class GameCreateActivity extends OrdoActivity {

    private ImageView mAddToNumber;

    private ImageView mSubtractFromNumber;

    private EditText mNumberBox;

    private Button mCreateButton;

    private ImageView mReturnToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);

        setupViews();
    }

    public void setupViews() {
        mCreateButton = (Button) findViewById(R.id.createButton);
        mNumberBox = (EditText) findViewById(R.id.numberOfPictures);
        mAddToNumber = (ImageView) findViewById(R.id.addToNumber);
        mSubtractFromNumber = (ImageView) findViewById(R.id.subtractFromNumber);
        mReturnToMenu = (ImageView) findViewById(R.id.logoImage);

        mReturnToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMenu();
            }
        });
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupGameAndGoToLobby();
            }
        });
        mSubtractFromNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractFromNumber();
            }
        });
        mAddToNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToNumber();
            }
        });
    }

    public void addToNumber() {
        String getFromNumberBox = mNumberBox.getText().toString();
        int numberBoxInteger = Integer.valueOf(getFromNumberBox);

        mNumberBox.setText(String.valueOf(numberBoxInteger + 1));
    }

    private void subtractFromNumber() {
        String getFromNumberBox = mNumberBox.getText().toString();
        int numberBoxInteger = Integer.valueOf(getFromNumberBox);

        mNumberBox.setText(String.valueOf(numberBoxInteger - 1));
    }

    private void setupGameAndGoToLobby() {
        int numberOfObjects = Integer.valueOf(mNumberBox.getText().toString());

        Game.Config gameConfig = new Game.Config(numberOfObjects);
        DataModel.getDataModel().createGame(gameConfig, new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game data) {
                goToLobby();
            }

            @Override
            public void onDataError() {

            }
        });
    }

    private void goToLobby() {
        Intent intent = new Intent(this, GameLobbyActivity.class);
        startActivity(intent);
    }

    private void returnToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

}
