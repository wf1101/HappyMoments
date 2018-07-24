package com.happymoments.wenjie.happymoments;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    //    private FirebaseDatabase mDatabase;
    private MomentAdapter mMomentAdapter;
    private ListView mMomentList;
    private DatabaseReference mDataReference;
    private ChildEventListener mChildEventListener;

    // pass wordCloud data to wordcloud screen
    private ArrayList<Moment> mMoments = new ArrayList<>();
    private ArrayList<String> mWords = new ArrayList<>();
    private String[] wordCloud;

    // tags list
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // set the statusbar color to colorPrimaryDark
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#0f0805"));

        // Initialize message ListView and its adapter
        mMomentList = findViewById(R.id.moment_list);
        List<Moment> myMoments = new ArrayList<>();
        mMomentAdapter = new MomentAdapter(this, R.layout.moment_display, myMoments);
        mMomentList.setAdapter(mMomentAdapter);

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Moment newMoment = dataSnapshot.getValue(Moment.class);
                mMomentAdapter.add(newMoment);
                String[] oneMoment = newMoment.getmEditText().split(" ");
                mWords.addAll(Arrays.asList(oneMoment));

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
}
