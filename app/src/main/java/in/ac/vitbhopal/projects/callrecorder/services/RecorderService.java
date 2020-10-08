package in.ac.vitbhopal.projects.callrecorder.services;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import in.ac.vitbhopal.projects.callrecorder.RecorderConstants;
import in.ac.vitbhopal.projects.callrecorder.helper.PhoneStateChangeListener;
import in.ac.vitbhopal.projects.callrecorder.recorder.AbstractRecorder;
import in.ac.vitbhopal.projects.callrecorder.recorder.VersionedRecorderFactory;

public class RecorderService extends AccessibilityService {
    private AbstractRecorder recorder;
    private PhoneStateChangeListener phoneStateChangeListener;
    @Override
    public void onCreate() {
        super.onCreate();
        initializeRecorder();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) { }

    @Override
    public void onInterrupt() { }

    private void initializeRecorder() {
        try {
            recorder = VersionedRecorderFactory.getRecorder(getApplicationContext());
        } catch (IllegalArgumentException e) {
            Log.d(RecorderConstants.DEBUG_TAG, "Invalid android version detected! Shutting down...");
            stopSelf();
        }
    }



}
