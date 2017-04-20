package com.example.biancahaux.optimapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Event> mItems = new ArrayList<>();
    private Realm optiMappRealm;
    private CaldroidFragment caldroidFragment;
    Context context;

    public RecyclerListAdapter(List<Event> eventList, Context _context, CaldroidFragment _caldroidFragment) {

        mItems = eventList;
        caldroidFragment = _caldroidFragment;
        this.context = _context;

        RealmConfiguration config2 = new RealmConfiguration.Builder(context)
                .name("default2")
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();

        optiMappRealm = Realm.getInstance(config2);

        if(mItems.size()==0){
            String text = "Add events by choosing a date!";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            toast.show();
        }
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        TextView textViewTitle = holder.textViewTitle;
        TextView textViewDescription = holder.textViewDetails;

        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy");

        textViewTitle.setText(mItems.get(position).getEventTitle().toString() + "\n" + format.format(mItems.get(position).getEventDate()));
        textViewDescription.setText(mItems.get(position).getEventDescription().toString());
    }

    @Override
    public void onItemDismiss(int position) {
        optiMappRealm.beginTransaction();
        Event event = mItems.get(position);
        Intent intent = new Intent(context, MyReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, event.getId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        alarmManager.cancel(sender);
        Date test = new Date(mItems.get(position).getEventDate());
        caldroidFragment.clearTextColorForDate(new Date(mItems.get(position).getEventDate()));
        caldroidFragment.refreshView();
        mItems.remove(position);
        notifyItemRemoved(position);
        optiMappRealm.commitTransaction();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        optiMappRealm.beginTransaction();
        Event prev = mItems.remove(fromPosition);
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
            this.textViewTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            this.textViewDetails = (TextView) itemView.findViewById(R.id.tvDescription);
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