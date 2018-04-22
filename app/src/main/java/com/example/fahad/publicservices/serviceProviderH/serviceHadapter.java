package com.example.fahad.publicservices.serviceProviderH;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.fahad.publicservices.R;
import com.example.fahad.publicservices.historyRecycelerView.HisoryObject;

import java.util.ArrayList;
import java.util.List;


public class serviceHadapter  extends RecyclerView.Adapter<serviceHviewholders>{
    private ArrayList<HisoryObject> itemList;
    private Context context;

    // functioun for populate
    public serviceHadapter(ArrayList<HisoryObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }
    @Override
    public serviceHviewholders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        serviceHviewholders hvc = new serviceHviewholders(layoutView);
        return hvc;
    }

    @Override
    public void onBindViewHolder(serviceHviewholders holder, int position) {
        holder.RequestId.setText(itemList.get(position).getRequestId());
        holder.time.setText(itemList.get(position).getTime());
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}

