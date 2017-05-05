package com.example.volcano.dewdrop.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.volcano.dewdrop.MainActivity;
import com.example.volcano.dewdrop.R;
import com.example.volcano.dewdrop.VideoActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by volcano on 12/04/17.
 */

public class StorageHelper {

    private StorageReference mStorageRef;

    private static StorageHelper storageHelper = null;

    private Uri pointingUri;

    private StorageHelper() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public static StorageHelper getInstance() {
        if (storageHelper == null) {
            storageHelper = new StorageHelper();
        }
        return storageHelper;
    }

    /**
     * Lo streaming dovrebbe attivarsi su stream asincroni.
     * Trovare metodo per bloccare thread principale sino al successo.
     *
     * @param video
     * @return
     */
    public void fetchVideoUri(final Context caller, final String video) {
        mStorageRef = mStorageRef.getRoot().child("/video/" + video + ".mp4");
        if (caller instanceof MainActivity) {
            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Intent i = new Intent(caller, VideoActivity.class);
                    i.putExtra("VIDEO", uri.toString());
                    caller.startActivity(i);
                }
            });
        } else {
            throw new RuntimeException("Caller Activity must be .MainActivity");
        }

    }


    public void uploadFile(File file, String uid) {
        StorageReference reference = mStorageRef.child("/images/" + uid);
        reference.putFile(Uri.fromFile(file));

    }

    public Uri getPointingUri() {
        return pointingUri;
    }

    public void setPointingUri(Uri pointingUri) {
        this.pointingUri = pointingUri;
    }

    public Uri getImageUrl(String uid,Continuation continuation) {
        System.out.println(mStorageRef.child("/images/" + uid));

        mStorageRef.child("/images/" + uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setPointingUri(uri);

            }


        })
        .continueWith(continuation);
        return getPointingUri();
    }

    public void downloadFile(Uri pathToFile) {
        StorageReference reference = mStorageRef.child(pathToFile.toString());
        reference.getFile(pathToFile);
    }

}

