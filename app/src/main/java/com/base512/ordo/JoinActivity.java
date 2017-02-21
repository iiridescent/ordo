package com.base512.ordo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JoinActivity extends AppCompatActivity {

    private Button mExitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        setupViews();
    }

    private void setupViews() {
        mExitButton = (Button) findViewById(R.id.exitButton);

        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToMenu();
            }
        });
    }

    private void returnToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

}
