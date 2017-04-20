package com.example.biancahaux.optimapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class Login extends AppCompatActivity {

    private Realm optiMappRealm;
    private Account currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        RealmConfiguration config2 = new RealmConfiguration.Builder(this)
                .name("default2")
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();

        optiMappRealm = Realm.getInstance(config2);

        currentUser = optiMappRealm.where(Account.class)
                .equalTo("currentUser", true)
                .findFirst();


        final Button btnLogin = (Button) findViewById(R.id.btnActuallyLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText name = (EditText) findViewById(R.id.loginName);
                EditText password = (EditText) findViewById(R.id.loginPassword);


                RealmResults<Account> results = optiMappRealm.where(Account.class)
                        .equalTo("name", name.getText().toString())
                        .equalTo("password", password.getText().toString())
                        .findAll();


                Context context = getApplicationContext();
                int duration = Toast.LENGTH_LONG;

                    if(results.size() == 1){

                        optiMappRealm.beginTransaction();
                        RealmResults<Account> accounts =
                                optiMappRealm.where(Account.class).findAll();


                        for(int i= 0; i <accounts.size(); i++){
                            accounts.get(i).setCurrentUser(false);
                        }
                        results.first().setCurrentUser(true);

                        optiMappRealm.commitTransaction();

                        String text = "Logging in for " + results.first().getName().toString();
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        name.setText("");
                        password.setText("");
                        Intent loginIntent = new Intent(Login.this, DashboardActivity.class);
                        startActivity(loginIntent);
                    }
                    else if(results.size() == 0){

                            String text = "Account " + name.getText().toString() + " doesn't exist.";
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
            }
        });

        final Button btnCreateProfile = (Button) findViewById(R.id.btnCreateProfile);
        btnCreateProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent createIntent = new Intent(Login.this, CreateProfile.class);
                startActivity(createIntent);
            }
        });

        if(currentUser != null){
            Intent loginIntent = new Intent(Login.this, DashboardActivity.class);
            startActivity(loginIntent);
        }

    }


    @Override
    protected void onDestroy() {
        optiMappRealm.close();
        super.onDestroy();
    }
}
