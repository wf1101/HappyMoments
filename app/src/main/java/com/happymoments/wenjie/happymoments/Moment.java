package com.happymoments.wenjie.happymoments;

import java.util.ArrayList;
import java.util.Date;


public class Moment {
    private String mEditText;
    private int mHappinessLevel;
    private ArrayList<String> mCheckbox;
    private Date mDate;

    // default constructor takes no parameters - for firebase purpose
    public Moment() {
    }

    public Moment(Date date, int happinessLevel, ArrayList<String> checkbox, String editText) {
        mDate = date;
        mHappinessLevel = happinessLevel;
        mCheckbox = checkbox;
        mEditText = editText;
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


}
