package com.base512.ordo.data.source.game;

import android.content.Context;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.UserGameGuesses;
import com.base512.ordo.data.source.BaseDataSource;

import java.util.ArrayList;

/**
 * Created by Thomas on 2/22/2017.
 */

public class GameModel implements GameRepository.OnGameStateChangeListener{
    private final Context mContext;

    /** The current state of the game. */
    private Game mGame;

    private UserGameGuesses mUserGameGuesses;

    private GameRepository mGameRepository;

    private ArrayList<GameRepository.OnGameStateChangeListener> mOnGameStateChangeListeners;

    private ArrayList<GameRepository.OnGameRosterChangeListener> mOnGameRosterChangeListeners;

    public GameModel(Context context) {
        mContext = context;
        mGameRepository = new GameRepository();
        mOnGameStateChangeListeners = new ArrayList<>();
        mOnGameRosterChangeListeners = new ArrayList<>();
    }

    public void createGame(Game.Config gameConfig, BaseDataSource.GetDataCallback<Game> gameCreateDataCallback) {
        mGameRepository.createGame(gameConfig, mContext, gameCreateDataCallback);
        mGame = null;
    }

    public void getCurrentGame(final BaseDataSource.GetDataCallback<Game> gameDataCallback) {
        if (mGame == null) {
            mGameRepository.getCurrentGame(mContext, new BaseDataSource.GetDataCallback<Game>() {
                @Override
                public void onDataLoaded(Game game) {
                    mGame = game;
                    gameDataCallback.onDataLoaded(game);
                }

                @Override
                public void onDataError() {
                    gameDataCallback.onDataError();
                }
            });
        } else {
            gameDataCallback.onDataLoaded(mGame);
        }
    }

    public void getGame(String id, final BaseDataSource.GetDataCallback<Game> getGameCallback) {
        if (mGame == null || !mGame.getId().equals(id)) {
            mGameRepository.getGame(id, new BaseDataSource.GetDataCallback<Game>() {
                @Override
                public void onDataLoaded(Game game) {
                    // mGame = game;
                    getGameCallback.onDataLoaded(game);
                }

                @Override
                public void onDataError() {
                    getGameCallback.onDataError();
                }
            });
        } else {
            getGameCallback.onDataLoaded(mGame);
        }
    }

    public void setCurrentGame(Game game, BaseDataSource.UpdateDataCallback updateGameCallback) {
        mGameRepository.setCurrentGame(game, mContext, updateGameCallback);
        mGameRepository.setOnGameStateChangeListener(this);
    }

    public void setGameState(Game.State state, final BaseDataSource.UpdateDataCallback updateGameCallback) {
        Game game = mGame.withState(state);
        mGameRepository.setCurrentGame(game, mContext, updateGameCallback);
        mGame = state == Game.State.FINISHED ? null : game;
    }

    public void setGuesses(UserGameGuesses userGameGuesses, BaseDataSource.UpdateDataCallback updateUserGameGuessesCallback) {
        mUserGameGuesses = userGameGuesses;
        mGameRepository.setGuesses(userGameGuesses, updateUserGameGuessesCallback);
    }

    public void getGuesses(final BaseDataSource.GetDataCallback<UserGameGuesses> getUserGameGuessesCallback) {
        if(mUserGameGuesses != null) {
            getUserGameGuessesCallback.onDataLoaded(mUserGameGuesses);
        } else {
            mGameRepository.getGuesses(new BaseDataSource.GetDataCallback<UserGameGuesses>() {
                @Override
                public void onDataLoaded(UserGameGuesses data) {
                    mUserGameGuesses = data;
                    getUserGameGuessesCallback.onDataLoaded(data);
                }

                @Override
                public void onDataError() {
                    getUserGameGuessesCallback.onDataError();
                }
            });
        }
    }

    public void addOnGameStateChangedListener(GameRepository.OnGameStateChangeListener onGameStateChangeListener) {
        mOnGameStateChangeListeners.add(onGameStateChangeListener);
    }

    @Override
    public void onStateChanged(Game.State newState) {
        for(GameRepository.OnGameStateChangeListener onGameStateChangeListener : mOnGameStateChangeListeners) {
            onGameStateChangeListener.onStateChanged(newState);
        }
    }
}
