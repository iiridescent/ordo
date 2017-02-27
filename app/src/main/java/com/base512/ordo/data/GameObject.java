package com.base512.ordo.data;

public class GameObject extends DataObject {

    private final String mName;

    private final String mImageUrl;

    public GameObject(String id, String name, String imageUrl) {
        super(id);

        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
}
