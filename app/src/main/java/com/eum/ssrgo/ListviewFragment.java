package com.eum.ssrgo;

import android.app.ListFragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ListviewFragment extends ListFragment {

/*    private static final String Num = "num";
    private static final String Date = "date";
    private static final String Section1 = "section1";
    private static final String Section2 = "section2";*/

    //MainActivity에서 받아온 데이터들을 현 프래그먼트 리스트에 넣어두기위한 전역변수 선언
    public static ArrayList t_num = new ArrayList();
    public static ArrayList t_time = new ArrayList();
    public static ArrayList t_latitude = new ArrayList();
    public static ArrayList t_longitude = new ArrayList();
    public static ArrayList strArray = new ArrayList();


    //어답터 객체생성
    ListViewAdapter adapter ;

    public ListviewFragment(){

    }

    //MainActivity에서 데이터 가져옴
    public ListviewFragment(String num, String date, String section1, String section2) {

        t_num.add(num);
        t_time.add(date);
        t_latitude.add(section1);
        t_longitude.add(section2);



        Log.d("date ","넘버 :"+ num + ","+"날짜 :"+ date + ","+"lat :" + section1 + ","+"lon :" + section2);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("onCreateView 실행","");
/*        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,t_num) ;*/
        adapter = new ListViewAdapter();
        setListAdapter(adapter);

        //축척된 위도,경도값 getLocation함수로 전달
        for(int i =0; i<t_latitude.size(); i++) {
            getLocation(String.valueOf(t_latitude.get(i)),String.valueOf(t_longitude.get(i)));
        }

            int j=1;
        //아이템 추가.
        for(int i=0; i<t_num.size(); i++) {
           /* if(t_num.size() > 0 && t_num.size() <100)*/
            if(i%10==0) {
                adapter.addItem(String.valueOf(j), String.valueOf(t_time.get(i)), String.valueOf(strArray.get(i)), String.valueOf(strArray.get(i+2)));
                j++;
            }
        }


        return super.onCreateView(inflater, container, savedInstanceState);

    }
    public void getLocation(String slat, String slng){
        //주소 저장
        String str = null;
        Geocoder geocoder = new Geocoder(getActivity(), Locale.KOREA);

        //형변환  String -> Double
        double lat = Double.valueOf(slat).doubleValue();
        double lng = Double.valueOf(slng).doubleValue();
       /* Log.d("형변환 확인 ", String.valueOf(lat));*/

        List<Address> address;
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(lat, lng, 1);
                if (address != null && address.size() > 0) {
                    str = address.get(0).getAddressLine(0).toString();
                    strArray.add(str);
                }
            }
        } catch (IOException e) {
            Log.e("주소 오류", "주소를 찾지 못하였습니다.");
            e.printStackTrace();
        }

    }

/*    @Override
    public void onListItemClick (ListView l, View v, int position, long id) {
        // get TextView's Text.
        ListViewItem item = (ListViewItem) l.getItemAtPosition(position);


        // TODO : use item data.
    }*/
}