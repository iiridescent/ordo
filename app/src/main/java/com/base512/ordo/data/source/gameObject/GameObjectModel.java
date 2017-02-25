package com.base512.ordo.data.source.gameObject;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;

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
        mGameObjects = new LinkedHashMap<>();
    }

    public void getGameObject(String id, BaseDataSource.GetDataCallback<GameObject> gameObjectDataCallback) {
        if(mGameObjects == null || mGameObjects.get(id) == null) {
            mGameObjectRepository.getGameObject(id, gameObjectDataCallback);
        } else {
            gameObjectDataCallback.onDataLoaded(mGameObjects.get(id));
        }
    }

    public void loadGameObjects(final BaseDataSource.LoadDataCallback<GameObject> gameObjectsLoadDataCallback) {
        if(mGameObjects == null || mGameObjects.size() == 0) {
            mGameObjectRepository.loadGameObjects(new BaseDataSource.LoadDataCallback<GameObject>() {
                @Override
                public void onDataLoaded(LinkedHashMap<String, GameObject> gameObjects) {
                    mGameObjects = gameObjects;
                    gameObjectsLoadDataCallback.onDataLoaded(gameObjects);
                }

                @Override
                public void onDataError() {
                    gameObjectsLoadDataCallback.onDataError();
                }
            });
        } else {
            gameObjectsLoadDataCallback.onDataLoaded(mGameObjects);
        }
    }

    public void getGameObjectDrawable(String id, BaseDataSource.GetDataCallback<Integer> drawableDataCallback) {
        mGameObjectRepository.getGameObjectResource(id, mContext, drawableDataCallback);
    }
}
