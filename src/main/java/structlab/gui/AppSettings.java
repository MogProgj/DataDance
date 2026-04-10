package structlab.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class AppSettings {

    private final BooleanProperty motionEnabled = new SimpleBooleanProperty(true);
    private final BooleanProperty compactMode = new SimpleBooleanProperty(false);
    private final BooleanProperty showRawTraces = new SimpleBooleanProperty(true);
    private final BooleanProperty highDensity = new SimpleBooleanProperty(false);

    public BooleanProperty motionEnabledProperty() { return motionEnabled; }
    public boolean isMotionEnabled() { return motionEnabled.get(); }
    public void setMotionEnabled(boolean v) { motionEnabled.set(v); }

    public BooleanProperty compactModeProperty() { return compactMode; }
    public boolean isCompactMode() { return compactMode.get(); }
    public void setCompactMode(boolean v) { compactMode.set(v); }

    public BooleanProperty showRawTracesProperty() { return showRawTraces; }
    public boolean isShowRawTraces() { return showRawTraces.get(); }
    public void setShowRawTraces(boolean v) { showRawTraces.set(v); }

    public BooleanProperty highDensityProperty() { return highDensity; }
    public boolean isHighDensity() { return highDensity.get(); }
    public void setHighDensity(boolean v) { highDensity.set(v); }
}
