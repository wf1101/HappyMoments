package com.happymoments.wenjie.happymoments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // TAG
    private static final String TAG = "This is main activity";

    // datepicker variables
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateListener;


    // Moment object parameters
    private SeekBar mSeekBar;
    private TextView mTextRate;
    private int mRateValue;
    private Date mDate;
    private EditText mTextMoment;
    private String mPhotoUrl;
    private String mCurrentUserUid;
    private ImageView mDisplayPhoto;


    private Button mSendBtn;
    private Button mPhotoPickedBtn;


    // Firebase instance variables
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private StorageReference mStorage;

    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;

//    private ArrayList<String> mTagList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        // set the date display to today
        mDisplayDate = findViewById(R.id.text_date_field);

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        String displayToday = calendar.get(Calendar.DAY_OF_MONTH)+ "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
        mDisplayDate.setHint(displayToday);

        // display the datepicker
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_DarkActionBar,
                        mDateListener,
                        year, month, day
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }


        });

        // datelistener for datepicker
        mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        // check if user is logged in
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null){
                    // user is signed in
                    mCurrentUserUid = mUser.getUid();
//                    Toast.makeText(MainActivity.this, "You're now signed in! Welcome!", Toast.LENGTH_SHORT).show();
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

        // Initialize Firebase components
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        // Initialize buttons

        mDisplayPhoto = findViewById(R.id.photo_view);


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

         // click imagePickedButton to pick a picture
        mPhotoPickedBtn =findViewById(R.id.photo_picker_btn);
        mPhotoPickedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/*");
//                imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(imageIntent, "completing action using"), RC_PHOTO_PICKER);
            }
        });




         // click send button to create a new data entry to database
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
                mPhotoUrl = "https://firebasestorage.googleapis.com/v0/b/happymoments-51ef5.appspot.com/o/FYF-870.jpg?alt=media&token=fbc37fe8-fa73-45c2-9061-faece5ee9e7e";
                Moment newMoment = new Moment(mDate, mRateValue, tagsList, textMoment, mPhotoUrl);

                // write the object to database
                mDatabase.child(mCurrentUserUid).push().setValue(newMoment);
                mTextRate.setText(originalRateDisplayText);
                mTextMoment.setText("");
                mDisplayPhoto.setImageURI(null);

            }
        });

    }

    // click profile button and go to profile screen
    public void goToProfile () {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    // click more button to go to checkbox layout
    public void goToMoreCheckbox(View v) {
        setContentView(R.layout.activity_checkbox);
    }

    // click save button to go back to home layout
    public void goBackHome (View v) {
        setContentView(R.layout.activity_main);
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
        if (requestCode == RC_SIGN_IN ) {
            if (resultCode == RESULT_OK) {
                // sign-in success, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // sign-in canceled by user, finish activity, close app
                Toast.makeText(this, "Sign in canceld!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            // picked image success, then store data
            Uri selectedImage = data.getData();

            // display photo on the imageview
            mDisplayPhoto.setImageURI(selectedImage);

            // get a reference to store file at user_UID/<filename>
            StorageReference photoRef = mStorage.child(mCurrentUserUid).child(selectedImage.getLastPathSegment());
            Log.v("storage red", photoRef + "");
            // upload file to firebase storage
            // TODO: 7/16/18  Cannot upload imgae to firebase
//            photoRef.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    toastMessage("Upload done!");
//                }
//            });



          photoRef.putFile(selectedImage)
                 .addOnSuccessListener(MainActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                          toastMessage("Upload done!");
//                                mPhotoUrl = taskSnapshot.getDownloadUrl();
                      }
                   });
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
        switch (item.getItemId()){
            case R.id.log_out:
                // sign out
                toastMessage("log out");
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.profile_btn:
                goToProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
