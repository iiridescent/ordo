package com.base512.ordo;

import com.google.android.flexbox.FlexboxLayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.base512.ordo.data.source.DataModel;

public class GameStudyActivity extends AppCompatActivity {

    // Game object container layout
    private FlexboxLayout mContainerLayout;

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
        mContainerLayout = (FlexboxLayout) findViewById(R.id.study_row_container);
        mCreatorLabel = (TextView) findViewById(R.id.creatorLabel);

        mCreatorLabel.setText(DataModel.getDataModel().getUser().getId());
    }

    public void displayRows() {

        int numberOfItems = 8;

        int rowSize = 3;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int imageWidth = displayMetrics.widthPixels / rowSize;

        for(int i = 0; i < numberOfItems; i++) {


            ImageView imageView = new ImageView(this, null, R.style.StudyImage);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.creditcard, getTheme()));
            imageView.setAdjustViewBounds(true);
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.ALIGN_SELF_AUTO,
                    FlexboxLayout.LayoutParams.ALIGN_SELF_AUTO
            );

            params.width = imageWidth;
            imageView.setLayoutParams(params);

            mContainerLayout.addView(imageView);
        }
    }
}
