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


    ProgressDialog dialog;
    boolean run = true;

    //어답터 객체생성
    ListviewAdapter adapter ;
    public ListviewFragment(){

    }
    public ListviewFragment(HashMap maplist,ArrayList data_set){
        mapList = (HashMap<String, ArrayList>) maplist.clone();
        t_data_set = data_set;
    }

    //MainActivity에서 데이터 가져옴
    public ListviewFragment(ArrayList key,String num, String date, String section1, String section2) {

        t_key=key;
        t_num.add(num);
        t_time.add(date);
        t_latitude.add(section1);
        t_longitude.add(section2);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("onCreateView 실행","");
        adapter = new ListviewAdapter();
        setListAdapter(adapter);

        Log.d("date ","maplist "+ mapList.get(t_key.get(0))+"num :"+ t_num.get(0) +"날짜 :"+ t_time.get(0) + ","+"lat :" + t_longitude.get(0) + ","+"lon :" + t_longitude.get(0));

        int t=0;
        for (String key : mapList.keySet()) {
           Log.d("f_test", key);
            ArrayList t_list = mapList.get(key);

            Log.d("f_test", String.valueOf(t_list.get(t)));
            t++;
        }
        /*
        for(int i=0; i<t_key.size(); i++)
        {
            ArrayList ar = new ArrayList();
            ar.add(mapList.get(t_key.get(i)));

        }*/

        Log.e("F_Thread", "Thread start");
        timeThread();
        //축척된 위도,경도값 getLocation함수로 전달
  /*      for(int i =0; i<t_latitude.size(); i++) {
            getLocation(String.valueOf(t_latitude.get(i)),String.valueOf(t_longitude.get(i)));
        }*/
        if(run==true)
        {
            run=false;
            dialog.dismiss();
        }


        for(int i=0; i<t_key.size(); i++) {
            int j = 1;
                adapter.addItem(String.valueOf(j), String.valueOf(mapList.get(t_key.get(0))), String.valueOf("b"), String.valueOf("c"));
            j++;
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