package in.ac.vitbhopal.projects.callrecorder.recorder;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.util.Log;

import in.ac.vitbhopal.projects.callrecorder.RecorderConstants;

public abstract class AbstractRecorder {
    private final MediaRecorder recorder;
    private final Context ctx;
    private final AbstractVirtualDisplayHandler virtualDisplayHandler;

    private boolean recording = false;


    public AbstractRecorder(Context ctx, MediaRecorder recorder, AbstractVirtualDisplayHandler virtualDisplayHandler) {
        this.ctx = ctx;
        this.recorder = recorder;
        this.virtualDisplayHandler = virtualDisplayHandler;
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
            onStart();
        } catch (Exception e) {
            Log.d(RecorderConstants.DEBUG_TAG, "Unable to start recorder: " + e.getMessage(), e);
            recorder.reset();
            return false;
        }
        return true;
    }

    public abstract void onStart();

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
            onStop();
        }
        return true;
    }

    public abstract void onStop();

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
        //if (projectionPermissionIntent == null) return false;
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
     *  Returns instance of implementation of AbstractVirtualDisplayHandler specific to current Projection Handler
     * @return Instance of provided AbstractVirtualDisplayHandler
     */
    public final AbstractVirtualDisplayHandler getVirtualDisplayHandler() {
        return virtualDisplayHandler;
    }

    /**
     * Function called on #start() to initialize MediaRecorder instance according to requirement
     * Note: Order of preparation is important in MediaRecorder API
     * @param recorder Internal MediaRecorder instance to be prepared
     * @throws Exception Any exceptions thrown in this function would be caught on called function failing as a result
     */
    protected abstract void prepare(MediaRecorder recorder) throws Exception;
}
