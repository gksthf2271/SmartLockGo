package com.eum.ssrgo;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static ArrayList t_key = new ArrayList();
    public ArrayList t_data_set = new ArrayList();
    public static HashMap<String,ArrayList> mapList;
    public static HashMap<String, ArrayList> mapList_time;
    public static HashMap<String, ArrayList> mapList_lat;
    public static HashMap<String, ArrayList> mapList_log;


    ProgressDialog dialog;
    boolean run = true;
    int count=1;

    //어답터 객체생성
    ListviewAdapter adapter ;
    public ListviewFragment(){

    }
    public ListviewFragment(ArrayList key,HashMap maplist,ArrayList data_set, HashMap maplist_t, HashMap maplist_lat, HashMap maplist_log ){
        t_key=key;
        mapList = (HashMap<String, ArrayList>) maplist.clone();
        mapList_time = (HashMap<String, ArrayList>) maplist_t.clone();
        mapList_lat = (HashMap<String, ArrayList>) maplist_lat.clone();
        mapList_log = (HashMap<String, ArrayList>) maplist_log.clone();
        t_data_set = data_set;
    }

    //MainActivity에서 데이터 가져옴
    public ListviewFragment(String num, String date, String section1, String section2) {

        t_num.add(num);
        t_time.add(date);
        t_latitude.add(section1);
        t_longitude.add(section2);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("onCreateView 실행", "");
        adapter = new ListviewAdapter();
        setListAdapter(adapter);

        for(int i=0; i<t_key.size(); i++)
        Log.d("date ","maplist "+ mapList.get(t_key.get(i)));

        ///   for (String key : mapList.keySet()) {
 /*       for(int i=0; i<t_key.size(); i++) {
            Log.d("f_test", String.valueOf(t_key.get(i)));
            //ArrayList t_list = mapList.get(t_key.get(i));
            t_list_time = mapList_time.get(t_key.get(i));
            t_list_lat = mapList_lat.get(t_key.get(i));
            t_list_log = mapList_log.get(t_key.get(i));
            for(int c=0; c<t_list_time.size(); c++){
            Log.d("f_test1",", t_list_time :" +  String.valueOf(t_list_time.get(c)) +
                    ", t_list_lat :" + String.valueOf(t_list_lat.get(c)) +
                    ", t_list_log :" + String.valueOf(t_list_log.get(c)));
            }

        }*/


        Log.e("F_Thread", "Thread start");
        //축척된 위도,경도값 getLocation함수로 전달
        ArrayList t_list_time = null;
        ArrayList t_list_lat = null;
        ArrayList t_list_log = null;


        int cot=0;
        for (String key : mapList.keySet()) {


            Log.d("f_test", String.valueOf(key));

            ArrayList t_list=  mapList.get(key);

            //t_list.add(mapList.get(key));
            Log.d("t_list 값 : ", String.valueOf(t_list.get(0)));

            getLocation(String.valueOf(t_list.get(1)), String.valueOf(t_list.get(2)));
            getLocation(String.valueOf(t_list.get(t_list.size() - 2)), String.valueOf(t_list.get(t_list.size() - 1)));

            adapter.addItem(String.valueOf(t_num.get(count)), String.valueOf(t_list.get(0)), String.valueOf(strArray.get(cot)), String.valueOf(strArray.get(cot+1)));

            cot = cot+2;
            count++;
        }




        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void getLocation(String slat, String slng){
        //주소 저장
//        strArray.clear();
        String str = null;
        Geocoder geocoder = new Geocoder(getActivity(), Locale.KOREA);

        //형변환  String -> Double
        double lat = Double.valueOf(slat).doubleValue();
        double lng = Double.valueOf(slng).doubleValue();
        Log.d("형변환 확인 ", String.valueOf(lat));

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

    @Override
    public void onListItemClick (ListView l, View v, int position, long id) {
            // get TextView's Text.
                ListViewItem item = (ListViewItem) l.getItemAtPosition(position);
                String c_num = item.getmNum() ;
                String c_date = item.getmDate() ;
                String c_sec1 = item.getmSection1();
                String c_sec2 = item.getmSection2();
        LinearLayout riding_list_layout = (LinearLayout) v.findViewById(R.id.riding_list_row);
        riding_list_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
            });

            // TODO : use item data.
    }

    public void timeThread() {
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading");
        dialog.setMessage("불러 오는 중 입니다.");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
        new Thread(new Runnable() {
            public void run() {
                while (run == true) {
                    // TODO Auto-generated method stub
                    try {
                        Thread.sleep(10000);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }
}