package com.example.fahad.publicservices;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
//import android.location.LocationListener;
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

    private Button mLogout, mRequest;
    private LatLng pickupLocation;

    private Boolean requestBol = false;
    private Marker pickupMarker ;
    private RadioGroup mRadioGroup;
    private String distination;
    private String requestService;



    private LinearLayout mDriverInfo;
    private ImageView mDriverProfileImage;
    private TextView mDriverName , mDriverPhone ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLogout= (Button)findViewById(R.id.btn_logout);
        mRequest= (Button)findViewById(R.id.btn_call_worker);


        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        //__________________________________________
        mRadioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        mRadioGroup.check(R.id.carfix);

        mDriverInfo = (LinearLayout) findViewById(R.id.DriverInfo);
        mDriverProfileImage = (ImageView) findViewById(R.id.DriverProfileImage);
        mDriverName = (TextView) findViewById(R.id.DriverName);
        mDriverPhone = (TextView) findViewById(R.id.DriverPhone);


        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol){
                    requestBol = false;
                    geoQuery.removeAllListeners();
                    driverLocationRef.removeEventListener(driverLocationRefListener);


                    if (serviceFoundID != null){
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

                    if(pickupMarker  != null)
                        pickupMarker.remove();

                    if(nServiceMarker != null)
                        nServiceMarker.remove();


                    mRequest.setText("You canceled the request");

                    mDriverInfo.setVisibility(View.GONE);
                    mDriverName.setText("");
                    mDriverPhone.setText("");
                    mDriverProfileImage.setImageResource(R.drawable.profile);

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

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                distination = place.getName().toString();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });

    }
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
                    DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(key);
                    mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                Map<String, Object> ServiceMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (serviceFound) {
                                    return;
                                }
                                if (ServiceMap.get("service").equals(requestService)) {
                                    serviceFound = true;
                                    serviceFoundID = dataSnapshot.getKey();
                                    DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceFoundID).child("CustomerRequest");
                                    //userID --- customerID
                                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("userRideID", userID);
                                    map.put("distination", distination);
                                    serviceRef.updateChildren(map);
                                    getserviceLocation();
                                    getDriverInfo();
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

    private void getDriverInfo() {
            mDriverInfo.setVisibility(View.VISIBLE);
            DatabaseReference mCustomerDatabase  = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceFoundID);
            mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                        if (map.get("name") != null) {
                            mDriverName.setText(map.get("name").toString());
                        }

                        if (map.get("phone") != null) {
                            mDriverPhone.setText(map.get("phone").toString());
                        }

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }});} //contune foe displsy 4



    private Marker nServiceMarker;
    private DatabaseReference driverLocationRef;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
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
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
}
