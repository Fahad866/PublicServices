package com.example.fahad.publicservices.historyRecycelerView;
public class HisoryObject{
    private String RequestId;
    private String time;



    public HisoryObject(String RequestId, String time){
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

