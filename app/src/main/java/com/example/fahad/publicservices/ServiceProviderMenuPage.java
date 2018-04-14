package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ServiceProviderMenuPage extends AppCompatActivity {

    Button Log_Out, Contact_Us, Edit_Profile, Receive_Requests , Previous_Requests;
    private RatingBar RatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_provider_menu_page);

        getServiceProviderRate();

        Receive_Requests = (Button) findViewById(R.id.Receive_Requests);
        Previous_Requests = (Button) findViewById(R.id.Previous_Request);
        Edit_Profile = (Button) findViewById(R.id.Edit_Profile);
        Contact_Us = (Button) findViewById(R.id.Contact_Us);
        Log_Out = (Button) findViewById(R.id.Log_Out);
        RatingBar = (RatingBar) findViewById(R.id.ratingBar);


        Edit_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceProviderMenuPage.this, EditProfile.class);
                intent.putExtra("accountType", "ServiceProvider");
                startActivity(intent);
            }
        });

        //================================================================================

        Previous_Requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceProviderMenuPage.this, historyServiceProvider.class);
                intent.putExtra("CustomerOrServiceProvider", "ServiceProvider");
                startActivity(intent);
            }
        });

        //================================================================================


        Contact_Us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceProviderMenuPage.this, ContactUs.class);
                startActivity(intent);
            }
        });

        //================================================================================

        Receive_Requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceProviderMenuPage.this, ServiceProviderMapsActivity.class);
                startActivity(intent);
            }
        });

        //================================================================================

        Log_Out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ServiceProviderMenuPage.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }

    private void getServiceProviderRate(){
        String ServiceProviderFoundID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference CustomerDatabase  = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderFoundID);
        CustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    //if alredy name same in db
                    Map<String , Object> map=(Map<String , Object>)dataSnapshot.getValue();
                    int ratingSum = 0 ;
                    float ratingsTotal = 0;
                    float ratingAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum+Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    // avg
                    if (ratingsTotal != 0){
                        ratingAvg = ratingSum /ratingsTotal;
                        RatingBar.setRating(ratingAvg);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});}

    }

