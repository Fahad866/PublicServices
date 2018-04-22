package com.example.fahad.publicservices.historyRecycelerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.fahad.publicservices.HistoryCustomerSingle;
import com.example.fahad.publicservices.R;

public class HistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView RequestId;
    public TextView time;
    public HistoryViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        RequestId = (TextView) itemView.findViewById(R.id.RequestId);
        time = (TextView) itemView.findViewById(R.id.time);
    }
    @Override
    public void onClick(View view) {
        //passing value between activities
        Intent i = new Intent(view.getContext(), HistoryCustomerSingle.class);
        //pass  the info for Request  bundle allow pass multi between ac
        Bundle b = new Bundle();
        b.putString("RequestId",RequestId.getText().toString());
        i.putExtras(b);
        view.getContext().startActivity(i);
    }
}
