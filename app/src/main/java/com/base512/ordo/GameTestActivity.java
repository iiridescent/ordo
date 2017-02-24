package com.base512.ordo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;

public class GameTestActivity extends OrdoActivity {

    private Button mNextButton;
    private ImageView mGuessButton;
    private EditText mGuessField;
    private ImageView mReturnToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_test);
        setupViews();
    }

    public void setupViews() {
        mNextButton = (Button) findViewById(R.id.gameTestResultsButton);
        mGuessButton = (ImageView) findViewById(R.id.gameTestGuessButton);
        mGuessField = (EditText) findViewById(R.id.gameTestGuessField);
        mReturnToMenu = (ImageView) findViewById(R.id.logoImage);


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
    }

    private void guess() {
        final String guessText = mGuessField.getText().toString();

        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(Game data) {
                boolean isCorrect = false;
                for(GameObject object : data.getGameObjects()) {
                    if(object.getName().equals(guessText)) {
                        isCorrect = true;
                    }
                }
                Toast.makeText(GameTestActivity.this, String.valueOf(isCorrect), Toast.LENGTH_LONG).show();
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
