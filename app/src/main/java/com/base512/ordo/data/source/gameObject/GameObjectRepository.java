package com.base512.ordo.data.source.gameObject;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;

/**
 * Created by Thomas on 2/22/2017.
 */

public class GameObjectRepository implements GameObjectDataSource {

    // Key to game objects in database
    private static final String OBJECTS = "objects";
    private static final String IMAGE_NAME = "imageName";
    private static final String NAME = "name";

    private static SharedPreferences sPrefs;

    private static DatabaseReference sDatabaseReference;

    @Override
    public void getGameObject(@NonNull final String id, final BaseDataSource.GetDataCallback<GameObject> gameObjectDataCallback) {
        final DatabaseReference databaseReference = getDatabaseReference();

        Query gameObjectQuery = databaseReference.child(id);
        gameObjectQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    GameObject gameObject = new GameObject(
                            id,
                            dataSnapshot.child(NAME).getValue(String.class),
                            dataSnapshot.child(IMAGE_NAME).getValue(String.class)
                    );
                    gameObjectDataCallback.onDataLoaded(gameObject);
                } else {
                    gameObjectDataCallback.onDataError();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                gameObjectDataCallback.onDataError();
            }
        });
    }

    private static DatabaseReference getDatabaseReference() {
        if (sDatabaseReference == null) {
            sDatabaseReference = FirebaseDatabase.getInstance().getReference().child(OBJECTS);
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
