package in.ac.vitbhopal.projects.callrecorder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

@RequiresApi(api = Build.VERSION_CODES.P)
public class MainActivity extends AppCompatActivity {
    private static final String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            requestPermissions();
        }
    }


    public void requestPermissions() {
        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    RecorderConstants.REQUESTCODE_PERMISSIONS
            );
        } else {
            requestAccessibilityService();
        }
    }


    public void requestAccessibilityService() {
        if (!isAccessibilityEnabled()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, RecorderConstants.REQUESTCODE_ACCESSIBILITY);
        } else {
            moveTaskToBack(true);
        }
    }

    public boolean hasPermissions(String... permissions) {
        for (String perm: permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RecorderConstants.REQUESTCODE_ACCESSIBILITY:
                moveTaskToBack(true);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RecorderConstants.REQUESTCODE_ACCESSIBILITY:
                // Recursively force user to provide required permissions
                if (grantResults.length < permissions.length) {
                    requestPermissions();
                } else {
                    requestAccessibilityService();
                }
                break;
        }
    }

    private boolean isAccessibilityEnabled() {
        String prefString = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (prefString == null) return false;
        return prefString.contains(getApplicationContext().getPackageName()+ "/"+getApplicationContext().getPackageName() +"services.RecorderService");
    }
}