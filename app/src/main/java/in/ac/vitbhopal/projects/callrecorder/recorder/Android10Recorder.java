package in.ac.vitbhopal.projects.callrecorder.recorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;

import in.ac.vitbhopal.projects.callrecorder.RecorderConstants;
import in.ac.vitbhopal.projects.callrecorder.helper.ScreenInfo;
import in.ac.vitbhopal.projects.callrecorder.projection.ProjectionHandler;
import in.ac.vitbhopal.projects.callrecorder.utils.DateUtils;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Android10Recorder extends AbstractRecorder {

    private final File outputFile;
    public Android10Recorder(Context context, MediaRecorder recorder, ProjectionHandler vDHandler) {
        super(context, recorder, vDHandler);
        outputFile = context.getExternalFilesDir("CallRecorderTest");
    }

    @Override
    public void onStart() {
        Log.d(RecorderConstants.DEBUG_TAG, "Started recording!");
    }

    @Override
    public void onStop() {
        getVirtualDisplayHandler().releaseVirtualDisplay();
        Log.d(RecorderConstants.DEBUG_TAG, "Stopped recording!");
    }

    @Override
    protected void prepare(MediaRecorder recorder) throws Exception {
        boolean shouldRecordScreen = getVirtualDisplayHandler().isReady() && isCameraInUse();

        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        if (shouldRecordScreen) {
            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        }
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        if (shouldRecordScreen) {
            setUpRecorderForScreenRecord(recorder);
        }
        String filePath = new File(outputFile, DateUtils.getFormattedDate()).getAbsolutePath();
        recorder.setOutputFile(filePath);
        // -----------------------------------------------------
        recorder.prepare();
        // -----------------------------------------------------
        getVirtualDisplayHandler().createVirtualDisplay();
    }

    private void setUpRecorderForScreenRecord(MediaRecorder recorder) {
        ScreenInfo screenInfo = getVirtualDisplayHandler().getScreenInfo();
        recorder.setVideoSize(screenInfo.getWidth(), screenInfo.getHeight());
        recorder.setVideoEncodingBitRate(5 * screenInfo.getWidth() * screenInfo.getHeight());
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        recorder.setVideoFrameRate(30);
    }
}
