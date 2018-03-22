package com.example.fahad.publicservices;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
//import android.location.LocationListener;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
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
    LocationRequest mLocationRequest;
    private Button mLogout , mRequestStatus;
    private int status = 0;
    private String userID="" ;
    private Boolean isLoggingOut = false;
    private SupportMapFragment mapFragment;

    private LinearLayout mCustomerInfo;
    private ImageView mCustomerProfileImage;
    private TextView mCustomerName , mCustomerPhone , mCustomerDistination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);//start map
        polylines = new ArrayList<>();

        mCustomerInfo = (LinearLayout) findViewById(R.id.CustomerInfo);
        mCustomerProfileImage = (ImageView) findViewById(R.id.CustomerProfileImage);
        mCustomerName = (TextView) findViewById(R.id.CustomerName);
        mCustomerPhone = (TextView) findViewById(R.id.CustomerPhone);
        mCustomerDistination = (TextView) findViewById(R.id.CustomerDistination);

        String serviceId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceId).child("CustomerRequest").child("userRideID");

        mLogout = (Button) findViewById(R.id.btn_logout);
        mLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isLoggingOut = true;
                DisconnectServiceProvider();

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ServiceProviderMapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        getAssigmedService();
    }


    //checking for change inside db
    private void getAssigmedService(){
        String serviceId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceId).child("CustomerRequest").child("userRideID");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userID = dataSnapshot.getValue().toString();
                    getAssigmedUserPickupLocation();
                    getAssigmedUserDistination();
                    getAssigmedUserInfo();
                }else{
                    eresePolyline();
                    userID ="";
                    if(pickupMarker != null){
                        pickupMarker.remove();
                    }
                    if (servicePickupLocationRefListener != null){
                        servicePickupLocationRef.removeEventListener(servicePickupLocationRefListener);
                    }
                    mCustomerInfo.setVisibility(View.GONE);
                    mCustomerName.setText("");
                    mCustomerPhone.setText("");
                    mCustomerDistination.setText("Distination: --");
                    mCustomerProfileImage.setImageResource(R.drawable.profile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getAssigmedUserDistination(){
        String serviceId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProvider").child(serviceId).child("CustomerRequest").child("distination");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String distination = dataSnapshot.getValue().toString();
                    mCustomerDistination.setText("Distination: "+distination);

                }else{
                    mCustomerDistination.setText("Distination: --");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getAssigmedUserInfo() {
        mCustomerInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase  = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("name") != null) {
                        mCustomerName.setText(map.get("name").toString());
                    }

                    if (map.get("phone") != null) {
                        mCustomerPhone.setText(map.get("phone").toString());
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});} //contune foe displsy 4


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
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup location ").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
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
                .waypoints(new LatLng(mLastLocation.getLatitude() , mLastLocation.getLongitude()), pickupLatLng)
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

    private void DisconnectServiceProvider(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("servierAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }





    @Override
    protected void onStop() {
        super.onStop();

        if(!isLoggingOut){
            DisconnectServiceProvider();
        }

    }


    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    //Directions
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private void eresePolyline(){
        for(Polyline lines : polylines){
            lines.remove();
        }
        polylines.clear();
    }


}

