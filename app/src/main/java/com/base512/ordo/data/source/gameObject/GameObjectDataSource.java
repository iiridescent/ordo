package com.base512.ordo.data.source.gameObject;

import android.content.Context;
import android.support.annotation.NonNull;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;

/**
 * Created by Thomas on 2/22/2017.
 */

public interface GameObjectDataSource {
    void getGameObject(@NonNull String id, BaseDataSource.GetDataCallback<GameObject> gameObjectDataCallback);
}
