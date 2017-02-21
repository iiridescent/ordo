package com.base512.ordo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private ImageView mUnlockButton;

    private EditText mLoginInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupViews();
    }

    private void setupViews() {

        mLoginInput = (EditText) findViewById(R.id.loginInput);

        mUnlockButton = (ImageView) findViewById(R.id.unlockButton);

        mUnlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage();
            }
        });
    }

    private void showMessage() {

        String getFromLoginInput = mLoginInput.getText().toString();

        String welcomeMessage = "0";

        if (getFromLoginInput.equals("anima")) {
            welcomeMessage = "Welcome, Gray";
        } else if (getFromLoginInput.equals("weirdname")) {
            welcomeMessage = "Welcome, Thomas";
        }else{
            welcomeMessage = "ERROR";
        }

        Toast.makeText(this, welcomeMessage, Toast.LENGTH_LONG).show();
    }

}
