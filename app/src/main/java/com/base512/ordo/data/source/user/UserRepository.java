package com.base512.ordo.data.source.user;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.base512.ordo.data.User;
import com.base512.ordo.data.source.BaseDataSource;

/**
 * Created by Thomas on 2/21/2017.
 */

final class UserRepository implements UserDataSource {
    // Key to users objects in database
    private static final String USERS = "users";
    private static final String GAMES_PLAYED = "gamesPlayed";
    private static final String HIGH_SCORE = "highScore";

    // Key to saved user object in shared preferences
    private static final String USER_ID = "user_id";
    private static final String USER_HIGH_SCORE = "user_high_score";
    private static final String USER_GAMES_PLAYED = "user_games_played";

    private static SharedPreferences sPrefs;

    private static DatabaseReference sDatabaseReference;

    UserRepository() {}

    @Override
    public User getSavedUser(Context context) {
        final SharedPreferences prefs = getSharedPreferences(context);

        String keyCode = prefs.getString(USER_ID, null);

        if (keyCode == null) {
            return null;
        }

        int highScore = prefs.getInt(USER_HIGH_SCORE, 0);
        int gamesPlayed = prefs.getInt(USER_GAMES_PLAYED, 0);

        User user = new User(keyCode, gamesPlayed, highScore);

        return user;
    }

    @Override
    public void setSavedUser(Context context, @NonNull User user) {
        final SharedPreferences prefs = getSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();

        editor.putString(USER_ID, user.getId());
        editor.putInt(USER_HIGH_SCORE, user.getHighScore());
        editor.putInt(USER_GAMES_PLAYED, user.getGamesPlayed());

        editor.apply();
    }

    @Override
    public void getUser(@NonNull final String keyCode, @NonNull final BaseDataSource.GetDataCallback<User> getUserCallback) {
        final DatabaseReference databaseReference = getDatabaseReference();

        Query query = databaseReference.child(keyCode);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getKey().equals(keyCode)) {
                    User user = new User(
                            keyCode,
                            dataSnapshot.child(HIGH_SCORE).getValue(Integer.class),
                            dataSnapshot.child(GAMES_PLAYED).getValue(Integer.class));

                    getUserCallback.onDataLoaded(user);
                } else {
                    getUserCallback.onDataError();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getUserCallback.onDataError();
            }
        });
    }

    @Override
    public void setHighScore(@NonNull String keyCode, int highScore, Context context, @NonNull BaseDataSource.UpdateDataCallback updateUserCallback) {
        DatabaseReference highScoreReference = getDatabaseReference().child(keyCode).child(HIGH_SCORE);
        highScoreReference.setValue(highScore);

        setSavedUser(context, getSavedUser(context).withHighScore(highScore));

        if(updateUserCallback != null) {
            updateUserCallback.onDataUpdated(keyCode);
        }
    }

    @Override
    public void addGamesPlayed(@NonNull final String keyCode, final Context context, @NonNull final BaseDataSource.UpdateDataCallback updateUserCallback) {
        final DatabaseReference gamesPlayedReference = getDatabaseReference().child(keyCode).child(GAMES_PLAYED);
        gamesPlayedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int gamesPlayed = dataSnapshot.getValue(Integer.class) + 1;
                gamesPlayedReference.setValue(gamesPlayed);

                setSavedUser(context, getSavedUser(context).withGamesPlayed(gamesPlayed));

                if(updateUserCallback != null) {
                    updateUserCallback.onDataUpdated(keyCode);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static DatabaseReference getDatabaseReference() {
        if (sDatabaseReference == null) {
            sDatabaseReference = FirebaseDatabase.getInstance().getReference().child(USERS);
        }

        return sDatabaseReference;
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        if (sPrefs == null) {
            sPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }

        return sPrefs;
    }
}
