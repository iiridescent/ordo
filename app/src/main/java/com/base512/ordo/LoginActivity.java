package com.base512.ordo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.data.User;
import com.base512.ordo.data.source.BaseDataSource;

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
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {

        String keyCode = mLoginInput.getText().toString().toLowerCase();

        DataModel.getDataModel().attemptLogin(keyCode, new BaseDataSource.GetDataCallback<User>() {
            @Override
            public void onDataLoaded(User user) {
                DataModel.getDataModel().setUser(user);
                returnToMenu();
            }

            @Override
            public void onDataError() {
                Toast.makeText(LoginActivity.this, "FAILURRRE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void returnToMenu() {
        Intent menuIntent = new Intent(LoginActivity.this, MenuActivity.class);
        startActivity(menuIntent);
        finish();
    }

}
