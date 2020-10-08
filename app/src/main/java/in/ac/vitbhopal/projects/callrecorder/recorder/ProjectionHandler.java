package in.ac.vitbhopal.projects.callrecorder.recorder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.icu.text.SimpleDateFormat;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.util.Locale;

import in.ac.vitbhopal.projects.callrecorder.RecorderConstants;
import in.ac.vitbhopal.projects.callrecorder.helper.Disposable;
import in.ac.vitbhopal.projects.callrecorder.helper.ScreenInfo;
import in.ac.vitbhopal.projects.callrecorder.utils.ScreenUtils;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class ProjectionHandler extends AbstractVirtualDisplayHandler {
    private final MediaProjectionManager projectionManager;
    private final MediaRecorder recorder;

    @Nullable private MediaProjection projection;
    @Nullable private VirtualDisplay virtualDisplay;


    public ProjectionHandler(Context ctx, MediaRecorder recorder) {
        super(ctx, ScreenUtils.getScreenInfo(ctx));
        this.recorder = recorder;
        this.projectionManager = (MediaProjectionManager) ctx.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        prepareBroadcastReceiver();
    }

    @Override
    public boolean isReady() {
        return projection != null && virtualDisplay == null;
    }

    @Override
    public boolean createVirtualDisplay() {
        if (!isReady()) return false;
        virtualDisplay = projection.createVirtualDisplay(
                "RecorderVD",
                getScreenInfo().getWidth(),
                getScreenInfo().getHeight(),
                getScreenInfo().getDensity(),
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                recorder.getSurface(),
                null,
                null
        );
        return true;
    }

    @Override
    public boolean releaseVirtualDisplay() {
        if (virtualDisplay == null) return false;
        virtualDisplay.release();
        virtualDisplay = null;
        return true;
    }

    private void prepareBroadcastReceiver() {
        IntentFilter broadcastIntentFilter = new IntentFilter();
        broadcastIntentFilter.addAction(RecorderConstants.ACTION_BR_PROJECTIONDATA);
        getContext().registerReceiver(projectionDataReceiver, broadcastIntentFilter);
    }

    public void dispose() {
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        if (projection != null) {
            projection.stop();
            projection = null;
        }
        getContext().unregisterReceiver(projectionDataReceiver);
    }

    private final BroadcastReceiver projectionDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras == null) return;
            Object result = intent.getExtras().get("ProjectionResultData");
            // Implicit null check included with instanceof check
            if (!(result instanceof Intent)) {
                Log.d(RecorderConstants.DEBUG_TAG, "Invalid permission data received ");
                return;
            }
            Intent data = (Intent) result;
            // Initialing projection field with a clone of input data to enforce immutability
            projection = projectionManager.getMediaProjection(Activity.RESULT_OK, (Intent) data.clone());
        }
    };
}
