package com.base512.ordo.data.source;

import android.content.Context;
import android.support.annotation.NonNull;

import com.base512.ordo.data.User;

public interface UserDataSource {
    User getSavedUser(Context context);

    void setSavedUser(Context context, @NonNull User user);

    void getUser(@NonNull String keyCode, @NonNull BaseDataSource.GetDataCallback<User> getUserCallback);

    void setHighScore(@NonNull String keyCode, int highScore, @NonNull Context context, @NonNull BaseDataSource.UpdateDataCallback updateUserCallback);

    void addGamesPlayed(@NonNull String keyCode, int additionalGamesPlayed, @NonNull Context context, @NonNull BaseDataSource.UpdateDataCallback updateUserCallback);
}
