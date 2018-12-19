package net.hellonature;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.util.ArrayList;


public class PermissionActivity extends Activity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("Permission", MODE_PRIVATE);
        setContentView(R.layout.activity_permission);
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_scale);
        addPermissionListener();

    }

    private void addPermissionListener(){
        Button confirm = findViewById(R.id.button_confirm);
        final PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                nextActivity();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                finish();
            }
        };

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TedPermission.with(PermissionActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }});

    }

    private void nextActivity(){
        finish();
        Intent intent = new Intent(PermissionActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
