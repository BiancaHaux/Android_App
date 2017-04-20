package com.example.biancahaux.optimapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        final Button btnProfile = (Button) findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent profileIntent = new Intent(DashboardActivity.this, Profile.class);
                startActivity(profileIntent);
            }
        });


        final Button btnCalendar = (Button) findViewById(R.id.btnCalendar);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent calendarIntent = new Intent(DashboardActivity.this, CalendarActivity.class);
                startActivity(calendarIntent);
            }
        });


        final Button btnMedication = (Button) findViewById(R.id.btnMedication);
        btnMedication.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent medicationIntent = new Intent(DashboardActivity.this, Medication.class);
                startActivity(medicationIntent);
            }
        });


    }

}
