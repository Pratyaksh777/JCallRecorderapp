package in.ac.vitbhopal.projects.callrecorder.services;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import in.ac.vitbhopal.projects.callrecorder.RecorderConstants;
import in.ac.vitbhopal.projects.callrecorder.recorder.AbstractRecorder;
import in.ac.vitbhopal.projects.callrecorder.recorder.Android10Recorder;
import in.ac.vitbhopal.projects.callrecorder.recorder.ProjectionHandler;
import in.ac.vitbhopal.projects.callrecorder.recorder.VersionedRecorderFactory;

public class RecorderService extends AccessibilityService {
    private AbstractRecorder recorder;

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
