package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class historyServiceProviderSingle extends AppCompatActivity{
    private String RequestId , CurrentCustomerId ,CustomerID , ServiceProviderId,CustomerOrServiceProvider;
    private TextView date;
    private TextView CustomerPhone;
    private TextView CustomerName;
    private DatabaseReference historyRequestInfoDb ;
    private TextView Price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_service_provider_single);

        Price = (TextView) findViewById(R.id.ppp);
        date = (TextView) findViewById(R.id.date);
        CustomerPhone = (TextView) findViewById(R.id.servicephone);
        CustomerName = (TextView) findViewById(R.id.servicename);


        CurrentCustomerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RequestId = getIntent().getExtras().getString("RequestId");
        historyRequestInfoDb = FirebaseDatabase.getInstance().getReference().child("history").child(RequestId);
        getRequestInfo();
        getCustomerInfo();
    }

    private void getRequestInfo() {
        historyRequestInfoDb.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        if (child.getKey().equals("Customer")){
                            CustomerID = child.getValue().toString();
                            if (!CustomerID.equals(CurrentCustomerId)){
                                CustomerOrServiceProvider = "ServiceProvider";
                                getCustomerInf("Customer",CustomerID);
                            }
                        }
                        if (child.getKey().equals("ServiceProvider")){
                            ServiceProviderId = child.getValue().toString();
                            if (!ServiceProviderId.equals(CurrentCustomerId)){
                                CustomerOrServiceProvider = "Customer";
                                getCustomerInf("ServiceProvider",ServiceProviderId);
                            }
                        }

                        if (child.getKey().equals("timestamp")){
                            date.setText(getDate(Long.valueOf(child.getValue().toString())));

                        }



                    }
                }
            }

            private String getDate(Long timestamp) {
                Calendar cal = Calendar.getInstance(Locale.getDefault());//calender get instsnce//local location of user
                cal.setTimeInMillis(timestamp*1000);
                String date = DateFormat.format("dd-MM-yyyy hh:mm" ,cal).toString();//arrange the time like yy-dd-mm



                return date;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void getCustomerInf(String CustomerOrServiceProvider, String otherCustomerId) {
        DatabaseReference CustomerDb = FirebaseDatabase.getInstance().getReference().child("Users").child(CustomerOrServiceProvider).child(otherCustomerId);
        CustomerDb.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String,Object>map = (Map<String,Object>)dataSnapshot.getValue();
                    if (map.get("name")!=null){
                        CustomerName.setText(map.get("name").toString());
                    }
                    if (map.get("phone")!=null){
                        CustomerPhone.setText(map.get("phone").toString());
                    }


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getCustomerInfo(){
        historyRequestInfoDb.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String,Object>map = (Map<String,Object>)dataSnapshot.getValue();
                    if (map.get("price")!=null){
                        Price.setText(map.get("price").toString());
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


