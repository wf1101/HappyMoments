package com.happymoments.wenjie.happymoments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private SeekBar mSeekBar;
    private TextView mTextRate;
    private int mRateValue;
    private Date mDate;
    private EditText mTextMoment;
    private Button mSendBtn;

    // Firebase instance variables
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        // Initialize Firebase components
        mDatabase = FirebaseDatabase.getInstance().getReference().child("moment");


        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null){
                    // user is signed in
                    Toast.makeText(MainActivity.this, "You're now signed in! Welcome!", Toast.LENGTH_SHORT).show();
                } else {
                    // user is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


        // click profile button and go to profile screen
        final Button profileBtn = findViewById(R.id.profile_btn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        // logic for seekBar to display progress level out of 10
        mSeekBar = findViewById(R.id.seek_bar_rate);
        mTextRate = findViewById(R.id.text_display_rate);
        final String originalRateDisplayText = mTextRate.getText().toString();
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRateValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String display = mRateValue + "/" + seekBar.getMax();
                mTextRate.setText(display);
            }
        });

        // click send button to send data to database
        mSendBtn = findViewById(R.id.send_btn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create a new Moment object takes four parameters
                // @param Date mDate
                // @param int mRateValue
                // @param ArrayList<String> tagsList
                // @param String mTextMoment
                mDate = new Date();
                mTextMoment = findViewById(R.id.text_moment);
                String textMoment = mTextMoment.getText().toString();

                ArrayList<String> tagsList = new ArrayList<>();
                // TODO: 7/11/18 how to find all checked boxes
                CheckBox boxFamily = findViewById(R.id.box_family);
                if (boxFamily.isChecked()){
                    tagsList.add(boxFamily.getText().toString());
                }

                CheckBox boxBooks = findViewById(R.id.box_books);
                if (boxBooks.isChecked()){
                    tagsList.add(boxBooks.getText().toString());
                }

                CheckBox boxFood = findViewById(R.id.box_food);
                if (boxFood.isChecked()){
                    tagsList.add(boxFood.getText().toString());
                }

                // create a new instance of Moment
                Moment newMoment = new Moment(mDate, mRateValue, tagsList, textMoment);

                // write the object to database
                mDatabase.push().setValue(newMoment);
                mTextRate.setText(originalRateDisplayText);
                mTextMoment.setText("");

            }
        });



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

    // the back button can close the app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN ){
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceld!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // toolbar options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Add log out function - use Firebase UI
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        toastMessage("log out");
        switch (item.getItemId()){
            case R.id.log_out:
                // sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
