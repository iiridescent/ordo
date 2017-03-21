package com.base512.ordo.data.source.gameObject;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.base512.ordo.R;
import com.base512.ordo.data.GameObject;
import com.base512.ordo.data.source.BaseDataSource;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Thomas on 2/22/2017.
 */

public class GameObjectRepository implements GameObjectDataSource {

    // Key to game objects in database
    private static final String OBJECTS = "objects";
    private static final String IMAGE_NAME = "imageName";
    private static final String IMAGE_URL = "imageUrl";
    private static final String NAME = "name";
    private static final String NAMES = "names";

    private static final HashMap<String, Integer> sStubImages = new HashMap<>();

    private static SharedPreferences sPrefs;

    private static DatabaseReference sDatabaseReference;
    private static StorageReference sStorageReference;

    @Override
    public void getGameObject(@NonNull final String id, final BaseDataSource.GetDataCallback<GameObject> gameObjectDataCallback) {
        final DatabaseReference databaseReference = getDatabaseReference();

        Query gameObjectQuery = databaseReference.child(id);
        gameObjectQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    ArrayList<String> names = new ArrayList<>();
                    if(dataSnapshot.child(NAMES).exists()) {
                        for(DataSnapshot nameSnapshot : dataSnapshot.child(NAMES).getChildren()) {
                            names.add(nameSnapshot.getValue(String.class));
                        }
                    } else {
                        names.add(dataSnapshot.child(NAME).getValue(String.class));
                    }

                    GameObject gameObject = new GameObject(
                            id,
                            names,
                            dataSnapshot.child(IMAGE_URL).getValue(String.class)
                    );
                    gameObjectDataCallback.onDataLoaded(gameObject);
                } else {
                    gameObjectDataCallback.onDataError();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                gameObjectDataCallback.onDataError();
            }
        });
    }

    @Override
    public void loadGameObjects(final BaseDataSource.LoadDataCallback<GameObject> gameObjectsDataCallback) {
        Query gameObjectsQuery = getDatabaseReference();

        gameObjectsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinkedHashMap<String, GameObject> gameObjects = new LinkedHashMap<>();

                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    ArrayList<String> names = new ArrayList<>();
                    if(child.child(NAMES).exists()) {
                        for(DataSnapshot nameSnapshot : child.child(NAMES).getChildren()) {
                            names.add(nameSnapshot.getValue(String.class));
                        }
                    } else {
                        names.add(child.child(NAME).getValue(String.class));
                    }
                    GameObject gameObject = new GameObject(
                            child.getKey(),
                            names,
                            child.child(IMAGE_URL).getValue(String.class)
                    );
                    gameObjects.put(gameObject.getId(), gameObject);
                }

                gameObjectsDataCallback.onDataLoaded(gameObjects);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                gameObjectsDataCallback.onDataError();
            }
        });
    }

    @Override
    public void getGameObjectResource(@NonNull String id, Context context, @NonNull BaseDataSource.GetDataCallback<Integer> drawableDataCallback) {
        HashMap<String, Integer> stubImages = getStubImages();

        if(stubImages.get(id) != null) {
            drawableDataCallback.onDataLoaded(stubImages.get(id));
        } else {
            drawableDataCallback.onDataError();
        }
    }

    @Override
    public void uploadGameObject(final GameObject gameObject, String gameObjectImageUrl, Context context, final BaseDataSource.UpdateDataCallback uploadGameObjectCallback) {
        final DatabaseReference gameObjectReference = getDatabaseReference().push();
        final StorageReference gameObjectFileReference = getStorageReference().child(gameObjectReference.getKey());

        final TaskCompletionSource<String> uploadImageTaskCompletionSource = new TaskCompletionSource<>();
        Task<String> uploadImageTask = uploadImageTaskCompletionSource.getTask();

        Glide.with(context.getApplicationContext())
                .load(gameObjectImageUrl)
                .asBitmap()
                .toBytes()
                .override(500, 500)
                .centerCrop()
                .into(new SimpleTarget<byte[]>() {
                    @Override
                    public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                        UploadTask uploadTask = gameObjectFileReference.putBytes(resource);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                uploadGameObjectCallback.onDataError();
                                uploadImageTaskCompletionSource.setException(exception);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                uploadImageTaskCompletionSource.setResult(downloadUrl.toString());
                            }
                        });
                    }
                });

        uploadImageTask.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.getException() != null) {
                    uploadGameObjectCallback.onDataError();
                    return;
                }

                String imageUrl = task.getResult();
                HashMap<String, Object> gameObjectValues = new HashMap<>();
                gameObjectValues.put(NAMES, gameObject.getNames());
                gameObjectValues.put(IMAGE_URL, imageUrl);
                gameObjectReference.updateChildren(gameObjectValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.getException() != null) {
                            uploadGameObjectCallback.onDataError();
                        }
                        uploadGameObjectCallback.onDataUpdated(gameObjectReference.getKey());
                    }
                });
            }
        });
    }

    private static HashMap<String, Integer> getStubImages() {
        if(sStubImages.isEmpty()) {
            sStubImages.put("bowtie", R.drawable.bowtie);
            sStubImages.put("creditcard", R.drawable.creditcard);
            sStubImages.put("eraser", R.drawable.eraser);
            sStubImages.put("pencil", R.drawable.pencil);
            sStubImages.put("pendrive", R.drawable.pendrive);
            sStubImages.put("ring", R.drawable.ring);
            sStubImages.put("solocup", R.drawable.solocup);
            sStubImages.put("topsider", R.drawable.topsider);
            return sStubImages;
        } else {
            return sStubImages;
        }
    }

    private static DatabaseReference getDatabaseReference() {
        if (sDatabaseReference == null) {
            sDatabaseReference = FirebaseDatabase.getInstance().getReference().child(OBJECTS);
        }

        return sDatabaseReference;
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        if (sPrefs == null) {
            sPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }

        return sPrefs;
    }

    private static StorageReference getStorageReference() {
        if(sStorageReference == null) {
            sStorageReference = FirebaseStorage.getInstance().getReference().child(OBJECTS);
        }

        return sStorageReference;
    }
}
