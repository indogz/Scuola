package com.example.volcano.dewdrop.utils;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
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
     * @param video
     * @return
     */
    public Uri fetchVideoUri(String video) {
        mStorageRef = mStorageRef.getRoot().child("/video/" + video + ".mp4");
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            executorService.awaitTermination(40, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.v("VERBOSE", "Interrupted execution");
        }
        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pointingUri = uri;
                    }
                });
                return pointingUri;
            }
        });



        return pointingUri;
    }


    public void uploadFile(File file, String uid) {

        StorageReference reference = mStorageRef.child("/images");
        //Create dump copy;
        int dot = file.getName().lastIndexOf('.');
        String extension = file.getName().substring(dot);
        File rename = new File(uid);
        file.renameTo(rename);
        reference.putFile(Uri.fromFile(file));


    }

    public void downloadFile(Uri pathToFile) {
        StorageReference reference = mStorageRef.child(pathToFile.toString());
        reference.getFile(pathToFile);
    }

}

