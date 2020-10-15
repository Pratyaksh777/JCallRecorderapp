package in.ac.vitbhopal.projects.callrecorder.helper;

import androidx.core.util.Consumer;

import java.util.HashSet;
import java.util.Set;

public abstract class PhoneStateChangeListener implements Disposable {
    private final Set<Consumer<PhoneState>> listeners = new HashSet<>();
    private final Set<Consumer<PhoneState>> tickingListener = new HashSet<>();
    public final void onStateChange(Consumer<PhoneState> listener) {
        listeners.add(listener);
    }
    public final void  onObservationTick(Consumer<PhoneState> tickListener) {
        listeners.add(tickListener);
    }

    public final void removeStateChangeListener(Consumer<PhoneState> listener) {
        listeners.remove(listener);
    }

    public final void removeObserverTickListener(Consumer<PhoneState> listener) {
        listeners.remove(listener);
    }

    public final void notifyStateChange(PhoneState newState) {
        for (Consumer<PhoneState> listener: listeners) {
            listener.accept(newState);
        }
    }
    public final void notifyTick(PhoneState newState) {
        for (Consumer<PhoneState> listener: tickingListener) {
            listener.accept(newState);
        }
    }
}
