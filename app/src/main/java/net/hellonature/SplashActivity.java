package net.hellonature;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import android.widget.LinearLayout.LayoutParams;

import com.gun0912.tedpermission.TedPermission;


public class SplashActivity extends Activity {

    ImageView splash;
    WebView bannerview;
    FrameLayout bannerframe;
    FrameLayout splashframe;
    Animation slideIn;
    Animation slideOut;

    SharedPreferences preferences;
    //private Boolean isGranted;

    private static final String FCM_START_URL1 = "start-url";
    private static final String FCM_START_URL2 = "start_url";
    private static final String SITE_BANNER = "https://www.hellonature.net/mobile_shop/app/index.html";
    private Boolean delay = false;
    private static final String TAG = "#SplashActivity";
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        preferences = getSharedPreferences("Permission", MODE_PRIVATE);
        //isGranted = TedPermission.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        bannerframe = (FrameLayout) findViewById(R.id.bannerframe);
        slideIn = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_right);
        slideOut = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_left);
        slideIn.setFillAfter(true);
        slideOut.setFillAfter(true);

        // 저장소 사용에 대한 설정이 없는 경우
        /*if(!isGranted){
            prevActivity();
            return;
        }*/

        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_scale);


        Intent intent = getIntent();
        if(intent != null) {

            // 앱푸시 접근
            String startURL = "";
            // 이전 버전
            if(intent.hasExtra(FCM_START_URL1)){
                startURL = intent.getStringExtra(FCM_START_URL1);
            }

            //  나중에 이버전으로 교체 할 예정
            if(intent.hasExtra(FCM_START_URL2)){
                startURL = intent.getStringExtra(FCM_START_URL2);
            }


            Log.i("startUrl","===="+startURL);
            Log.i("startUrl","===="+startURL);
            Log.i("startUrl","===="+startURL);
            Log.i("startUrl","===="+startURL);
            Log.i("startUrl","===="+startURL);
            if(!startURL.isEmpty()){
                nextActivity(startURL);
                return;
            }
        }


       // addSplash();
        addBanner();
    }


    @Override
    public void onBackPressed(){

    }
    private void addDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d("@@@@deepLink", deepLink.toString());
                            nextActivity(deepLink.toString());
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    /** 스플래시 화면 구성 **/
    public void addSplash(){
        removeSplash();
    }

    /** 스플래시 화면 제거 **/
    private void removeSplash(){
       /* new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(delay){
                            splashframe.startAnimation(slideOut);
                            bannerframe.startAnimation(slideIn);
                        } else {
                            nextActivity(null);
                        }
                    }
                }, 8000);
    */
        nextActivity(null);
    }


    /** 앱 배너 구성 **/
    public void addBanner(){

        bannerview = (WebView) findViewById(R.id.bannerview);
        WebSettings webSettings = bannerview.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess( true );
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT );
        bannerview.addJavascriptInterface(new WebAppInterface(SplashActivity.this), "Android");

        // alert 경고를 사용하기 위해 크롬클라이언트를 사용하야됨.
        bannerview.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result){
                return super.onJsAlert(view, url, message, result);
            }
        });

        bannerview.loadUrl(SITE_BANNER);
    }


    /** 앱 배너 열기 **/
    public void openBanner(Boolean bool) {
        delay = bool;
    }


    public void nextActivity(String link){
        Log.i("=-=-=-=-=-=-=","=-=-=-=-=-=-=");
        finish();
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(link != null){
            intent.putExtra("start-url", link);
        }
        startActivity(intent);
    }

}


