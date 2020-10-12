package in.ac.vitbhopal.projects.callrecorder.services;

import android.accessibilityservice.AccessibilityService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import androidx.annotation.RequiresApi;
import androidx.core.util.Consumer;
import in.ac.vitbhopal.projects.callrecorder.MainActivity;
import in.ac.vitbhopal.projects.callrecorder.RecorderConstants;
import in.ac.vitbhopal.projects.callrecorder.helper.PhoneState;
import in.ac.vitbhopal.projects.callrecorder.helper.PhoneStateChangeListener;
import in.ac.vitbhopal.projects.callrecorder.helper.PhoneStateObserver;
import in.ac.vitbhopal.projects.callrecorder.recorder.AbstractRecorder;
import in.ac.vitbhopal.projects.callrecorder.recorder.VersionedRecorderFactory;


@RequiresApi(api = Build.VERSION_CODES.N)
public class RecorderService extends AccessibilityService {
    private AbstractRecorder recorder;
    private PhoneStateChangeListener phoneStateChangeListener;
    @Override
    public void onCreate() {
        super.onCreate();
        initializeRecorder();
        initializeStateChangeHandler();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) { }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onInterrupt() { }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent restartIntent = new Intent(getApplicationContext(), getClass());
        restartIntent.setPackage(getPackageName());
        PendingIntent pendingServiceRestartIntent = PendingIntent.getService(getApplicationContext(), 1, restartIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, pendingServiceRestartIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recorder != null) {
            recorder.dispose();
        }
        if (phoneStateChangeListener != null) {
            phoneStateChangeListener.dispose();
        }
    }

    private void initializeRecorder() {
        try {
            recorder = VersionedRecorderFactory.getRecorder(getApplicationContext());
        } catch (IllegalArgumentException e) {
            Log.d(RecorderConstants.DEBUG_TAG, "Invalid android version detected! Shutting down...");
            stopSelf();
        }
    }

    private void initializeStateChangeHandler() {
        phoneStateChangeListener = new PhoneStateObserver(getApplicationContext());

        phoneStateChangeListener.onStateChange(new Consumer<PhoneState>() {
            @Override
            public void accept(PhoneState state) {
                if (recorder.isRecording() && state == PhoneState.IDLE) {
                    recorder.stop();
                } else if (!recorder.isRecording() && (state == PhoneState.CELLULAR_CALL || state == PhoneState.VoIP_CALL)) {
                    recorder.start();
                }
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(intent);
    }
}
