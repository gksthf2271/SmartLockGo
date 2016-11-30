package com.eum.ssrgo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eum.ssrgo.ble.BluetoothLeService;
import com.eum.ssrgo.ble.DeviceControlActivity;
import com.eum.ssrgo.ble.SampleGattAttributes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    public  String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public  String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private int test;
    private int j=0;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothGatt mBluetoothGatt;
    private static final long SCAN_PERIOD = 10000;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    Fragment fragment;
    private static BluetoothGattCharacteristic characteristic;

    private boolean mConnected = true;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private static final String TAG = "MainActivity";
    private static Context thiscontext;
    int chk_num =0;


    //map
    private MapFragment mMapFragment;
    private GoogleMap mMap;
    private long UPDATE_INTERVAL = 10000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 sec */
    private String user_id = null;

    //poly line
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> arrayPoints = null;

    //GOOGLE API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public Location mCurrentLocation;
    private Marker mMarker;
    private Circle circle1;
    private Circle circle2;

    private NavigationView navigationView;
    private View navHeaderView;
    private LocationManager locationManager;
    private int requestcode = 1;


    //view static 변수.
    private static LinearLayout layout_userInfo;
    private static TextView tv_username;
    private static TextView tv_useremail;
    private static Button bt_login;
    private static Button bt_logout;
    private static Button bt_joinus;
    private static FloatingActionButton fab;
    private static RelativeLayout layout_ridingData;

    ProgressDialog dialog;
    boolean run = true;
    boolean speed_run = true;
    static String timerBuffer; // 04:11:15 등의 경과 시간 문자열이 저장될 버퍼 정의
    static int oldTime; // 타이머가 ON 되었을 때의 시각을 기억하고 있는 변수

    //파이어베이스에서 불러온값을 ListviewFragment로 보내기위한 전역변수

    public ArrayList f_time = new ArrayList();
    public ArrayList f_latitude = new ArrayList();
    public ArrayList f_longitude = new ArrayList();

    private long backKeyPressedTime = 0;

    private double mySpeed;

    public List<Riding> riding_list = new ArrayList<>();
    public List<RidingList> ridinglist_list = new ArrayList<>();
    public List<RidingList> ridingbig_list = new ArrayList<>();
    public ArrayList<GetRidingList> getriding_list = new ArrayList<>();
    private DatabaseReference mDatabase;

    // DB 날짜 저장 년/월/일
    private String Year=null;
    private String Month=null;
    private String Day=null;
    private String Time=null;
    private boolean mScanning;


    // 시작시간, 끝시간
    private String startTime=null;
    private String endTime=null;
    private String diffTime;

    // 이동거리
    private float[] distance = new float[1];
    private float[] moving_distance = new float[1];
    private float totaldistance = 0f;
    private String string_totaldistance = null;



    private void init() {
        //Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //View init
        navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        tv_username = (TextView) navHeaderView.findViewById(R.id.tv_UserName);
        tv_useremail = (TextView) navHeaderView.findViewById(R.id.tv_userEmail);
        LinearLayout tv_layout = (LinearLayout) navHeaderView.findViewById(R.id.tv_Layout);
        bt_joinus = (Button) navHeaderView.findViewById(R.id.bt_Join);
        bt_login = (Button) navHeaderView.findViewById(R.id.bt_Login);
        bt_logout = (Button) navHeaderView.findViewById(R.id.bt_Logout);

        layout_userInfo = (LinearLayout) findViewById(R.id.layout_userInfo);
        layout_ridingData = (RelativeLayout) findViewById(R.id.layout_ridingData);


        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.bt_Join:
                        Intent intent_ = new Intent(thiscontext, SignUpActivity.class);
                        startActivityForResult(intent_, requestcode);
                        break;
                    case R.id.bt_Login:
                        Intent intent1 = new Intent(thiscontext, SignInActivity.class);
                        startActivityForResult(intent1, requestcode);
                        break;
                    case R.id.bt_Logout:
                        setupView("LOGOUT");
                        break;
                }
            }
        };
        bt_joinus.setOnClickListener(onClickListener);
        bt_login.setOnClickListener(onClickListener);
        bt_logout.setOnClickListener(onClickListener);
    }

    //로그인,로그아웃시의 View Change
    private void setupView(String requestCode) {
        if (requestCode == "LOGIN") {
            bt_joinus.setVisibility(GONE);
            bt_login.setVisibility(GONE);
            bt_logout.setVisibility(View.VISIBLE);
            //layout_userInfo.setVisibility(View.VISIBLE);

        } else if (requestCode == "LOGOUT") {
            bt_joinus.setVisibility(View.VISIBLE);
            bt_login.setVisibility(View.VISIBLE);
            bt_logout.setVisibility(GONE);
            layout_userInfo.setVisibility(GONE);

        } else if (requestCode == "JOIN US") {
            bt_joinus.setVisibility(GONE);
            bt_login.setVisibility(GONE);
            bt_logout.setVisibility(View.VISIBLE);
            layout_userInfo.setVisibility(View.VISIBLE);
        }
    }

    /**
     * BLE 때문에 onResume함수 구현해두었음. 만약 지도 refresh 관련 문제 생기면 참조 할 것
     * */
    @Override
    protected void onResume(){
        super.onResume();
        Log.e(TAG,"onResume! ");

        //BLE Receiver 등록.
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        //Bluetooth 사용 설정 요청
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                //// TODO: 2016-11-03 BT설정 관련 Intent를 StartActivity로 해야하는지? onActivityResult에서 처리할 데이터는??
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        //setListAdapter(mLeDeviceListAdapter);
        //BLE Device Scan Start
        //scanLeDevice(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mServiceConnection != null) unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult is called!");
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                setupView("LOGIN");
                TextView tv_userName = (TextView) findViewById(R.id.tv_UserName);
                TextView tv_userEmail = (TextView) findViewById(R.id.tv_userEmail);

                //result가 ok인 경우
                String email = data.getStringExtra("user_email");
                String name = data.getStringExtra("user_name");
                user_id = data.getStringExtra("user_id");

                tv_userEmail.setText(email);
                tv_userName.setText(name + "님 환영합니다.");

                // 기록요약탭에 일단 user_id 값을 뿌리게 해봄;
                //// TODO: 2016-11-03 관련 작업 마무리 할 것. 안쓰면 지우고, View는 private static으로 선언하고 init()내에서 findViewById 해줄것.

            } else {
                Log.e(TAG, "resultCode is " + resultCode);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate!");

        mHandler = new Handler();

        ////////BLE ADAPTER INIT
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter.isEnabled()) Log.e (TAG,"BluetoothAdapter is enabled!!");
        if(mBluetoothAdapter == null) Log.e("TAG","Bluetooth adapter is null");
        /////////

        thiscontext = getApplicationContext();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Firebase token Get
        String myToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "My Token is :" + myToken);

        //fab init 및 listener
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationUpdates();
                //fab버튼누르면 라이딩으로 바뀜.
                navigationView.setCheckedItem(R.id.nav_riding);
                //Riding 시작
                startRiding();
            }
        });

        //navigation drawer의 toggle sync start.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Log.e(TAG, "nav is ok");
        navigationView.setCheckedItem(R.id.nav_home);
        //loading
        timeThread();

        //LocationServices를 사용하기 위해 ApiClient 연결
        mGoogleApiClient = new GoogleApiClient.Builder(thiscontext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        //MapAsync 시작
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.e(TAG, "mapFragment Async 성공!");



        //GPS사용 여부 체크 후 GPS설정창 띄워줌
        LocationManager locationManager = (LocationManager) thiscontext.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            showSettingsAlert();
        }
        init();
        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {

        if (enable) {
            Log.e(TAG,"ScanLeDevice is called!");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    public void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(thiscontext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.e("Permission ", " checkSelfPermission is able");
        } else {
            Log.e("Permission ", " checkSelfPermission is unable");
        }
    }


    public void onBackPressed() {
        // super.onBackPressed(); //지워야 실행됨

        AlertDialog.Builder d = new AlertDialog.Builder(this);

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            d.setMessage("정말 종료하시겠습니까?");
            d.setPositiveButton("예", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // process전체 종료
                    finish();
                }
            });
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            d.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        d.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                Log.e(TAG,"메뉴 scan 클릭");

                //GATT를 close해주고 그 뒤에 Service를 해제 해줘야함.
                mBluetoothGatt.close();
                mBluetoothLeService.disconnect();

                //mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                Log.e(TAG,"메뉴 stop 클릭");
                scanLeDevice(false);
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(0);
                final Intent intent = new Intent(this, DeviceControlActivity.class);

                EXTRAS_DEVICE_NAME = device.getName();
                EXTRAS_DEVICE_ADDRESS = device.getAddress();

                //BLE Service Bind
                Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

                //intent 처리 제외
                //intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                //intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                //startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*android.app.Fragment fragment = null;*/
        //fragment 한번초기화하기위한 조건문
        if(j==0) {
            fragment = null;
            j++;
        }
        FragmentManager fragmentManager = getFragmentManager();
        final RelativeLayout layout_summaryData = (RelativeLayout) findViewById(R.id.layout_summaryData);
        final RelativeLayout layout_ridingData = (RelativeLayout) findViewById(R.id.layout_ridingData);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        if (id == R.id.nav_home) {

            //전단계 fragment를 지우기위한 조건문
            if(fragment != null) {
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }

            Log.e(TAG, "네비 홈 눌렸다!");
            navigationView.setCheckedItem(R.id.nav_home);

            layout_ridingData.setVisibility(GONE);
            layout_summaryData.setVisibility(GONE);
            fab.setVisibility(View.VISIBLE);

            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startLocationUpdates();
                    //fab버튼누르면 라이딩으로 바뀜.
                    navigationView.setCheckedItem(R.id.nav_riding);
                    startRiding();
                }
            });
        } else if (id == R.id.nav_riding) {

            if(fragment != null) {
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }
            Log.e(TAG, "네비 라이딩 눌렸다!");

            navigationView.setCheckedItem(R.id.nav_riding);
            layout_ridingData.setVisibility(View.VISIBLE);
            layout_summaryData.setVisibility(GONE);
            fab.setVisibility(GONE);

            Button btn = (Button) findViewById(R.id.btn_cancel);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //fab버튼누르면 홈으로 바뀜.
                    navigationView.setCheckedItem(R.id.nav_home);
                    layout_ridingData.setVisibility(GONE);
                    fab.setVisibility(View.VISIBLE);

                }
            });


        } else if (id == R.id.nav_search) {
            if(fragment != null) {
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }
            Log.e(TAG, "네비 길찾기 눌렸다!");

            navigationView.setCheckedItem(R.id.nav_search);
            layout_ridingData.setVisibility(GONE);
            layout_summaryData.setVisibility(GONE);
            fab.setVisibility(GONE);


            // 데이터베이스에서 list 출력 테스트(test)
            for(int i=0 ; i < riding_list.size() ; i++) {
                Log.e(TAG," riding_list 번호 출력 : " + i);
                Log.e(TAG," riding_list.lat 출력 : " + riding_list.get(i).latitude);
                Log.e(TAG," riding_list.lon 출력 : " + riding_list.get(i).longitude);
                Log.e(TAG," riding_list.time 출력 : " + riding_list.get(i).time);
            }

        } else if (id == R.id.nav_record_summary) {
            if(fragment != null) {
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }
            Log.e(TAG, "네비 기록요약 눌렸다!");
            navigationView.setCheckedItem(R.id.nav_record_summary);
            layout_ridingData.setVisibility(GONE);
            layout_summaryData.setVisibility(View.VISIBLE);
            fab.setVisibility(GONE);
            RidingListGet(user_id,Year,Month,Day);
            drawPolyLine();
            diffOfDate();

        } else if (id == R.id.nav_record_height) {
            if(fragment != null) {
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }

            Log.e(TAG, "네비 고도정보 눌렸다!");
            navigationView.setCheckedItem(R.id.nav_record_height);
            layout_ridingData.setVisibility(GONE);
            layout_summaryData.setVisibility(GONE);
            fab.setVisibility(GONE);
            RidingListGet(user_id,Year,Month,Day);
            drawPolyLine();

        } else if (id == R.id.nav_record_section) {
            fragment = new ListviewFragment();
            Log.e(TAG, "네비 기록 목록 눌렸다!");
            navigationView.setCheckedItem(R.id.nav_record_section);

            layout_ridingData.setVisibility(GONE);
            layout_summaryData.setVisibility(GONE);
            fab.setVisibility(GONE);
            RidingListGet(user_id, Year, Month, Day);

            //기록목록 저장 번들
            Bundle bundle_date = new Bundle();
            Bundle bundle_lat = new Bundle();
            Bundle bundle_long = new Bundle();
/*            ListView listView;
            ListviewAdapter ListviewAdapter;
            ArrayList<ListviewFragment> data;

            listView = (ListView)findViewById(R.id.listview);
            data = new ArrayList<ListviewFragment>();*/

                for (int i = 0; i < f_time.size(); i++) {
                    Object obj_time = f_time.get(i);
                    bundle_date.putString("time", String.valueOf(obj_time));
                    Log.d("khs_bundle", String.valueOf(bundle_date));
                    fragment.setArguments(bundle_date);

                    Object obj_latitude = f_latitude.get(i);
                    bundle_lat.putString("latitude", String.valueOf(obj_latitude));
                    Log.d("khs_bundle2", String.valueOf(bundle_lat));
                    fragment.setArguments(bundle_lat);

                    Object obj_longitude = f_longitude.get(i);
                    bundle_long.putString("longitude", String.valueOf(obj_longitude));
                    Log.d("khs_bundle3", String.valueOf(bundle_long));
                    fragment.setArguments(bundle_long);
                    ListviewFragment.newInstance(String.valueOf(i),String.valueOf(bundle_date),String.valueOf(bundle_lat),String.valueOf(bundle_long));
                }

            fragmentTransaction.add(R.id.fragment_place,fragment);
            //fragmentTransaction.add(R.id.fragment_place, ListviewFragment.newInstance("1",String.valueOf(bundle_date),String.valueOf(bundle_lat),String.valueOf(bundle_long)));
            fragmentTransaction.commit();
/*            fragment = (ListviewFragment) getFragmentManager().findFragmentById(R.id.riding_list_row);
            fragment.getDate()*/


        } else if (id == R.id.nav_record_section_info) {
            if(fragment != null) {
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }
            navigationView.setCheckedItem(R.id.nav_record_section_info);

            layout_ridingData.setVisibility(GONE);
            layout_summaryData.setVisibility(GONE);
            fab.setVisibility(GONE);
            RidingListGet(user_id,Year,Month,Day);
            drawPolyLine();

        } else if (id == R.id.nav_edit) {
            navigationView.setCheckedItem(R.id.nav_edit);
            fragment = new EditFragment();

            layout_ridingData.setVisibility(GONE);
            layout_summaryData.setVisibility(GONE);
            fab.setVisibility(GONE);

            fragmentTransaction.replace(R.id.fragment_place, fragment );
            fragmentTransaction.commit();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //구글맵 관련 핸들링은 onMapReady가 끝난 이후에 해야함 .
    //맵 객체 생성이 끝난 후에 핸들링 해야 NXE를 피할 수 있다
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady!");

        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(37.56, 126.97);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));


        int permissionCheck = ContextCompat.checkSelfPermission(thiscontext, Manifest.permission.MAPS_RECEIVE);
        mMap.setMyLocationEnabled(true);


        //MyLocation 의 callback 함수
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                drawMarker(location);
            }
        };

        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(thiscontext, locationResult);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    /**
     * GPS 정보를 가져오지 못했을때
     * 설정값으로 갈지 물어보는 alert 창
     */
    public void showSettingsAlert() {

        if (!MainActivity.this.isFinishing()) {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);

            alertDialog.setTitle("GPS 사용유무셋팅");
            alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");

            // OK 를 누르게 되면 설정창으로 이동합니다.
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            MainActivity.this.startActivity(intent);
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

    //GOOGLE API가 CONNECTED 되는 경우
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.e(TAG, "onConnected!");
        // 여기까지 지도 좌표찍힌것

        setLocationRequest();

        if (ContextCompat.checkSelfPermission(thiscontext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        }
        mCurrentLocation = LocationServices.FusedLocationApi.
                getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            getLocationStatement(mCurrentLocation);

            Log.e(TAG, "mCurrentLocation Location Statement is Ready");
        } else Log.e(TAG, "mCurrentLocation is null!");

    }

    //Location을 요청할때 받아올 객체의 설정값(정확도/업데이트 간격/호출범위 등)
    public void setLocationRequest() {
        Log.e(TAG, "setLocationRequest!");

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setSmallestDisplacement(5);//location changed 호출 범위 지정(미터)
        // Request location updates
    }

    @Override
    public void onLocationChanged(Location location) {

        getLocationStatement(location);
        drawMarkerWithCircle(location);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //속도가 있을때만 찍으면 이동중일때만 onLocationChanged를 handling 할 수 있다
        if (location.getSpeed() != 0) {

            mySpeed = location.getSpeed() * 3.6;
            Toast.makeText(thiscontext, "SPEED is " + location.getSpeed() * 3.6, Toast.LENGTH_SHORT).show();

            MarkerOptions marker = new MarkerOptions();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
        }
        if (location.getSpeed() == 0) {
            mySpeed = 0;
        }

    }

    private void drawMarker(Location location) {

        //기존 마커 지우기
        mMap.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        //currentPosition 위치로 카메라 중심을 옮기고 화면 줌을 조정한다. 줌범위는 2~21, 숫자클수록 확대
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 17));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        if (run == true) {
            run = false;
            Log.e(TAG, "Thread end");
            dialog.dismiss();
        }

        //최초 circle은 안보이게 설정 해둠. 실제로 구현할때도 visible은 false로 되어야 함
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        CircleOptions Min_circleOptions = new CircleOptions()
                .center(latlng)
                .radius(2)
                .visible(false);
        circle1 = mMap.addCircle(Min_circleOptions);

        //첫번째 위치 최대값 범위 생성
        CircleOptions MAX_circleOptions = new CircleOptions()
                .center(latlng)
                .radius(5)
                .visible(false);
        circle2 = mMap.addCircle(MAX_circleOptions);

        //마커 추가
        mMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .visible(false));
        mCurrentLocation = location;
    }

    public void drawMarkerWithCircle(Location location) {

        //todo 1차 = GPS의 정확도, 2차 = 만약 WIFI측위랑 GPS측위 둘다 사용중일때 GPS측위의 정확도가 높으면 WIFI 측위정보 무시.

        Log.e(TAG, "drawMarkerWithCircle is called!");
        /*float[] min_distance = new float[2];*/

        float[] max_distance = new float[2];
        double CurrentLat = mCurrentLocation.getLatitude();
        double CurrentLon = mCurrentLocation.getLongitude();

        double thisLat = location.getLatitude();
        double thisLon = location.getLongitude();
        LatLng thisLatLon = new LatLng(location.getLatitude(), location.getLongitude());  // 첫 시작점에서 안바뀐다.... 이 값을 다른 클래스에 넘겨서 저장했다가 받아야겠다...

        LatLng CurrenLatLon = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());


        // double bef_lat; // 이전 경도
        double bef_lon; // 이전 위도
        double dist; // 거리 (현재위치 - 이전위치) 거리*//*

        if (CurrentLat != thisLat) {
            if (CurrentLon != thisLon) {
                Log.e(TAG, "Location data is not same");
                //첫번째 위치값의 최소Circle
                Location.distanceBetween(thisLat, thisLon, CurrentLat, CurrentLon, moving_distance);
                //Location.distanceBetween(thisLat,thisLon,circle2.getCenter().latitude,circle1.getCenter().longitude,max_distance);
                totaldistance = totaldistance + moving_distance[0];
                Log.e(TAG,"data : " + totaldistance );

                PolylineOptions rectOptions = new PolylineOptions()
                        .add(thisLatLon).add(CurrenLatLon);
                Polyline polyline = mMap.addPolyline(rectOptions);

                Riding riding = new Riding(thisLat,thisLon);

                //FireBaseTest(String userID, Double latitude, Double longitude);
                RidingListSet(riding.latitude, riding.longitude);

                mCurrentLocation.setLatitude(location.getLatitude());
                mCurrentLocation.setLongitude(location.getLongitude());

                /*//조건2. 첫번째 위치의 minCircle < 현재위치 < maxCircle 이어야 의미 있는 이동값.
                if(min_distance[0] > circle1.getRadius()){
                    Log.e(TAG,"첫번째 조건 통과");
                    if(max_distance[0] < circle2.getRadius()){
                        Log.e(TAG,"두번째 조건 통과, mCurrentLocation 위치 변경");
                        mCurrentLocation = location;
                        getLocationStatement(location);
                        //Log.e(mCurrentLocation.get)
                        //새로운 위치 지정. 위치이동
                        LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
                        //최소값 범위 생성
                        CircleOptions Min_circleOptions = new CircleOptions()
                                .center(latlng)
                                .radius(2);
                        circle1 = mMap.addCircle(Min_circleOptions);

                        //최대값 범위 생성
                        CircleOptions MAX_circleOptions = new CircleOptions()
                                .center(latlng)
                                .radius(5);
                        circle2 = mMap.addCircle(MAX_circleOptions);

                        //마커 생성
                        MarkerOptions markerOptions = new MarkerOptions().position(latlng);
                        mMarker = mMap.addMarker(markerOptions);

                        //poly Line 설정, 현재 NXE 발생함
                        *//**//*polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.RED);
                        polylineOptions.width(5);
                        arrayPoints.add(latlng);
                        polylineOptions.addAll(arrayPoints);
                        mMap.addPolyline(polylineOptions);*//**//*
                    }
                }*/
            }
        }
        /*else{
            //맨처음에는 반드시 값이 동일함.
            Log.e(TAG,"Location data is same");

            LatLng latlng = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            CircleOptions Min_circleOptions = new CircleOptions()
                    .center(latlng)
                    .radius(2);
            circle1 = mMap.addCircle(Min_circleOptions);

            //첫번째 위치 최대값 범위 생성
            CircleOptions MAX_circleOptions = new CircleOptions()
                    .center(latlng)
                    .radius(5);
            circle2 = mMap.addCircle(MAX_circleOptions);

            //초기 마커 생성
            MarkerOptions markerOptions = new MarkerOptions().position(latlng);
            mMarker = mMap.addMarker(markerOptions);
        }*/

    }

    //location의 각종 정보를 LOGGING
    public void getLocationStatement(Location location) {
        Log.e(TAG, "==========================================================================");
        Log.e(TAG, "Lon :" + location.getLongitude() + "  // Lat:" + location.getLatitude() + "  // Provider:" + location.getProvider() + "  // Speed:" + mySpeed);
        Log.e(TAG, "Accuracy:" + location.getAccuracy() + "  // Time:" + location.getTime() + "  // Bearing:" + location.getBearing()); // bearing = heading to location
        Log.e(TAG, "User_ID:" + user_id );
        Log.e(TAG, "==========================================================================");


    }

    //오류 처리 함수들
    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Loading을 표현하기 위해 사용하는 Thread
    public void timeThread() {
        dialog = new ProgressDialog(MainActivity.this);
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
        });/*.start();*/
    }

    protected void onStart() {
        super.onStart();
        Thread myThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        handler.sendMessage(handler.obtainMessage());
                        Thread.sleep(250);
                    } catch (Throwable t) {
                    }
                }
            }
        });

        myThread.start();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            TextView txt_speed = (TextView) findViewById(R.id.txt_speed);
            txt_speed.setText("Current Speed : " + mySpeed);
            TextView txt_distance = (TextView) findViewById(R.id.txt_distance);
            txt_distance.setText("이동거리 : " + totaldistance);
        }
    };

    /*private void FireBaseTest(String userId, String Riding, Float latitude, Float longitude) {
        Riding riding = new Riding(latitude, longitude);
        //GEO정보를 활용하려면..
        //child("RidingRecord").child(userId).child(RidingKey).setValue(Riding);
        //RidingKey값은 userId처럼 자동 생성 및 고유한 KEY여야 한다.
        //Riding 객체는 주행거리(시작점 / 끝점) 및 주행 시간이 저장되어야 한다.
        //User 객체에 Module의 마지막 위치(주차위치)가 저장되어야 한다.

        mDatabase.child("users").child(userId).child(Riding).setValue(riding);
    }*/


    private void RidingListSet(Double latitude, Double longitude) {

        Riding riding = new Riding(latitude, longitude);

        //날짜 format 변경 해주는 부분
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String strNow = sdfNow.format(date);

        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        Year = year.format(date);

        SimpleDateFormat month = new SimpleDateFormat("MM");
        Month = month.format(date);

        SimpleDateFormat day = new SimpleDateFormat("dd");
        Day = day.format(date);

        //라이딩 객체 init
        riding.time = strNow;
        riding_list.add(riding);
    }

    //// TODO: 2016-11-09 함수별로 역할이 정확해야한다.  자주쓰이게 될 함수이므로
    //// TODO: 2016-11-09  파라메터를 받아서 (Riding_list) 처리하도록 구현해야 한다.
    public void drawPolyLine(){

        for(int i=0 ; i < (riding_list.size())-1 ; i++) {

            LatLng ridingLatLng1 = new LatLng(riding_list.get(i).latitude, riding_list.get(i).longitude);
            LatLng ridingLatLng2 = new LatLng(riding_list.get(i+1).latitude, riding_list.get(i+1).longitude);
            Location.distanceBetween(riding_list.get(i).latitude, riding_list.get(i).longitude,riding_list.get(i+1).latitude ,riding_list.get(i+1).longitude , distance);

            PolylineOptions rectOptions = new PolylineOptions()
                    .add(ridingLatLng1).add(ridingLatLng2);
            Polyline polyline = mMap.addPolyline(rectOptions);


            Log.e(TAG, " 폴리라인 그리기1 ! : " + ridingLatLng1);
            Log.e(TAG, " 폴리라인 그리기2 ! : " + ridingLatLng2);
            Log.e(TAG, " 시간 : " + riding_list.get(i).time);
            Log.e(TAG, " 거리  : " + Arrays.toString(distance));
            Log.e(TAG, " 거리  : " + distance);

            totaldistance = totaldistance + distance[0];
            Log.e(TAG,"data : " + totaldistance );
            string_totaldistance = Float.toString(totaldistance);
        }

        /*for(int i = 0 ; i < distance.length ; i ++){

        }*/

    }

    public void diffOfDate(){

        try {
            for(int i=0 ; i < riding_list.size() ; i++) {
                startTime = riding_list.get(0).time;
                endTime = riding_list.get((riding_list.size())-1).time;


                SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                SimpleDateFormat hms = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                Date beginDate = ymdhms.parse(startTime);
                Date endDate = ymdhms.parse(endTime);

                // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴

                long diff = endDate.getTime() - beginDate.getTime();
                //long diffDays = diff / (60 * 60 * 1000);
                Date diffDays = new Date(diff);
                diffTime = hms.format(diffDays);
                Log.e(TAG, "시작=" + startTime);
                Log.e(TAG, "끝=" + endTime);
                Log.e(TAG, "날짜차이=" + diff);
                Log.e(TAG, "날짜차이=" + diffTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mLeDeviceListAdapter.addDevice(device);
                            mDeviceAddress = device.getAddress();
                            mLeDeviceListAdapter.notifyDataSetChanged();

                            mBluetoothGatt = device.connectGatt(thiscontext, false, new BluetoothGattCallback() {
                            });
                            Log.e(TAG, "==================OnLeScan=================");
                            Log.e(TAG, "DEVICE NAME :  " + device.getName());
                            Log.e(TAG, "DEVICE RSSI :  " + rssi);
                        }
                    });
                }
            };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    //BLE 가능 여부 확인 및 서비스 bind.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.

            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG,"onServicedisconnected!");
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Log.e(TAG,"Action is GATT_CONNECTED");
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.e(TAG,"Action is GATT_DISCONNECTED");

                //어댑터 disable은 아예 스마트폰의 ble를 꺼버림
                //mBluetoothAdapter.disable();

                invalidateOptionsMenu();
                //mBluetoothAdapter.enable();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.e(TAG,"Action is DISCOVERED");
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.e(TAG,"Action is DATA AVAILABLE");
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void displayData(String send_data){
        String push_data = "PUSH DATA \n";
        byte[] pushData = push_data.getBytes();
        characteristic.setValue(pushData);
        mBluetoothGatt.writeCharacteristic(characteristic);
        Log.e(TAG,"DATA IS :" + send_data + " RSSI IS : " + mBluetoothGatt.readRemoteRssi());
    }


    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = MainActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                Log.e(TAG,"mLeDevices is added!!");
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);

            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);

        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();

            Log.e(TAG, "===================================");
            Log.e(TAG, "GATT Services find! UUID is " + uuid.toString());

            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);

            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        if (mConnected != false) {
            if (mBluetoothLeService != null) {
                //특정 Characteristic을 등록 해준다.
                if (mGattCharacteristics != null) {
                    characteristic = mGattCharacteristics.get(2).get(0);
                    final int charaProp = characteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        if (mNotifyCharacteristic != null) {
                            mBluetoothLeService.setCharacteristicNotification(
                                    mNotifyCharacteristic, false);
                            mNotifyCharacteristic = null;
                        }
                        mBluetoothLeService.readCharacteristic(characteristic);
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);
                    }
                }
            }
        }
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void startRiding() {

        layout_ridingData.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);

        TextView txt_speed = (TextView) findViewById(R.id.txt_speed);
        txt_speed.setText("Current Speed : " + mySpeed);

        riding_list = new ArrayList<Riding>();  //라이딩을 시작하면 리스트를 비워준다

        //Speed_run은 항상 True상태, Riding중 휴식에 대한 처리를 위해서 Flag변수를 둔건지?
        //// TODO: 2016-11-03 Riding 휴식 시간 처리.
        if (speed_run == true) {
            /*speed_run = false;*/
            Log.e(TAG, "Speed Thread end");
        }

        //DB테스트용 onClickListener

        // 터치를 할 경우에 좌표와 시간을 받아와서 riding 리스트에 넣어서 종료를 누를시에
        // mDatabase.child("users").child(user_id).child(Year).child(Month).child(Day).child("Riding").push().setValue(riding_list); 이러한 경로로 들어감
        // push 를 해서 키값이 자동생성되고 그 안에는 주행기록들이 리스트로 들어있다.

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //RIding객체를 현재 좌표값으로 만들어주고, 그 객체를 List에 add만 해주면됨.
                //FireBaseTest(latLng.latitude,latLng.longitude);
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String strNow = sdfNow.format(date);
                Time = strNow;

                SimpleDateFormat year = new SimpleDateFormat("yyyy");
                Year = year.format(date);

                SimpleDateFormat month = new SimpleDateFormat("MM");
                Month = month.format(date);

                SimpleDateFormat day = new SimpleDateFormat("dd");
                Day = day.format(date);
                Riding riding = new Riding(latLng.latitude,latLng.longitude);

                riding.time = strNow;

                Log.e(TAG, "time : " +  riding.time + "      latitude : " + riding.latitude + "      longitude : " + riding.longitude );
                //맵을 터치시 해당 위/경도로 라이딩 리스트에 추가한다.
                riding_list.add(riding);
//                mDatabase.child("users").child("TEST").child(Year).child(Month).child(Day).setValue(myRiding.list.get(0).time);

            }


        });
        //라이딩 종료 버튼
        Button btn = (Button) findViewById(R.id.btn_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RelativeLayout layout_summaryData = (RelativeLayout) findViewById(R.id.layout_summaryData);
                final RelativeLayout layout_ridingData = (RelativeLayout) findViewById(R.id.layout_ridingData);


               /* RidingListGet();
                drawPolyLine();
                diffOfDate();*/

                //종료 버튼을 눌렀을 때 저장한다.
                layout_ridingData.setVisibility(View.GONE);

                mMap.clear();
                if(user_id != null) {
                    if(riding_list.size() != 0 ){
                        //맵을 터치시 저장했던 정보를 DB에 저장, 저장할 떄는 Riding List 자체를 넣는다.
                        //저장 후 확인, 이걸 호출해서 리스트 로그를 출력

                        //push로 주면 유니크한키로 자동 추가
                        mDatabase.child("users").child(user_id).child(Year).child(Month).child(Day).child("Riding").push().setValue(riding_list);
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("dbg start","1");
                                RidingListGet(user_id, Year, Month, Day);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Toast.makeText(MainActivity.this, "DATA upload : " +riding_list.size(), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "DATA empty", Toast.LENGTH_SHORT).show();
                    }
                }
                fab.setVisibility(View.VISIBLE);
                navigationView.setCheckedItem(R.id.nav_record_summary);
                //fab버튼누르면 홈으로 바뀜.


                //로그인상태로 주행기록이 남아야하며, 안그러면  java.lang.IndexOutOfBoundsException: Invalid index 0, size is 0
                startTime = riding_list.get(0).time;
                endTime = riding_list.get((riding_list.size())-1).time;

                layout_ridingData.setVisibility(View.GONE);
                layout_summaryData.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
                drawPolyLine();
                TextView sum_ridingdistance = (TextView) findViewById(R.id.sum_distance);
                sum_ridingdistance.setText(string_totaldistance);
                TextView sum_ridingstarttime = (TextView) findViewById(R.id.sum_starttime);
                sum_ridingstarttime.setText(riding_list.get(0).time);
                TextView sum_ridingendtime = (TextView) findViewById(R.id.sum_endtime);
                sum_ridingendtime.setText(riding_list.get(riding_list.size()-1).time);

                Log.e(TAG,"st :"+riding_list.get(0).time );
                Log.e(TAG,"et :"+riding_list.get(riding_list.size()-1).time);



            }
        });
    }

    /**
     * 파이어베이스에서 지정된 노드 타입으로 리스트를 얻어온다.
     * @param user_id  //유저 이름의 데이터
     * @param year  //해당 유저의 년도 데이터
     * @param month  //해당 유저의 월 데이터
     * @param day  //해당 유저의 일 데이터
     */
    public void RidingListGet(final String user_id, final String year, final String month, final String day) {;
        //https://firebase.google.com/docs/reference/android/com/google/firebase/database/DataSnapshot <<참고
        //데이터베이스 변수에 이벤트 설정, 이벤트 설정시 바로 현재 값을 동기화한다.
        mDatabase.child("users").child(user_id).child(year).child(month).child(day).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GenericTypeIndicator<Map<String, List<Riding>>> genericTypeIndicator = new GenericTypeIndicator<Map<String, List<Riding>>>() {};
                Map<String, List<Riding>> map = dataSnapshot.getValue(genericTypeIndicator);

                //List<List<Riding>> list = new ArrayList<List<Riding>>();


                for( String key : map.keySet() ){
                    Log.d("dbg", key + "///");
                    List<Riding> firebaseList = (List<Riding>) map.get(key);

                    //list.add(firebaseList);

                    for (int i =0; i < firebaseList.size(); i ++){

                        Log.d("dbg", "시간 : " + firebaseList.get(i).time +
                                "/위도" + firebaseList.get(i).latitude +
                                "/경도" + firebaseList.get(i).longitude);

                                f_time.add(firebaseList.get(i).time);
                                f_latitude.add(firebaseList.get(i).latitude);
                                f_longitude.add(firebaseList.get(i).longitude);

                                Log.d("copy", String.valueOf(f_time.get(i)));
                                Log.d("copy", String.valueOf(f_latitude.get(i)));
                                Log.d("copy", String.valueOf(f_longitude.get(i)));


                    }//리스트 출력
                    //firebaseList 리스트를 어딘가에 저장해서 사용하시면 됩니다.
                    //list.get(0);

                }
                //설정했던 동기화는 해제시켜준다.
                mDatabase.child("users").child(user_id).child(year).child(month).child(day).removeEventListener(this);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
