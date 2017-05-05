package com.example.volcano.dewdrop.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.volcano.dewdrop.MainActivity;
import com.example.volcano.dewdrop.R;
import com.example.volcano.dewdrop.SignInActivity;
import com.example.volcano.dewdrop.utils.DatabaseOpenHelper;
import com.example.volcano.dewdrop.utils.ProgressDialogFragment;
import com.example.volcano.dewdrop.utils.StorageHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.DataBufferObserver;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;

/**
 * Created by volcano on 02/04/17.
 */

public class Authenticator {

    private FragmentActivity boundActivity;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    public final int RC_SIGN_IN = 1;
    private String uid;
    private CountDownLatch latch;

    public Authenticator(FragmentActivity boundActivity) {
        this.boundActivity = boundActivity;
        addFirebaseListener();
    }

    public Authenticator(FragmentActivity boundActivity, CountDownLatch latch) {
        this(boundActivity);
        this.latch = latch;
    }


    public void createNewUser(final String email, String password, final Bundle extras) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(boundActivity, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String name = extras.getString("Name");
                        String surname = extras.getString("Surname");
                        String birthDate = extras.getString("Birthdate");
                        Uri image = extras.getParcelable("Image");
                        User newUser = new User(authResult.getUser().getUid(), name, surname, birthDate, email, image);
                        DatabaseOpenHelper databaseOpenHelper = DatabaseOpenHelper.getInstance();
                        databaseOpenHelper.addChild("Users", newUser);
                    }
                });

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void signIn(String email, final String password) {
        final ProgressDialogFragment progressDialogFragment=new ProgressDialogFragment();
        progressDialogFragment.show(boundActivity.getFragmentManager(),"TAG");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(boundActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                /*If sign in fails, display a message to the user. If sign in succeeds
                         the auth state listener will be notified and logic to handle the
                         signed in user can be handled in the listener.*/
                        System.out.println(task.getResult().getUser().getUid());
                        if (!task.isSuccessful()) {
                            Toast.makeText(boundActivity, "Cannot sign in. Check your network.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            final Intent intent = new Intent(boundActivity, MainActivity.class);
                            final Bundle extras = new Bundle();

                            if (boundActivity instanceof SignInActivity) {
                                Continuation continuation = new Continuation() {
                                    User user = new User();

                                    @Override
                                    public Object then(@NonNull final Task task) throws Exception {
                                        final DatabaseReference databaseReference = DatabaseOpenHelper.getInstance().select("Users", getmAuth().getCurrentUser().getUid());
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Iterator iterator = dataSnapshot.getChildren().iterator();
                                                while (iterator.hasNext()) {
                                                    DataSnapshot ds = (DataSnapshot) iterator.next();
                                                    user.setNAME(ds.child("NAME").getValue().toString());
                                                    user.setSURNAME(ds.child("SURNAME").getValue().toString());
                                                    user.setEMAIL(ds.child("EMAIL").getValue().toString());
                                                    user.setBIRTHDATE(ds.child("BIRTHDATE").getValue().toString());
                                                    task.continueWith(new Continuation() {
                                                        @Override
                                                        public Object then(@NonNull Task task) throws Exception {
                                                            System.out.println(databaseReference);
                                                            // databaseReference=databaseReference.child(databaseReference.getKey());
                                                            System.out.println(databaseReference);
                                                            user.setUID(getmAuth().getCurrentUser().getUid());
                                                            user.setPHOTO_URL(((Uri) task.getResult()));
                                                            extras.putString("NAME", user.getNAME() + " " + user.getSURNAME());
                                                            extras.putString("EMAIL", getmAuth().getCurrentUser().getEmail());
                                                            extras.putString("PHOTO", ((Uri) task.getResult()).toString());
                                                            intent.putExtras(extras);
                                                            System.out.println(extras);
                                                            progressDialogFragment.dismiss();
                                                            boundActivity.startActivity(intent);
                                                            return intent;
                                                        }
                                                    });

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }


                                        });

                                        return task.getResult();
                                    }
                                };

                                StorageHelper.getInstance().getImageUrl(getmAuth().getCurrentUser().getUid(), continuation);


                            }
                        }

                    }
                });
    }


    public void googleAuthInit() {
            /* Configure sign-in to request the user's ID, email address, and basic
             profile. ID and basic profile are included in DEFAULT_SIGN_IN.*/
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(boundActivity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

            /* Build a GoogleApiClient with access to the Google Sign-In API and the
             options specified by gso.*/
        mGoogleApiClient = new GoogleApiClient.Builder(boundActivity)
                .enableAutoManage(boundActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(boundActivity, "Connection failed, retry", Toast.LENGTH_LONG).show();
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Log.d("INIT", "Init done");
    }

    public void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        boundActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(boundActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(boundActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }


    public void addFirebaseListener() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    notifyCaller(user.getUid());
                } else {
                    // com.example.volcano.dewdrop.auth.User is signed out
                    Toast.makeText(boundActivity, "onAuthStateChanged:signed_out:", Toast.LENGTH_LONG).show();
                }
                // ...
            }
        };
    }

    public void notifyCaller(Object message) {
        if (boundActivity instanceof SignInActivity)
            ((SignInActivity) boundActivity).setMessage(message);
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public FirebaseAuth.AuthStateListener getmAuthListener() {
        return mAuthListener;
    }

}



