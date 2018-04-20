package com.example.fahad.publicservices;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.fahad.publicservices.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;



public class CustomerMainActivity extends AppCompatActivity {

    Button btnSignIn , btnRegister;
    RelativeLayout rootLayout;
    TextView forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        //init view
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);
        forgetPassword = (TextView)findViewById(R.id.forgetPassword);




//-----------------------------------------------------------------------------------------


        //Event on Register button clicked
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });
        //Event on signin button clicked
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInDialog();
            }
        });
        //Event on forgetPassword button clicked
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });

    }


//-----------------------------------------------------------------------------------------

    private void showSignInDialog() {
        //this dialog will show in front of the main Activity
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use Email to sign in");


        LayoutInflater inflater = LayoutInflater.from(this);
        View signin_layout = inflater.inflate(R.layout.layout_signin , null);

        final MaterialEditText edtEmail = signin_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = signin_layout.findViewById(R.id.edtPassword);

        dialog.setView(signin_layout);


        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //check validation
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "password too short !!", Snackbar.LENGTH_LONG).show();
                    return;
                }



                //waiting dialog
                final SpotsDialog waitingDialog = new SpotsDialog(CustomerMainActivity.this);
                waitingDialog.show();

                //signin
                FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();
                                String CustomerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference CustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(CustomerID);
                                CustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            //if the customer email and password found in customer database
                                            startActivity(new Intent(CustomerMainActivity.this, CustomerMenuPage.class));
                                            finish();
                                        }else {
                                            //if the customer email and password not found in customer database
                                            Snackbar.make(rootLayout, "You are not Customer", Snackbar.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            //cases this Failure will occur:
                            // 1- Email badly formatted
                            // 2- Email not found
                            // 3- no internet connection
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss();
                                Snackbar.make(rootLayout, "failed " + e.getMessage(), Snackbar.LENGTH_LONG).show();

                            }
                        });
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

//-----------------------------------------------------------------------------------------

    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use Email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register , null);

        final MaterialEditText edtEmail = register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtName = register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = register_layout.findViewById(R.id.edtPhone);

        dialog.setView(register_layout);

        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //check validation
                if(TextUtils.isEmpty(edtEmail.getText().toString())){
                    Snackbar.make(rootLayout , "Please enter email address" , Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    Snackbar.make(rootLayout , "Please enter password" , Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(edtPassword.getText().toString().length() < 6){
                    Snackbar.make(rootLayout , "password too short !!" , Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(edtName.getText().toString())){
                    Snackbar.make(rootLayout , "Please enter name" , Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(edtPhone.getText().toString())){
                    Snackbar.make(rootLayout , "Please enter phone number" , Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(edtPhone.getText().toString().length() < 10){
                    Snackbar.make(rootLayout , "phone number too short !!" , Snackbar.LENGTH_LONG).show();
                    return;
                }

                //register new user
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        //save user to db
                        User user = new User();
                        user.setEmail(edtEmail.getText().toString());
                        user.setPassword(edtPassword.getText().toString());
                        user.setName(edtName.getText().toString());
                        user.setPhone(edtPhone.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("Users").child("Customer")
                                .child(FirebaseAuth.getInstance().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(rootLayout , "Register successfully" , Snackbar.LENGTH_LONG).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    //cases this Failure will occur:
                                    // 1- Email badly formatted
                                    // 2- no internet connection
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(rootLayout , "failed " + e.getMessage() , Snackbar.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            //cases this Failure will occur:
                            //1- Email is already exist
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout , "failed " + e.getMessage() , Snackbar.LENGTH_LONG).show();
                            }
                        });

            }
        });


        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //-----------------------------------------------------------------------------------------

    private void forgetPassword(){
        Intent intent = new Intent(CustomerMainActivity.this , ForgetPassword.class);
        startActivity(intent);
    }
}
