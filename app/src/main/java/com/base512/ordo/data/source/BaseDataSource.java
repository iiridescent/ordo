package com.base512.ordo.data.source;

import com.base512.ordo.data.DataObject;

import java.util.LinkedHashMap;

public interface BaseDataSource {
    interface LoadDataCallback<T extends DataObject> extends AccessDataCallback {
        void onDataLoaded(LinkedHashMap<String, T> data);
    }

    interface GetDataCallback<T extends DataObject> extends AccessDataCallback {
        void onDataLoaded(T data);
    }

    interface UpdateDataCallback extends AccessDataCallback {
        void onDataUpdated(String id);
    }
}