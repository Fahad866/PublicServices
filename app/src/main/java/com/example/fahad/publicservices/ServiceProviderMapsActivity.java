package com.example.fahad.publicservices;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
//import android.location.LocationListener;
import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcminarro.roundkornerlayout.RoundKornerLinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceProviderMapsActivity extends FragmentActivity implements OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks ,
        GoogleApiClient.OnConnectionFailedListener ,
        LocationListener ,
        RoutingListener
{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private LatLng destinationLatLang;
    private Switch workingS ;
    LocationRequest mLocationRequest;
    private int status = 0 ;
    private Button mWorkStatus , gotIt , Submit;
    private String userID="";//customerID
    private Boolean isLoggingOut = false;
    SupportMapFragment mapFragment;
    FrameLayout service_provider_map;

    //_____________________________
    //this for desplay the information user //can send photo and text 1
    private LinearLayout userinfo ;
    private RoundKornerLinearLayout userproblem , payment;
    private ImageView mUserproblemImage;// can send image for problem
    private TextView muserText ,mph,mname , mUserDestination ;//can send text
    private String mprice;
    private EditText price;
    // ____________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        polylines = new ArrayList<>();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ServiceProviderMapsActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }else {
            mapFragment.getMapAsync(this);//start map
        }
        //contune foe displsy 2


        userinfo = (LinearLayout)findViewById(R.id.userinfo);
        userproblem = (RoundKornerLinearLayout)findViewById(R.id.userproblem);
        payment = (RoundKornerLinearLayout)findViewById(R.id.payment);
        mUserproblemImage = (ImageView) findViewById(R.id.userimageProblem);
        muserText = (TextView) findViewById(R.id.userTextProblem);
        mph = (TextView) findViewById(R.id.userphone);
        mname = (TextView) findViewById(R.id.username);
        price = (EditText) findViewById(R.id.price);

        //__________________________________________

        workingS = (Switch) findViewById(R.id.workingS);
        workingS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    connectService();
                }else {
                    disconnectService();
                }
            }
        });

        //__________________________________________
        //for when finish the work
        mWorkStatus =(Button)findViewById(R.id.WorkStatus);
        mWorkStatus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (status){
                    case 1:
                        status=2;
                        mWorkStatus.setText("Completed");
                        break;
                    case 2:
                        recoredWork();
                        endWork();
                        payment.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        ////-----------------------------------

        gotIt = (Button)findViewById(R.id.gotIt);
        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userproblem.setVisibility(View.GONE);
            }
        });


        //-----------------------------------------

        Submit = (Button)findViewById(R.id.Submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setprice();
                payment.setVisibility(View.GONE);
            }
        });

        //-----------------------------------------


        getAssigmeduser();
    }

    private void setprice() {
        DatabaseReference historyRideInfoDb = FirebaseDatabase.getInstance().getReference().child("history").child(requestId);
        mprice = price.getText().toString();
        Map userInfo = new HashMap();
        userInfo.put("price", mprice);
        historyRideInfoDb.updateChildren(userInfo);
    }


    private void connectService(){

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ServiceProviderMapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    //checking for change inside db
    private void getAssigmeduser(){
        String serviceId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceId).child("CustomerRequest").child("userRideID");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    status = 1;
                    userID = dataSnapshot.getValue().toString();
                    getAssigmedUserPickupLocation();
                    //contune foe displsy 3
                    getAssigmedUserInfo();


                }else{
                    endWork();
                }}
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});}


    //contune foe displsy 4
    private void getAssigmedUserInfo(){
        userinfo.setVisibility(View.VISIBLE);
        userproblem.setVisibility(View.VISIBLE);
        DatabaseReference mUserDatabase  = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userID);
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    //if alredy name same in db
                    Map<String , Object> map=(Map<String , Object>)dataSnapshot.getValue();
                    //if any an correct plase cheke name db
                    if (map.get("Text")!=null){
                        muserText.setText(map.get("Text").toString());
                    }
                    if (map.get("problemImage")!=null) {
                        Glide.with(getApplication()).load(map.get("problemImage").toString()).into(mUserproblemImage);
                    }
                    if (map.get("name")!=null){
                        mname.setText(map.get("name").toString());
                    }
                    if (map.get("phone")!=null) {
                        mph.setText(map.get("phone").toString());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});} //contune foe displsy 4
    ///
    private  void endWork(){
        mWorkStatus.setText("Arrived");
        erasePolylines();

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(userid).child("CustomerRequest");
        driverRef.removeValue();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userID);
        userID = "";//userID == coustomerid

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (servicePickupLocationRefListener != null){
            servicePickupLocationRef.removeEventListener(servicePickupLocationRefListener);
        }
        //contuunie for display 5
        userinfo.setVisibility(View.GONE);
        userproblem.setVisibility(View.GONE);
        muserText.setText("");
        mUserproblemImage.setImageResource(R.drawable.carfix);
        //end
    }


    String requestId;
    private void recoredWork() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(userId).child("history");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userID).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        requestId = historyRef.push().getKey();
        serviceRef.child(requestId).setValue(true);
        userRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("ServiceProvider", userId);
        map.put("Customer", userID);
        map.put("rating", 0);//defult
        map.put("timestamp", getCurrenTimestamp());
        historyRef.child(requestId).updateChildren(map);
    }
        private Long getCurrenTimestamp() { //long becuse a number
            Long timestamp = System.currentTimeMillis()/1000;
            return timestamp;

        }





    //
    //here take listener and marker here becuse need delete
    Marker pickupMarker;
    private DatabaseReference servicePickupLocationRef;
    private ValueEventListener servicePickupLocationRefListener;
    private void getAssigmedUserPickupLocation(){

        servicePickupLocationRef = FirebaseDatabase.getInstance().getReference().child("CustomerRequest").child(userID).child("l");//USERID = CUSTOMERID
        servicePickupLocationRefListener = servicePickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !userID.equals("")){
                    List<Object> map=(List<Object>)dataSnapshot.getValue();
                    double LocationLat=0;
                    double LocationLng=0;
                    if (map.get(0)!= null){
                        LocationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1)!= null){
                        LocationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng pickupLatLng = new LatLng(LocationLat,LocationLng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup location").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));

                    getRouteToMarker(pickupLatLng);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void getRouteToMarker(LatLng pickupLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),pickupLatLng)
                .build();
        routing.execute();
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
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onLocationChanged(Location location) {
        if (getApplicationContext() != null) {
            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("servierAvailable");
            DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("serviceWorking");
            GeoFire geoFireAvailable = new GeoFire(refAvailable);
            GeoFire geoFireWorking = new GeoFire(refWorking);

            switch (userID){ // Customerid = userID
                case "" :
                    geoFireWorking.removeLocation(userId);
                    geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;
                default:
                    geoFireAvailable.removeLocation(userId);
                    geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;
            }
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ServiceProviderMapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    private void disconnectService(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("servierAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
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
        if(!isLoggingOut){
            disconnectService();
        }
    }
    //// for line between user and service
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null){
            Toast.makeText(this,"Error "+e.getMessage(),Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"Something want wrong  ",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

        }

    }
    @Override
    public void onRoutingCancelled() {
    }
    private void erasePolylines(){
        for (Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }
}