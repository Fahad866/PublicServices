package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class CustomerMenuPage extends AppCompatActivity {

    Button Log_Out, Contact_Us, Edit_Profile, Request_Services , Previous_Request_Customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_menu_page);

        Request_Services = (Button) findViewById(R.id.Request_Services);
        Previous_Request_Customer = (Button) findViewById(R.id.Previous_Request_Customer);
        Edit_Profile = (Button) findViewById(R.id.Edit_Profile);
        Contact_Us = (Button) findViewById(R.id.Contact_Us);
        Log_Out = (Button) findViewById(R.id.Log_Out);


        //================================================================================

        Edit_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMenuPage.this , EditProfile.class);
                intent.putExtra("accountType","Customer");
                startActivity(intent);
            }
        });

        //================================================================================

        Previous_Request_Customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMenuPage.this , historyCustomer.class);
                startActivity(intent);
            }
        });

        //================================================================================

        Contact_Us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMenuPage.this , ContactUs.class);
                startActivity(intent);
            }
        });

        //================================================================================

        Request_Services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMenuPage.this , Customer_Service_details.class);
                startActivity(intent);
            }
        });

        //================================================================================

        Log_Out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMenuPage.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


    }
}

