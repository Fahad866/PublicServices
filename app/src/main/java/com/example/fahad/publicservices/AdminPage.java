package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminPage extends AppCompatActivity {

    private Button Services_Details , Delete_Customer , Delete_Service_Provider;
    private Button btn_DeleteC , btn_DeleteSP;
    private EditText Customer_ID , Service_Provider_ID;
    private LinearLayout Customer_Layout , Service_Provider_Layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);

        Services_Details = (Button)findViewById(R.id.Services_Details);
        Delete_Customer = (Button)findViewById(R.id.Delete_Customer);
        Delete_Service_Provider = (Button)findViewById(R.id.Delete_Service_Provider);
        btn_DeleteC = (Button)findViewById(R.id.btn_DeleteC);
        btn_DeleteSP = (Button)findViewById(R.id.btn_DeleteSP);
        Customer_ID = (EditText)findViewById(R.id.Customer_ID);
        Service_Provider_ID = (EditText)findViewById(R.id.Service_Provider_ID);
        Customer_Layout = (LinearLayout)findViewById(R.id.Customer_Layout);
        Service_Provider_Layout = (LinearLayout)findViewById(R.id.Service_Provider_Layout);


//--------------------------------------------------------------------------
        Services_Details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPage.this , AdminServicesDetails.class);
                startActivity(intent);
            }
        });
//--------------------------------------------------------------------------
        Delete_Customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Service_Provider_Layout.setVisibility(View.GONE);
                Customer_Layout.setVisibility(View.VISIBLE);
                DeleteCustomer();
            }
        });
//--------------------------------------------------------------------------
        Delete_Service_Provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer_Layout.setVisibility(View.GONE);
                Service_Provider_Layout.setVisibility(View.VISIBLE);
                DeleteServiceProvider();
            }
        });

    }


    DatabaseReference CustomerRef;
    private void DeleteCustomer() {
        String CustomerID = Customer_ID.getText().toString();
          CustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(CustomerID);

        btn_DeleteC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CustomerRef.equals(null)) {   //**************************************
                    CustomerRef.removeValue();
                    Toast.makeText(AdminPage.this , "Customer Deleted" , Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(AdminPage.this , "ID not found" , Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    DatabaseReference ServiceProviderRef;
    private void DeleteServiceProvider() {
        String ServiceProviderID = Service_Provider_ID.getText().toString();
        ServiceProviderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderID);

        btn_DeleteSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ServiceProviderRef.equals(null)) {  //**************************************
                    ServiceProviderRef.removeValue();
                    Toast.makeText(AdminPage.this , "Service Provider Deleted" , Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(AdminPage.this , "ID not found" , Toast.LENGTH_LONG).show();
                }

            }
        });
    }


}
