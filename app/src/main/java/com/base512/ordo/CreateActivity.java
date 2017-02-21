package com.base512.ordo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class CreateActivity extends AppCompatActivity {

    private ImageView mAddToNumber;

    private ImageView mSubtractFromNumber;

    private EditText mNumberBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        setupViews();
    }

    public void setupViews() {

        mNumberBox = (EditText) findViewById(R.id.numberOfPictures);

        mAddToNumber = (ImageView) findViewById(R.id.addToNumber);

        mSubtractFromNumber = (ImageView) findViewById(R.id.subtractFromNumber);

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

    private void addToNumber() {

        String getFromNumberBox = mNumberBox.getText().toString();

        int numberBoxInteger = Integer.valueOf(getFromNumberBox);

        mNumberBox.setText(String.valueOf(numberBoxInteger + 1));

    }

    private void subtractFromNumber() {
        String getFromNumberBox = mNumberBox.getText().toString();

        int numberBoxInteger = Integer.valueOf(getFromNumberBox);

        mNumberBox.setText(String.valueOf(numberBoxInteger - 1));
    }
}
