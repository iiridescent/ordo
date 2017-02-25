package com.base512.ordo.data.source.user;


import android.content.Context;

import com.base512.ordo.data.User;
import com.base512.ordo.data.source.BaseDataSource;

public class UserModel {

    private final Context mContext;

    /** The current state of the user. */
    private User mUser;

    private UserRepository mUserRepository;

    public UserModel(Context context) {
        mContext = context;
        mUserRepository = new UserRepository();
    }

    public User getUser() {
        if (mUser == null) {
            mUser = mUserRepository.getSavedUser(mContext);
        }

        return mUser;
    }

    public void getUser(String keyCode, final BaseDataSource.GetDataCallback<User> getUserCallback) {
        if (mUser == null || !mUser.getId().equals(keyCode)) {
            mUserRepository.getUser(keyCode, new BaseDataSource.GetDataCallback<User>() {
                @Override
                public void onDataLoaded(User user) {
                    mUser = user;
                    getUserCallback.onDataLoaded(user);
                }

                @Override
                public void onDataError() {
                    getUserCallback.onDataError();
                }
            });
        } else {
            getUserCallback.onDataLoaded(mUser);
        }
    }

    public void saveUser(User user) {
        mUserRepository.setSavedUser(mContext, user);
        mUser = user;
    }

    public void setHighScore(int highScore, BaseDataSource.UpdateDataCallback updateHighScoreCallback) {
        mUserRepository.setHighScore(getUser().getId(), highScore, mContext, updateHighScoreCallback);
    }

    public void incrementGamesPlayed(BaseDataSource.UpdateDataCallback updateGamesPlayedCallback) {
        mUserRepository.addGamesPlayed(getUser().getId(), mContext, updateGamesPlayedCallback);
    }

}
