package in.ac.vitbhopal.projects.callrecorder.recorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;

import in.ac.vitbhopal.projects.callrecorder.projection.ProjectionHandler;

public class VersionedRecorderFactory {
    private VersionedRecorderFactory() { }

    public static AbstractRecorder getRecorder(Context ctx) {
        int CURRENT_VERSION = Build.VERSION.SDK_INT;
        if (CURRENT_VERSION >= Build.VERSION_CODES.Q) {
            MediaRecorder mediaRecorder = new MediaRecorder();
            return new Android10Recorder(ctx, mediaRecorder, new ProjectionHandler(ctx, mediaRecorder));
        } else {
            throw new IllegalArgumentException("Invalid android version.");
        }
    }
}
