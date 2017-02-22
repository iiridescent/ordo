package com.base512.ordo.data.source;

import android.content.Context;

import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.User;
import com.base512.ordo.data.source.game.GameModel;
import com.base512.ordo.data.source.gameObject.GameObjectModel;
import com.base512.ordo.data.source.user.UserModel;

public class DataModel {

    /** The single instance of this data model that exists for the life of the application. */
    private static final DataModel sDataModel = new DataModel();

    private Context mContext;

    // The model from which user data are fetched
    private UserModel mUserModel;

    // The model from which game object are fetched
    private GameObjectModel mGameObjectModel;

    // The model from which game data are fetched
    private GameModel mGameModel;

    public static DataModel getDataModel() {
        return sDataModel;
    }

    private DataModel() {}

    public void setContext(Context context) {
        if (mContext != null) {
            throw new IllegalStateException("context has already been set");
        }
        mContext = context.getApplicationContext();

        mUserModel = new UserModel(mContext);
        mGameObjectModel = new GameObjectModel(mContext);
        mGameModel = new GameModel(mContext);
    }

    public User getUser() {
        return mUserModel.getUser();
    }

    public boolean isUserAuthenticated() {
        return mUserModel.getUser() != null;
    }

    public void setUser(User user) {
        mUserModel.saveUser(user);
    }

    public void attemptLogin(String keyCode, BaseDataSource.GetDataCallback<User> userDataCallback) {
        mUserModel.getUser(keyCode, userDataCallback);
    }

    public void setHighScore(int highScore) {
        mUserModel.saveUser(getUser().withHighScore(highScore));
    }

    public void setGamesPlayed(int gamesPlayed) {
        mUserModel.saveUser(getUser().withGamesPlayed(gamesPlayed));
    }

    public void getGameObject(String id, BaseDataSource.GetDataCallback<GameObject> gameObjectDataCallback) {
        mGameObjectModel.getGameObject(id, gameObjectDataCallback);
    }

}
