package in.ac.vitbhopal.projects.callrecorder.recorder;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.media.MediaRecorder;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import in.ac.vitbhopal.projects.callrecorder.helper.ScreenInfo;
import in.ac.vitbhopal.projects.callrecorder.utils.DateUtils;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Android10Recorder extends AbstractRecorder {
    private final File outputFile;
    public Android10Recorder(Context context, MediaRecorder recorder, ProjectionHandler vDHandler) {
        super(context, recorder, vDHandler);
        outputFile = context.getExternalFilesDir("CallRecorderTest");
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
    }

    private void setUpRecorderForScreenRecord(MediaRecorder recorder) {
        ScreenInfo screenInfo = getVirtualDisplayHandler().getScreenInfo();
        recorder.setVideoSize(screenInfo.getWidth(), screenInfo.getHeight());
        recorder.setVideoEncodingBitRate(5 * screenInfo.getWidth() * screenInfo.getHeight());
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        recorder.setVideoFrameRate(30);
    }
}
