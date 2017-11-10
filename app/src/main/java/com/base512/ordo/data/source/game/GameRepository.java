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
import com.base512.ordo.data.User;
import com.base512.ordo.data.UserGameGuesses;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.DataModel;

import java.lang.reflect.Array;
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
    private static final String START_TIME = "startTime";
    private static final String PLAYERS = "players";
    private static final String GUESSES = "guesses";
    private static final String STUDY_DURATION = "studyDuration";

    // Key to current game object in shared preferences
    private static final String CURRENT_GAME_ID = "current_game_id";
    private static final String CURRENT_GAME_STATE = "current_game_state";
    private static final String CURRENT_GAME_OBJECTS = "current_game_objects";
    private static final String CURRENT_GAME_CREATOR = "current_game_creator";
    private static final String CURRENT_GAME_START_TIME = "current_game_start_time";
    private static final String CURRENT_GAME_STUDY_DURATION = "current_game_study_duration";

    private static SharedPreferences sPrefs;

    private static DatabaseReference sDatabaseReference;

    private OnGameStateChangeListener mOnGameStateChangeListener;

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
        final long startTime = prefs.getLong(CURRENT_GAME_START_TIME, Long.MIN_VALUE);
        final int studyDuration = prefs.getInt(CURRENT_GAME_STUDY_DURATION, 30);

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
                Game game = new Game(id, state, gameObjects, creator, startTime, studyDuration);
                gameLoadDataCallback.onDataLoaded(game);
            }
        });
    }

    @Override
    public void setCurrentGame(@NonNull final Game game, @NonNull final Context context, final BaseDataSource.UpdateDataCallback updateGameCallback) {
        SharedPreferences prefs = getSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        HashSet<String> gameObjectIds = new HashSet<>();

        for(GameObject gameObject : game.getGameObjects()) {
            gameObjectIds.add(gameObject.getId());
        }

        if(game.getState() != Game.State.FINISHED) {
            editor.putString(CURRENT_GAME_ID, game.getId());
            editor.putString(CURRENT_GAME_CREATOR, game.getCreator());
            editor.putInt(CURRENT_GAME_STATE, game.getState().ordinal());
            editor.putInt(CURRENT_GAME_STUDY_DURATION, game.getStudyDuration());
            editor.putStringSet(CURRENT_GAME_OBJECTS, gameObjectIds);
            editor.putLong(CURRENT_GAME_START_TIME, game.getStartTime());
        } else {
            editor.putString(CURRENT_GAME_ID, null);
        }

        editor.apply();

        final DatabaseReference gameReference = getDatabaseReference().child(game.getId());

        if (game.getState() == Game.State.FINISHED) {
            gameReference.removeValue();
            final DatabaseReference guessesReference = getDatabaseReference().child(GUESSES).child(game.getId());
            guessesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        guessesReference.removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if(updateGameCallback != null) {
                updateGameCallback.onDataUpdated(game.getId());
            }
            return;
        }

        if(game.getCreator().equals(DataModel.getDataModel().getUser().getId())) {

            HashMap<String, Object> gameObjectValues = new HashMap<>();

            for (GameObject gameObject : game.getGameObjects()) {
                gameObjectValues.put(gameObject.getId(), true);
            }

            HashMap<String, Object> gameValues = new HashMap<>();

            gameValues.put(STATE, game.getState().ordinal());
            gameValues.put(CREATOR, DataModel.getDataModel().getUser().getId());
            gameValues.put(OBJECTS, gameObjectValues);
            gameValues.put(START_TIME, game.getStartTime());
            gameReference.updateChildren(gameValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (updateGameCallback != null) {
                        updateGameCallback.onDataUpdated(game.getId());
                    }
                }
            });
        } else {
            gameReference.child(PLAYERS).child(DataModel.getDataModel().getUser().getId()).setValue(true);

            final ValueEventListener stateEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        gameReference.child(STATE).removeEventListener(this);
                        return;
                    }
                    getCurrentGame(context, new BaseDataSource.GetDataCallback<Game>() {
                        @Override
                        public void onDataLoaded(Game data) {

                            Game.State state = Game.State.values()[dataSnapshot.getValue(Integer.class)];
                            if(data.getState() != state) {
                                mOnGameStateChangeListener.onStateChanged(state);
                            }
                        }

                        @Override
                        public void onDataError() {

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            gameReference.child(STATE).addValueEventListener(stateEventListener);
            if(updateGameCallback != null) {
                updateGameCallback.onDataUpdated(game.getId());
            }
        }
    }

    @Override
    public void getGame(@NonNull final String gameId, final BaseDataSource.GetDataCallback<Game> gameLoadDataCallback) {
        final DatabaseReference gameReference = getDatabaseReference().child(gameId);

/*        TaskCompletionSource<Game> gameLoadTaskCompletionSource = new TaskCompletionSource<>();
        Task<Game> gameLoadTask = gameLoadTaskCompletionSource.getTask();*/

        gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists() || !dataSnapshot.child(OBJECTS).exists()) {
                    gameLoadDataCallback.onDataError();
                    return;
                }

                final Game.State state = Game.State.values()[dataSnapshot.child(STATE).getValue(Integer.class)];
                final String creator = dataSnapshot.child(CREATOR).getValue(String.class);
                final int studyDuration = dataSnapshot.child(STUDY_DURATION).getValue(Integer.class);
                final long startTime = dataSnapshot.child(START_TIME).exists() ? dataSnapshot.child(START_TIME).getValue(Long.class) : Long.MIN_VALUE;
                ArrayList<String> objectIds = new ArrayList<>();
                ArrayList<String> players = new ArrayList<>();

                if(dataSnapshot.child(PLAYERS).exists()) {
                    for (DataSnapshot child : dataSnapshot.child(PLAYERS).getChildren()) {
                        players.add(child.getKey());
                    }
                }

                for (DataSnapshot child : dataSnapshot.child(OBJECTS).getChildren()) {
                    objectIds.add(child.getKey());
                }

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
                        Game game = new Game(gameId, state, gameObjects, creator, startTime, studyDuration);
                        gameLoadDataCallback.onDataLoaded(game);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                gameLoadDataCallback.onDataError();
            }
        });
    }

    @Override
    public void setGameState(@NonNull String gameId, @NonNull Game.State state, @NonNull Context context, @NonNull BaseDataSource.GetDataCallback<Game> updateGameCallback) {

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

        DataModel.getDataModel().getGameObjectsForType(config.getObjectType(), new BaseDataSource.LoadDataCallback<GameObject>() {
            @Override
            public void onDataLoaded(LinkedHashMap<String, GameObject> data) {
                ArrayList<GameObject> availableGameObjects = new ArrayList<>(((LinkedHashMap<String, GameObject>) data.clone()).values());
                for(int i = 0; i < config.getNumberOfObjects(); i++) {
                    int randomIndex = (int) Math.min(Math.round(Math.random() * availableGameObjects.size()), availableGameObjects.size()-1);
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
                gameValues.put(STUDY_DURATION, config.getStudyDuration());
                gameReference.updateChildren(gameValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Game game = new Game(
                                gameCodeTask.getResult(),
                                Game.State.WAITING,
                                gameObjects,
                                DataModel.getDataModel().getUser().getId(),
                                Long.MIN_VALUE,
                                config.getStudyDuration()
                        );
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
                        editor.putLong(CURRENT_GAME_START_TIME, game.getStartTime());
                        editor.putInt(CURRENT_GAME_STUDY_DURATION, game.getStudyDuration());

                        editor.apply();
                        gameDataCallback.onDataLoaded(game);
                    }
                });
            }
        });

    }

    @Override
    public void getGuesses(@NonNull final BaseDataSource.GetDataCallback<UserGameGuesses> userGameGuessesDataCallback) {
        final DatabaseReference databaseReference = getDatabaseReference();

        DataModel.getDataModel().getCurrentGame(new BaseDataSource.GetDataCallback<Game>() {
            @Override
            public void onDataLoaded(final Game game) {
                final User user = DataModel.getDataModel().getUser();

                Query userGameGuessesQuery = databaseReference.child(GUESSES).child(game.getId()).child(user.getId());
                userGameGuessesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> guesses = new ArrayList<>();
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            guesses.add(child.getKey());
                        }
                        userGameGuessesDataCallback.onDataLoaded(new UserGameGuesses(user.getId(), game.getId(), guesses));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        userGameGuessesDataCallback.onDataError();
                    }
                });
            }

            @Override
            public void onDataError() {
                userGameGuessesDataCallback.onDataError();
            }
        });
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

    void setOnGameStateChangeListener(OnGameStateChangeListener onGameStateChangeListener) {
        mOnGameStateChangeListener = onGameStateChangeListener;
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

    public interface OnGameRosterChangeListener {
        void onPlayerAdded(String newPlayerId);

        void onPlayerRemoved(String removedPlayerId);
    }

    public interface OnGameStateChangeListener {
        void onStateChanged(Game.State newState);
    }
}
