package com.happymoments.wenjie.happymoments;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.DOMStringList;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
//    private FirebaseDatabase mDatabase;
//    private MomentAdapter mMomentAdapter;
//    private ListView mMomentList;
    private DatabaseReference mDataReference;
    private ChildEventListener mChildEventListener;
    private String mUserName;
    private String mUserEmail;

    // pass wordCloud data to wordcloud screen
    private ArrayList<Moment> mMoments = new ArrayList<>();
    private ArrayList<String> mWords = new ArrayList<>();
    private String[] wordCloud;

    // User's posted moments and scores
    private int mNumbersOfMoments = 0;
    private int mTotalScores = 0;
    private TextView mNumberMoments;
    private TextView mTotalPoints;

    // find top 5 tags
    private HashMap<String, Integer> mTagPoints = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // add action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        // initialize and display current user name and email
        mUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        TextView name = findViewById(R.id.user_name);
        TextView email = findViewById(R.id.user_email);
        name.setText(mUserName);
        email.setText(mUserEmail);

        // dislay number of moments and total points
        mNumberMoments = findViewById(R.id.numbers_moments_view);
        mTotalPoints = findViewById(R.id.points_display_view);


        // click arrow button and go to setting page
        final ImageButton settingBtn = findViewById(R.id.go_setting);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(ProfileActivity.this, SettingActivity.class);
                startActivity(settingIntent);
            }
        });


        // click wordcloud btn to go to word cloud screen
        final Button wordCloudBtn = findViewById(R.id.word_cloud_btn);
        wordCloudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wordIntent = new Intent(ProfileActivity.this, WordCloudActivity.class);
                wordIntent.putStringArrayListExtra("wordCloud", mWords);
                startActivity(wordIntent);
            }
        });

        // Click topmoment btn to go to Top Moment screen
        final Button topMomentBtn = findViewById(R.id.bar_chart_btn);
        topMomentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent barChartIntent = new Intent(ProfileActivity.this, BarChartActivity.class);
                HashMap<String, Integer> topTags = findTopTags();
                barChartIntent.putExtra("hashmap", topTags);
                startActivity(barChartIntent);

            }
        });

        // click histoty btn to go to history screen
        final Button history = findViewById(R.id.history_btn);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(ProfileActivity.this, HistoryActivity.class);
                startActivity(historyIntent);
            }
        });


        // Initialize message ListView and its adapter
//        mMomentList = findViewById(R.id.moment_list);
//        List<Moment> myMoments = new ArrayList<>();
//        mMomentAdapter = new MomentAdapter(this, R.layout.moment_display, myMoments);
//        mMomentList.setAdapter(mMomentAdapter);

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Moment newMoment = dataSnapshot.getValue(Moment.class);
//                mMomentAdapter.add(newMoment);
                String[] oneMoment = newMoment.getmEditText().split(" ");
                mWords.addAll(Arrays.asList(oneMoment));

                // get numbers of moments and total score of current user
                mTotalScores += newMoment.getmHappinessLevel();
                mNumbersOfMoments += 1;
                mNumberMoments.setText("Moments: " + mNumbersOfMoments);
                mTotalPoints.setText("Happy Points: " + mTotalScores);

                // write all the tags and points to hashmap
                ArrayList<String> newTags = newMoment.getmCheckbox();
                for (String tag: newTags) {
                    if (mTagPoints.containsKey(tag)) {
                        int points = mTagPoints.get(tag);
                        int updatePoints = points + newMoment.getmHappinessLevel();
                        mTagPoints.put(tag, updatePoints);
                    } else {
                        mTagPoints.put(tag, 0);
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        String childComponent = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDataReference = FirebaseDatabase.getInstance().getReference().child(childComponent);
        mDataReference.addChildEventListener(mChildEventListener);

    }


    // toolbar options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // click home button and go to home screen
    public void goToHome() {
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
    }

    // click info button and popup info view
    public void goToInfo() {
        AlertDialog.Builder infoAlert = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.info_layout, null);
        infoAlert.setView(view);
        infoAlert.show();
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
            case R.id.home_btn:
                goToHome();
                return true;
            case R.id.info_btn:
                goToInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // Find top 5 tags
    private HashMap findTopTags() {
        HashMap topTags = new HashMap();


        return topTags;
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
