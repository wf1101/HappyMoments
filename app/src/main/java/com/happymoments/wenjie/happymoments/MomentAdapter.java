package com.happymoments.wenjie.happymoments;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MomentAdapter extends ArrayAdapter<Moment> {
    public MomentAdapter(Context context, int resource, List<Moment> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.moment_display, parent, false);
        }

        TextView date = convertView.findViewById(R.id.date_display);
        TextView text = convertView.findViewById(R.id.text_display);
        TextView rate = convertView.findViewById(R.id.rate_display);
        TextView tags = convertView.findViewById(R.id.tags_display);

        Moment newMoment = getItem(position);

        date.setText(newMoment.getmDate());
        text.setText(newMoment.getmEditText());
        rate.setText(newMoment.getmHappinessLevel() + "");

        String checkedTags = "" ;
        ArrayList<String> tagList = newMoment.getmCheckbox();
        for (String tag: tagList) {
            checkedTags += tag + " ";
        }
        tags.setText(checkedTags);

        return convertView;
    }
}
