package com.example.biancahaux.optimapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class CreateProfile extends AppCompatActivity {

    public Realm optiMappRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);


        RealmConfiguration config2 = new RealmConfiguration.Builder(this)
                .name("default2")
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();

        optiMappRealm = Realm.getInstance(config2);


        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent loginIntent = new Intent(CreateProfile.this, Login.class);
                startActivity(loginIntent);
            }
        });

        final Button btnCreateProfile = (Button) findViewById(R.id.btnActuallyCreateProfile);
        btnCreateProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                optiMappRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        EditText name = (EditText) findViewById(R.id.editName);
                        EditText password = (EditText) findViewById(R.id.editPassword);


                        Account newAccount = realm.createObject(Account.class);
                        newAccount.setName(name.getText().toString());
                        newAccount.setPassword(password.getText().toString());

                        RealmResults<Account> results =
                                realm.where(Account.class).findAll();

                        ArrayList<Account> results1 = new ArrayList<Account>();
                        results1.addAll(results);

                        for(Account account : results1){
                            account.setCurrentUser(false);
                        }
                        newAccount.setCurrentUser(true);
                    }
                }, new Realm.Transaction.Callback(){
                        @Override
                        public void onSuccess(){
                            EditText name = (EditText) findViewById(R.id.editName);
                            EditText password = (EditText) findViewById(R.id.editPassword);
                            name.setText("");
                            password.setText("");
                            Intent createIntent = new Intent(CreateProfile.this, DashboardActivity.class);
                            startActivity(createIntent);
                        }

                        @Override
                        public void onError(Exception e){

                                Context context = getApplicationContext();
                                Toast toast = Toast.makeText(context, "Failed to create account. Account might already exist.", Toast.LENGTH_LONG);
                                toast.show();
                        }
                });

            }
        });

    }

    @Override
    protected void onDestroy() {
        optiMappRealm.close();
        super.onDestroy();
    }
}
