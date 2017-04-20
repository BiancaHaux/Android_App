package com.example.biancahaux.optimapp;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class MedItem extends RealmObject {

    @PrimaryKey
    private String medItemTitle;
    private RealmList<FloatValue> quantity;
    private RealmList<StringValue> medItemDescriptions;
    private int creationDate;
    private FloatValue storedValue;

    public MedItem(){
        quantity = new RealmList<FloatValue>();
    }

    public MedItem(String title){
        this.medItemTitle = title;
    }

    public String getMedItemTitle() {
        return medItemTitle;
    }

    public void setMedItemTitle(String medItemTitle) {
        this.medItemTitle = medItemTitle;
    }

    public RealmList<StringValue> getMedItemDescriptions() {
        return medItemDescriptions;
    }

    public void setMedItemDescriptions(RealmList<StringValue> medItemDescriptions) {
        this.medItemDescriptions = medItemDescriptions;
    }

    public RealmList<FloatValue> getQuantity() {
        return quantity;
    }

    public void setQuantity(RealmList<FloatValue> quantity) {
        this.quantity = quantity;
    }

    public int getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(int creationDate) {
        this.creationDate = creationDate;
    }


    public FloatValue getStoredValue() {
        return storedValue;
    }

    public void setStoredValue(FloatValue storedValue) {
        this.storedValue = storedValue;
    }


}
