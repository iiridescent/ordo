package com.base512.ordo.data;

/**
 * Base data object
 *
 * Created with the assumption that every object has a UID
 */
public abstract class DataObject {
    private final String mId;

    protected DataObject(String id) {
        this.mId = id;
    }

    protected DataObject() {
        mId = null;
    }

    public String getId() {
        return mId;
    }
}
