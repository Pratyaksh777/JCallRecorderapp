package in.ac.vitbhopal.projects.callrecorder.utils;

import android.icu.text.SimpleDateFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Date;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.N)
public final class DateUtils {
    private DateUtils() { }

    private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.UK);


    public static String getFormattedDate() {
        return dateFormatter.format(new Date());
    }
}
