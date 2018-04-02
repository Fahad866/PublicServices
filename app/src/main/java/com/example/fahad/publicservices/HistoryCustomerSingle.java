package com.example.fahad.publicservices;

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
    private String rideId , currentuserId ,userID , serviceId,usersUserOrProvider;
    private TextView date;
    private TextView userphone;
    private TextView username;
    private TextView mprice;
    private ImageView mUserproblemImage;
    private RatingBar ratingBar;
    private DatabaseReference historyRideInfoDb ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_customer_single);

        mUserproblemImage  = (ImageView) findViewById(R.id.imagePr);
        date = (TextView) findViewById(R.id.date);
        userphone = (TextView) findViewById(R.id.userphone);
        username = (TextView) findViewById(R.id.username);

        mprice = (TextView) findViewById(R.id.price);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar) ;

        currentuserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rideId = getIntent().getExtras().getString("rideId");
        historyRideInfoDb = FirebaseDatabase.getInstance().getReference().child("history").child(rideId);
        getRideInfo();
        getUserInf();
    }

    private void getRideInfo() {
        historyRideInfoDb.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        if (child.getKey().equals("Customer")){
                            userID = child.getValue().toString();
                            if (!userID.equals(currentuserId)){
                                usersUserOrProvider = "ServiceProvider";
                                getUserInfo("Customer",userID);
                            }
                        }
                        if (child.getKey().equals("ServiceProvider")){
                            serviceId = child.getValue().toString();
                            if (!serviceId.equals(currentuserId)){
                                usersUserOrProvider = "Customer";
                                getUserInfo("ServiceProvider",serviceId);
                                displayUserRelatedObject();
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

    private void displayUserRelatedObject() {
        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                historyRideInfoDb.child("rating").setValue(rating);
                DatabaseReference mServiceRatingDb = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceId).child("rating");
                mServiceRatingDb.child(rideId).setValue(rating);
            }
        });
    }

    private void getUserInfo(String otheruserUseOrService, String otherUserId) {
        DatabaseReference mOtherUsersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(otheruserUseOrService).child(otherUserId);
        mOtherUsersDb.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String,Object>map = (Map<String,Object>)dataSnapshot.getValue();
                    if (map.get("name")!=null){
                        username.setText(map.get("name").toString());
                    }
                    if (map.get("phone")!=null){
                        userphone.setText(map.get("phone").toString());
                    }

                    if (map.get("profileImageUrl")!=null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mUserproblemImage);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getUserInf(){
        historyRideInfoDb.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String,Object>map = (Map<String,Object>)dataSnapshot.getValue();
                    if (map.get("price")!=null){
                        mprice.setText(map.get("price").toString());
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


