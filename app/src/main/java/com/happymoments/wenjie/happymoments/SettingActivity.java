package com.happymoments.wenjie.happymoments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {
    private String mUserName;
    private String mUserEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // add action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        // display current user's name and email
        mUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        EditText name = findViewById(R.id.setting_name_field);
        EditText email = findViewById(R.id.setting_email_field);
        name.setText(mUserName);
        email.setText(mUserEmail);

        // confirm button to update user name and email
        Button confirm = findViewById(R.id.confirm_btn);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = findViewById(R.id.setting_name_field);
                String newName = name.getText().toString();
                if (mUserName != newName ) {
                    mUserName = newName;
                }

                EditText email = findViewById(R.id.setting_email_field);
                String newEmail= email.getText().toString();
                if (mUserEmail != newEmail ) {
                    mUserEmail = newEmail;
                }

                toastMessage("Changes saved");

            }
        });


    }

    // toolbar options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Add log out function - use Firebase UI
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        toastMessage("log out");
        switch (item.getItemId()){
            case R.id.log_out:
                // sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
