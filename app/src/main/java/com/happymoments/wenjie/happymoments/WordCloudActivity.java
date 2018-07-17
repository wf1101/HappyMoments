package com.happymoments.wenjie.happymoments;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordCloudActivity extends AppCompatActivity {
    private DatabaseReference mDataReference;
    private ArrayList<Moment> mMomentList = new ArrayList<>();
    private ChildEventListener mChildEventListener;
    private ArrayList<String> mWords;
    private String[] wordCloud;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_cloud);

//        // get all moments data from database
//        mMomentList = new ArrayList<>();

//        mChildEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Moment newMoment = dataSnapshot.getValue(Moment.class);
//                mMomentList.add(newMoment);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };

//        ValueEventListener momentListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Moment m = dataSnapshot.getValue(Moment.class);
//                mMomentList.add(m);
//                System.out.println("moment-123" + m.getmEditText());
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };

//        String childComponent = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        mDataReference = FirebaseDatabase.getInstance().getReference().child(childComponent);

//        mDataReference.addValueEventListener(momentListener);
        // Initialize message ListView and its adapter
//        mMomentList = findViewById(R.id.moment_list);
//        List<Moment> myMoments = new ArrayList<>();
//        mMomentAdapter = new MomentAdapter(this, R.layout.moment_display, myMoments);
//        mMomentList.setAdapter(mMomentAdapter);

        System.out.println("dadajda: " + getIntent().getExtras().getParcelable("word"));
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Moment newMoment = dataSnapshot.getValue(Moment.class);
                System.out.println("momo: " + newMoment.getmEditText());
                mMomentList.add(newMoment);
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

        // convert moments data into arraylist then to array
        mWords = new ArrayList<>();
        for (Moment m: mMomentList) {
           String[] oneMoment = m.getmEditText().split(" ");
            mWords.addAll(Arrays.asList(oneMoment));
        }

//        wordCloud = mWords.toArray(new String[mWords.size()]);
        wordCloud = new String[]{ "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb",
                "Ice Cream Sandwich", "Jelly Bean", "KitKat", "love", "Alex", "my", "hubby", "Lollipop", "Marshmallow"};



        final WebView d3 = (WebView) findViewById(R.id.d3);

        WebSettings ws = d3.getSettings();
        ws.setJavaScriptEnabled(true);
        d3.loadUrl("file:///android_asset/d3.html");
        d3.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                StringBuffer sb = new StringBuffer();
                sb.append("wordCloud([");
                for (int i = 0; i < wordCloud.length; i++) {
                    sb.append("'").append(wordCloud[i]).append("'");
                    if (i < wordCloud.length - 1) {
                        sb.append(",");
                    }
                }
                sb.append("])");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    d3.evaluateJavascript(sb.toString(), null);
                } else {
                    d3.loadUrl("javascript:" + sb.toString());
                }
            }
        });

    }

}
