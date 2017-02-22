package com.base512.ordo.data;

import java.util.ArrayList;

/**
 * Created by Thomas on 2/21/2017.
 */

public class Game extends DataObject {
    public enum State { WAITING, STUDY, TEST, FINISHED }

    private final State mState;

    private final GameObject[] mGameObjects;

    private final String mCreator;

    public Game(String id, State state, GameObject[] gameObjects, String creator) {
        super(id);

        mState = state;
        mGameObjects = gameObjects;
        mCreator = creator;
    }
}
