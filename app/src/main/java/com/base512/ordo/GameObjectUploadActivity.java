package com.base512.ordo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;
import com.bumptech.glide.Glide;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Thomas on 2/25/2017.
 */

public class GameObjectUploadActivity extends BaseGameActivity {

    static final int REQUEST_GALLERY = 1;

    private AnimatedVectorDrawableCompat mImageAnimation;
    private AnimatedVectorDrawableCompat mImageAnimationReverse;

    @BindView(R.id.gameObjectUploadImagePromptIcon)
    ImageView mUploadImagePromptIcon;

    @BindView(R.id.gameObjectUploadImagePromptFlash)
    ImageView mUploadImagePromptFlash;

    @BindView(R.id.gameObjectUploadImagePreview)
    ImageView mUploadImagePreview;

    @BindView(R.id.gameObjectUploadNamesContainer)
    LinearLayout mNamesContainer;

    @BindView(R.id.gameObjectUploadButton)
    TextView mUploadButton;

    @BindView(R.id.gameObjectUploadNameAddButton)
    LinearLayout mNameAddButton;

    @BindView(R.id.gameObjectUploadTypeSelector)
    RadioGroup mTypeSelector;

    private CameraIconAnimateRunnable mCameraIconAnimateRunnable;
    private CameraIconFlashRunnable mCameraIconFlashRunnable;

    private GameObject.Type objectType;

    private boolean isAnimationReversed = false;
    private boolean mInFlash = false;

    private Uri mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_object_upload);
        ButterKnife.bind(this);
        setupViews();
    }

    private void setupViews() {
        mUploadImagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptUpload();
            }
        });

        mNameAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNameField();
            }
        });

        objectType = GameObject.Type.ITEM;

        mTypeSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.gameObjectUploadTypeItem:
                        objectType = GameObject.Type.ITEM;
                        break;
                    case R.id.gameObjectUploadTypeLicensePlate:
                        objectType = GameObject.Type.US_LICENSE_PLATE;
                        break;
                }
            }
        });

        startAnimating();

        addNameField();
    }

    private void addNameField() {
        EditText editText = new EditText(this, null, R.style.NameFieldStyle, R.style.NameFieldStyle);
        CalligraphyUtils.applyFontToTextView(this, editText, CalligraphyConfig.get().getFontPath());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(params);

        mNamesContainer.addView(editText);
    }

    private void startAnimating() {
        mImageAnimation = AnimatedVectorDrawableCompat.create(this, R.drawable.camera_phone_anim);
        mImageAnimationReverse = AnimatedVectorDrawableCompat.create(this, R.drawable.camera_phone_anim_reverse);

        mUploadImagePromptIcon.setImageDrawable(mImageAnimation);

        mCameraIconFlashRunnable = new CameraIconFlashRunnable();
        mCameraIconAnimateRunnable = new CameraIconAnimateRunnable();
        mUploadImagePromptIcon.postDelayed(mCameraIconAnimateRunnable, 1500);
    }

    // FIXME: 3/21/2017
    // This needs to use multiple objects as input

    private void attemptUpload() {
        ArrayList<String> objectNames = new ArrayList<>();

        // FIXME: 3/21/2017 This is a total stub
        //objectNames.add(mNameField.getText().toString());

        for(int i = 0; i < mNamesContainer.getChildCount(); i++) {
            EditText childField = (EditText) mNamesContainer.getChildAt(i);
            objectNames.add(childField.getText().toString());
        }

        boolean hasError = false;

        if(mImage == null) {
            Toast.makeText(this, "image is required", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        for (String objectName : objectNames) {
            if (objectName.isEmpty() || objectName.equals(" ")) {
                Toast.makeText(this, "one or more names are missing", Toast.LENGTH_SHORT).show();
                hasError = true;
            }
        }

        if(hasError) {
            return;
        }

        GameObject gameObject = new GameObject(null, objectNames, null, objectType);
        setLoadingState(true);
        DataModel.getDataModel().addGameObject(gameObject, mImage.toString(), new BaseDataSource.UpdateDataCallback() {
            @Override
            public void onDataUpdated(String id) {
                setLoadingState(false);
                clearUi();
                Toast.makeText(GameObjectUploadActivity.this, "uploaded object", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataError() {
                setLoadingState(false);
                Toast.makeText(GameObjectUploadActivity.this, "failed to upload object", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearUi() {
        mImage = null;
        mNamesContainer.removeAllViews();
        addNameField();
        mUploadImagePreview.setImageDrawable(null);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            Uri fullPhotoUri = data.getData();

            Glide.with(this).load(fullPhotoUri).into(mUploadImagePreview);
            mImage = fullPhotoUri;
        }
    }

    @Override
    protected void exit() {
        goToMenu();
    }

    class CameraIconAnimateRunnable implements Runnable {
        @Override
        public void run() {
            if(isAnimationReversed) {
                mUploadImagePromptIcon.setImageDrawable(mImageAnimationReverse);
                mImageAnimationReverse.start();
            } else {
                mUploadImagePromptIcon.setImageDrawable(mImageAnimation);
                mImageAnimation.start();
            }

            isAnimationReversed = !isAnimationReversed;

            mUploadImagePromptIcon.postDelayed(mCameraIconAnimateRunnable, 1700);
            mUploadImagePromptFlash.postDelayed(mCameraIconFlashRunnable, 1500);
        }
    }

    class CameraIconFlashRunnable implements Runnable {

        @Override
        public void run() {
            if(!mInFlash) {
                mUploadImagePromptIcon.setVisibility(View.INVISIBLE);
                mUploadImagePromptIcon.setBackgroundTintList(getColorStateList(android.R.color.transparent));

                mUploadImagePromptFlash.setVisibility(View.VISIBLE);
                mUploadImagePromptFlash.postDelayed(mCameraIconFlashRunnable, 200);

                if(isAnimationReversed) {
                    mUploadImagePromptFlash.setImageDrawable(mImageAnimation);
                } else {
                    mUploadImagePromptFlash.setImageDrawable(mImageAnimationReverse);
                }

                mInFlash = true;
            } else {
                mUploadImagePromptIcon.setVisibility(View.VISIBLE);
                mUploadImagePromptIcon.setBackgroundTintList(null);

                mUploadImagePromptFlash.setVisibility(View.INVISIBLE);

                mInFlash = false;
            }
        }
    }
}
