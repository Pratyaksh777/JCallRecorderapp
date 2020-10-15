package in.ac.vitbhopal.projects.callrecorder.recorder;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;

import in.ac.vitbhopal.projects.callrecorder.RecorderConstants;
import in.ac.vitbhopal.projects.callrecorder.helper.Disposable;
import in.ac.vitbhopal.projects.callrecorder.projection.AbstractVirtualDisplayHandler;

public abstract class AbstractRecorder implements Disposable {
    private final MediaRecorder recorder;
    private final Context ctx;
    private final AbstractVirtualDisplayHandler virtualDisplayHandler;
    private boolean recording = false;
    private File currentSaveFile = null;


    public AbstractRecorder(Context ctx, MediaRecorder recorder, AbstractVirtualDisplayHandler virtualDisplayHandler) {
        this.ctx = ctx;
        this.recorder = recorder;
        this.virtualDisplayHandler = virtualDisplayHandler;
    }

    /**
     * @return The application context associated with current recorder
     */
    public Context getContext() {
        return ctx;
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
            setCurrentSaveFile(null);
        }
        return true;
    }

    @Nullable
    public final File getCurrentSaveFile() {
        return currentSaveFile;
    }

    protected final void setCurrentSaveFile(@Nullable File file) {
        this.currentSaveFile = file;
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
        virtualDisplayHandler.dispose();
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
     * Note: Order of preparation is important in MediaRecorder API. currentSaveFile is assumed to be set in this functions implementation!
     * @param recorder Internal MediaRecorder instance to be prepared
     * @throws Exception Any exceptions thrown in this function would be caught on called function failing as a result
     */
    protected abstract void prepare(MediaRecorder recorder) throws Exception;
}
