package com.eum.ssrgo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by KHR on 2016-10-19.
 */
@IgnoreExtraProperties
public class RidingList  {

    public List<Riding> list;

    public RidingList() {
    }

    public RidingList(List<Riding> list){
        this.list = list;
    }


    public List<Riding> getList() {
        return list;
    }

    public void setList(List<Riding> list) {
        this.list = list;
    }
}