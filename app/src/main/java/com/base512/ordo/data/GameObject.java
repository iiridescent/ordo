package com.base512.ordo.data;

public class GameObject extends DataObject {

    private final String mName;

    private final String mImageName;

    public GameObject(String id, String name, String imageName) {
        super(id);

        mName = name;
        mImageName = imageName;
    }

    public String getName() {
        return mName;
    }

    public String getImageName() {
        return mImageName;
    }
}
