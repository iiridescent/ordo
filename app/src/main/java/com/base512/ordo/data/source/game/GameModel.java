package com.base512.ordo.data.source.game;

import android.content.Context;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.UserGameGuesses;
import com.base512.ordo.data.source.BaseDataSource;

/**
 * Created by Thomas on 2/22/2017.
 */

public class GameModel {
    private final Context mContext;

    /** The current state of the game. */
    private Game mGame;

    private UserGameGuesses mUserGameGuesses;

    private GameRepository mGameRepository;

    public GameModel(Context context) {
        mContext = context;
        mGameRepository = new GameRepository();
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

    public void saveGame(Game game) {
        mGameRepository.setCurrentGame(game, mContext);
    }

    public void setGuesses(UserGameGuesses userGameGuesses, BaseDataSource.UpdateDataCallback updateUserGameGuessesCallback) {
        mUserGameGuesses = userGameGuesses;
        mGameRepository.setGuesses(userGameGuesses, updateUserGameGuessesCallback);
    }
}
