package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnUser , btnServiceProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUser = (Button)findViewById(R.id.btnUser);
        btnServiceProvider = (Button)findViewById(R.id.btnServiceProvider);

        btnUser.setOnClickListener(new View.OnClickListener() {
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
