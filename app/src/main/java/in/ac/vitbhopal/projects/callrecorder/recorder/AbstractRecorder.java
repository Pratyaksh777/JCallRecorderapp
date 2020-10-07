package in.ac.vitbhopal.projects.callrecorder.recorder;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;

import in.ac.vitbhopal.projects.callrecorder.RecorderConstants;

public abstract class AbstractRecorder {
    private final MediaRecorder recorder;
    private boolean recording = false;
    public Intent projectionPermissionIntent = null;

    public AbstractRecorder(MediaRecorder recorder) {
        this.recorder = recorder;
    }

    /**
     *  Safely tries to start the recorder. Internally calls AbstractRecorder#prepare(MediaRecorder) that's defined in implementing classes
     *  @return true, if recording successfully started
     */
    public final boolean start() {
        if (isRecording()) return false;
        try {
            prepare(recorder);
            recorder.start();
            recording = true;
        } catch (Exception e) {
            Log.d(RecorderConstants.DEBUG_TAG, "Unable to start recorder: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     *  Safely tries to stop the recorder. Internal MediaRecorder is assured to be reset after the call to this function
     *  @return true, if recording successfully started
     */
    public final boolean stop() {
        if (!isRecording()) return false;
        recording = false;
        try {
            recorder.stop();
        } catch (Exception e) {
            Log.d(RecorderConstants.DEBUG_TAG, "Unable to stop recorder: " + e.getMessage(), e);
            return false;
        } finally {
            recorder.reset();
        }
        return true;
    }

    /**
     * @return Current recording state of recorder
     */
    public final boolean isRecording() {
        return recording;
    }

    /**
     * To be called when internal mediaRecorder must be released. Likely on Service#onDestroy
     */
    public final void dispose() {
        stop();
        recorder.release();
    }

    /**
     *  Uses deprecated Camera 1 API // Needs to potentially be changed to new CameraManager
     *
     * @return true, if camera is currently in use (Video call / just from camera apps)
     */
    public final boolean isCameraInUse() {
        if (projectionPermissionIntent == null) return false;
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            return true;
        } finally {
            if (camera != null) {
                camera.release();
            }
        }
        return false;
    }

    /**
     * Function called on #start() to initialize MediaRecorder instance according to requirement
     * @param recorder Internal MediaRecorder instance to be prepared
     * @throws Exception Any exceptions thrown in this function would be caught on called function failing as a result
     */
    protected abstract void prepare(MediaRecorder recorder) throws Exception;
}
