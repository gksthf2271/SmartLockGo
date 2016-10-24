package com.eum.ssrgo;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    // TOKEN Refresh 해줘야 함
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        Log.e(TAG, "ID iS .. " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }


    private void sendRegistrationToServer(String token) {
    }
}