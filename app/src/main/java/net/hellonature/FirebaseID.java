package net.hellonature;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.FirebaseInstanceId;



import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;



/**
 * Created by hellonature on 2017. 2. 13..
 */

public class FirebaseID extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // ID 토큰 업데이트

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("Refreshed token: " + refreshedToken);
        MainActivity.setUserAgent();
        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
        //sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]




    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */

    private void sendRegistrationToServer(String token) {

        OkHttpClient client = new OkHttpClient();
        MediaType.parse("application/json");
        RequestBody body = new FormBody.Builder()
                .add("type", "regist")
                .add("uid", token)
                .build();

        //request
        Request request = new Request.Builder()
                .url("https://dev.hellonature.net/mobile_shop/mypage/push/fcm.php")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}