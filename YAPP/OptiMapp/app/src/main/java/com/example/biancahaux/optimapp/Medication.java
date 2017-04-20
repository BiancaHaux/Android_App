package com.example.biancahaux.optimapp;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.*;
import java.lang.Integer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;

public class Medication extends AppCompatActivity {

    private Realm optiMappRealm;
    private String currentMedicationTitle;
    private RecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    Account currentUser;
    private LineData data;
    private ArrayList<String> labels;
    private RealmList<MedItem> listMedications;
    private boolean flagExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RealmConfiguration config2 = new RealmConfiguration.Builder(this)
                .name("default2")
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();

        optiMappRealm = Realm.getInstance(config2);

        currentUser = optiMappRealm.where(Account.class)
                .equalTo("currentUser", true)
                .findFirst();
        listMedications= currentUser.getUserMedicationList();

        LineChart lineChart = (LineChart) findViewById(R.id.chart);


        RealmList<MedItem> medItems = currentUser.getUserMedicationList();
        labels = new ArrayList<String>();


        ArrayList<Entry> entries1 = new ArrayList<>();

        int counter = 0;
        for(MedItem med : medItems){
            RealmList<FloatValue> quantities = med.getQuantity();
            int index = 0;
            for(FloatValue quant : quantities){
                index++;
            }
            if(counter<index){
                counter=index;
            }
        }

        for(int i = 0; i<counter; i++){
            labels.add("");
        }

        LineDataSet dataset1 = new LineDataSet(entries1, "");
        data = new LineData(labels, dataset1);
        int colorIndex = 0;

        for(MedItem med : medItems){
            colorIndex++;
            int index=0;
            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            RealmList<FloatValue> quantities = med.getQuantity();

            for(FloatValue quant : quantities){

                entries.add(new Entry(quant.getFloatValue(), index));
                labels.add(String.valueOf(index));
                index++;
            }

            LineDataSet set = new LineDataSet(entries, med.getMedItemTitle());
            int color=0;
            switch(colorIndex){
                case 0:  color = Color.BLUE;
                    break;
                case 1:  color = Color.RED;
                    break;
                case 2:  color = Color.GREEN;
                    break;
                case 3:  color = Color.MAGENTA;
                    break;
                case 4:  color = Color.YELLOW;
                    break;
                case 5:  color = Color.LTGRAY;
                    break;
                case 6:  color = Color.BLACK;
                    break;
            }
            set.setColor(color);
            data.addDataSet(set);
        }


        lineChart.setData(data);
        lineChart.setDescription("Medication");
        lineChart.setPinchZoom(true);
        lineChart.setBackgroundColor(getResources().getColor(R.color.primaryLight));
        lineChart.notifyDataSetChanged();





        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogLayout = View.inflate(Medication.this, R.layout.medication_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(Medication.this, android.R.style.Theme_Material_Dialog_Alert);
                builder.setView(dialogLayout);

                final AlertDialog dialog = builder.create();
                dialog.show();

                Button btnCreateEvent = (Button) dialogLayout.findViewById(R.id.medCreateEvent);
                Button btnAbortEvent = (Button) dialogLayout.findViewById(R.id.medAbortEvent);

                btnCreateEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView medTitle = (TextView) dialogLayout.findViewById(R.id.tvMedTitle);
                        TextView medQuantity = (TextView) dialogLayout.findViewById(R.id.tvMedQuantity);
                        TextView medDescription = (TextView) dialogLayout.findViewById(R.id.editMedDescription);
                        currentMedicationTitle = medTitle.getText().toString();
                        MedItem item = null;

                        optiMappRealm.beginTransaction();
                        FloatValue value = new FloatValue();
                        value.setFloatValue(Float.valueOf(medQuantity.getText().toString()));
                        StringValue stringValue = new StringValue();
                        stringValue.setStringValue(medDescription.getText().toString());
                        optiMappRealm.commitTransaction();

                        for (MedItem itemM : listMedications) {
                            String string1 = itemM.getMedItemTitle().toString();
                            String string2 = medTitle.getText().toString();
                            boolean isTrue = string1.equals(string2);
                            if (isTrue) {
                                flagExists = true;
                                item = itemM;
                            }
                        }
                        if (flagExists) {
                            optiMappRealm.beginTransaction();
                            RealmList<FloatValue> quantList = item.getQuantity();
                            quantList.add(value);
                            RealmList<StringValue> stringList = item.getMedItemDescriptions();
                            stringList.add(stringValue);
                            optiMappRealm.commitTransaction();
                        }

                        if (!flagExists) {
                            optiMappRealm.beginTransaction();
                            MedItem medItem = optiMappRealm.createObject(MedItem.class);
                            medItem.setMedItemTitle(medTitle.getText().toString());
                            RealmList<StringValue> stringList = medItem.getMedItemDescriptions();
                            stringList.add(stringValue);
                            RealmList<FloatValue> quantList = medItem.getQuantity();
                            quantList.add(value);

                            currentUser.getUserMedicationList().add(medItem);
                            optiMappRealm.commitTransaction();
                        }

                        String text = "Medication has been added to the list!";
                        Toast.makeText(getApplicationContext(), text,
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
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
        });




        RealmList<MedItem> meds = currentUser.getUserMedicationList();

        MedicationListAdapter recyclerAdapter = new MedicationListAdapter(meds, this.getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.medication_recycler_view);
        mRecyclerView.setAdapter(recyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(recyclerAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }



}
