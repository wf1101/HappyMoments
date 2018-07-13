package com.happymoments.wenjie.happymoments;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.client.AuthUiInitProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mLoginBtn;
    private Button mSignUpBtn;
    private TextView mStatus;

    // Firebase variable
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email_field);
        mPassword = findViewById(R.id.password_field);
        mLoginBtn = findViewById(R.id.login_btn);
        mSignUpBtn = findViewById(R.id.sign_up_btn);
        mStatus = findViewById(R.id.status_text);
        mAuth = FirebaseAuth.getInstance();
//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                mUser = firebaseAuth.getCurrentUser();
//                if (mUser != null){
//                    // user is signed in
//                } else {
//                    // user is signed out
//                    startActivityForResult(
//                            AuthUI.getInstance().createSignInIntentBuilder().setProviders(
//                                    AuthUI.EMAIL_PROV
//                            )
//                    );
//                }
//            }
//        };

    }


    // check on start check user
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
        }
    }

    // create a new user with email
    private void createAccount(String email, String password) {
        Log.d("creat account", email);

        // check if form is valid
        if (!validateForm()) {
            return;
        }

        // create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.v("sign up ", "success");
                    mUser = mAuth.getCurrentUser();
                } else {
                    Toast.makeText(LoginActivity.this, "Auth failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // existing user sign in
    private void signIn(String email, String password) {
        Log.v("sign in", email);
        if (!validateForm()) {
            return;
        }

        // sign in with email
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.v("sign in: ", "success");
                    mUser = mAuth.getCurrentUser();
                } else {
                    Log.v("sign in: ", "failed");
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }

                if (!task.isSuccessful()) {
                    mStatus.setText(R.string.auth_failed);
                }

            }
        });
    }


    // sign out user
    private void signOut() {
        mAuth.signOut();
    }


    // check if form is valid
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    public void onClick(View view){
//        int i = view.getId();
//        if (i == R.id.login_btn) {
//            createAccount(mEmail.getText().toString(), mPassword.getText().toString());
//        } else if (i == R.id.sign_up_btn) {
//            signIn(mEmail.getText().toString(), mPassword.getText().toString());
//        }

        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
}
