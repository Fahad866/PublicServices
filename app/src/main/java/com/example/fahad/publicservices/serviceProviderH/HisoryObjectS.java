package com.example.fahad.publicservices.serviceProviderH;



public class HisoryObjectS{
    private String RequestId;
    private String time;



    public HisoryObjectS(String RequestId, String time){
        this.RequestId = RequestId;
        this.time=time;
    }
    public String getRequestId(){
        return RequestId;
    }
    public void setRequestId(String RequestId) {
        this.RequestId = RequestId;
    }

    public String getTime(){
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
}

