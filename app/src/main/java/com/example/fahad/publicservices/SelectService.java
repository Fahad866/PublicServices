package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class SelectService extends AppCompatActivity {

    ImageView barber , carpenter , doctor , car_wash , car_fix , mobile_repair , plumber , electrician;
    private DatabaseReference ServiceProviderDatabase;
    private String serviceProviderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_service);

        barber = (ImageView)findViewById(R.id.barber);
        carpenter = (ImageView)findViewById(R.id.carpenter);
        doctor = (ImageView)findViewById(R.id.doctor);
        car_wash = (ImageView)findViewById(R.id.car_wash);
        car_fix = (ImageView)findViewById(R.id.car_fix);
        mobile_repair = (ImageView)findViewById(R.id.mobile_repair);
        plumber = (ImageView)findViewById(R.id.plumber);
        electrician = (ImageView)findViewById(R.id.electrician);

        serviceProviderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ServiceProviderDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceProviderId);


        barber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map CustomerInfo = new HashMap();
                //save the service of service provider in his database
                CustomerInfo.put("service","barber");
                ServiceProviderDatabase.updateChildren(CustomerInfo);
                Toast.makeText(SelectService.this , "Register successfully" , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SelectService.this , ServiceProviderMainActivity.class);
                startActivity(intent);
            }
        });

        //========================================================

        carpenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map CustomerInfo = new HashMap();
                //save the service of service provider in his database
                CustomerInfo.put("service","carpenter");
                ServiceProviderDatabase.updateChildren(CustomerInfo);
                Toast.makeText(SelectService.this , "Register successfully" , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SelectService.this , ServiceProviderMainActivity.class);
                startActivity(intent);
            }
        });

        //========================================================

        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map CustomerInfo = new HashMap();
                //save the service of service provider in his database
                CustomerInfo.put("service","doctor");
                ServiceProviderDatabase.updateChildren(CustomerInfo);
                Toast.makeText(SelectService.this , "Register successfully" , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SelectService.this , ServiceProviderMainActivity.class);
                startActivity(intent);
            }
        });

        //========================================================

        car_wash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map CustomerInfo = new HashMap();
                //save the service of service provider in his database
                CustomerInfo.put("service","car_wash");
                ServiceProviderDatabase.updateChildren(CustomerInfo);
                Toast.makeText(SelectService.this , "Register successfully" , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SelectService.this , ServiceProviderMainActivity.class);
                startActivity(intent);
            }
        });

        //========================================================

        car_fix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map CustomerInfo = new HashMap();
                //save the service of service provider in his database
                CustomerInfo.put("service","car_fix");
                ServiceProviderDatabase.updateChildren(CustomerInfo);
                Toast.makeText(SelectService.this , "Register successfully" , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SelectService.this , ServiceProviderMainActivity.class);
                startActivity(intent);
            }
        });

        //========================================================

        mobile_repair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map CustomerInfo = new HashMap();
                //save the service of service provider in his database
                CustomerInfo.put("service","mobile_repair");
                ServiceProviderDatabase.updateChildren(CustomerInfo);
                Toast.makeText(SelectService.this , "Register successfully" , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SelectService.this , ServiceProviderMainActivity.class);
                startActivity(intent);
            }
        });

        //========================================================

        plumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map CustomerInfo = new HashMap();
                //save the service of service provider in his database
                CustomerInfo.put("service","plumber");
                ServiceProviderDatabase.updateChildren(CustomerInfo);
                Toast.makeText(SelectService.this , "Register successfully" , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SelectService.this , ServiceProviderMainActivity.class);
                startActivity(intent);
            }
        });

        //========================================================

        electrician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map CustomerInfo = new HashMap();
                //save the service of service provider in his database
                CustomerInfo.put("service","electrician");
                ServiceProviderDatabase.updateChildren(CustomerInfo);
                Toast.makeText(SelectService.this , "Register successfully" , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SelectService.this , ServiceProviderMainActivity.class);
                startActivity(intent);
            }
        });

        //========================================================


    }
}
