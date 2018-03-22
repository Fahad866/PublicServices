package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactUs extends AppCompatActivity {

    EditText subject , message ;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);

        subject = (EditText)findViewById(R.id.subject);
        message = (EditText)findViewById(R.id.massege);
        btnSend = (Button)findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = "publicservices2018@gmail.com";
                String sub = subject.getText().toString();
                String mas = message.getText().toString();

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL , new String[]{to});
                email.putExtra(Intent.EXTRA_SUBJECT , sub);
                email.putExtra(Intent.EXTRA_TEXT , mas);

                email.setType("message/rcf822");
                startActivity(Intent.createChooser(email , "choose app to send mail"));

            }
        });

    }
}
