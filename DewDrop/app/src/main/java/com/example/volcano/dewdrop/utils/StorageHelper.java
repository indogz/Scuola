package com.example.volcano.dewdrop.utils;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.volcano.dewdrop.MainActivity;
import com.example.volcano.dewdrop.VideoActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;

/**
 * Created by volcano on 12/04/17.
 */

public class StorageHelper {

    private static StorageHelper storageHelper = null;
    private StorageReference mStorageRef;
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

    @SuppressWarnings("VisibleForTests")
    public void uploadVideo(InputStream file, final DialogFragment loadingMask) {
        StorageReference reference = mStorageRef.child("video");
        System.out.println(reference);
        StorageMetadata.Builder builder = new StorageMetadata.Builder();
        builder.setContentType("video/mp4");
        reference.putStream(file, builder.build()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                loadingMask.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println(taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount() + "%");
            }
        });
    }

    public void uploadImage(File file, String uid) {
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

