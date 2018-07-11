package com.happymoments.wenjie.happymoments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private SeekBar mSeekBar;
    private TextView mTextRate;
    private int mRateValue;
    private Date mDate;
    private EditText mTextMoment;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        Moment newMoment = new Moment(mDate, mRateValue, tagsList, textMoment);
    }
}
