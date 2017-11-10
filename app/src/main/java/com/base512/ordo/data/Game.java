package com.base512.ordo.data;

/**
 * Created by Thomas on 2/21/2017.
 */

public class Game extends DataObject {
    public static class Config {
        private final int mNumberOfObjects;

        private final int mStudyDuration;

        private final GameObject.Type mObjectType;

        public Config(int numberOfObjects, int studyDuration, GameObject.Type objectType) {
            mNumberOfObjects = numberOfObjects;
            mStudyDuration = studyDuration;
            mObjectType = objectType;
        }

        public int getNumberOfObjects() {
            return mNumberOfObjects;
        }

        public int getStudyDuration() {
            return mStudyDuration;
        }

        public GameObject.Type getObjectType() {
            return mObjectType;
        }
    }

    public enum State { WAITING, STUDY, TEST, FINISHED }

    private final State mState;

    private final GameObject[] mGameObjects;

    private final String mCreator;

    private final long mStartTime;

    private final int mStudyDuration;

    public Game(String id, State state, GameObject[] gameObjects, String creator, long startTime, int studyDuration) {
        super(id);

        mState = state;
        mGameObjects = gameObjects;
        mCreator = creator;
        mStartTime = startTime;
        mStudyDuration = studyDuration;
    }

    public State getState() {
        return mState;
    }

    public GameObject[] getGameObjects() {
        return mGameObjects;
    }

    public String getCreator() {
        return mCreator;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public int getStudyDuration() { return mStudyDuration; }

    public Game withState(State state) {
        long startTime;
        if(state == State.STUDY) {
            startTime = System.currentTimeMillis();
        } else {
            startTime = Long.MIN_VALUE;
        }

        return new Game(getId(), state, mGameObjects, mCreator, startTime, mStudyDuration);
    }
}
