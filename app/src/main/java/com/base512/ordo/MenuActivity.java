package com.base512.ordo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private Button mNewGame;

    private Button mJoinGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setupViews();
    }

    private void setupViews(){
        mNewGame = (Button) findViewById(R.id.newGameButton);

        mJoinGame = (Button) findViewById(R.id.joinGameButton);

        mNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGame();
            }
        });

        mJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinGame();
            }
        });
    }

    private void newGame() {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    private void joinGame() {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }
}
