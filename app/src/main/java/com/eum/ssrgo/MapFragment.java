/*
package com.eum.ssrgo;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import static com.eum.ssrgo.R.layout.riding_list;


//Created by kimhansol on 2016-11-20.



public class MapFragment extends Fragment {

    boolean speed_run = true;
    private double mySpeed;
    private boolean ridingState = true;
    private static FloatingActionButton fab;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public Location mCurrentLocation;
    private static final String TAG = "MapFragment";
    ProgressDialog dialog;
    boolean run = true;
    private GoogleMap googleMap;
    private MapView mapView;
    private boolean mapsSupported = true;
    private GoogleMap mMap;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_map,container,false);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        initializeMap();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity().getApplicationContext());

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        initializeMap();
    }

    private void initializeMap() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
       final NavigationView navigationView = (NavigationView) getView().findViewById(R.id.nav_view);

        if (googleMap == null && mapsSupported) {
            mapView = (MapView) getActivity().findViewById(R.id.map);
            mapView.getMapAsync((OnMapReadyCallback) getActivity());
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            //gps.getLocation();

            fab = (FloatingActionButton) mapView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startLocationUpdates();
                    //fab버튼누르면 라이딩으로 바뀜.
                    navigationView.setCheckedItem(R.id.nav_riding);

                    //fab flag변수
                    ridingState = false;
                    startRiding();
                }
            });
            // GPS 사용유무 가져오기
            boolean isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            if (!isGPSEnabled) {
                showSettingsAlert();
            }

            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) getActivity())
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) getActivity())
                    .build();
            mGoogleApiClient.connect();

        }
    }

    public void showSettingsAlert() {

        if (!getActivity().isFinishing()) {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());

            alertDialog.setTitle("GPS 사용유무셋팅");
            alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");

            // OK 를 누르게 되면 설정창으로 이동합니다.
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            getActivity().startActivity(intent);
                        }
                    });
            // Cancle 하면 종료 합니다.
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();

            if (run == true) {
                run = false;
                Log.e(TAG, "Thread end");
                dialog.dismiss();
            }
        }
    }
    public void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) getActivity());
            Log.e("Permission ", " checkSelfPermission is able");
        } else {
            Log.e("Permission ", " checkSelfPermission is unable");
        }
    }

    public void startRiding() {
        final RelativeLayout layout_ridingData = (RelativeLayout) getView().findViewById(R.id.layout_ridingData);

        layout_ridingData.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);


        TextView txt_speed = (TextView) getView().findViewById(R.id.txt_speed);
        txt_speed.setText("Current Speed : " + mySpeed);

        if (speed_run == true) {
speed_run = false;


            Log.e(TAG, "Speed Thread end");

        }



        //DB테스트용 onClickListener
 mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                       @RequiresApi(api = Build.VERSION_CODES.N)
                                       @Override
                                       public void onMapClick(LatLng latLng) {

                                           FireBaseTest(latLng.latitude,latLng.longitude);

                                           mDatabase.child("users").child("TEST").child("Riding").child(riding_list.get(0).time.toString()).setValue(riding_list);
                                           Log.e(TAG, "riding_list : " +  riding_list.get(0).time.toString() +  riding_list.size());
                                       }
                                   });


        //라이딩 종료 버튼

        Button btn = (Button) getView().findViewById(R.id.btn_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //fab버튼누르면 홈으로 바뀜.

                navigationView.setCheckedItem(R.id.nav_home);
                layout_ridingData.setVisibility(View.GONE);
                mMap.clear();

                if(user_id != null) {
                    if(riding_list.size() != 0 ){

                        mDatabase.child("users").child("TEST").child("Riding").child(riding_list.get(0).time).setValue(riding_list);

                        Log.e(TAG,"riding_list.get(0) : " + riding_list.get(0));

                    }else {

                        Toast.makeText(getActivity(), "DATA upload : " + riding_list.get(0).latitude + riding_list.get(0).longitude + riding_list.size(), Toast.LENGTH_SHORT).show();

                        //riding list 초기화
                        riding_list = null;

                        //ridingState = riding fab flag변수임
                        ridingState = true;
                    }
                }
                fab.setVisibility(View.VISIBLE);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void FireBaseTest(Double latitude, Double longitude) {

        Riding riding = new Riding(latitude, longitude);
        //날짜 format 변경 해주는 부분
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String strNow = sdfNow.format(date);

        //라이딩 객체 init
        riding.time = strNow;
        riding_list.add(riding);
    }


}
*/
