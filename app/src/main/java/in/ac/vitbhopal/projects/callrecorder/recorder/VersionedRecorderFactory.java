package in.ac.vitbhopal.projects.callrecorder.recorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class VersionedRecorderFactory {
    private VersionedRecorderFactory() { }

    public static AbstractRecorder getRecorder(Context ctx) {
        int CURRENT_VERSION = Build.VERSION.SDK_INT;
        MediaRecorder mediaRecorder = new MediaRecorder();
        if (CURRENT_VERSION >= Build.VERSION_CODES.Q) {
            return new Android10Recorder(ctx, mediaRecorder);
        } else {
            // temporary solution since just testing for android 10
            return new Android10Recorder(ctx, mediaRecorder);
            // throw new IllegalArgumentException("Invalid android version.");
        }
    }
}
