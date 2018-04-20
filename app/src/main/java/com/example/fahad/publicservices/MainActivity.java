package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnCustomer , btnServiceProvider;


    //first method will be start when the activity open
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCustomer = (Button)findViewById(R.id.btnUser);
        btnServiceProvider = (Button)findViewById(R.id.btnServiceProvider);

        //when the app start it will call this startService
        startService(new Intent(MainActivity.this , KillApp.class));


        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , CustomerMainActivity.class));
            }
        });



        btnServiceProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , ServiceProviderMainActivity.class));
            }
        });

    }
}
