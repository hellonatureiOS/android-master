package net.hellonature;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class MainActivity extends Activity{

    static CookieManager cookieManager;
    static WebView webview;
    static Activity activity;
    static String currentVersion;
    static String uagt;
    static String appToken = "";
    Boolean isFinish = false;
    private static final String TAG = "#MainActivity";
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;

    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private static final int DIALOG_PROGRESS_WEBVIEW = 0;
    private static final int DIALOG_PROGRESS_MESSAGE = 1;

    private static final int DIALOG_ISP = 2;
    private static final int DIALOG_CARDAPP = 3;
    private static String DIALOG_CARDNM = "";
    public static final String SITE_DOMAIN = "https://www.hellonature.net";//운영
    //public static final String SITE_DOMAIN = "https://dev.hellonature.net";
    String PUSH_SITE_DOMAIN = "https://api.hellonature.net/push/update/";
    //String PUSH_SITE_DOMAIN = "https://dev.hellonature.net/push/update/";
    String LOADING_SITE_DOMAIN = "https://www.hellonature.net/mobile_shop/#/welcome";
    //String LOADING_SITE_DOMAIN = "https://dev.hellonature.net/mobile_shop/#/welcome";
    public static final String SITE_BASE = SITE_DOMAIN + "/mobile_shop";
    private static final String FCM_START_URL = "start-url";
    private static final String FCM_PUSH_NUMBER = "push_no";
    private android.app.AlertDialog alertIsp;
    private final static int MY_PERMISSIONS_READ_CONTACTS = 0x1;
    private Boolean isGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isGranted = TedPermission.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // 저장소 사용에 대한 설정이 없는 경우
        if(!isGranted){
            prevActivity();
            return;
        }


        prepare();
        createWebview();
        addInstapector();
        initialize();
        addDynamicLink();
        /****
         * 2018-11-15 스플래쉬 이동 by ds
         */
        moveSplash();
    }
    /****
     * 2018-11-15 고객 동의 화면 스플래쉬 액티비티에서 메인으로 옴겨옴 by ds
     */
    private void prevActivity(){
        finish();
        Intent intent = new Intent(MainActivity.this, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    /****
     * 2018-11-15 스플래쉬 이동 by ds
     */
    private void moveSplash()
    {
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(intent);
    }
    private void prepare(){
        activity = this;
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_scale);

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
                            Log.i("@@@@deepLink", deepLink.toString()+"==========");
                            Log.i("@@@@deepLink", deepLink.toString()+"==========");
                            Log.i("@@@@deepLink", deepLink.toString()+"==========");
                            Log.i("@@@@deepLink", deepLink.toString()+"==========");
                            Log.i("@@@@deepLink", deepLink.toString()+"==========");
                            Log.i("@@@@deepLink", deepLink.toString()+"==========");

                            startWebview(deepLink.toString());
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

    private void initialize(){
        Intent intent = getIntent();
        if(intent != null) {
            Log.i("======init======","=======init=======");
            Log.i("======init======","=======init=======");
            Log.i("======init======","=======init=======");
            Log.i("======init======","=======init=======");
            Log.i("======init======","=======init=======");
            // 앱푸시 수신확인 여부 전송
            if(intent.hasExtra(FCM_PUSH_NUMBER)){
                sendFcmRecord(intent.getStringExtra(FCM_PUSH_NUMBER));
            }

            // 앱푸시 시작 페이지
            if(intent.hasExtra(FCM_START_URL)){
                String startURL = intent.getStringExtra(FCM_START_URL);
                startWebview(startURL);
                return;
            }

            // 기타 시작 페이지
            if(intent.getData() != null) {
                Uri uri = intent.getData();

                if(uri != null) {
                    String scheme = uri.getScheme();
                    if(scheme != null){
                        // 딥 링크
                        if(scheme.equals(getResources().getString(R.string.app_scheme))){
                            String link = uri.getQueryParameter(getResources().getString(R.string.app_query_param));
                            if(link != null && !link.isEmpty()) {
                                startWebview(link);
                                return;
                            }
                        }
                        // 카카오 링크
                        else if(scheme.equals(getResources().getString(R.string.kakao_scheme))){
                            String query = uri.getQuery();
                            if(query != null && !query.isEmpty()) {
                                startWebview(SITE_DOMAIN + query);
                                return;
                            }
                        }
                    }
                }
            }
        }

        // 통상 접근시 앱배너 만들고 웹뷰 시작
        startWebview(SITE_BASE);

    }

    private void addInstapector(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
                Log.d("test", "debug");
            }
        }
    }


    private void createWebview(){
        webview = (WebView) findViewById(R.id.webview);
        final WebSettings webSettings = webview.getSettings();

        // 자바스크립트 허용
        webSettings.setJavaScriptEnabled(true);
        // 웹뷰 내 파일 엑세스 활성화 여부
        webSettings.setAllowFileAccess(true);
        // 멀티윈도우 사용 여부
        webSettings.setSupportMultipleWindows(true);
        // 플러그인 사용 여부
        webSettings.setPluginState(WebSettings.PluginState.ON);

        // 안드로이드 제공 줌 아이콘 사용 여부
        // 확대, 축소 기능 사용 여부
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // 자바스크립트 window.open을 사용 허용 여부
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 와이드 뷰포트 사용여부
        webSettings.setUseWideViewPort(true);
        // 웹뷰보다 큰 컨텐츠클 웹뷰에 맞출지 여부
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setAllowContentAccess(true);
        // 세션스토리지 허용 여부
        webSettings.setDatabaseEnabled(true);
        // 로컬 스토리지 허용 여부
        webSettings.setDomStorageEnabled(true);

        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            webSettings.setTextZoom(100);
        }

        final int sdk = android.os.Build.VERSION.SDK_INT;

        methodInvoke( webSettings, "setMixedContentMode", new Class[] { int.class }, new Object[] { webSettings.MIXED_CONTENT_ALWAYS_ALLOW });
        methodInvoke( cookieManager, "setAcceptThirdPartyCookies", new Class[] { WebView.class, boolean.class }, new Object[] { webview, true });

        webview.addJavascriptInterface(new WebAppInterface(MainActivity.this), "Android");
        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged (WebView view, int newProgress){

                if(newProgress >= 100){

                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                }
            }
            /*
            @Override
            public void onProgressChanged (WebView view, int newProgress){
                Log.i("Progress====",""+newProgress);
                boolean isLoading = false;
                if(newProgress >= 100 && !isFinish){
                    Log.i("newProgress====",""+newProgress);
                    isFinish = true;
                    isLoading = true;
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                }

                if(isLoading)
                {
                    finishedSplash();

                }
            }
            public void finishedSplash()
            {

                Log.i("finishedSplash====","finishedSplash====");
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }*/

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e("LogName", consoleMessage.message() + '\n' + consoleMessage.messageLevel() + '\n' + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                if (!activity.isFinishing()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("알림 메시지")
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            result.confirm();
                                        }
                                    })
                            .setCancelable(false)
                            .create()
                            .show();

                    return true;
                }
                return false;
            }

            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;
                Intent intent = (Intent) methodInvoke(fileChooserParams, "createIntent", null, null);
                startActivityForResult(intent, REQUEST_SELECT_FILE);
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView targetWebView = new WebView( MainActivity.this ); // pass a context
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView( targetWebView );
                resultMsg.sendToTarget();
                return true;
            }
        });

        webview.setWebViewClient(new WebViewClient() {
            /*****
             * 2018-11-15 런치이미지 종료시점 추가 by ds
             * url 호출을 https://www.hellonature.net/mobile_shop
             * 갔다가
             * https://www.hellonature.net/mobile_shop/#/home
             * 갔다가
             * https://www.hellonature.net/mobile_shop/#/welcome
             * 으로 최종으로 온다. 추후에 이문제 해결하면 모바일 로딩속도가 빨라질것 같다.
             * 지금은 최종 welcom 에 왔을떄 스플래쉬 이미지 꺼주기 실행.
             * @param view
             * @param url
             */



            @Override
            public void onPageFinished(WebView view, final String url)
            {

                if(url.contains(SITE_BASE))
                {
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            finishedSplash();
                        }
                    }, 2000);// 0.5초 정도 딜레이를 준 후 시작
                }

            }

            public void finishedSplash()
            {
                if(!isFinish)
                {
                    Log.i("finishedSplash====","finishedSplash====");
                    isFinish = true;
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }


            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                switch(errorCode) {
                    case ERROR_AUTHENTICATION: break;               // 서버에서 사용자 인증 실패
                    case ERROR_FILE_NOT_FOUND: break;               // 파일을 찾을 수 없습니다
                    case ERROR_CONNECT: break;                          // 서버로 연결 실패
                    case ERROR_FAILED_SSL_HANDSHAKE: break;    // SSL handshake 수행 실패
                    case ERROR_UNSUPPORTED_AUTH_SCHEME: break; // 지원되지 않는 인증 체계
                    case ERROR_IO: break;                              // 서버에서 읽거나 서버로 쓰기 실패
                    case ERROR_PROXY_AUTHENTICATION: break;   // 프록시에서 사용자 인증 실패
                    case ERROR_REDIRECT_LOOP: break;               // 너무 많은 리디렉션
                    case ERROR_TIMEOUT: break;                          // 연결 시간 초과
                    case ERROR_TOO_MANY_REQUESTS: break;     // 페이지 로드중 너무 많은 요청 발생
                    case ERROR_BAD_URL: // 잘못된 URL
                    case ERROR_FILE: // 일반 파일 오류
                    case ERROR_UNSUPPORTED_SCHEME:
                    case ERROR_HOST_LOOKUP:
                        webview.loadUrl(SITE_BASE);
                        break;           // 서버 또는 프록시 호스트 이름 조회 실패
                    case ERROR_UNKNOWN: // 일반 오류 ex)net_cache_miss
                        webview.loadUrl("javascript:history.go(-1)");
                        break;
                }

            }


            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent;
                try{
                    Log.d("<INIPAYMOBILE>", "intent url : " + url);
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    Log.d("<INIPAYMOBILE>", "intent getDataString : " + intent.getDataString());
                    Log.d("<INIPAYMOBILE>", "intent getPackage : " + intent.getPackage() );

                } catch (URISyntaxException ex) {
                    Log.e("<INIPAYMOBILE>", "URI syntax error : " + url + ":" + ex.getMessage());
                    return false;
                }

                Uri uri = Uri.parse(intent.getDataString());
                intent = new Intent(Intent.ACTION_VIEW, uri);


                if (Uri.parse(url).getScheme().equals("intent") || Uri.parse(url).getScheme().equals(R.string.app_name)) {
                    try {
                        Intent i = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        try {
                            startActivity(i);
                        } catch (Exception e1) {
                            paymentDialog(intent, url);
                        }
                    } catch (Exception e) {}

                    return true;
                } else if (!Uri.parse(url).getScheme().equals("http") && !Uri.parse(url).getScheme().equals("https")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (ActivityNotFoundException e) {
                        paymentDialog(intent, url);
                    }
                    return true;
                } else {
                }
                return false;
            }
        });
    }

    public static void setUserAgent() {
        if(webview == null){
            return;
        }
        webview.post(new Runnable() {
            @Override
            public void run() {
                // 앱 버전 체크
                String appVersion = BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE;
                Boolean appUpdated = true;


                if(currentVersion != null && !currentVersion.isEmpty()) {
                    appUpdated = appVersion.compareTo(currentVersion) == 0;
                    Log.d("@@@", appVersion + " : " + currentVersion );
                }

                //웹뷰 UserAgent 설정
                WebSettings webSettings = webview.getSettings();
                String userAgent = webSettings.getUserAgentString();
                if (uagt != null) {
                    userAgent = uagt;
                } else {
                    uagt = userAgent;
                }

                userAgent +=  " token/" + appToken;
                userAgent +=  " platform/android_app";
                userAgent +=  " updated/" + appUpdated;
                userAgent +=  " notification/" + enabledNotification();
                webSettings.setUserAgentString(userAgent);
                Log.d("@@@@", userAgent);
            }
        });
    }

    public void notificationSetting(){

        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$AppNotificationSettingsActivity");
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo().uid);
        intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName()); //for Android O
        startActivity(intent);
    }

    public void startWebview(final String url){
        Log.d(">>>", url);
        webview.post(new Runnable(){
            @Override
            public void run(){
                String oldToken = getPreference("appToken");
                appToken = FirebaseInstanceId.getInstance().getToken();
                if(appToken != null && !appToken.equals(oldToken)){
                    savePreference("appToken", appToken);
                    cookieManager.removeSessionCookie();
                    cookieManager.removeAllCookie();
                    webview.clearCache(true);
                }

                setUserAgent();
                webview.stopLoading();
                webview.loadUrl(url);
            }
        });
    }

    public boolean paymentDialog(Intent intent, String url) {
        if( url.startsWith("ispmobile://"))
        {
//            webview.loadData("<html><body></body></html>", "text/html", "euc-kr");
            showDialog(DIALOG_ISP);
            return false;
        }

        //현대앱카드
        else if( intent.getDataString().startsWith("hdcardappcardansimclick://"))
        {
            DIALOG_CARDNM = "HYUNDAE";
            Log.e("INIPAYMOBILE", "INIPAYMOBILE, 현대앱카드설치 ");
//            webview.loadData("<html><body></body></html>", "text/html", "euc-kr");
            showDialog(DIALOG_CARDAPP);
            return false;
        }

        //신한앱카드
        else if( intent.getDataString().startsWith("shinhan-sr-ansimclick://"))
        {
            DIALOG_CARDNM = "SHINHAN";
            Log.e("INIPAYMOBILE", "INIPAYMOBILE, 신한카드앱설치 ");
//            webview.loadData("<html><body></body></html>", "text/html", "euc-kr");
            showDialog(DIALOG_CARDAPP);
            return false;
        }

        //삼성앱카드
        else if( intent.getDataString().startsWith("mpocket.online.ansimclick://"))
        {
            DIALOG_CARDNM = "SAMSUNG";
            Log.e("INIPAYMOBILE", "INIPAYMOBILE, 삼성카드앱설치 ");
//            webview.loadData("<html><body></body></html>", "text/html", "euc-kr");
            showDialog(DIALOG_CARDAPP);
            return false;
        }

        //롯데 모바일결제
        else if( intent.getDataString().startsWith("lottesmartpay://"))
        {
            DIALOG_CARDNM = "LOTTE";
            Log.e("INIPAYMOBILE", "INIPAYMOBILE, 롯데모바일결제 설치 ");
//            webview.loadData("<html><body></body></html>", "text/html", "euc-kr");
            showDialog(DIALOG_CARDAPP);
            return false;
        }
        //롯데앱카드(간편결제)
        else if(intent.getDataString().startsWith("lotteappcard://"))
        {
//            DIALOG_CARDNM = "LOTTE";
            DIALOG_CARDNM = "LOTTEAPPCARD";
            Log.e("INIPAYMOBILE", "INIPAYMOBILE, 롯데앱카드 설치 ");
//            webview.loadData("<html><body></body></html>", "text/html", "euc-kr");
            showDialog(DIALOG_CARDAPP);
            return false;
        }

        //KB앱카드
        else if( intent.getDataString().startsWith("kb-acp://"))
        {
            DIALOG_CARDNM = "KB";
            Log.e("INIPAYMOBILE", "INIPAYMOBILE, KB카드앱설치 ");
//            webview.loadData("<html><body></body></html>", "text/html", "euc-kr");
            showDialog(DIALOG_CARDAPP);
            return false;
        }

        //하나SK카드 통합안심클릭앱
        else if( intent.getDataString().startsWith("hanaansim://"))
        {
            DIALOG_CARDNM = "HANASK";
            Log.e("INIPAYMOBILE", "INIPAYMOBILE, 하나카드앱설치 ");
//            webview.loadData("<html><body></body></html>", "text/html", "euc-kr");
            showDialog(DIALOG_CARDAPP);
            return false;
        }

        else if( intent.getDataString().startsWith("droidxantivirusweb"))
        {
            /*************************************************************************************/
            Log.d("<INIPAYMOBILE>", "ActivityNotFoundException, droidxantivirusweb 문자열로 인입될시 마켓으로 이동되는 예외 처리: " );
            /*************************************************************************************/

            Intent hydVIntent = new Intent(Intent.ACTION_VIEW);
            hydVIntent.setData(Uri.parse("market://search?q=net.nshc.droidxantivirus"));
            startActivity(hydVIntent);

        }
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (requestCode == REQUEST_SELECT_FILE)
            {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        else
            Toast.makeText(MainActivity.this.getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed(){
        if(webview.canGoBack())
        {
            webview.goBack();
            return;
        }
        finish();
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack())
//        {
//            webview.goBack();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    protected Dialog onCreateDialog(int id) {//ShowDialog

        switch(id){

            case DIALOG_PROGRESS_WEBVIEW:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("로딩중입니다. \n잠시만 기다려주세요.");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;

            case DIALOG_PROGRESS_MESSAGE:
                break;


            case DIALOG_ISP:

                alertIsp =  new android.app.AlertDialog.Builder(activity)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("알림")
                        .setMessage("모바일 ISP 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.")
                        .setPositiveButton("설치", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String ispUrl = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp";
                                webview.loadUrl(ispUrl);
//                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(activity, "(-1)결제를 취소 하셨습니다." , Toast.LENGTH_SHORT).show();
//                                finish();
                            }

                        })
                        .create();

                return alertIsp;

            case DIALOG_CARDAPP :
                return getCardInstallAlertDialog(DIALOG_CARDNM);

        }//end switch

        return super.onCreateDialog(id);

    }//end onCreateDialog

    private android.app.AlertDialog getCardInstallAlertDialog(final String coCardNm){

        final Hashtable<String, String> cardNm = new Hashtable<String, String>();
        cardNm.put("HYUNDAE", "현대 앱카드");
        cardNm.put("SAMSUNG", "삼성 앱카드");
        cardNm.put("LOTTE",   "롯데 모바일 결제");
        cardNm.put("LOTTEAPPCARD", "롯데 앱카드");
        cardNm.put("SHINHAN", "신한 앱카드");
        cardNm.put("KB", 	  "국민 앱카드");
        cardNm.put("HANASK",  "하나SK 통합안심클릭");
        //cardNm.put("SHINHAN_SMART",  "Smart 신한앱");

        final Hashtable<String, String> cardInstallUrl = new Hashtable<String, String>();
        cardInstallUrl.put("HYUNDAE", "market://details?id=com.hyundaicard.appcard");
        cardInstallUrl.put("SAMSUNG", "market://details?id=kr.co.samsungcard.mpocket");
        cardInstallUrl.put("LOTTE",   "market://details?id=com.lotte.lottesmartpay");
        cardInstallUrl.put("LOTTEAPPCARD",   "market://details?id=com.lcacApp");
        cardInstallUrl.put("SHINHAN", "market://details?id=com.shcard.smartpay");
        cardInstallUrl.put("KB", 	  "market://details?id=com.kbcard.cxh.appcard");
        cardInstallUrl.put("HANASK",  "market://details?id=com.ilk.visa3d");
        //cardInstallUrl.put("SHINHAN_SMART",  "market://details?id=com.shcard.smartpay");//여기 수정 필요!!2014.04.01

        android.app.AlertDialog alertCardApp =  new android.app.AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("알림")
                .setMessage( cardNm.get(coCardNm) + " 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.")
                .setPositiveButton("설치", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String installUrl = cardInstallUrl.get(coCardNm);
                        Uri uri = Uri.parse(installUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        Log.d("<INIPAYMOBILE>","Call : "+uri.toString());
                        try{
                            startActivity(intent);
                        }catch (ActivityNotFoundException anfe) {
                            Toast.makeText(activity, cardNm.get(coCardNm) + "설치 url이 올바르지 않습니다" , Toast.LENGTH_SHORT).show();
                        }
                        //finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(activity, "(-1)결제를 취소 하셨습니다." , Toast.LENGTH_SHORT).show();
//                        finish();
                    }
                })
                .create();

        return alertCardApp;

    }//end getCardInstallAlertDialog


    // 디바이스 예외 처리
    private final static Object methodInvoke(Object obj, String method, Class<?>[] parameterTypes, Object[] args) {
        try {
            Method m = obj.getClass().getMethod(method, parameterTypes);
            return m.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    private void sendFcmRecord(String fcmID) {
        RequestBody body = new FormBody.Builder()
                .add("uid", appToken)
                .build();

        Request request = new Request.Builder()
                .url(PUSH_SITE_DOMAIN + fcmID)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();


        // 클라이언트 개체를 만듬
        final OkHttpClient client = new OkHttpClient();
        // 새로운 요청을 한다
        client.newCall(request).enqueue(new Callback() {
            // 통신이 성공했을 때
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 통신 결과를 로그에 출력한다
                final String responseBody = response.body().string();
                Log.d("@@@@", responseBody);
            }

            // 통신이 실패했을 때
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d("@@@@", e.getMessage());
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        //checkMessage(intent);
    }

    public static boolean enabledNotification(){

        return NotificationManagerCompat.from(activity).areNotificationsEnabled();
    }

    public void notificationEnabled() {

        webview.post(new Runnable(){
            @Override
            public void run(){
                webview.loadUrl("javascript:events.dispatch('hn.app.notification.enabled'," + enabledNotification() + ")");
            }
        });
    }


    private String getPreference(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }

    // 값 저장하기
    private void savePreference(String key, String value){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // 값(Key Data) 삭제하기
    private void removePreference(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }

    // 값(ALL Data) 삭제하기
    private void removeAllPreference(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        notificationEnabled();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}

