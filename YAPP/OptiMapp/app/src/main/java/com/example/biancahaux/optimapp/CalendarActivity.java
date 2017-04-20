package com.example.biancahaux.optimapp;

import android.app.Application;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.Format;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class CalendarActivity extends AppCompatActivity {

    public final static String
            DIALOG_TITLE = "dialogTitle",
            MONTH = "month",
            YEAR = "year",
            SHOW_NAVIGATION_ARROWS = "true",
            DISABLE_DATES = "disableDates",
            SELECTED_DATES = "selectedDates",
            MIN_DATE = "minDate",
            MAX_DATE = "maxDate",
            ENABLE_SWIPE = "enableSwipe",
            START_DAY_OF_WEEK = "startDayOfWeek",
            SIX_WEEKS_IN_CALENDAR = "sixWeeksInCalendar",
            ENABLE_CLICK_ON_DISABLED_DATES = "enableClickOnDisabledDates",
            SQUARE_TEXT_VIEW_CELL = "squareTextViewCell",
            THEME_RESOURCE = "themeResource";

    protected boolean enableSwipe = true;
    protected boolean showNavigationArrows = true;
    private RecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private Realm optiMappRealm;
    private String currentEventTitle;
    private static CalendarActivity instance;
    private ArrayList<Event> eventList = new ArrayList<>();

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        instance = this;

        RealmConfiguration config2 = new RealmConfiguration.Builder(this)
                .name("default2")
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();

        optiMappRealm = Realm.getInstance(config2);

        final Account currentUser = optiMappRealm.where(Account.class)
                .equalTo("currentUser", true)
                .findFirst();

        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));

        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);

        args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);


        args.putBoolean(SHOW_NAVIGATION_ARROWS, showNavigationArrows);
        args.putBoolean(ENABLE_SWIPE, enableSwipe);

        HashMap<Date, Integer> map = new HashMap<>();
        RealmList<Event> list = currentUser.getUserEvents();

        for(Event event : list){
            map.put(new Date(event.getEventDate()), R.color.event);
        }

        caldroidFragment.setTextColorForDates(map);

        caldroidFragment.refreshView();
        caldroidFragment.setArguments(args);


        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();



        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date _date, View view) {
                final Calendar date = Calendar.getInstance();
                date.setTime(_date);

                final View dialogLayout = View.inflate(CalendarActivity.this, R.layout.event_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                builder.setView(dialogLayout);

                final AlertDialog dialog = builder.create();
                dialog.show();

                Button btnCreateEvent = (Button) dialogLayout.findViewById(R.id.tvCreateEvent);
                Button btnAbortEvent = (Button) dialogLayout.findViewById(R.id.tvAbortEvent);
                TextView eDate = (TextView) dialogLayout.findViewById(R.id.tvEventDate);
                SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
                eDate.setText(format.format(date.getTime()));

            btnCreateEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    optiMappRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            Event event = realm.createObject(Event.class);

                            EditText eventTitle = (EditText) dialogLayout.findViewById(R.id.tvEventTitle);
                            EditText eventDescription = (EditText) dialogLayout.findViewById(R.id.tvEventDescription);
                            TimePicker eventTime = (TimePicker) dialog.findViewById(R.id.timePicker1);
                            CheckBox notify = (CheckBox) dialog.findViewById(R.id.cbNotify);
                            eventTime.is24HourView();
                            Calendar eventDate = date;
                            boolean notifyUser = notify.isChecked();

                            int year = date.get(Calendar.YEAR);
                            int month= date.get(Calendar.MONTH);
                            int day = date.get(Calendar.DAY_OF_MONTH);
                            int hour = eventTime.getCurrentHour();
                            int minute = eventTime.getCurrentMinute();
                            int second = 0;

                            date.set(year, month, day, hour, minute, second);
                            long dateInMillis = date.getTimeInMillis();

                            event.setEventTitle(eventTitle.getText().toString());
                            event.setEventDescription(eventDescription.getText().toString());
                            event.setEventDate(dateInMillis);
                            event.setNotifyUser(notifyUser);
                            event.setId((int) System.currentTimeMillis());

                            currentEventTitle = eventTitle.getText().toString();

                        }
                    }, new Realm.Transaction.Callback() {
                        @Override
                        public void onSuccess() {

                            //Add the new event to the current user
                            optiMappRealm.beginTransaction();

                            RealmList<Event> listEvents = currentUser.getUserEvents();
                            Event event = optiMappRealm.where(Event.class).equalTo("eventTitle", currentEventTitle).findFirst();
                            listEvents.add(event);
                            optiMappRealm.commitTransaction();

                            if(event.isNotifyUser()) {
                                Intent intent = new Intent(getApplicationContext(), ScheduleNotification.class);
                                intent.putExtra("eventName", currentEventTitle);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Alarm has been set!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Event has been added to the list!", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();

                        }

                        @Override
                        public void onError(Exception e) {
                            Context context = getApplicationContext();
                            Toast toast = Toast.makeText(context, "Failed creating event, might exist already.", Toast.LENGTH_SHORT);
                            toast.show();
                            dialog.dismiss();
                        }
                    });
                }
            });

            btnAbortEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = "Aborted";
                    Toast.makeText(getApplicationContext(), text,
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

        }

            @Override
            public void onChangeMonth(int month, int year) {
            }

            @Override
            public void onLongClickDate(Date date, View view) {
            }

            @Override
            public void onCaldroidViewCreated() {
            }

        };

        caldroidFragment.setCaldroidListener(listener);



        RealmList<Event> events = currentUser.getUserEvents();


        RecyclerListAdapter recyclerAdapter = new RecyclerListAdapter(events, this.getApplicationContext(), caldroidFragment);
        mRecyclerView = (RecyclerView) findViewById(R.id.calendar_recycler_view);
        mRecyclerView.setAdapter(recyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(recyclerAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

    }
    @Override
    protected void onDestroy() {
        optiMappRealm.close();
        super.onDestroy();
    }

}
