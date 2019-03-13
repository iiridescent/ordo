package com.base512.ordo.data.source.user;

import android.content.Context;
import android.support.annotation.NonNull;

import com.base512.ordo.data.User;
import com.base512.ordo.data.source.BaseDataSource;

public interface UserDataSource {
    User getSavedUser(Context context);

    void setSavedUser(Context context, @NonNull User user);

    void getUser(@NonNull String keyCode, @NonNull BaseDataSource.GetDataCallback<User> getUserCallback);

    void createUser(@NonNull String keyCode, @NonNull BaseDataSource.GetDataCallback<User> createUserCallback);

    void setHighScore(@NonNull String keyCode, int highScore, @NonNull Context context, @NonNull BaseDataSource.UpdateDataCallback updateUserCallback);

    void addGamesPlayed(@NonNull String keyCode, @NonNull Context context, @NonNull BaseDataSource.UpdateDataCallback updateUserCallback);
}
