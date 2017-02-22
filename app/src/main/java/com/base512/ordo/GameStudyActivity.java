package com.base512.ordo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameStudyActivity extends AppCompatActivity {

    // Game object container layout
    private LinearLayout mContainerLayout;

    // This label shows the keyCode of the game creator
    private TextView mCreatorLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setupViews();
        displayRows();
    }

    public void setupViews() {
        mContainerLayout = (LinearLayout) findViewById(R.id.study_row_container);
        mCreatorLabel = (TextView) findViewById(R.id.creatorLabel);
    }

    public void displayRows() {

        int numberOfItems = 30;

        int rowSize = 3;

        for(int i = 0; i < numberOfItems / rowSize; i++) {

            LayoutInflater inflater = LayoutInflater.from(this);

            LinearLayout row = (LinearLayout) inflater.inflate(R.layout.study_row, null, false);

            for(int j = 0; j < 5; j++) {
                ImageView imageView = new ImageView(this, null, R.style.StudyImage);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.creditcard, getTheme()));
                imageView.setAdjustViewBounds(true);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.weight = 1;

                imageView.setLayoutParams(params);

                row.addView(imageView);
            }

            mContainerLayout.addView(row);
        }
    }
}
