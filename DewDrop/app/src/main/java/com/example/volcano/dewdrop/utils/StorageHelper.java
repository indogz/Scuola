package com.example.volcano.dewdrop.utils;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
/**
 * Created by volcano on 12/04/17.
 */

public class StorageHelper {

    private StorageReference mStorageRef;

    private StorageHelper() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public static StorageHelper getInstance() {
        return new StorageHelper();
    }

    public void uploadFile(File file, String uid) {

        StorageReference reference = mStorageRef.child("/images");
        //Create dump copy;
        int dot=file.getName().lastIndexOf('.');
        String extension=file.getName().substring(dot);
        File rename=new File(uid);
        file.renameTo(rename);
        reference.putFile(Uri.fromFile(file));


    }

    public void downloadFile(Uri pathToFile) {
        StorageReference reference = mStorageRef.child(pathToFile.toString());
        reference.getFile(pathToFile);
    }


}

