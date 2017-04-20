package com.example.biancahaux.optimapp;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;


@RealmClass
public class Event extends RealmObject {

    @PrimaryKey
    private int id;
    private String eventTitle;
    private String eventDescription;
    private long eventDate;
    private boolean notifyUser;

    public Event(){};

    public Event(String _title, String _description, long _date, boolean _notifyUser){
        setEventTitle(_title);
        setEventDescription(_description);
        setEventDate(_date);
        setNotifyUser(_notifyUser);
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }


    public boolean isNotifyUser() {
        return notifyUser;
    }

    public void setNotifyUser(boolean notifyUser) {
        this.notifyUser = notifyUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



}
