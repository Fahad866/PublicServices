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

public class Customer_Service_details extends AppCompatActivity {
    private EditText mTexeProblem;
    private ImageView mImageProblem;
    private Button mConfirm;
    private FirebaseAuth mAthu;
    private DatabaseReference mUserDatabase;
    private String userID;
    private String textprblem;
    private Uri resultUri ;
    private String mProblemUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_service_details);

        mTexeProblem = (EditText)findViewById(R.id.TextProblem);
        mImageProblem = (ImageView)findViewById(R.id.imageProblem) ;
        mConfirm = (Button)findViewById(R.id.Confirm);


        mAthu = FirebaseAuth.getInstance();
        userID= mAthu.getCurrentUser().getUid();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userID);
        getUserinfo();
        mImageProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);//GO GALLARY
                intent.setType("image/*");// the type for image
                startActivityForResult(intent,1);

            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
                Intent intent = new Intent(Customer_Service_details.this,CustomerMapsActivity.class);
                startActivity(intent);
                return;
            }
        });

    }

    private void getUserinfo(){
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    //if alredy name same in db
                    Map<String , Object> map=(Map<String , Object>)dataSnapshot.getValue();
                    //if any an correct plase cheke name db
                    if (map.get("Text")!=null){
                        textprblem=map.get("Text").toString();
                        mTexeProblem.setText(textprblem);
                    }
                    if (map.get("problemImage")!=null) {
                        mProblemUrl = map.get("problemImage").toString();
                        Glide.with(getApplication()).load(mProblemUrl).into(mImageProblem);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    //save to db
    private void saveUserInfo() {
        textprblem=mTexeProblem.getText().toString();
        Map userInfo = new HashMap();
        userInfo.put("Text",textprblem);
        mUserDatabase.updateChildren(userInfo);
        if (resultUri!=null){//save image in db
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Profile_Image ").child(userID);//like dbrefrence
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
                    newImage.put("problemImage", downloadUrl.toString());
                    mUserDatabase.updateChildren(newImage);

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
            mImageProblem.setImageURI(resultUri);
        }
    }
}
