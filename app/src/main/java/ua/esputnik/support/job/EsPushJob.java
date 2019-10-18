package ua.esputnik.support.job;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import ua.esputnik.support.R;
import ua.esputnik.support.activity.MainActivity;
import ua.esputnik.support.db.AppDatabase;
import ua.esputnik.support.db.DatabaseInit;
import ua.esputnik.support.db.PushEntity;

public class EsPushJob extends Worker {

    private static final String TAG = "EsPushJob";
    private static final  SimpleDateFormat FORMAT = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss'Z'");

    private static final ScalarsConverterFactory FACTORY = ScalarsConverterFactory.create();
    private final EsService service;
    private final AppDatabase db;

    public EsPushJob(@NonNull Context context,
                     @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        /// TODO better to use httpUrlConnection which already is okhttp
        final ConnectionPool connectionPool = new ConnectionPool(1, 5, TimeUnit.SECONDS);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://esputnik.com/")
                .client(client)
                .addConverterFactory(FACTORY)
                .build();

        service = retrofit.create(EsService.class);
        db = DatabaseInit.getDB(context);
    }


    @Override
    public Result doWork() {
        Map<String, Object> keyValueMap = getInputData().getKeyValueMap();
        sendNotification(keyValueMap);

        String interaction = getInputData().getString("es_interaction_id");
        try {
            service.interactionUpdate(interaction,
                String.format("{\"token\":\"%s\",\"time\":\"%s\",\"status\":\"DELIVERED\"}",
                    keyValueMap.get("token"), FORMAT.format(new Date()))
                ).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.success();
    }


    public interface EsService {
        @retrofit2.http.Headers(
            {
                "Content-type: application/json"
            })
        @PUT("api/v1/interactions/{iid}/status")
        retrofit2.Call<String> interactionUpdate(
                @Path("iid") String iid,
                @Body String body);

    }

    private void sendNotification(Map<String, Object> pushData) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "defChannelId";//getString(R.string.default_notification_channel_id);

        String content = (String) pushData.get("es_content");
        String title = (String) pushData.get("es_title");


        Bitmap pushImage = null;
        try {
            URL url = new URL((String) pushData.get("es4_link"));
            pushImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            Log.e(TAG, "ERROR " + e.getMessage(), e);
        }


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.es_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setLargeIcon(pushImage)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(pushImage)
                        .bigLargeIcon(null))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


        rememberPushData(pushData, content, title, pushImage);

    }

    private void rememberPushData(Map<String, Object> pushData, String content, String title, Bitmap pushImage) {
        PushEntity pushEntity = new PushEntity();
        pushEntity.iid = (String) pushData.get("es_interaction_id");
        pushEntity.title = title;
        pushEntity.content = content;
        db.pushDao().insertAll(pushEntity);
    }


}
