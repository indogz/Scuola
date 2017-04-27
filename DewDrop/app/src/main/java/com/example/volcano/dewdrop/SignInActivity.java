package com.example.volcano.dewdrop;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.volcano.dewdrop.auth.Authenticator;
import com.example.volcano.dewdrop.auth.User;
import com.example.volcano.dewdrop.utils.DatabaseOpenHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;

/**
 * Estensione FragmentActivity per abilitare automanage
 **/
public class SignInActivity extends FragmentActivity implements Linkable, Logo.OnFragmentInteractionListener {


    private final int SIGN_UP = 2;
    private String lastNotice=null;

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

    public void nextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("NAME", authenticator.getmAuth().getCurrentUser().getDisplayName());
        extras.putString("EMAIL", authenticator.getmAuth().getCurrentUser().getEmail());
        extras.putString("PHOTO", authenticator.getmAuth().getCurrentUser().getPhotoUrl().toString());
        intent.putExtras(extras);
        System.out.println(extras);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        linkViews();
        addLogo();
        authenticator = new Authenticator(this);
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

        ActivityContents.googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticator.googleAuthInit();
                authenticator.googleSignIn();
            }
        });
        ActivityContents.signUpLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivityForResult(intent, SIGN_UP);
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
            Toast.makeText(this, "Signed in", Toast.LENGTH_LONG).show();
            nextActivity();
        } else if (requestCode == SIGN_UP) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                String email = extras.getString("Email");
                String password = extras.getString("Password");
                String name = extras.getString("Name");
                String surname = extras.getString("Surname");
                String birthDate = extras.getString("Birthdate");
                Uri image = extras.getParcelable("Image");
                Authenticator authenticator = new Authenticator(this);
                authenticator.createNewUser(email, password);
                User newUser = new User(lastNotice,name, surname, birthDate, email, image);
                DatabaseOpenHelper databaseOpenHelper = DatabaseOpenHelper.getInstance();
                databaseOpenHelper.writeUser(newUser);
            }
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
        if (authenticator.getmAuthListener() != null) {
            authenticator.getmAuth().removeAuthStateListener(authenticator.getmAuthListener());
        }
    }

    public void setMessage(Object message) {
        if (message instanceof String) {
            lastNotice = (String) message;
        }
    }

}
