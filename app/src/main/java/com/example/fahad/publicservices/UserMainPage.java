package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserMainPage extends AppCompatActivity {

    Button Log_Out, Contact_Us, Edit_Profile, Request_Services;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_page);

        Request_Services = (Button) findViewById(R.id.Request_Services);
        Edit_Profile = (Button) findViewById(R.id.Edit_Profile);
        Contact_Us = (Button) findViewById(R.id.Contact_Us);
        Log_Out = (Button) findViewById(R.id.Log_Out);

        Edit_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMainPage.this , EditProfile.class);
                startActivity(intent);
            }
        });

        //================================================================================

        Contact_Us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMainPage.this , ContactUs.class);
                startActivity(intent);
            }
        });

        //================================================================================

        Request_Services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMainPage.this , Customer_Service_details.class);
                startActivity(intent);
            }
        });

        //================================================================================

        Log_Out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserMainPage.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


    }
}

