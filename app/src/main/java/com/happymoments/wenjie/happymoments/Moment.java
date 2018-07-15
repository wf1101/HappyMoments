package com.happymoments.wenjie.happymoments;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;


public class Moment {
    private String mEditText;
    private int mHappinessLevel;
    private ArrayList<String> mCheckbox;
    private Date mDate;
    private String mPhotoUrl;

    // default constructor takes no parameters - for firebase purpose
    public Moment() {
    }

    public Moment(Date date, int happinessLevel, ArrayList<String> checkbox, String editText, String photoUrl) {
        mDate = date;
        mHappinessLevel = happinessLevel;
        mCheckbox = checkbox;
        mEditText = editText;
        mPhotoUrl = photoUrl;
    }

    public int getmHappinessLevel() {
        return mHappinessLevel;
    }

    public ArrayList<String> getmCheckbox() {
        return mCheckbox;
    }

    public String getmEditText() {
        return mEditText;
    }

    public Date getmDate(){
        return mDate;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

}
