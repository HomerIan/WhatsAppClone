package com.homerianreyes.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSignUp;
    private TextView txtLogin;
    private EditText edtSignUpEmail;
    private EditText edtSignUpUsername;
    private EditText edtSignUpPassword;

    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Save the current Installation to Back4App
        ParseInstallation.getCurrentInstallation().saveInBackground();

        setTitle("Sign Up");

        edtSignUpEmail = findViewById(R.id.edtSignUpEmail);
        edtSignUpUsername = findViewById(R.id.edtSignUpUsername);
        edtSignUpPassword = findViewById(R.id.edtSignUpPassword);
        edtSignUpPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(btnSignUp);
                }

                return false;
            }
        });

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);
        txtLogin = findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(this);

        if(ParseUser.getCurrentUser() != null){
            //transitionToSocialMediaActivity();
        }

    }//onCreate

    @Override
    public void onClick(View view) {

        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){

            case R.id.btnSignUp:

                    if (edtSignUpEmail.getText().toString().equals("") ||
                        edtSignUpUsername.getText().toString().equals("") ||
                        edtSignUpPassword.getText().toString().equals("")){

                        FancyToast.makeText(SignUpActivity.this, "Fill Up the Form", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    } else {

                        final ParseUser appUser = new ParseUser();
                        appUser.setEmail(edtSignUpEmail.getText().toString());
                        appUser.setUsername(edtSignUpUsername.getText().toString());
                        appUser.setPassword(edtSignUpPassword.getText().toString());

                        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                        progressDialog.setMessage("Signing up " + edtSignUpUsername.getText().toString() + ", please wait...");
                        progressDialog.show();

                        appUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null) {
                                    FancyToast.makeText(SignUpActivity.this, "Sign up successfully!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,false).show();
                                    edtSignUpEmail.getText().clear();
                                    edtSignUpUsername.getText().clear();
                                    edtSignUpPassword.getText().clear();
                                    transitionToSocialMediaActivity();
                                } else {
                                    FancyToast.makeText(SignUpActivity.this, e.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,false).show();
                                }
                                progressDialog.dismiss();
                            }
                        });
                    }
                break;
            case R.id.txtLogin:
                transitionToLogInActivity();
                break;
        }
    }//onClick

    private void transitionToLogInActivity() {
        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    private void transitionToSocialMediaActivity() {
        Intent intent = new Intent(SignUpActivity.this, SocialMediaActivity.class);
        startActivity(intent);
        finish();
    }

    public void rootLayoutTapped(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }//rootLayoutTapped
}