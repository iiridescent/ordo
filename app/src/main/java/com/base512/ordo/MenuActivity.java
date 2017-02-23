package com.base512.ordo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.base512.ordo.data.source.DataModel;
import com.base512.ordo.util.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuActivity extends OrdoActivity {

    @BindView(R.id.newGameButton)
    Button mNewGameButton;

    @BindView(R.id.joinGameButton)
    Button mJoinGameButton;

    @BindView(R.id.logoImage)
    ImageView mLogoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        checkAuthentication();
        setupViews();
    }

    private void setupViews(){
        mNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGame();
            }
        });
        mJoinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinGame();
            }
        });
    }

    private void checkAuthentication() {
        if (!DataModel.getDataModel().isUserAuthenticated()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void newGame() {
        ActivityUtils.openActivityWithTransition(this, GameCreateActivity.class, null, mLogoImage, getString(R.string.logo_transition_name));
    }

    private void joinGame() {
        Intent intent = new Intent(this, GameJoinActivity.class);
        startActivity(intent);
    }
}
