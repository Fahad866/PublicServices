package com.example.fahad.publicservices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcminarro.roundkornerlayout.RoundKornerLinearLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button btn_Request;
    private LatLng CustomerLocation;
    private String Service;
    private Boolean requestBol = false;
    SupportMapFragment mapFragment;
    private Marker CustomerLocationMarker ;// delete marker when cancel
    private RatingBar mRatingBar;// rating bar for see rating for provider
    private LinearLayout  mServiceProviderInfoTop;
    private RoundKornerLinearLayout mServiceProviderInfo;
    private CircleImageView mServiceProviderImage;// can send image for problem
    private TextView mServiceProviderName ,mServiceProviderPhone ,mServiceProviderNameTop,mServiceProviderPhoneTop;
    private RadioGroup mRadioGroup;
    private Button btn_gotIt , btn_cancel ;
    private TextView RequestStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        btn_gotIt=(Button)findViewById(R.id.gotIt);
        mapFragment.getMapAsync(CustomerMapsActivity.this); //start map

        CheckingLocationConnection();
        CheckingInternetConnection();

        btn_cancel = (Button)findViewById(R.id.btn_cancle);
        mServiceProviderInfo = (RoundKornerLinearLayout)findViewById(R.id.seviceinfo);
        mServiceProviderInfoTop = (LinearLayout)findViewById(R.id.seviceinfotop);
        mServiceProviderImage = (CircleImageView) findViewById(R.id.seviceImge);
        mServiceProviderName = (TextView) findViewById(R.id.seviceName);
        mServiceProviderPhone  = (TextView) findViewById(R.id.servicphone);
        mServiceProviderNameTop= (TextView) findViewById(R.id.servicnametop);
        mServiceProviderPhoneTop  = (TextView) findViewById(R.id.servicphonetop);
        RequestStatus = (TextView)findViewById(R.id.RequestStatus);

        mRadioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        mRadioGroup.check(R.id.electration);

        mRatingBar=(RatingBar)findViewById(R.id.ratingBar);
        btn_gotIt=(Button)findViewById(R.id.gotIt);
        btn_Request= (Button)findViewById(R.id.request);

        btn_gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServiceProviderInfo.setVisibility(View.GONE);
                mServiceProviderInfoTop.setVisibility(View.VISIBLE);
            }
        });


        //========================================================================================================


        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
           public void onClick(View v) {
         if (requestBol) {
            cancel();
            Vibrator c = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            c.vibrate(1000);
         }
            }
        }
        );


        //========================================================================================================


        btn_Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestStatus.setVisibility(View.VISIBLE);
                btn_cancel.setEnabled(true);
                btn_Request.setEnabled(false);

                int selectId = mRadioGroup.getCheckedRadioButtonId();
                //create radio button
                final RadioButton radioButton =(RadioButton)findViewById(selectId);
                if (radioButton.getText()== null){
                    return;
                }
                Service = radioButton.getText().toString();
                requestBol = true;

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");

                //save location to database
                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));


                CustomerLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                CustomerLocationMarker = mMap.addMarker(new MarkerOptions().position(CustomerLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));


                 RequestStatus.setText("looking for service provider Available ...");
                 getClosestServiceProvider();
             }
        }
        );
    }

    public void CheckingLocationConnection() {
        String title = "GPS not found";
        String message = "Click Setting and enable GPS";
        LocationManager locationManager;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    mapFragment.getMapAsync(CustomerMapsActivity.this); //start map
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
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


    //========================================================================================================


    private int radius = 1;
    private boolean ServiceProviderFound = false;
    private String ServiceProviderFoundID ;
    GeoQuery geoQuery;
    private void getClosestServiceProvider(){
        DatabaseReference ServiceProviderLocation = FirebaseDatabase.getInstance().getReference().child("ServicesProvidersAvailable");

        GeoFire geoFire = new GeoFire(ServiceProviderLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(CustomerLocation.latitude,CustomerLocation.longitude),radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!ServiceProviderFound && requestBol) {
                    DatabaseReference MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(key);
                    MuserDatabase.addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String , Object> ServiceMap=(Map<String , Object>)dataSnapshot.getValue();
                                if (ServiceProviderFound){
                                    return;
                                }
                                if (ServiceMap.get("service").equals(Service)){
                                    ServiceProviderFound = true;
                                    ServiceProviderFoundID = dataSnapshot.getKey();
                                    DatabaseReference ServiceProviderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderFoundID).child("CustomerRequest");
                                    String CustomerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("CustomerRequestID",CustomerID);
                                    ServiceProviderRef.updateChildren(map);

                                    getServiceProviderLocation();
                                    getServiceProviderInfo();
                                    WorkEnded();

                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }

            @Override
            public void onGeoQueryReady() {
                if(!ServiceProviderFound)
                {
                    radius++;
                    getClosestServiceProvider();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    //========================================================================================================


    private Marker ServiceProviderMarker;
    private DatabaseReference ServiceProviderLocationRef;
    private ValueEventListener ServiceProviderLocationRefListener;

    private void getServiceProviderLocation(){
        ServiceProviderLocationRef = FirebaseDatabase.getInstance().getReference().child("ServicesProvidersWorking").child(ServiceProviderFoundID).child("l");
        ServiceProviderLocationRefListener = ServiceProviderLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&& requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;

                    RequestStatus.setText("Service Provider Found");

                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng ServiceProviderLatLng = new LatLng(locationLat,locationLng);
                    if(ServiceProviderMarker != null){
                        ServiceProviderMarker.remove();
                    }
                    ServiceProviderMarker = mMap.addMarker(new MarkerOptions().position(ServiceProviderLatLng).title("Your Service Provider").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                    Location loc1 = new Location("");
                    loc1.setLatitude(CustomerLocation.latitude);
                    loc1.setLongitude(CustomerLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(ServiceProviderLatLng.latitude);
                    loc2.setLongitude(ServiceProviderLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);
                    if (distance<1000){
                        RequestStatus.setText("Be Ready Service Provider So Close");
                        btn_cancel.setEnabled(false);
                    }else {
                        RequestStatus.setText("Service Provider Far from you:" + String.valueOf(distance)+"m");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    //========================================================================================================


    private void getServiceProviderInfo(){
        mServiceProviderInfo.setVisibility(View.VISIBLE);
        DatabaseReference CustomerDatabase  = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderFoundID);
        CustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String , Object> map=(Map<String , Object>)dataSnapshot.getValue();

                    if (map.get("profileImageUrl")!= null) {
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mServiceProviderImage);
                    }
                    if (map.get("name")!=null){
                        mServiceProviderName.setText(map.get("name").toString());
                        mServiceProviderNameTop.setText(map.get("name").toString());
                    }
                    if (map.get("phone")!=null) {
                        mServiceProviderPhone.setText(map.get("phone").toString());
                        mServiceProviderPhoneTop.setText(map.get("phone").toString());
                    }
                    // calculating provider rating use loop
                    int ratingSum = 0 ;
                    float ratingsTotal = 0;
                    float ratingAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    // avg
                    if (ratingsTotal != 0){
                        ratingAvg = ratingSum /ratingsTotal;
                        mRatingBar.setRating(ratingAvg);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    //========================================================================================================


    private DatabaseReference ServiceProviderHasEndedRef;
    private ValueEventListener ServiceProviderHasEndedRefListener;

    private void WorkEnded(){
        ServiceProviderHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderFoundID).child("CustomerRequest").child("CustomerRequestID");
        ServiceProviderHasEndedRefListener = ServiceProviderHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    endWork();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});
    }


    //========================================================================================================


    private  void cancel() {
        requestBol = false;
        geoQuery.removeAllListeners();
        if (ServiceProviderFoundID != null) {
            DatabaseReference ServiceProviderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderFoundID).child("CustomerRequest");
            ServiceProviderRef.removeValue();
            ServiceProviderFoundID = null;
        }
        String CustomerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(CustomerId);
        //
        if (CustomerLocationMarker != null) {
            CustomerLocationMarker.remove();
        }
        if (ServiceProviderMarker != null) {
            ServiceProviderMarker.remove();
        }

        Toast.makeText(getApplicationContext(),"The Request has been Canceled" ,Toast.LENGTH_LONG).show();

        mServiceProviderInfo.setVisibility(View.GONE);
        mServiceProviderInfoTop.setVisibility(View.GONE);
        mServiceProviderPhone.setText("");
        mServiceProviderName.setText("");
        mServiceProviderImage.setImageResource(R.drawable.profile);
        DatabaseReference ServiceDetails = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(CustomerId);
        ServiceDetails.child("ProblemImage").setValue("https://firebasestorage.googleapis.com/v0/b/publicservices-f6743.appspot.com/o/no_image.png?alt=media&token=1f18abbd-eb33-45d6-9d8e-ca1f522b42f8");
        ServiceDetails.child("TextProblem").setValue("No Description");
        btn_cancel.setEnabled(false);
        RequestStatus.setVisibility(View.GONE);
        btn_Request.setEnabled(true);
    }


    //========================================================================================================


    public void endWork() {
        requestBol = false;
        geoQuery.removeAllListeners();

        ServiceProviderHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderFoundID).child("CustomerRequest").child("CustomerRequestID");

        ServiceProviderLocationRef.removeEventListener(ServiceProviderLocationRefListener);
        ServiceProviderHasEndedRef.removeEventListener(ServiceProviderHasEndedRefListener);

        if (ServiceProviderFoundID != null) {
            DatabaseReference ServiceProviderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderFoundID).child("CustomerRequest");
            ServiceProviderRef.removeValue();
            ServiceProviderFoundID = null;
        }

        ServiceProviderFound = false;
        radius = 1;
        String CustomerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(CustomerId);

        if (CustomerLocationMarker != null) {
            CustomerLocationMarker.remove();
        }
        if (ServiceProviderMarker != null) {
            ServiceProviderMarker.remove();
        }


        mServiceProviderInfo.setVisibility(View.GONE);
        mServiceProviderInfoTop.setVisibility(View.GONE);
        mServiceProviderPhone.setText("");
        mServiceProviderName.setText("");
        mServiceProviderImage.setImageResource(R.drawable.profile);
        DatabaseReference ServiceDetails = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(CustomerId);
        ServiceDetails.child("ProblemImage").setValue("https://firebasestorage.googleapis.com/v0/b/publicservices-f6743.appspot.com/o/no_image.png?alt=media&token=1f18abbd-eb33-45d6-9d8e-ca1f522b42f8");
        ServiceDetails.child("TextProblem").setValue("No Description");
        btn_cancel.setEnabled(false);
        RequestStatus.setVisibility(View.GONE);
        btn_Request.setEnabled(true);
    }


    //========================================================================================================


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapsActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(getApplicationContext()!=null){
            mLastLocation = location;

            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //check that we have all Permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapsActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        //refresh the location every 1000ms
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if (grantResults.length >0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mapFragment.getMapAsync(this);//start map

                }else {
                    Toast.makeText(getApplicationContext(),"Please provide the permission",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }



    @Override
    public void onBackPressed() {
            if(ServiceProviderFoundID != null){
                Toast.makeText(CustomerMapsActivity.this , "Wait until the order is finished" , Toast.LENGTH_LONG).show();
            }else{
                super.onBackPressed();
            }
    }

    @Override
    protected void onDestroy() {
        if(ServiceProviderFoundID == null){
            super.onDestroy();
        }else {
            super.onDestroy();
            cancel();
        }

    }


}


