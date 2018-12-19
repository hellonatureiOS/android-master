package net.hellonature;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.List;

/**
 * Created by james on 2014. 2. 4..
 */
public class BaseUtil {
    private static final int ANIMATION_POP = 1;
    private static final int ANIMATION_LEFT = 2;


    public static void goToNextActivity(Activity activity, Activity nextActivity) {
        goToNextActivity(activity, nextActivity, false);
    }

    public static void goToNextActivity(Activity activity, Activity nextActivity, Boolean isFinish) {
        goToNextActivity(activity, nextActivity, isFinish, ANIMATION_POP);
    }

    public static void goToNextActivity(Activity activity, Activity nextActivity, Boolean isFinish, int animationType) {
        Intent nextIntent = new Intent(activity, nextActivity.getClass());
//        nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(nextIntent);
        if (isFinish)
            activity.finish();

        switch (animationType) {
            case ANIMATION_LEFT:
                activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case ANIMATION_POP:
            default:
                break;
        }
    }

    public static void makeToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }

    public static void makeToast(Context context, int rid) {
        Toast.makeText(context, context.getText(rid), Toast.LENGTH_LONG).show();
    }


    public static void makeAlert(Activity activity, String title, String message, DialogInterface.OnClickListener yes) {
        if (!activity.isFinishing()) {
            new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, yes)
                .setNegativeButton(android.R.string.no, null).show();
        }
    }

    public static void makeAlertNoCancel(Activity activity, String title, String message, DialogInterface.OnClickListener yes) {
        if (!activity.isFinishing()) {
            new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, yes)
                    .setCancelable(false).show();
        }
    }

    public static void makeAlert(Activity activity, String title, String message, DialogInterface.OnClickListener yes, DialogInterface.OnClickListener no, int yesStringId, int noStringId) {
        new AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(yesStringId, yes)
            .setNegativeButton(noStringId, no).show();
    }

    public static void makeAlert(Activity activity, String title, String message, DialogInterface.OnClickListener yes, DialogInterface.OnClickListener or, DialogInterface.OnClickListener no, int yesStringId, int orStringId, int noStringId) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(orStringId, or)
                .setPositiveButton(yesStringId, yes)
                .setNegativeButton(noStringId, no).show();
    }

    public static boolean isPackageExists(@NonNull final Context context, @NonNull final String targetPackage) {
        List<ApplicationInfo> packages = context.getPackageManager().getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (targetPackage.equals(packageInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

}
