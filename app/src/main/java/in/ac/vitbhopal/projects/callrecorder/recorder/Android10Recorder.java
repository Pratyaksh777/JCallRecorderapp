package in.ac.vitbhopal.projects.callrecorder.recorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;

import in.ac.vitbhopal.projects.callrecorder.RecorderConstants;
import in.ac.vitbhopal.projects.callrecorder.utils.DateUtils;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Android10Recorder extends AbstractRecorder {

    private final File outputFolder;
    public Android10Recorder(Context context, MediaRecorder recorder) {
        super(context, recorder);
        outputFolder = context.getExternalFilesDir("CallRecorderTest");
    }

    @Override
    public void onStart() {
        Log.d(RecorderConstants.DEBUG_TAG, "Started recording!");
    }

    @Override
    public void onStop() {
        Log.d(RecorderConstants.DEBUG_TAG, "Stopped recording!");
    }

    @Override
    protected void prepare(MediaRecorder recorder) throws Exception {
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File out = new File(outputFolder, DateUtils.getFormattedDate() + ".3gp");
        String filePath = out.getAbsolutePath();
        recorder.setOutputFile(filePath);
        setCurrentSaveFile(out);
        // -----------------------------------------------------
        recorder.prepare();
        // -----------------------------------------------------
    }
}
