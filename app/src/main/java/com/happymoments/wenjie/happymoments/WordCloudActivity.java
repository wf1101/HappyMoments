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
    private ArrayList<String> mWords = new ArrayList<>();
    private String[] wordCloud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_cloud);

        Bundle b = getIntent().getExtras();
        mWords = b.getStringArrayList("wordCloud");
        for (String w: mWords) {
        }

//        String[] out = (String[]) (in.subList(0, n)).toArray();
//        wordCloud = (String[]) (mWords.subList(0, 20)).toArray();
        wordCloud = mWords.toArray(new String[mWords.size()]);
//        wordCloud = new String[]{ "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb",
//                "Ice Cream Sandwich", "Jelly Bean", "KitKat", "love", "Alex",
//                "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb","my", "hubby", "Lollipop", "Marshmallow"};


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
