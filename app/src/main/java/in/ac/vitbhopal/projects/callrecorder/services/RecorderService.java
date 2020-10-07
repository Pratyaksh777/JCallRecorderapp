package in.ac.vitbhopal.projects.callrecorder.services;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.Nullable;

import in.ac.vitbhopal.projects.callrecorder.recorder.AbstractRecorder;

public class RecorderService extends AccessibilityService {
    AbstractRecorder recorder; //TODO::
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() { }

}
