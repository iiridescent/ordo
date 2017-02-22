package com.base512.ordo.data.source.gameObject;

import android.content.Context;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;
import com.base512.ordo.data.source.game.GameRepository;

import java.util.LinkedHashMap;

/**
 * Created by Thomas on 2/22/2017.
 */

public class GameObjectModel {
    private final Context mContext;

    /** Cached list of game objects. */
    private LinkedHashMap<String, GameObject> mGameObjects;

    private GameObjectRepository mGameObjectRepository;

    public GameObjectModel(Context context) {
        mContext = context;
        mGameObjectRepository = new GameObjectRepository();
    }

    public void getGameObject(String id, BaseDataSource.GetDataCallback<GameObject> gameObjectDataCallback) {
        if(mGameObjects.get(id) == null) {
            mGameObjectRepository.getGameObject(id, gameObjectDataCallback);
        } else {
            gameObjectDataCallback.onDataLoaded(mGameObjects.get(id));
        }
    }
}
