package com.example.fahad.publicservices.serviceProviderH;

/**
 * Created by ammar on 3/31/2018.
 */

public class HisoryObjectS{
    private String rideId;
    private String time;



    public HisoryObjectS(String rideId, String time){
        this.rideId = rideId;
        this.time=time;
    }
    public String getRideId(){
        return rideId;
    }
    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getTime(){
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
}

