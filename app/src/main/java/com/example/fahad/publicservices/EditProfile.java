package com.example.fahad.publicservices;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fahad.publicservices.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    EditText edtName , edtPhone ;
    Button btnSubmit;
    CircleImageView image;
    private Uri resultUri ;
    private String  Phone , Name , ProfileUrl;

    private String accountType ;
    private FirebaseAuth Athu;
    private DatabaseReference CustomerDatabase;
    private String CustomerID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        edtName = (EditText)findViewById(R.id.edtName);
        edtPhone = (EditText)findViewById(R.id.edtPhone);

        image = (CircleImageView)findViewById(R.id.image);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        accountType = getIntent().getStringExtra("accountType");

        if(accountType.equals("Customer")){
            image.setVisibility(View.GONE);

        }

        //init firebase
        Athu = FirebaseAuth.getInstance();
        CustomerID= Athu.getCurrentUser().getUid();
        CustomerDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(accountType).child(CustomerID);

        getCustomerInfo();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);//GO GALLARY
                intent.setType("image/*");// the type for image
                startActivityForResult(intent,1);
            }
        });

        getCustomerInfo();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomerInfo();
            }
        });


    }

    private void getCustomerInfo() {

        CustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    //if alredy name same in db
                    Map<String , Object> map=(Map<String , Object>)dataSnapshot.getValue();
                    //if any an correct plase cheke name db

                    if (map.get("name")!=null){
                        Name=map.get("name").toString();
                        edtName.setText(Name);
                    }
                    if (map.get("phone")!=null) {
                        Phone = map.get("phone").toString();
                        edtPhone.setText(Phone);
                    }

                    if (map.get("profileImageUrl")!=null){
                        ProfileUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(ProfileUrl).into(image);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveCustomerInfo() {


        Name = edtName.getText().toString();
        Phone = edtPhone.getText().toString();

        Map CustomerInfo = new HashMap();
        CustomerInfo.put("phone",Phone);
        CustomerInfo.put("name",Name);

        CustomerDatabase.updateChildren(CustomerInfo);

        if (resultUri!=null){//save image in db
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Profile_Image ").child(CustomerID);
            Bitmap bitmap=null;
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
                    newImage.put("profileImageUrl",downloadUrl.toString());
                    CustomerDatabase.updateChildren(newImage);

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
        if (requestCode ==1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            image.setImageURI(resultUri);
        }
    }
}




