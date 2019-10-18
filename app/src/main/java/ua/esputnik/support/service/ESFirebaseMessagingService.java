package ua.esputnik.support.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import ua.esputnik.support.activity.LoginActivity;
import ua.esputnik.support.job.EsPushJob;

public class ESFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "ESFirebase";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "N : " + remoteMessage.getData());
        Log.d(TAG, "N : " + remoteMessage.getNotification());

        sendStatus(remoteMessage.getData());
    }

    private void sendStatus(Map<String, String> payload) {

        Map<String, Object> dd = new HashMap<>();
        for (Map.Entry<String, String> entry :  payload.entrySet()) {
            dd.put(entry.getKey(), entry.getValue());
        }

        dd.put("token",
            getSharedPreferences(LoginActivity.sharedPrefFile, MODE_PRIVATE)
                    .getString("token", "notoken"));



        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(EsPushJob.class)
                .setInputData(new Data.Builder().putAll(dd).build())
                .build();
        WorkManager.getInstance()
                .beginWith(work)
                .enqueue();

    }

}
