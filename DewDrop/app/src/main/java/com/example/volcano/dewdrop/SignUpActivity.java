package com.example.volcano.dewdrop;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SignUpActivity extends Activity implements Linkable{

    static class ActivityContents{}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @Override
    public void linkViews() {

    }
}
