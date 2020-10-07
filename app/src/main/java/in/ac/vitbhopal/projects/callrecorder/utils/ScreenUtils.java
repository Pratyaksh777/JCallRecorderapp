package in.ac.vitbhopal.projects.callrecorder.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import in.ac.vitbhopal.projects.callrecorder.helper.ScreenInfo;

public class ScreenUtils {
    private ScreenUtils() { }

    public static ScreenInfo getScreenInfo(Context ctx) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int dpDensity = (int) displayMetrics.density;
        int dpHeight = Math.round(displayMetrics.heightPixels / (float) dpDensity);
        dpHeight -= dpHeight % 10;
        int dpWidth = Math.round(displayMetrics.widthPixels / (float) dpDensity);
        dpWidth -= dpWidth % 10;
        return new ScreenInfo(dpWidth, dpHeight, dpDensity);
    }
}
