package com.base512.ordo.data;

import java.util.ArrayList;

/**
 * An object to be memorized in a game
 *
 * {@link mNames} is a list because any object can have multiple correct names
 */
public class GameObject extends DataObject {

    private final ArrayList<String> mNames;

    private final String mImageUrl;

    private final Type mType;

    public GameObject(String id, ArrayList<String> names, String imageUrl, Type type) {
        super(id);

        mNames = names;
        mImageUrl = imageUrl;
        mType = type;
    }

    public ArrayList<String> getNames() {
        return mNames;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public Type getType() {
        return mType;
    }

    public enum Type {
        ITEM,
        US_LICENSE_PLATE
    }
}
