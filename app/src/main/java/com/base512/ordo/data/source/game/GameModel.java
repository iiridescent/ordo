package com.base512.ordo.data.source.game;

import android.content.Context;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.source.BaseDataSource;

/**
 * Created by Thomas on 2/22/2017.
 */

public class GameModel {
    private final Context mContext;

    /** The current state of the game. */
    private Game mGame;

    private GameRepository mGameRepository;

    public GameModel(Context context) {
        mContext = context;
        mGameRepository = new GameRepository();
    }

    public void getCurrentGame(BaseDataSource.GetDataCallback<Game> gameDataCallback) {
        if (mGame == null) {
            mGameRepository.getCurrentGame(mContext, gameDataCallback);
        } else {
            gameDataCallback.onDataLoaded(mGame);
        }
    }

    public void getGame(String id, BaseDataSource.GetDataCallback<Game> getGameCallback) {
        if (mGame == null || !mGame.getId().equals(id)) {
            mGameRepository.getGame(id, getGameCallback);
        } else {
            getGameCallback.onDataLoaded(mGame);
        }
    }

    public void saveGame(Game game) {
        mGameRepository.setCurrentGame(game, mContext);
    }
}
