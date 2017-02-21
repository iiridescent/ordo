package com.base512.ordo.data.source;


import android.content.Context;

import com.base512.ordo.data.User;

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

    public void saveUser(User user) {
        mUserRepository.setSavedUser(mContext, user);
    }

}
