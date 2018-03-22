package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SelectService extends AppCompatActivity {

    ImageView barber , carpenter , doctor , car_wash , car_fix , mobile_repair , plumber , electrician;
    private DatabaseReference mServiceDatabase;
    private String serviceId;
    private FirebaseAuth mAthu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_service);

        barber = (ImageView)findViewById(R.id.barber);
        carpenter = (ImageView)findViewById(R.id.carpenter);
        doctor = (ImageView)findViewById(R.id.doctor);
        car_wash = (ImageView)findViewById(R.id.car_wash);
        car_fix = (ImageView)findViewById(R.id.car_fix);
        mobile_repair = (ImageView)findViewById(R.id.mobile_fix);
        plumber = (ImageView)findViewById(R.id.plumber);
        electrician = (ImageView)findViewById(R.id.electrician);


        mAthu = FirebaseAuth.getInstance();
        serviceId = mAthu.getCurrentUser().getUid();
        mServiceDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceId);


        barber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map userInfo = new HashMap();
                userInfo.put("service","barber");
                Intent intent = new Intent(SelectService.this , ServiceProviderMapsActivity.class);
                startActivity(intent);
            }
        });

        //========================================================

        carpenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map userInfo = new HashMap();
                userInfo.put("service","carpenter");
                Intent intent = new Intent(SelectService.this , ServiceProviderMapsActivity.class);
                startActivity(intent);
            }
        });

        //========================================================

        barber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //========================================================

        barber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //========================================================

        barber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //========================================================

        barber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //========================================================

        barber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //========================================================

        barber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //========================================================

        barber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //========================================================

    }
}
