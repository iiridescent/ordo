package com.base512.ordo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameTestActivity extends OrdoActivity {

    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_test);
        setupViews();
    }

    public void setupViews() {
        mNextButton = (Button) findViewById(R.id.nextButton);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToResults();
            }
        });
    }

    private void sendToResults() {
        Intent intent = new Intent(this, GameResultsActivity.class);
        startActivity(intent);
    }
}
