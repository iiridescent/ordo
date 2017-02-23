package com.base512.ordo.data;

import java.util.ArrayList;

/**
 * Created by Thomas on 2/23/2017.
 */

public class UserGameGuesses extends DataObject {

    private final String mGameId;
    private final ArrayList<String> mGuesses;

    public UserGameGuesses(String userId, String gameId, ArrayList<String> guesses) {
        super(userId);
        mGameId = gameId;
        if(guesses == null) {
            mGuesses = new ArrayList<>();
        } else {
            mGuesses = guesses;
        }
    }

    public String getUserId() {
        return getId();
    }

    public String getGameId() {
        return mGameId;
    }

    public ArrayList<String> getGuesses() {
        return mGuesses;
    }
}
