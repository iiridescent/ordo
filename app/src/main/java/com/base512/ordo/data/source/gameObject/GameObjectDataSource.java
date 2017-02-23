package com.base512.ordo.data.source.gameObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.base512.ordo.data.Game;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;

/**
 * Created by Thomas on 2/22/2017.
 */

public interface GameObjectDataSource {
    void getGameObject(@NonNull String id, BaseDataSource.GetDataCallback<GameObject> gameObjectDataCallback);

    void loadGameObjects(BaseDataSource.LoadDataCallback<GameObject> gameObjectsDataCallback);

    void getGameObjectResource(@NonNull String id, Context context, @NonNull BaseDataSource.GetDataCallback<Integer> drawableDataCallback);
}
