package in.ac.vitbhopal.projects.callrecorder.projection;

import android.content.Context;
import android.icu.text.SimpleDateFormat;

import java.text.DateFormat;
import java.util.Locale;

import in.ac.vitbhopal.projects.callrecorder.helper.Disposable;
import in.ac.vitbhopal.projects.callrecorder.helper.ScreenInfo;

public abstract class AbstractVirtualDisplayHandler implements Disposable {
    private final Context ctx;
    private final ScreenInfo screenInfo;
    public AbstractVirtualDisplayHandler(Context ctx, ScreenInfo screenInfo) {
        this.ctx = ctx;
        this.screenInfo = screenInfo;
    }
    public final Context getContext() {
        return ctx;
    }

    public abstract boolean isReady();

    public abstract boolean createVirtualDisplay();

    public abstract boolean releaseVirtualDisplay();

    public final ScreenInfo getScreenInfo() {
        return screenInfo;
    }
}
