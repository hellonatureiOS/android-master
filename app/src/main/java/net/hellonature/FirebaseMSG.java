package net.hellonature;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

import trikita.log.Log;


/**
 * Created by hellonature on 2017. 2. 13..
 */

public class FirebaseMSG extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    private static final String FCM_START_URL1 = "start-url";
    private static final String FCM_START_URL2 = "start_url";
    private static final String FCM_PUSH_NUMBER = "push_no";
    private static final String CHANNEL_ID = "hn_channel";


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    private RemoteMessage.Notification notification;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, remoteMessage.getData().toString());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, remoteMessage.getNotification().toString());
        }
        notification = remoteMessage.getNotification();
        sendNotification(notification.getTitle(), notification.getBody(), remoteMessage.getData());
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param body FCM message body received.
     */
    private void sendNotification(String title, String body, Map payload) {

        //클릭했을 때 시작할 액티비티에게 전달하는 Intent 객체 생성
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.d(TAG, "sendNotification: " + intent);
        // 메세지 데이터 인지 판단
        if (payload.size() > 0) {
            Log.d(TAG, "sendNotification: " + payload);
            if(payload.get(FCM_START_URL1) != null) {
                // 시작 페이지 전달
                intent.putExtra(FCM_START_URL1, payload.get(FCM_START_URL1).toString());
            }
            if(payload.get(FCM_START_URL2) != null) {
                // 시작 페이지 전달
                intent.putExtra(FCM_START_URL2, payload.get(FCM_START_URL2).toString());
            }

            if(payload.get(FCM_PUSH_NUMBER) != null) {
                // 푸시 아이디 전달
                intent.putExtra(FCM_PUSH_NUMBER, payload.get(FCM_PUSH_NUMBER).toString());
            }
        }

        //클릭할 때까지 액티비티 실행을 보류하고 있는 PendingIntent 객체 생성
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon) //상태표시줄에 보이는 아이콘 모양
                //.setTicker("알림") //알림이 발생될 때 잠시 보이는 글씨
                .setContentTitle(title) //알림창에서의 제목
                .setContentText(body)  //알림창에서의 글씨
                //.setLargeIcon(R.drawable.lageicon)//상태바를 드래그하여 아래로 내리면 보이는 알림창(확장 상태바)의 아이콘 모양 지정
                .setAutoCancel(true) //클릭하면 자동으로 알림 삭제
                .setSound(defaultSoundUri)  //알림에 사운드 기능 추가
                .setContentIntent(pendingIntent); //PendingIntent 설정
                //.setVibrate(new long[]{ 0, 3000 }); // pattern의 첫번째 파라미터는 wait시간, 두번째는 진동시간(단위 ms)
        NotificationManager notificationManager = //Notification 객체 생성
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());  //NotificationManager가 알림(Notification)을 표시, id 0는 알림구분용
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        String id = CHANNEL_ID;
        CharSequence name = "Media playback";
        String description = "Media playback controls";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);
        channel.setShowBadge(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(channel);
    }
}
