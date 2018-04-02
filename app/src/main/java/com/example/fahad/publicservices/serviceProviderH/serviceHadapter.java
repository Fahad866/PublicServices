package com.example.fahad.publicservices.serviceProviderH;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.fahad.publicservices.R;
import java.util.List;


public class serviceHadapter  extends RecyclerView.Adapter<serviceHviewholders>{
    private List<HisoryObjectS> itemList;
    private Context context;

    // functioun for populate
    public serviceHadapter(List<HisoryObjectS> itemList, Context context) {
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
        holder.rideId.setText(itemList.get(position).getRideId());
        holder.time.setText(itemList.get(position).getTime());
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}

