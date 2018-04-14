package com.example.fahad.publicservices;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminServicesDetails extends AppCompatActivity {

    TextView historyID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_services_details);

        historyID = (TextView) findViewById(R.id.historyID);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("history");

    }
}
