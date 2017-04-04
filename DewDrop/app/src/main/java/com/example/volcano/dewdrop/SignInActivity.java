package com.example.volcano.dewdrop;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.volcano.dewdrop.auth.Authenticator;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

/**
 * Estensione FragmentActivity per abilitare automanage
 **/
public class SignInActivity extends FragmentActivity implements Linkable,Logo.OnFragmentInteractionListener {


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    static class ActivityContents {
        static EditText usernameBox;
        static EditText passwordBox;
        static Button signInButton;
        static SignInButton googleButton;
        static TextView signUpLabel;
        static TextView forgottenLabel;
    }

    private Authenticator authenticator;

    public void linkViews() {
        ActivityContents.usernameBox = (EditText) findViewById(R.id.usernameBox);
        ActivityContents.passwordBox = (EditText) findViewById(R.id.passwordBox);
        ActivityContents.signInButton = (Button) findViewById(R.id.signinButton);
        ActivityContents.googleButton = (SignInButton) findViewById(R.id.googleAuth);
        ActivityContents.signUpLabel = (TextView) findViewById(R.id.signupLabel);
        ActivityContents.forgottenLabel = (TextView) findViewById(R.id.forgottenLabel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        linkViews();
        addLogo();
        authenticator=new Authenticator(this);
        authenticator.addFirebaseListener();
        attachListeners();
    }

    private void addLogo() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, Logo.newInstance("a", "b"));
        ft.commit();

    }

    private void attachListeners() {
        ActivityContents.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               authenticator.signIn(ActivityContents.usernameBox.getText().toString(), ActivityContents.passwordBox.getText().toString());
            }
        });

        ActivityContents.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticator.googleSignIn();
            }
        });
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == authenticator.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            authenticator.handleSignInResult(result);
        }
    }





    @Override
    public void onStart() {
        super.onStart();
        authenticator.getmAuth().addAuthStateListener(authenticator.getmAuthListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authenticator.getmAuthListener()!= null) {
            authenticator.getmAuth().removeAuthStateListener(authenticator.getmAuthListener());
        }
    }


}
