package com.example.volcano.dewdrop.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.volcano.dewdrop.R;
import com.example.volcano.dewdrop.SignInActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.DataBufferObserver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.Serializable;
import java.util.Observer;

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

    public Authenticator(FragmentActivity boundActivity) {
        this.boundActivity = boundActivity;
        addFirebaseListener();
    }


    public void createNewUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(boundActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.d("Exception", task.getException().getMessage());
                            Toast.makeText(boundActivity, "Error",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(boundActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(boundActivity, "onAuthStateChanged:onComplete", Toast.LENGTH_LONG).show();

                        /*If sign in fails, display a message to the user. If sign in succeeds
                         the auth state listener will be notified and logic to handle the
                         signed in user can be handled in the listener.*/
                        if (!task.isSuccessful()) {
                            Toast.makeText(boundActivity, "Cannot sign in. Check your network.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (boundActivity instanceof SignInActivity) {
                                ((SignInActivity) boundActivity).nextActivity();
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

        Toast.makeText(boundActivity.getApplicationContext(), "GSO", Toast.LENGTH_LONG).show();
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
        Toast.makeText(boundActivity, "Init done", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(boundActivity, "onAuthStateChanged:signed_in:" + user.getUid(), Toast.LENGTH_LONG).show();
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
