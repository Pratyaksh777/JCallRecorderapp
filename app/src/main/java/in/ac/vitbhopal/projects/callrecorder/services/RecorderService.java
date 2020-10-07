package in.ac.vitbhopal.projects.callrecorder.services;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import in.ac.vitbhopal.projects.callrecorder.recorder.AbstractRecorder;
import in.ac.vitbhopal.projects.callrecorder.recorder.Android10Recorder;
import in.ac.vitbhopal.projects.callrecorder.recorder.ProjectionHandler;

public class RecorderService extends AccessibilityService {
    private AbstractRecorder recorder;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeRecorder();
    }


    private void initializeRecorder() {
        int CURRENT_VERSION = Build.VERSION.SDK_INT;
        if (CURRENT_VERSION >= Build.VERSION_CODES.Q) {
            MediaRecorder mediaRecorder = new MediaRecorder();
            recorder = new Android10Recorder(getApplicationContext(), mediaRecorder, new ProjectionHandler(getApplicationContext(), mediaRecorder));
        } else {
            throw new IllegalArgumentException("Invalid android version.");
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() { }

}
