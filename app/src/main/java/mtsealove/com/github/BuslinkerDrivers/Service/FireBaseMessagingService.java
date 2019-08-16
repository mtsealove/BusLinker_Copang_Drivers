package mtsealove.com.github.BuslinkerDrivers.Service;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import mtsealove.com.github.BuslinkerDrivers.Entity.Load;
import mtsealove.com.github.BuslinkerDrivers.LoadActivity;
import mtsealove.com.github.BuslinkerDrivers.MainActivity;
import mtsealove.com.github.BuslinkerDrivers.R;
import mtsealove.com.github.BuslinkerDrivers.RunInfoActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            try{
                JSONObject jsonObject=new JSONObject(remoteMessage.getData());
                Log.e("테스트", String.valueOf(jsonObject.getInt("charge")));
                Log.e(TAG, jsonObject.toString());
                remoteMessage.getData().get("charge");
            }catch (Exception e){
                e.printStackTrace();
            }

            if (/* Check if data needs to be processed by long running job */ true) {
            } else {
                handleNow();
            }

        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }


    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent;
        if (isRunning(getBaseContext())) {  //애플리케이션이 실행중이면
            intent = new Intent(this, RunInfoActivity.class);
        }
        else    //실행중이 아니면
            intent = new Intent(this, LoadActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setColor(0xffffcc00)
                        .setSmallIcon(R.drawable.icon_logo_negative)
                        .setContentTitle("운행정보")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private boolean isRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoList = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfoList.size(); i++) {
            if (processInfoList.get(i).processName.equals(context.getPackageName()))
                return true;
        }
        return false;
    }
}