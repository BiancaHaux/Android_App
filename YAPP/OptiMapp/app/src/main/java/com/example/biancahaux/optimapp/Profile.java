package com.example.biancahaux.optimapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Profile extends AppCompatActivity {

    private Realm optiMappRealm;
    private Account currentUser;
    private TextView name;
    private EditText notes;
    private EditText age;
    private EditText allergies;
    private CheckBox cbSmoker;
    private EditText preConditions;
    private Button btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        RealmConfiguration config2 = new RealmConfiguration.Builder(this)
                .name("default2")
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();

        optiMappRealm = Realm.getInstance(config2);

         currentUser = optiMappRealm.where(Account.class)
                .equalTo("currentUser", true)
                .findFirst();

        name = (TextView) findViewById(R.id.tvProfileName);
        notes = (EditText) findViewById(R.id.etProfileNotes);
        age = (EditText) findViewById(R.id.etProfileAge);
        allergies = (EditText) findViewById(R.id.etProfileAllergies);
        cbSmoker = (CheckBox) findViewById(R.id.cbSmoker);
        preConditions = (EditText) findViewById(R.id.etPreConditions);
        btnDeleteAccount = (Button) findViewById(R.id.btnDeleteAccount);

        name.setText(currentUser.getName());
        notes.setText(currentUser.getUserNotes());
        age.setText(currentUser.getAge());
        allergies.setText(currentUser.getUserAllergies());
        cbSmoker.setChecked(currentUser.isSmoker());
        preConditions.setText(currentUser.getPreConditions());

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optiMappRealm.beginTransaction();
                currentUser.removeFromRealm();
                optiMappRealm.commitTransaction();
                Intent profileIntent = new Intent(Profile.this, CreateProfile.class);
                startActivity(profileIntent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        optiMappRealm.beginTransaction();
        currentUser.setName(name.getText().toString());
        currentUser.setUserNotes(notes.getText().toString());
        currentUser.setAge(age.getText().toString());
        currentUser.setUserAllergies(allergies.getText().toString());
        currentUser.setIsSmoker(cbSmoker.isChecked());
        currentUser.setPreConditions(preConditions.getText().toString());
        optiMappRealm.commitTransaction();

        optiMappRealm.close();
        super.onDestroy();
    }
}