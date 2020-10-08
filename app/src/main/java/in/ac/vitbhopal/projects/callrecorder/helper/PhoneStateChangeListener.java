package in.ac.vitbhopal.projects.callrecorder.helper;

import androidx.core.util.Consumer;

import java.util.HashSet;
import java.util.Set;

public abstract class PhoneStateChangeListener implements Disposable {
    private final Set<Consumer<PhoneState>> listeners = new HashSet<>();

    public final void onStateChange(Consumer<PhoneState> listener) {
        listeners.add(listener);
    }

    public final void removeStateChangeListener(Consumer<PhoneState> listener) {
        listeners.remove(listener);
    }

    public final void notifyStateChange(PhoneState newState) {
        for (Consumer<PhoneState> listener: listeners) {
            listener.accept(newState);
        }
    }
}
