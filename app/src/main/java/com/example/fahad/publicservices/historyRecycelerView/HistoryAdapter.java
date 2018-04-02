package com.example.fahad.publicservices.historyRecycelerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.fahad.publicservices.R;

import java.util.List;

/**
 * Created by ammar on 3/23/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolders>{
    private List<HisoryObject> itemList;
    private Context context;

    // functioun for populate
    public HistoryAdapter(List<HisoryObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }
    @Override
    public HistoryViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        HistoryViewHolders hvc = new HistoryViewHolders(layoutView);
        return hvc;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolders holder, int position) {
        holder.rideId.setText(itemList.get(position).getRideId());
        holder.time.setText(itemList.get(position).getTime());
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
