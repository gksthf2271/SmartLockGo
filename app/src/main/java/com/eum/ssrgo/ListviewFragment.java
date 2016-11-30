package com.eum.ssrgo;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ListviewFragment extends Fragment {

    private static final String Num = "num";
    private static final String Date = "date";
    private static final String Section1 = "section1";
    private static  final String Section2 = "section2";

    private  String mNum;
    private String mDate;
    private String mSection1;
    private String mSection2;



    public String getmNum() {
        return mNum;
    }

    public void setmNum(String mNum) {
        this.mNum = mNum;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmSection1() {
        return mSection1;
    }

    public void setmSection1(String mSection1) {
        this.mSection1 = mSection1;
    }

    public String getmSection2() {
        return mSection2;
    }

    public void setmSection2(String mSection2) {
        this.mSection2 = mSection2;
    }

    public ListviewFragment() {
    }

    public static ListviewFragment newInstance(String num, String date, String section1, String section2) {
        ListviewFragment fragment = new ListviewFragment();


            Bundle bundle_num = new Bundle();
            Bundle bundle_date = new Bundle();
            Bundle bundle_section1 = new Bundle();
            Bundle bundle_section2 = new Bundle();

            Log.d("khs_lat",bundle_date.getString("latitude"));

            bundle_num.putString(Num, num);
            bundle_date.putString(Date, date);
            bundle_section1.putString(Section1, section1);
            bundle_section2.putString(Section2, section2);

            fragment.setArguments(bundle_num);
            fragment.setArguments(bundle_date);
            fragment.setArguments(bundle_section1);
            fragment.setArguments(bundle_section2);

        for(int i=0; i<bundle_date.size() ;i++) {
            Log.d("khs_test :", String.valueOf(bundle_date));
        }
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
/*
        ArrayList mNum = new ArrayList();
        ArrayList mDate = new ArrayList();
        ArrayList mSection1 = new ArrayList();
        ArrayList mSection2 = new ArrayList();


        mNum.add(getArguments().getString(Num));
        mDate.add(getArguments().getString(Date));
        mSection1.add(getArguments().getString(Section1));
        mSection2.add(getArguments().getString(Section2));*/


      if(getArguments() != null) {
            mNum = getArguments().getString(Num);
            mDate = getArguments().getString(Date);
            mSection1 = getArguments().getString(Section1);
            mSection2 = getArguments().getString(Section2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            Log.d("Test", "Param1 : " + mNum + " Param2 :" + mDate + "Param3 : " + mSection1 + "Param4 : " + mSection2);


        return inflater.inflate(R.layout.listview_list, container, false);
    }
}