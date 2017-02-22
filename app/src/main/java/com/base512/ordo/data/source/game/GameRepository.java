package com.base512.ordo.data.source.game;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Thomas on 2/21/2017.
 */

public class GameRepository implements GameDataSource {

    // Key to games in database
    private static final String GAMES = "games";
    private static final String OBJECTS = "objects";
    private static final String STATE = "state";
    private static final String CREATOR = "createdBy";

    // Key to current game object in shared preferences
    private static final String CURRENT_GAME_ID = "current_game_id";
    private static final String CURRENT_GAME_STATE = "current_game_state";
    private static final String CURRENT_GAME_OBJECTS = "current_game_objects";
    private static final String CURRENT_GAME_CREATOR = "current_game_creator";

    private static SharedPreferences sPrefs;

    private static DatabaseReference sDatabaseReference;

    GameRepository() {}

    @Override
    public void getCurrentGame(@NonNull Context context, @NonNull final BaseDataSource.GetDataCallback<Game> gameLoadDataCallback) {
        SharedPreferences prefs = getSharedPreferences(context);

        final String id = prefs.getString(CURRENT_GAME_ID, null);

        if (id == null) {
            gameLoadDataCallback.onDataError();
            return;
        }

        final Game.State state = Game.State.values()[prefs.getInt(CURRENT_GAME_STATE, 0)];
        Set<String> objectIds = prefs.getStringSet(CURRENT_GAME_OBJECTS, null);
        final String creator = prefs.getString(CURRENT_GAME_CREATOR, null);

        final ArrayList<Task<GameObject>> gameObjectLoadTasks = new ArrayList<>();

        for(String objectId : objectIds) {
            final TaskCompletionSource<GameObject> gameObjectLoadTaskCompletionSource = new TaskCompletionSource<>();
            Task<GameObject> gameObjectLoadTask = gameObjectLoadTaskCompletionSource.getTask();

            DataModel.getDataModel().getGameObject(objectId, new BaseDataSource.GetDataCallback<GameObject>() {
                @Override
                public void onDataLoaded(GameObject gameObject) {
                    gameObjectLoadTaskCompletionSource.setResult(gameObject);
                }

                @Override
                public void onDataError() {
                    gameObjectLoadTaskCompletionSource.setException(new NullPointerException("Couldn't find referenced object"));
                }
            });

            gameObjectLoadTasks.add(gameObjectLoadTask);
        }

        Tasks.whenAll(gameObjectLoadTasks).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                GameObject[] gameObjects = new GameObject[gameObjectLoadTasks.size()];
                for(Task<GameObject> gameObjectLoadTask : gameObjectLoadTasks) {
                    if(gameObjectLoadTask.getException() != null) {
                        gameLoadDataCallback.onDataError();
                        return;
                    }
                    gameObjectLoadTask.getResult();
                }
                Game game = new Game(id, state, gameObjects, creator);
                gameLoadDataCallback.onDataLoaded(game);
            }
        });
    }

    @Override
    public void setCurrentGame(@NonNull Game game, @NonNull Context context) {

    }

    @Override
    public void getGame(@NonNull String gameId, BaseDataSource.GetDataCallback<Game> gameDataCallback) {

    }

    @Override
    public void setGameState(@NonNull String gameId, @NonNull Game.State state, @NonNull Context context, @NonNull BaseDataSource.UpdateDataCallback updateGameCallback) {

    }

    @Override
    public void createGame(@NonNull Game.Config config, @NonNull Context context, BaseDataSource.GetDataCallback<Game> gameDataCallback) {
        SharedPreferences prefs = getSharedPreferences(context);


    }

    private static DatabaseReference getDatabaseReference() {
        if (sDatabaseReference == null) {
            sDatabaseReference = FirebaseDatabase.getInstance().getReference().child(GAMES);
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
