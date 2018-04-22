package com.example.fahad.publicservices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import com.example.fahad.publicservices.historyRecycelerView.HisoryObject;
import com.example.fahad.publicservices.serviceProviderH.serviceHadapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class historyServiceProvider extends AppCompatActivity {

    private String ServiceProviderId;
    private RecyclerView mhistoryRecycelerView;
    private RecyclerView.Adapter mhistoryAdapter ;
    private RecyclerView.LayoutManager mhistoryLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_service_provider);

        CheckingInternetConnection();

        mhistoryRecycelerView = (RecyclerView)findViewById(R.id.RecycelerView);
        mhistoryRecycelerView.setNestedScrollingEnabled(false);
        mhistoryRecycelerView.setHasFixedSize(true);

        mhistoryLayoutManager = new LinearLayoutManager(historyServiceProvider.this);
        mhistoryRecycelerView.setLayoutManager(mhistoryLayoutManager);

        mhistoryAdapter = new serviceHadapter(getDataHistory(), historyServiceProvider.this);
        mhistoryRecycelerView.setAdapter(mhistoryAdapter);


        ServiceProviderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getServiceProviderHistoryids();

    }

    public void CheckingInternetConnection() {
        String title = "internet not found";
        String message = "Click Setting and enable internet";
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
    }

    private void getServiceProviderHistoryids() {
        DatabaseReference ServiceProviderHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderId).child("history");
        ServiceProviderHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timestamp*1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mm" ,cal).toString();
        return date;
    }


    private ArrayList resultHistory= new ArrayList<HisoryObject>();
    private ArrayList<HisoryObject> getDataHistory() {
        return resultHistory;
    }
}
