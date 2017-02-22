package com.base512.ordo.data.source.game;

import android.content.Context;
import android.support.annotation.NonNull;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.source.BaseDataSource;

/**
 * Created by Thomas on 2/21/2017.
 */

public class GameRepository implements GameDataSource {
    @Override
    public Game getCurrentGame(@NonNull Context context) {
        return null;
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
}
