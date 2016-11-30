package com.eum.ssrgo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by KHR on 2016-10-19.
 */
@IgnoreExtraProperties
public class GetRidingList {


    public Double latitude;
    public Double longitude;
    public String time;

    public Double getlatitude(){
        return latitude;
    }

    public Double getlongitude(){
        return longitude;
    }

    public String gettime(){
        return time;
    }

    public GetRidingList(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public GetRidingList(Double latitude, Double longitude, String time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }
}
