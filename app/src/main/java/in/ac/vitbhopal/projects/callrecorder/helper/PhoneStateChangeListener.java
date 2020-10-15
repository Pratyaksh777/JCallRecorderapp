package in.ac.vitbhopal.projects.callrecorder.helper;

import androidx.core.util.Consumer;

import java.util.HashSet;
import java.util.Set;

public abstract class PhoneStateChangeListener implements Disposable {
    private final Set<Consumer<PhoneState>> listeners = new HashSet<>();
    private final Set<Consumer<PhoneState>> tickingListener = new HashSet<>();
    private int tickingInterval = 1000;

    public final void onStateChange(Consumer<PhoneState> listener) {
        listeners.add(listener);
    }
    public final void  onObservationTick(Consumer<PhoneState> tickListener) {
        tickingListener.add(tickListener);
    }

    public final void removeStateChangeListener(Consumer<PhoneState> listener) {
        listeners.remove(listener);
    }

    public final void removeObserverTickListener(Consumer<PhoneState> listener) {
        tickingListener.remove(listener);
    }

    public final int getTickingInterval() {
        return tickingInterval;
    }

    protected final void setTickingInterval(int tickingInterval) {
        this.tickingInterval = tickingInterval;
    }

    protected final void notifyStateChange(PhoneState newState) {
        for (Consumer<PhoneState> listener: listeners) {
            listener.accept(newState);
        }
    }
    protected final void notifyTick(PhoneState newState) {
        for (Consumer<PhoneState> listener: tickingListener) {
            listener.accept(newState);
        }
    }
}
