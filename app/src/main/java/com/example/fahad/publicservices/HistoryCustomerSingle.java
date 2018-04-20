package com.example.fahad.publicservices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class HistoryCustomerSingle extends AppCompatActivity {
    private String RequestId , CurrentCustomerId , CustomerID , ServiceProviderId , CustomerOrServiceProvider;
    private TextView date;
    private TextView CustomerPhone;
    private TextView CustomerName;
    private TextView Price;
    private ImageView CustomerProblemImage;
    private RatingBar ratingBar;
    private DatabaseReference historyRequestInfoDb ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_customer_single);

        CheckingInternetConnection();

        CustomerProblemImage  = (ImageView) findViewById(R.id.imagePr);
        date = (TextView) findViewById(R.id.date);
        CustomerPhone = (TextView) findViewById(R.id.userphone);
        CustomerName = (TextView) findViewById(R.id.username);
        Price = (TextView) findViewById(R.id.price);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar) ;

        CurrentCustomerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RequestId = getIntent().getExtras().getString("RequestId");
        historyRequestInfoDb = FirebaseDatabase.getInstance().getReference().child("history").child(RequestId);
        getRequestInfo();
        getCustomerInfo();
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
                                displayCustomerRelatedObject();
                            }
                        }

                        if (child.getKey().equals("timestamp")){
                            date.setText(getDate(Long.valueOf(child.getValue().toString())));

                        }
                        if (child.getKey().equals("rating")){
                            ratingBar.setRating(Integer.valueOf(child.getValue().toString()));
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

    private void displayCustomerRelatedObject() {
        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                historyRequestInfoDb.child("rating").setValue(rating);
                DatabaseReference mServiceProviderRatingDb = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderId).child("rating");
                mServiceProviderRatingDb.child(RequestId).setValue(rating);
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

                    if (map.get("profileImageUrl")!=null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(CustomerProblemImage);
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


