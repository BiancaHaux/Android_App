package com.example.biancahaux.optimapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

/**
 * Created by Bianca Haux on 12.06.2016.
 */
public class MedicationListAdapter extends RecyclerView.Adapter<MedicationListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private List<MedItem> mItems = new ArrayList<>();
    private Realm optiMappRealm;
    private Account currentUser;

    public MedicationListAdapter(List<MedItem> eventList, Context context) {

        mItems = eventList;

        RealmConfiguration config2 = new RealmConfiguration.Builder(context)
                .name("default2")
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();

        optiMappRealm = Realm.getInstance(config2);

        currentUser = optiMappRealm.where(Account.class)
                .equalTo("currentUser", true)
                .findFirst();
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        TextView textViewTitle = holder.textViewTitle;
        TextView textViewDescription = holder.textViewDetails;
        RealmList<StringValue> list = mItems.get(position).getMedItemDescriptions();
        String descr = "";

        for(StringValue s : list){
            descr = descr + "\n" + s.getStringValue();

        }
        textViewTitle.setText(mItems.get(position).getMedItemTitle().toString());
        textViewDescription.setText(descr);
    }

    @Override
    public void onItemDismiss(int position) {
        optiMappRealm.beginTransaction();
        mItems.remove(position);
        notifyItemRemoved(position);
        optiMappRealm.commitTransaction();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        optiMappRealm.beginTransaction();
        MedItem prev = mItems.remove(fromPosition);
        mItems.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        optiMappRealm.commitTransaction();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView textViewTitle;
        public final TextView textViewDetails;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.textViewTitle = (TextView) itemView.findViewById(R.id.tvMedTitle);
            this.textViewDetails = (TextView) itemView.findViewById(R.id.tvMedDescription);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}