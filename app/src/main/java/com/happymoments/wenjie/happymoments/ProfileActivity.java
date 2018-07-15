package com.happymoments.wenjie.happymoments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
//    private FirebaseDatabase mDatabase;
    private MomentAdapter mMomentAdapter;
    private ListView mMomentList;
    private DatabaseReference mDataReference;
    private ChildEventListener mChildEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // click home button and go to home screen
        final Button homeBtn = findViewById(R.id.home_btn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });

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
