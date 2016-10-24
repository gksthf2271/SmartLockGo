package com.eum.ssrgo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by KHR on 2016-10-19.
 */
@IgnoreExtraProperties
public class Riding {

    public Double latitude;
    public Double longitude;
    public String time;



    public Riding() {
    }

    /*public Riding(Float latitude, Float longitude) {

        ArrayList<Pair<Float,Float>> array = null;
        Pair<Float,Float> latlng= null;

        array.add(latlng);

        this.latitude = latitude;
        this.longitude = longitude;
    }*/

    public Riding(Double latitude, Double longitude, String time) {

        /*ArrayList<Pair<Float,Float>> array = null;
        Pair<Float,Float> latlng= null;

        array.add(latlng);*/

        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;

    }

}