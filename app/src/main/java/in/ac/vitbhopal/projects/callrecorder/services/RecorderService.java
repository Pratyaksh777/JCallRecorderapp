package in.ac.vitbhopal.projects.callrecorder.services;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;
import androidx.core.util.Consumer;

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
    public void onInterrupt() { }

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

}
