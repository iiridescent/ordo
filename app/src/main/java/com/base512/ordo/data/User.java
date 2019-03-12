package com.base512.ordo.data;

/**
 * Created by Thomas on 2/21/2017.
 */

/**
 * Data class for game User
 */
public final class User extends DataObject {

    /** The number of games this user has played and finished **/
    private final int mGamesPlayed;

    /** The highest number of objects this user has been able to memorize **/
    private final int mHighScore;

    public User(String keyCode, int highScore, int gamesPlayed) {
        super(keyCode);
        mGamesPlayed = gamesPlayed;
        mHighScore = highScore;
    }

    public int getGamesPlayed() {
        return mGamesPlayed;
    }

    public int getHighScore() {
        return mHighScore;
    }

    public User withHighScore(int highScore) {
        return new User(getId(), highScore, mGamesPlayed);
    }

    public User withGamesPlayed(int gamesPlayed) {
        return new User(getId(), mHighScore, gamesPlayed);
    }
}
