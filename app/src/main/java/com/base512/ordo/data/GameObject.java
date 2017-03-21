package com.base512.ordo.data;

import java.util.ArrayList;

public class GameObject extends DataObject {

    private final ArrayList<String> mNames;

    private final String mImageUrl;

    public GameObject(String id, ArrayList<String> names, String imageUrl) {
        super(id);

        mNames = names;
        mImageUrl = imageUrl;
    }

    public ArrayList<String> getNames() {
        return mNames;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
}
