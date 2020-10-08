package in.ac.vitbhopal.projects.callrecorder.helper;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PhoneStateObserver extends PhoneStateChangeListener {
    private final Context ctx;
    private PhoneState curState = PhoneState.IDLE;
    private final Object lock = new Object();
    private final Timer scheduler = new Timer();
    private final AudioManager audioManager;


    public PhoneStateObserver(Context ctx) {
        this.ctx = ctx;
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        startStateObservation();
    }

    public PhoneState getCurState() {
        synchronized (lock) {
            return curState;
        }
    }

    private void setCurState(PhoneState curState) {
        synchronized (lock) {
            this.curState = curState;
        }

        notifyStateChange(curState);
    }

    private void startStateObservation() {
        scheduler.scheduleAtFixedRate(new ObservationTask(),1000,1000);
    }

    @Override
    public void dispose() {
        scheduler.cancel();
    }

    private class ObservationTask extends TimerTask {
        AsyncObserver observerTask;

        final class AsyncObserver extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                int mode = audioManager.getMode();
                PhoneState newState;
                switch (mode) {
                    case AudioManager.MODE_IN_CALL:
                        newState = PhoneState.CELLULAR_CALL;
                        break;
                    case AudioManager.MODE_IN_COMMUNICATION:
                        newState = PhoneState.VoIP_CALL;
                        break;
                    case AudioManager.MODE_CALL_SCREENING:
                    case AudioManager.MODE_NORMAL:
                    case AudioManager.MODE_RINGTONE:
                    default:
                        newState = PhoneState.IDLE;
                        break;
                }
                if (newState == getCurState()) return null;
                setCurState(newState);
                return null;
            }
        }

        public void run() {
            observerTask = new AsyncObserver();
            observerTask.execute();
        }
    }
}
