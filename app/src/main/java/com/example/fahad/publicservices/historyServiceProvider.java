package com.example.fahad.publicservices;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.fahad.publicservices.serviceProviderH.HisoryObjectS;
import com.example.fahad.publicservices.serviceProviderH.serviceHadapter;
import com.example.fahad.publicservices.serviceProviderH.serviceHviewholders;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.text.format.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class historyServiceProvider extends AppCompatActivity {

    private String userServiceprovider  , userId;
    private RecyclerView mhistoryRecycelerView;
    private  RecyclerView.Adapter mhistoryAdapter ;
    private  RecyclerView.LayoutManager mhistoryLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_service_provider);
        mhistoryRecycelerView = (RecyclerView)findViewById(R.id.RecycelerView);
        mhistoryRecycelerView.setNestedScrollingEnabled(false);
        mhistoryRecycelerView.setHasFixedSize(true);
        mhistoryLayoutManager = new LinearLayoutManager(historyServiceProvider.this);
        mhistoryRecycelerView.setLayoutManager(mhistoryLayoutManager);
        mhistoryAdapter = new serviceHadapter(getDataHistory(), historyServiceProvider.this);//pass the context and itemList
        mhistoryRecycelerView.setAdapter(mhistoryAdapter);

        userServiceprovider = getIntent().getExtras().getString("userServiceprovider");//this when recive from user menu
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserHistoryids();

    }

    private void getUserHistoryids() {
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userServiceprovider).child(userId).child("history");
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot history : dataSnapshot.getChildren()){
                        FetchRideInformation(history.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void FetchRideInformation(String rideKey) {
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("history").child(rideKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String rideId = dataSnapshot.getKey();
                    Long timestamp = 0L;//for show time for ride
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        if (child.getKey().equals("timestamp")){
                            timestamp = Long.valueOf(child.getValue().toString());
                        }
                    }
                    HisoryObjectS obj = new HisoryObjectS(rideId,getDate(timestamp));
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


    private ArrayList resultHistory= new ArrayList<HisoryObjectS>();
    private ArrayList<HisoryObjectS> getDataHistory() {
        return resultHistory;//for
    }
}
