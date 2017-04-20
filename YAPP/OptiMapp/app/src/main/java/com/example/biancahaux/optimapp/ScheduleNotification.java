package com.example.biancahaux.optimapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ScheduleNotification extends Activity {
    private Realm optiMappRealm;
    Account currentUser;
    private String eventTitle;
    private Event currentEvent;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmConfiguration config2 = new RealmConfiguration.Builder(this)
                .name("default2")
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();

        optiMappRealm = Realm.getInstance(config2);

        currentUser = optiMappRealm.where(Account.class)
                .equalTo("currentUser", true)
                .findFirst();
        eventTitle = getIntent().getStringExtra("eventName");
        currentEvent = currentUser.getUserEvents().where().equalTo("eventTitle", eventTitle).findFirst();
        scheduleNotification(getNotification());

    }


    protected void alarmManager(PendingIntent pendingIntent) {

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long dateInMillis = currentUser.getUserEvents().where().equalTo("eventTitle", eventTitle).findFirst().getEventDate();

        am.set(AlarmManager.RTC_WAKEUP, dateInMillis, pendingIntent);

    }

    private void scheduleNotification(Notification notification) {

        Intent notificationIntent = new Intent(this, MyReceiver.class);
        notificationIntent.putExtra(MyReceiver.NOTIFICATION_ID, currentEvent.getId());
        notificationIntent.putExtra(MyReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) currentEvent.getId(),
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager(pendingIntent);
        this.finish();
    }


    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(currentEvent.getEventTitle())
                .setContentText(currentEvent.getEventDescription()).setTicker("Reminder!")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(displayNotification()).setAutoCancel(true)
                .setSmallIcon(R.drawable.logo);
        return builder.build();
    }

    private PendingIntent displayNotification() {

        Intent myIntent = new Intent(this, CalendarActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, myIntent, 0);
        return pendingIntent;

    }


}