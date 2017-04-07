package com.example.volcano.dewdrop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.volcano.dewdrop.auth.Authenticator;

public class SignUpActivity extends FragmentActivity implements Linkable {


    private int PHOTO = 1;

    static class ActivityContents {
        static EditText nameEdit;
        static EditText surnameEdit;
        static EditText editBirth;
        static EditText editEmail;
        static EditText passwordEdit;
        static ImageView imageView;
        static Button chooseButton;
        static Spinner languages;
        static Button okButton;
        static Button cancelButton;

        static boolean isFilled() {
            /*return nameEdit.getText().toString().trim() != null &&
                    surnameEdit.getText().toString().trim() != null &&
                    editBirth.getText().toString().trim() != null &&
                    editEmail.getText().toString().trim() != null &&
                    passwordEdit.getText().toString().trim() != null &&
                    languages.isSelected();
                    */
            return true;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        linkViews();
        attachListeners();
    }

    @Override
    public void linkViews() {
        ActivityContents.nameEdit = (EditText) findViewById(R.id.nameEdit);
        ActivityContents.surnameEdit = (EditText) findViewById(R.id.surnameEdit);
        ActivityContents.editBirth = (EditText) findViewById(R.id.editBirth);
        ActivityContents.editEmail = (EditText) findViewById(R.id.editEmail);
        ActivityContents.passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        ActivityContents.imageView = (ImageView) findViewById(R.id.imageView);
        ActivityContents.chooseButton = (Button) findViewById(R.id.chooseButton);
        ActivityContents.languages = (Spinner) findViewById(R.id.spinner);
        ActivityContents.okButton = (Button) findViewById(R.id.okButton);
        ActivityContents.cancelButton = (Button) findViewById(R.id.cancelButton);
    }

    private boolean checkFields() {
        boolean check = true;
        if (ActivityContents.passwordEdit.getText().length() < 8) {
            check = false;
            ActivityContents.passwordEdit.setBackgroundColor(getColor(R.color.backgroundError));
            Toast.makeText(this, "Your password must have at least 8 characters", Toast.LENGTH_LONG).show();
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(ActivityContents.editEmail.getText().toString()).matches()) {
            check=false;
            ActivityContents.editEmail.setBackgroundColor(getColor(R.color.backgroundError));
            Toast.makeText(this, "Insert a correct email", Toast.LENGTH_LONG).show();
        }
        return check;
    }


    public void attachListeners() {
        ActivityContents.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                SignUpActivity.this.finish();
            }
        });

        ActivityContents.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFields()) {
                    if (ActivityContents.isFilled()) {
                        //ritorna Activity precedente e crea entry in database, account e logga
                        Intent intent = new Intent();
                        Bundle extras = new Bundle();
                        extras.putString("Email", ActivityContents.editEmail.getText().toString());
                        extras.putString("Password", ActivityContents.passwordEdit.getText().toString());
                        intent.putExtras(extras);
                        SignUpActivity.this.setResult(RESULT_OK, intent);
                        SignUpActivity.this.finish();

                    }
                }
            }
        });


        ActivityContents.chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUpActivity.this, "Choose", Toast.LENGTH_LONG).show();
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PHOTO);

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == PHOTO) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                ActivityContents.imageView.setImageURI(selectedImage);
            }

        }
    }

}
