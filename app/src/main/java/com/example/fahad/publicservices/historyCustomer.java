package com.example.fahad.publicservices;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;

import com.example.fahad.publicservices.historyRecycelerView.HisoryObject;
import com.example.fahad.publicservices.historyRecycelerView.HistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class historyCustomer extends AppCompatActivity {

    private String CustomerOrServiceProvider , CustomerId;

    private RecyclerView mhistoryRecycelerView;
    private RecyclerView.Adapter mhistoryAdapter ;
    private RecyclerView.LayoutManager mhistoryLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_customer);
        mhistoryRecycelerView = (RecyclerView)findViewById(R.id.historyRecycelerView);
        mhistoryRecycelerView.setNestedScrollingEnabled(false);
        mhistoryRecycelerView.setHasFixedSize(true);
        mhistoryLayoutManager = new LinearLayoutManager(historyCustomer.this);
        mhistoryRecycelerView.setLayoutManager(mhistoryLayoutManager);
        mhistoryAdapter = new HistoryAdapter(getDataHistory(), historyCustomer.this);//pass the context and itemList
        mhistoryRecycelerView.setAdapter(mhistoryAdapter);

        CustomerOrServiceProvider = getIntent().getExtras().getString("CustomerOrServiceProvider");//this when recive from user menu
        CustomerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getCustomerHistoryIds();

    }

    private void getCustomerHistoryIds() {
        DatabaseReference CustomerHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(CustomerOrServiceProvider).child(CustomerId).child("history");
        CustomerHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot history : dataSnapshot.getChildren()){
                        FetchRequestInformation(history.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void FetchRequestInformation(String RequestKey) {
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("history").child(RequestKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String RequestId = dataSnapshot.getKey();
                    Long timestamp = 0L;//for show time for ride
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        if (child.getKey().equals("timestamp")){
                            timestamp = Long.valueOf(child.getValue().toString());
                        }
                    }
                    HisoryObject obj = new HisoryObject(RequestId,getDate(timestamp));
                    resultHistory.add(obj);
                    mhistoryAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String getDate(Long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());//calender get instsnce//local location of user
        cal.setTimeInMillis(timestamp*1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mm" ,cal).toString();//arrange the time like yy-dd-mm



        return date;
    }


    private ArrayList resultHistory= new ArrayList<HisoryObject>();
    private ArrayList<HisoryObject> getDataHistory() {
        return resultHistory;//for
    }
}

