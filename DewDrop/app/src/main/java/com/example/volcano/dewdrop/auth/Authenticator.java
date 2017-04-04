package com.example.volcano.dewdrop.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.volcano.dewdrop.SignInActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                            Toast.makeText(boundActivity, "OK",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void googleAuthInit() {
            /* Configure sign-in to request the user's ID, email address, and basic
             profile. ID and basic profile are included in DEFAULT_SIGN_IN.*/
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

Toast.makeText(boundActivity.getApplicationContext(),"GSO",Toast.LENGTH_LONG).show();
            /* Build a GoogleApiClient with access to the Google Sign-In API and the
             options specified by gso.*/
        mGoogleApiClient = new GoogleApiClient.Builder(boundActivity)
                .enableAutoManage((FragmentActivity) boundActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(boundActivity, "Connection failed, retry", Toast.LENGTH_LONG).show();
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Toast.makeText(boundActivity,"Init done",Toast.LENGTH_LONG).show();
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

        } else {

        }
    }

    public void addFirebaseListener() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(boundActivity, "onAuthStateChanged:signed_in:" + user.getUid(), Toast.LENGTH_LONG).show();
                } else {
                    // User is signed out
                    Toast.makeText(boundActivity, "onAuthStateChanged:signed_out:", Toast.LENGTH_LONG).show();
                }
                // ...
            }
        };
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public FirebaseAuth.AuthStateListener getmAuthListener() {
        return mAuthListener;
    }
}
