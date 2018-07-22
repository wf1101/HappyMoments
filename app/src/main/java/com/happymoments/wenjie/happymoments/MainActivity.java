package com.happymoments.wenjie.happymoments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.text.SimpleDateFormat;
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
    private ImageButton mDatePickerBtn;


    // Moment object parameters
    private SeekBar mSeekBar;
    private TextView mTextRate;
    private int mRateValue;
    private String mDate;
    private EditText mTextMoment;
    private String mPhotoUrl;
    private String mCurrentUserUid;
    private ImageView mDisplayPhoto;


    private Button mSendBtn;
    private ImageButton mPhotoPickedBtn;

    // default display date today
    private String mDisplayToday;

    // Firebase instance variables
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private StorageReference mStorage;

    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;

    // all checkboxes
    private CheckBox volunteer;
    private CheckBox movie;
    private CheckBox music;
    private CheckBox peace;
    private CheckBox outdoor;
    private CheckBox travel;
    private CheckBox dinner;
    private CheckBox food;
    private CheckBox garden;
    private CheckBox seattle;
    private CheckBox summer;
    private CheckBox home;
    private CheckBox solitude;
    private CheckBox ada;
    private CheckBox family;
    private CheckBox study;
    private CheckBox friends;
    private CheckBox books;

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
        mDisplayToday = (calendar.get(Calendar.MONTH ) + 1 )+ "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
        mDisplayDate.setText(mDisplayToday);

        // display the datepicker
        mDatePickerBtn = findViewById(R.id.date_pick_btn);
        mDatePickerBtn.setOnClickListener(new View.OnClickListener() {
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
        mPhotoPickedBtn = findViewById(R.id.photo_picker_btn);
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

                mDate = mDisplayDate.getText().toString();
                mTextMoment = findViewById(R.id.text_moment);
                String textMoment = mTextMoment.getText().toString();

                ArrayList<String> tagsList = new ArrayList<>();
                tagsList = getCheckboxList();

                if (mRateValue != 0 && mTextMoment.getText().toString() != null) {

                    mPhotoUrl = "https://firebasestorage.googleapis.com/v0/b/happymoments-51ef5.appspot.com/o/FYF-870.jpg?alt=media&token=fbc37fe8-fa73-45c2-9061-faece5ee9e7e";
                    Moment newMoment = new Moment(mDate, mRateValue, tagsList, textMoment, mPhotoUrl);

                    // write the object to database
                    mDatabase.child(mCurrentUserUid).push().setValue(newMoment);

                    // set home screen to load main activity fresh
//                Intent freshHome = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(freshHome);
                    mTextRate.setText(originalRateDisplayText);
                    mTextMoment.setText("");
                    mDisplayPhoto.setImageURI(null);
                    mDisplayDate.setText(mDisplayToday);
                    mSeekBar.setProgress(0);
                } else {
                    toastMessage("Oops! Please rate your happiness level and describe this moment : )");
                }

            }
        });

    }

    // Find all the checked boxes and get the name of box
     private ArrayList<String> getCheckboxList() {
          // all check boxes
         // String[] rawCheckbox = new String[]{"friends", "books", "study", "family", "home", "ada",
         // "solitude", "summer", "seattle", "garden", "dinner", "food", "travel", "outdoor", "peace", "volunteer", "music", "movie"};
         ArrayList<String> tagsList = new ArrayList<>();

         friends = findViewById(R.id.friends_box);
         if (friends.isChecked()){
             tagsList.add(friends.getText().toString());
         }

         books = findViewById(R.id.books_box);
         if (books.isChecked()){
             tagsList.add(books.getText().toString());

         }

         study = findViewById(R.id.study_box);
         if (study.isChecked()){
             tagsList.add(study.getText().toString());
         }

         family = findViewById(R.id.family_box);
         if (family.isChecked()){
             tagsList.add(family.getText().toString());
         }

         home = findViewById(R.id.home_box);
         if (home.isChecked()){
             tagsList.add(home.getText().toString());
         }

         ada = findViewById(R.id.ada_box);
         if (ada.isChecked()){
             tagsList.add(ada.getText().toString());
         }

         solitude = findViewById(R.id.solitude_box);
         if (solitude.isChecked()){
             tagsList.add(solitude.getText().toString());
         }

         summer = findViewById(R.id.summer_box);
         if (summer.isChecked()){
             tagsList.add(summer.getText().toString());
         }

         seattle = findViewById(R.id.seattle_box);
         if (seattle.isChecked()){
             tagsList.add(seattle.getText().toString());
         }

         garden = findViewById(R.id.garden_box);
         if (garden.isChecked()){
             tagsList.add(garden.getText().toString());
         }

         food = findViewById(R.id.food_box);
         if (food.isChecked()){
             tagsList.add(food.getText().toString());
         }

         dinner = findViewById(R.id.dinner_box);
         if (dinner.isChecked()){
             tagsList.add(dinner.getText().toString());
         }

         travel = findViewById(R.id.travel_box);
         if (travel.isChecked()){
             tagsList.add(travel.getText().toString());
         }

         outdoor = findViewById(R.id.outdoor_box);
         if (outdoor.isChecked()){
             tagsList.add(outdoor.getText().toString());
         }

         peace = findViewById(R.id.peace_box);
         if (peace.isChecked()){
             tagsList.add(peace.getText().toString());
         }

         music = findViewById(R.id.music_box);
         if (music.isChecked()){
             tagsList.add(music.getText().toString());
         }

         movie = findViewById(R.id.movie_box);
         if (movie.isChecked()){
             tagsList.add(movie.getText().toString());
         }

         volunteer = findViewById(R.id.voluteer_box);
         if (volunteer.isChecked()){
             tagsList.add(volunteer.getText().toString());
         }
         return  tagsList;
     }

     // set all check box to unchecked after sending new moment data
    private void unCheckBoxes() {
        volunteer.setChecked(false);
        movie.setChecked(false);
        music.setChecked(false);
        peace.setChecked(false);
        outdoor.setChecked(false);
        travel.setChecked(false);
        dinner.setChecked(false);
        food.setChecked(false);
        garden.setChecked(false);
        seattle.setChecked(false);
        summer.setChecked(false);
        solitude.setChecked(false);
        ada.setChecked(false);
        home.setChecked(false);
        family.setChecked(false);
        study.setChecked(false);
        friends.setChecked(false);
        books.setChecked(false);
    }

    // click profile button and go to profile screen
    public void goToProfile () {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    // click info button to get pop up info message
    public void goToInfo() {
        AlertDialog.Builder infoAlert = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.info_layout, null);
//        infoAlert.setMessage("this is d=showing a test dajda dada this is d=showing a test dajda dada ")
//                .setTitle("Lear what makes you happy")
//                .setIcon(R.drawable.ic_popup)
//                .create();
        infoAlert.setView(view);
        infoAlert.show();
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


    // Add  toolbar functions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                // log out function - use Firebase UI
                toastMessage("Successfully logged out");
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.profile_btn:
                goToProfile();
                return true;
            case R.id.info_btn:
                goToInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
