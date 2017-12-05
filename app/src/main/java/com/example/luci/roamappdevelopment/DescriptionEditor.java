package com.example.luci.roamappdevelopment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class DescriptionEditor extends AppCompatActivity {
    Toolbar myToolbar;
    FloatingActionButton checkButton;
    EditText textArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_editor);
        setToolbar();
        textArea = (EditText) findViewById(R.id.area_edit_description);
        checkButton = (FloatingActionButton) findViewById(R.id.floating_edit_description);
        checkButton.setBackgroundColor(R.color.colorPrimary);
        myToolbar.setTitle("Edit your description");
        textArea.setText(UserProfile.userProfileBio);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfile.userProfileBio = textArea.getText().toString();
                FirebaseDatabaseManager.getInstance().writeProfileBio();
                MainActivity.scaleAndSetProfileBio();
                finish();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void setToolbar()
    {
        myToolbar = (Toolbar) findViewById(R.id.toolbar_for_description_editor);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
