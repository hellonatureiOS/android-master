package net.hellonature;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import trikita.log.Log;

public class WebAppInterface {
    Context mContext;

    public WebAppInterface(Context c) {
        mContext = c;
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void postMessage(String msg, String param) {
        Activity activity = (Activity) mContext;

        Log.i("@@@@postMessage", msg + " : " + param);
        switch(msg){
            case "close app banner" :
                ((SplashActivity) mContext).openBanner(false);
                ((SplashActivity) mContext).nextActivity(param);
                break;
            case "open app banner" :
                 ((SplashActivity) mContext).openBanner(true);
                break;
            case "store app version" :
//                ((MainActivity) mContext).currentVersion = param;
//                ((MainActivity) mContext).setUserAgent();
                break;
            case "push permission info":
                ((MainActivity) mContext).notificationEnabled();
                break;
            case "push permission setting":
                ((MainActivity) mContext).notificationSetting();
                break;

        }

    }

}
