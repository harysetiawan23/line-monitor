package com.example.harry.linemonitor.helper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.harry.linemonitor.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInstanceServi";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putString(getString(R.string.fcm_token), refreshedToken).apply();
    }

    private void sendRegistrationToServer(String refreshedToken) {
        Log.d("TOKEN ", refreshedToken);
    }
}
