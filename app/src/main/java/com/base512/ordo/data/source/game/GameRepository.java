package com.base512.ordo.data.source.game;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
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

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.UserGameGuesses;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
    private static final String GUESSES = "guesses";

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
                for(int i = 0; i < gameObjectLoadTasks.size(); i++) {
                    if(gameObjectLoadTasks.get(i).getException() != null) {
                        gameLoadDataCallback.onDataError();
                        return;
                    }
                    gameObjects[i] = gameObjectLoadTasks.get(i).getResult();
                }
                Game game = new Game(id, state, gameObjects, creator);
                gameLoadDataCallback.onDataLoaded(game);
            }
        });
    }

    @Override
    public void setCurrentGame(@NonNull Game game, @NonNull Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        HashSet<String> gameObjectIds = new HashSet<>();

        for(GameObject gameObject : game.getGameObjects()) {
            gameObjectIds.add(gameObject.getId());
        }

        editor.putString(CURRENT_GAME_ID, game.getId());
        editor.putString(CURRENT_GAME_CREATOR, game.getCreator());
        editor.putInt(CURRENT_GAME_STATE, game.getState().ordinal());
        editor.putStringSet(CURRENT_GAME_OBJECTS, gameObjectIds);

        editor.apply();
    }

    @Override
    public void getGame(@NonNull String gameId, BaseDataSource.GetDataCallback<Game> gameDataCallback) {

    }

    @Override
    public void setGameState(@NonNull String gameId, @NonNull Game.State state, @NonNull Context context, @NonNull BaseDataSource.UpdateDataCallback updateGameCallback) {

    }

    @Override
    public void createGame(@NonNull final Game.Config config, @NonNull final Context context, final BaseDataSource.GetDataCallback<Game> gameDataCallback) {
    final DatabaseReference databaseReference = getDatabaseReference();

        final TaskCompletionSource<String> gameCodeTaskCompletionSource = new TaskCompletionSource<>();
        final Task<String> gameCodeTask = gameCodeTaskCompletionSource.getTask();

        generateGameCode(new BaseDataSource.GetDataCallback<String>() {
            @Override
            public void onDataLoaded(String key) {
                gameCodeTaskCompletionSource.setResult(key);
            }

            @Override
            public void onDataError() {
                gameDataCallback.onDataError();
                return;
            }
        });

        final TaskCompletionSource<Void> gameObjectsLoadTaskCompletionSource = new TaskCompletionSource<>();
        Task<Void> gameObjectsLoadTask = gameObjectsLoadTaskCompletionSource.getTask();

        final GameObject[] gameObjects = new GameObject[config.getNumberOfObjects()];

        DataModel.getDataModel().getGameObjects(new BaseDataSource.LoadDataCallback<GameObject>() {
            @Override
            public void onDataLoaded(LinkedHashMap<String, GameObject> data) {
                ArrayList<GameObject> availableGameObjects = new ArrayList<>(((LinkedHashMap<String, GameObject>) data.clone()).values());
                for(int i = 0; i < config.getNumberOfObjects(); i++) {
                    int randomIndex = (int) Math.random() * availableGameObjects.size();
                    gameObjects[i] = availableGameObjects.get(randomIndex);
                    availableGameObjects.remove(randomIndex);
                }
                gameObjectsLoadTaskCompletionSource.setResult(null);
            }

            @Override
            public void onDataError() {
                gameDataCallback.onDataError();
                return;
            }
        });

        Tasks.whenAll(gameCodeTask, gameObjectsLoadTask).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference gameReference = databaseReference.child(gameCodeTask.getResult());
                HashMap<String, Object> gameValues = new HashMap<>();

                HashMap<String, Boolean> gameObjectIds = new HashMap<>();

                for(GameObject gameObject : gameObjects) {
                    gameObjectIds.put(gameObject.getId(), true);
                }

                gameValues.put(STATE, Game.State.WAITING.ordinal());
                gameValues.put(CREATOR, DataModel.getDataModel().getUser().getId());
                gameValues.put(OBJECTS, gameObjectIds);
                gameReference.updateChildren(gameValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Game game = new Game(
                                gameCodeTask.getResult(),
                                Game.State.WAITING,
                                gameObjects,
                                DataModel.getDataModel().getUser().getId()
                        );
                        setCurrentGame(game, context);
                        gameDataCallback.onDataLoaded(game);
                    }
                });
            }
        });

    }

    @Override
    public void getGuesses(@NonNull BaseDataSource.GetDataCallback<UserGameGuesses> userGameGuessesDataCallback) {

    }

    @Override
    public void setGuesses(@NonNull final UserGameGuesses userGameGuesses, @NonNull final BaseDataSource.UpdateDataCallback updateUserGameGuessesCallback) {
        DatabaseReference guessesReference = getDatabaseReference().child(GUESSES).child(userGameGuesses.getGameId()).child(userGameGuesses.getUserId());

        HashMap<String, Object> guessValues = new HashMap<>();

        for(String guess : userGameGuesses.getGuesses()) {
            guessValues.put(guess, true);
        }

        guessesReference.updateChildren(guessValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.getException() == null) {
                    updateUserGameGuessesCallback.onDataUpdated(userGameGuesses.getUserId());
                } else {
                    updateUserGameGuessesCallback.onDataError();
                }
            }
        });

        /*Query userGameGuessesQuery = databaseReference.child(GUESSES).child(userGameGuesses.getGameId()).child(userGameGuesses.getUserId());
        userGameGuessesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                updateUserGameGuessesCallback.onDataError();
            }
        });*/
    }

    private void generateGameCode(final BaseDataSource.GetDataCallback<String> getGameCodeCallback) {
        DatabaseReference databaseReference = getDatabaseReference();

        final String key = String.valueOf((int)(Math.random()*9999));
        Query keyQuery = databaseReference.child(key);

        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    generateGameCode(getGameCodeCallback);
                } else {
                    getGameCodeCallback.onDataLoaded(key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getGameCodeCallback.onDataError();
            }
        });
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
