package com.example.fahad.publicservices;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class KillApp extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {//when remove app
        super.onTaskRemoved(rootIntent);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();//like disconnectService
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ServicesProvidersAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }
}

