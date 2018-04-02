package com.example.fahad.publicservices.serviceProviderH;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.example.fahad.publicservices.R;
import com.example.fahad.publicservices.historyServiceProviderSingle;



public class serviceHviewholders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView rideId;
    public TextView time;
    public serviceHviewholders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        rideId = (TextView) itemView.findViewById(R.id.rideId);
        time = (TextView) itemView.findViewById(R.id.time);
    }
    @Override
    public void onClick(View view) {
        //passing value between activities
        Intent i = new Intent(view.getContext(), historyServiceProviderSingle.class);
        //pass  the info for ride  bundle allow pass multi between ac
        Bundle b = new Bundle();
        b.putString("rideId",rideId.getText().toString());
        i.putExtras(b);
        view.getContext().startActivity(i);
    }
}
