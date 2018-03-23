package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class ServiceProviderMainPage extends AppCompatActivity {

    Button Log_Out, Contact_Us, Edit_Profile, Receive_Services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_provider_main_page);

        Receive_Services = (Button) findViewById(R.id.Receive_Services);
        Edit_Profile = (Button) findViewById(R.id.Edit_Profile);
        Contact_Us = (Button) findViewById(R.id.Contact_Us);
        Log_Out = (Button) findViewById(R.id.Log_Out);

        Edit_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceProviderMainPage.this , EditProfile.class);
                intent.putExtra("accountType" , "ServiceProvider");
                startActivity(intent);
            }
        });

        //================================================================================

        Contact_Us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceProviderMainPage.this , ContactUs.class);
                startActivity(intent);
            }
        });

        //================================================================================

        Receive_Services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceProviderMainPage.this , ServiceProviderMapsActivity.class);
                startActivity(intent);
            }
        });

        //================================================================================

        Log_Out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ServiceProviderMainPage.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });



    }
}
