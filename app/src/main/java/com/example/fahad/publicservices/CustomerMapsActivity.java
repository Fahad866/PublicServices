package com.example.fahad.publicservices;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
//import android.location.LocationListener;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMapsActivity extends FragmentActivity implements OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks ,
        GoogleApiClient.OnConnectionFailedListener ,
        LocationListener
{


    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    SupportMapFragment mapFragment;
    private Button mRequest , gotIt;
    private LatLng pickupLocation;

    private Boolean requestBol = false;
    private Marker pickupMarker ;
    private RadioGroup mRadioGroup;
    private String distination;
    private String requestService;

    private LinearLayout mDriverInfo , DriverInfoTop;
    private ImageView mDriverProfileImage;
    private TextView mDriverName , mDriverPhone , mDriverNameTop , mDriverPhoneTop ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapsActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }else {
            mapFragment.getMapAsync(this);//start map
        }
        mDriverInfo = (LinearLayout)findViewById(R.id.DriverInfo);
        DriverInfoTop = (LinearLayout)findViewById(R.id.DriverInfoTop);
        mDriverProfileImage = (ImageView) findViewById(R.id.DriverProfileImage);
        mDriverName = (TextView) findViewById(R.id.DriverName);
        mDriverPhone  = (TextView) findViewById(R.id.DriverPhone);
        mDriverNameTop = (TextView) findViewById(R.id.DriverNameTop);
        mDriverPhoneTop  = (TextView) findViewById(R.id.DriverPhoneTop);

        //__________________________________________
        mRadioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        mRadioGroup.check(R.id.carfix);

        mRequest= (Button)findViewById(R.id.btn_call_worker);
        gotIt = (Button)findViewById(R.id.gotIt);

        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDriverInfo.setVisibility(View.GONE);
                DriverInfoTop.setVisibility(View.VISIBLE);
            }
        });

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestBol){
                    endWork();
                }else{
                    int selectId = mRadioGroup.getCheckedRadioButtonId();
                    //create radio button
                    final RadioButton radioButton =(RadioButton)findViewById(selectId);
                    if (radioButton.getText()== null){
                        return;
                    }
                    requestService = radioButton.getText().toString();

                    requestBol = true;

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                    pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));

                    mRequest.setText("Getting your Driver....");
                    getClosestService();



                }
            }
        });


    }

    //// for service info*/
    private int radius = 1;
    private boolean serviceFound = false;
    private String serviceFoundID ;


    GeoQuery geoQuery;
    private void getClosestService(){
        DatabaseReference serviclocation = FirebaseDatabase.getInstance().getReference().child("servierAvailable");

        GeoFire geoFire = new GeoFire(serviclocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude,pickupLocation.longitude),radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!serviceFound && requestBol) {
                    DatabaseReference MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(key);
                    MuserDatabase.addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String , Object> ServiceMap=(Map<String , Object>)dataSnapshot.getValue();
                                //if any an correct plase cheke name db
                                if (serviceFound){return;}
                                if (ServiceMap.get("service").equals(requestService)){
                                    serviceFound = true;
                                    serviceFoundID = dataSnapshot.getKey();
                                    DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceFoundID).child("CustomerRequest");
                                    //userID --- customerID
                                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("userRideID",userID);
                                    map.put("destination",distination);
                                    serviceRef.updateChildren(map);
                                    getserviceLocation();
                                    getserviceinfo();
                                    gethasWorkEnded();
                                    mRequest.setText("locking for service location ......");
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
                if(!serviceFound)
                {
                    radius++;
                    getClosestService();
                }
            }
            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    private Marker nServiceMarker;
    private   DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getserviceLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("serviceWorking").child(serviceFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&& requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    mRequest.setText("Driver Found");
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if(nServiceMarker != null){
                        nServiceMarker.remove();
                    }
                    nServiceMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("your driver").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);
                    float distance = loc1.distanceTo(loc2);

                    if (distance<100){
                        mRequest.setText("provider is here...." );// when the distance is 100 the provider is arrive
                    }else {
                        mRequest.setText("Worker found :" + String.valueOf(distance));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    private void getserviceinfo(){
        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference mUserDatabase  = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceFoundID);
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    //if alredy name same in db
                    Map<String , Object> map=(Map<String , Object>)dataSnapshot.getValue();
                    //if any an correct plase cheke name db
                    if (map.get("profileImageUrl")!=null) {
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mDriverProfileImage);
                    }
                    if (map.get("name")!=null){
                        mDriverName.setText(map.get("name").toString());
                        mDriverNameTop.setText(map.get("name").toString());
                    }
                    if (map.get("phone")!=null) {
                        mDriverPhone.setText(map.get("phone").toString());
                        mDriverPhoneTop.setText(map.get("phone").toString());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});} //contune foe displsy 4
    /// when finish work
    private DatabaseReference serviceHasEndedRef;
    private ValueEventListener serviceHasEndedRefListener;
    private void gethasWorkEnded(){
        serviceHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceFoundID).child("CustomerRequest").child("userRideID");
        serviceHasEndedRefListener = serviceHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{
                    endWork();

                    //end
                }}
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});}

    ///

    private  void endWork() {
        requestBol = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        serviceHasEndedRef.removeEventListener(serviceHasEndedRefListener);
        if (serviceFoundID != null) {
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceFoundID).child("CustomerRequest");
            driverRef.removeValue();
            serviceFoundID = null;
        }
        serviceFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if (pickupMarker != null) {
            pickupMarker.remove();
        }
        if (nServiceMarker != null) {
            nServiceMarker.remove();
        }
        mRequest.setText("you'r cancel call Again");
        //contuunie for display 5
        mDriverInfo.setVisibility(View.GONE);
        mDriverPhone.setText("");
        mDriverName.setText("");
        mDriverProfileImage.setImageResource(R.drawable.profile);

        //e
    }

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
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapsActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if (grantResults.length >0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mapFragment.getMapAsync(this);//start map

                }else {
                    Toast.makeText(getApplicationContext(),"Please provide the premission",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

}

