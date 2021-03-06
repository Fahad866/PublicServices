package com.example.fahad.publicservices;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Customer_Service_details extends AppCompatActivity {
    private EditText mTexeProblem;
    private CircleImageView mImageProblem;
    private Button btnConfirm;
    private DatabaseReference mCustomerDatabase;
    private String CustomerID;
    private String TextProblem;
    private Uri resultUri ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_service_details);

        CheckingInternetConnection();

        mTexeProblem = (EditText)findViewById(R.id.TextProblem);
        mImageProblem = (CircleImageView)findViewById(R.id.imageProblem) ;
        btnConfirm = (Button)findViewById(R.id.Confirm);


        CustomerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(CustomerID);

        if(mCustomerDatabase.child("TextProblem") != null){
            mCustomerDatabase.child("TextProblem").setValue("No Description");
        }

        if(mCustomerDatabase.child("ProblemImage") != null){
            mCustomerDatabase.child("ProblemImage").setValue("https://firebasestorage.googleapis.com/v0/b/publicservices-f6743.appspot.com/o/no_image.png?alt=media&token=1f18abbd-eb33-45d6-9d8e-ca1f522b42f8");
        }

        mImageProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK); //GO GALLERY
                intent.setType("image/*");// the type for image
                startActivityForResult(intent,1);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCustomerInfo();
                Intent intent = new Intent(Customer_Service_details.this,CustomerMapsActivity.class);
                startActivity(intent);
                return;
            }
        });



    }

    public void CheckingInternetConnection(){
        String title = "internet not found";
        String message = "Click Setting and enable internet";
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        if( connectivityManager.getActiveNetworkInfo() == null ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
    }


    //save to db
    private void saveCustomerInfo() {

        TextProblem = mTexeProblem.getText().toString();
        Map CustomerInfo = new HashMap();
        if(!TextProblem.equals(""))
            CustomerInfo.put("TextProblem",TextProblem);
        mCustomerDatabase.updateChildren(CustomerInfo);

        if (resultUri != null){//save image in db
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Image Problem").child(CustomerID);//like dbrefrence
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //image compression
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,boas);
            byte[] data = boas.toByteArray();
            UploadTask uploadTask =filePath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Map newImage = new HashMap();
                    newImage.put("ProblemImage", downloadUrl.toString());
                    mCustomerDatabase.updateChildren(newImage);

                    finish();
                    return;
                }
            });
        }else {
            finish();

        }
    }
    @Override//for image view for user
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mImageProblem.setImageURI(resultUri);
        }
    }
}
