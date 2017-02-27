package com.base512.ordo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;
import com.bumptech.glide.Glide;

import java.io.IOException;

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

    @BindView(R.id.gameObjectUploadNameField)
    EditText mNameField;

    @BindView(R.id.gameObjectUploadButton)
    TextView mUploadButton;

    private CameraIconAnimateRunnable mCameraIconAnimateRunnable;
    private CameraIconFlashRunnable mCameraIconFlashRunnable;

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

        startAnimating();
    }

    private void startAnimating() {
        mImageAnimation = AnimatedVectorDrawableCompat.create(this, R.drawable.camera_phone_anim);
        mImageAnimationReverse = AnimatedVectorDrawableCompat.create(this, R.drawable.camera_phone_anim_reverse);

        mUploadImagePromptIcon.setImageDrawable(mImageAnimation);

        mCameraIconFlashRunnable = new CameraIconFlashRunnable();
        mCameraIconAnimateRunnable = new CameraIconAnimateRunnable();
        mUploadImagePromptIcon.postDelayed(mCameraIconAnimateRunnable, 1500);
    }

    private void attemptUpload() {
        String objectName = mNameField.getText().toString();

        boolean hasError = false;

        if(mImage == null) {
            Toast.makeText(this, "image is required", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if(objectName.isEmpty() || objectName.equals(" ")) {
            Toast.makeText(this, "name is required", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if(hasError) {
            return;
        }

        GameObject gameObject = new GameObject(null, objectName, null);
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
        mNameField.setText("");
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
