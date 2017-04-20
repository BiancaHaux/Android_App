package com.example.biancahaux.optimapp;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bianca Haux on 07.05.2016.
 */
public class Account extends RealmObject{

    @PrimaryKey
    private String name;
    private String password;
    private boolean currentUser;
    private RealmList<Event> userEvents;
    private RealmList<MedItem> userMedicationList;
    private String userNotes;
    private String userAllergies;
    private String age;
    private boolean isSmoker;
    private String preConditions;

    public Account(){}

    public Account (String _name, String _password, boolean _currentUser){
        setName(_name);
        setPassword(_password);
        setCurrentUser(_currentUser);
        userEvents = new RealmList<Event>();
    }


    public void setName(String _name){
        this.name = _name;
    }
    public String getName(){
        return this.name;
    }

    public void setPassword(String _password){
        this.password = _password;
    }
    public String getPassword(){
        return this.password;
    }

    public boolean isCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        this.currentUser = currentUser;
    }


    public RealmList<Event> getUserEvents() {
        return userEvents;
    }

    public void setUserEvents(RealmList<Event> userEvents) {
        this.userEvents = userEvents;
    }

    public String getUserAllergies() {
        return userAllergies;
    }

    public void setUserAllergies(String userAllergies) {
        this.userAllergies = userAllergies;
    }

    public String getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }


    public RealmList<MedItem> getUserMedicationList() {
        return userMedicationList;
    }

    public void setUserMedicationList(RealmList<MedItem> userMedicationList) {
        this.userMedicationList = userMedicationList;
    }

    public boolean isSmoker() {
        return isSmoker;
    }

    public void setIsSmoker(boolean isSmoker) {
        this.isSmoker = isSmoker;
    }

    public String getPreConditions() {
        return preConditions;
    }

    public void setPreConditions(String preConditions) {
        this.preConditions = preConditions;
    }


}
