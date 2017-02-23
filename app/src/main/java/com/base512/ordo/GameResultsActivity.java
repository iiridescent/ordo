package com.base512.ordo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameResultsActivity extends AppCompatActivity {

    private Button mMenuButton;

    private Button mReplayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);
        setupViews();
    }

    public void setupViews() {
        mMenuButton = (Button) findViewById(R.id.sendToMenu);

        mReplayButton = (Button) findViewById(R.id.replayButton);

        mReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayGame();
            }
        });

        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToMenu();
            }
        });
    }

    private void replayGame() {
        Intent intent = new Intent(this, GameCreateActivity.class);
        startActivity(intent);
    }

    private void sendToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

}
