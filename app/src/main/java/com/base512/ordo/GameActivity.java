package com.base512.ordo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    private LinearLayout mContainerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getSupportActionBar().hide();
        setupViews();
        displayRows();
    }

    public void setupViews() {

        mContainerLayout = (LinearLayout) findViewById(R.id.study_row_container);

    }

    public void displayRows() {
        for(int i = 0; i <= 4; i++) {

            LayoutInflater inflater = LayoutInflater.from(this);

            LinearLayout row = (LinearLayout) inflater.inflate(R.layout.study_row, null, false);

            mContainerLayout.addView(row);

        }
    }
}
