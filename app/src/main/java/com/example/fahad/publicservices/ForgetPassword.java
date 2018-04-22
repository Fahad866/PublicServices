package com.example.fahad.publicservices;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    EditText email;
    Button resetPassword;
    LinearLayout ForgetPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);

        email = (EditText)findViewById(R.id.email);
        resetPassword = (Button)findViewById(R.id.resetPassword);
        ForgetPasswordLayout = (LinearLayout)findViewById(R.id.ForgetPasswordLayout);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try{
                    String CustomerEmail = email.getText().toString().trim();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(CustomerEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgetPassword.this , "Password Reset Email Sent" , Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ForgetPassword.this , MainActivity.class);
                                startActivity(intent);
                            }else {
                            Snackbar.make(ForgetPasswordLayout , "This Email is not Registered" , Snackbar.LENGTH_LONG ).show();
                        }
                        }
                    });

               }catch (IllegalArgumentException e){
                   Snackbar.make(ForgetPasswordLayout , "Please add email." , Snackbar.LENGTH_LONG ).show();
               }


                }

        });
    }


}
