package com.base512.ordo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameLobbyActivity extends AppCompatActivity {

    private Button mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        setupViews();
    }

    private void setupViews() {
        mStartButton = (Button) findViewById(R.id.startGameButton);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToGame();
            }
        });
    }

    private void sendToGame() {
        Intent intent = new Intent(this, GameStudyActivity.class);
        startActivity(intent);
    }

}
