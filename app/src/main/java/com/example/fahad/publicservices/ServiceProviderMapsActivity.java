package com.example.fahad.publicservices;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
        RoutingListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private Switch WorkingSwitch ;
    LocationRequest mLocationRequest;
    private Button btnComplete , gotIt , Submit;
    private String CustomerID = "";
    private Boolean isLoggingOut = false;
    SupportMapFragment mapFragment;
    private LinearLayout CustomerInfo ;
    private RoundKornerLinearLayout CustomerProblem , payment;
    private ImageView CustomerProblemImage;
    private TextView CustomerTextProblem , CustomerPhone , CustomerName;
    private String Price;
    private EditText edtPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        polylines = new ArrayList<>();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //start map

        CheckingLocationConnection();
        CheckingInternetConnection();


        CustomerInfo = (LinearLayout)findViewById(R.id.userinfo);
        CustomerProblem = (RoundKornerLinearLayout)findViewById(R.id.userproblem);
        payment = (RoundKornerLinearLayout)findViewById(R.id.payment);
        CustomerProblemImage = (ImageView) findViewById(R.id.userimageProblem);
        CustomerTextProblem = (TextView) findViewById(R.id.userTextProblem);
        CustomerPhone = (TextView) findViewById(R.id.userphone);
        CustomerName = (TextView) findViewById(R.id.username);
        edtPrice = (EditText) findViewById(R.id.price);

        WorkingSwitch = (Switch) findViewById(R.id.workingS);
        WorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    connectServiceProvider();
                }else {
                    disconnectServiceProvider();
                }
            }
        });


        btnComplete =(Button)findViewById(R.id.btnComplete);
        btnComplete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                recordedWork();
                payment.setVisibility(View.VISIBLE);
            }
        });

        ////-----------------------------------

        gotIt = (Button)findViewById(R.id.gotIt);
        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerProblem.setVisibility(View.GONE);
            }
        });


        //-----------------------------------------

        Submit = (Button)findViewById(R.id.Submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrice();

            }
        });

        //-----------------------------------------


        getAssignedCustomer();
    }

//==================================================================================================

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
                    mapFragment.getMapAsync(ServiceProviderMapsActivity.this); //start map
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }

    }

    public void CheckingInternetConnection() {
        String title = "internet not found";
        String message = "Click Setting and enable internet";
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() == null) {
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

//==================================================================================================

    private void setPrice() {
        DatabaseReference historyRequestInfoDb = FirebaseDatabase.getInstance().getReference().child("history").child(requestId);
        Price = edtPrice.getText().toString();
        Map CustomerInfo = new HashMap();
        CustomerInfo.put("price", Price);

        if(Price.toString().equals("0") || Price.toString().equals("")){
            Toast.makeText(ServiceProviderMapsActivity.this , "Please add price" , Toast.LENGTH_SHORT).show();
        }else{
            payment.setVisibility(View.GONE);
            endWork();
            edtPrice.setText("");
        }
        historyRequestInfoDb.updateChildren(CustomerInfo);

    }

//==================================================================================================

    //get ID of Assigned Customer
    private void getAssignedCustomer(){
        String ServiceProviderId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderId).child("CustomerRequest").child("CustomerRequestID");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    CustomerID = dataSnapshot.getValue().toString();
                    getAssignedCustomerLocation();
                    getAssignedCustomerInfo();
                }else{
                    endWork();
                }}
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});}

//==================================================================================================

    Marker CustomerLocationMarker;
    private DatabaseReference CustomerLocationRef;
    private ValueEventListener CustomerLocationRefListener;
    private void getAssignedCustomerLocation(){

        CustomerLocationRef = FirebaseDatabase.getInstance().getReference().child("CustomerRequest").child(CustomerID).child("l");
        CustomerLocationRefListener = CustomerLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !CustomerID.equals("")){
                    List<Object> map=(List<Object>)dataSnapshot.getValue();

                    double LocationLat=0;
                    double LocationLng=0;
                    if (map.get(0)!= null){
                        LocationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1)!= null){
                        LocationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng CustomerLocationLatLng = new LatLng(LocationLat,LocationLng);
                    CustomerLocationMarker = mMap.addMarker(new MarkerOptions().position(CustomerLocationLatLng).title("Customer Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));

                    getRouteToMarker(CustomerLocationLatLng);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

//==================================================================================================

    private void getAssignedCustomerInfo(){
        CustomerInfo.setVisibility(View.VISIBLE);
        CustomerProblem.setVisibility(View.VISIBLE);
        DatabaseReference CustomerDatabase  = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(CustomerID);
        CustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String , Object> map=(Map<String , Object>)dataSnapshot.getValue();

                    if (map.get("TextProblem")!=null){
                        CustomerTextProblem.setText(map.get("TextProblem").toString());
                    }
                    if (map.get("ProblemImage")!=null) {
                        Glide.with(getApplication()).load(map.get("ProblemImage").toString()).into(CustomerProblemImage);
                    }
                    if (map.get("name")!=null){
                        CustomerName.setText(map.get("name").toString());
                    }
                    if (map.get("phone")!=null) {
                        CustomerPhone.setText(map.get("phone").toString());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});}

//==================================================================================================

    public void endWork(){
        erasePolylines();

        String ServiceProviderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ServiceProviderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderId).child("CustomerRequest");
        ServiceProviderRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(CustomerID);
        CustomerID = "";

        if(CustomerLocationMarker != null){
            CustomerLocationMarker.remove();
        }
        if (CustomerLocationRefListener != null){
            CustomerLocationRef.removeEventListener(CustomerLocationRefListener);
        }

        CustomerInfo.setVisibility(View.GONE);
        CustomerProblem.setVisibility(View.GONE);


    }

//==================================================================================================

    String requestId;
    private void recordedWork() {

        String ServiceProviderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ServiceProviderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(ServiceProviderId).child("history");
        DatabaseReference CustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(CustomerID).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");

        requestId = historyRef.push().getKey();
        ServiceProviderRef.child(requestId).setValue(true);
        CustomerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("ServiceProvider", ServiceProviderId);
        map.put("Customer", CustomerID);
        map.put("rating", 0); //defult
        map.put("timestamp", getCurrenTimestamp());
        historyRef.child(requestId).updateChildren(map);
    }
    private Long getCurrenTimestamp() { //long because it is number
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;

    }

//==================================================================================================

    private void getRouteToMarker(LatLng CustomerLocationLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),CustomerLocationLatLng)
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
            mMap.getUiSettings().setZoomControlsEnabled(true);

            String ServiceProviderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("ServicesProvidersAvailable");
            DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("ServicesProvidersWorking");
            GeoFire geoFireAvailable = new GeoFire(refAvailable);
            GeoFire geoFireWorking = new GeoFire(refWorking);

            switch (CustomerID){
                case "" :
                    geoFireWorking.removeLocation(ServiceProviderId);
                    geoFireAvailable.setLocation(ServiceProviderId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;
                default:
                    geoFireAvailable.removeLocation(ServiceProviderId);
                    geoFireWorking.setLocation(ServiceProviderId, new GeoLocation(location.getLatitude(), location.getLongitude()));
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

    private void connectServiceProvider(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ServiceProviderMapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void disconnectServiceProvider(){
        WorkingSwitch.setChecked(false);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        String ServiceProviderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ServicesProvidersAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(ServiceProviderId);
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
                    Toast.makeText(getApplicationContext(),"Please provide the permission",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!isLoggingOut){
            disconnectServiceProvider();
        }
    }


    @Override
    public void onRoutingFailure(RouteException e) {
    }
    @Override
    public void onRoutingStart() {
    }

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        polylines = new ArrayList<>();
        //add routes to the map.
        for (int i = 0; i <route.size(); i++) {

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
        for (Polyline line : polylines){ //for each line in polylines
            line.remove();
        }
        polylines.clear(); //clear the array
    }

    @Override
    public void onBackPressed() {
        if(CustomerID.equals("")) {
            super.onBackPressed();
        }else if(CustomerID != "") {
            Toast.makeText(ServiceProviderMapsActivity.this , "wait until the order is finished" , Toast.LENGTH_LONG).show();
        }else if(Price.toString().equals("0") || Price.toString() == ""){
            Toast.makeText(ServiceProviderMapsActivity.this , " add price" , Toast.LENGTH_LONG).show();
        }else{
            super.onBackPressed();
        }
    }


    public void kill(){
        String CustomerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ServiceProviderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(CustomerId).child("CustomerRequest");
        ServiceProviderRef.removeValue();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(CustomerID);
        CustomerID = "";

        if(CustomerLocationMarker != null){
            CustomerLocationMarker.remove();
        }
        if (CustomerLocationRefListener != null){
            CustomerLocationRef.removeEventListener(CustomerLocationRefListener);
        }

    }

    }

