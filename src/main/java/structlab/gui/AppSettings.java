package structlab.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.prefs.Preferences;

public class AppSettings {

    private static final String PREF_MOTION = "motionEnabled";
    private static final String PREF_COMPACT = "compactMode";
    private static final String PREF_RAW_TRACES = "showRawTraces";
    private static final String PREF_HIGH_DENSITY = "highDensity";
    private static final String PREF_PLAYBACK_SPEED = "defaultPlaybackSpeed";
    private static final String PREF_AUTO_FIT = "autoFitGraph";
    private static final String PREF_SHOW_TRACKER = "showAlgorithmTracker";
    private static final String PREF_TRACKER_EXPANDED = "trackerExpanded";

    private static final boolean DEF_MOTION = true;
    private static final boolean DEF_COMPACT = false;
    private static final boolean DEF_RAW_TRACES = true;
    private static final boolean DEF_HIGH_DENSITY = false;
    private static final double DEF_PLAYBACK_SPEED = 1.0;
    private static final boolean DEF_AUTO_FIT = true;
    private static final boolean DEF_SHOW_TRACKER = true;
    private static final boolean DEF_TRACKER_EXPANDED = true;

    private final Preferences prefs = Preferences.userNodeForPackage(AppSettings.class);

    private final BooleanProperty motionEnabled = new SimpleBooleanProperty(prefs.getBoolean(PREF_MOTION, DEF_MOTION));
    private final BooleanProperty compactMode = new SimpleBooleanProperty(prefs.getBoolean(PREF_COMPACT, DEF_COMPACT));
    private final BooleanProperty showRawTraces = new SimpleBooleanProperty(prefs.getBoolean(PREF_RAW_TRACES, DEF_RAW_TRACES));
    private final BooleanProperty highDensity = new SimpleBooleanProperty(prefs.getBoolean(PREF_HIGH_DENSITY, DEF_HIGH_DENSITY));
    private final DoubleProperty defaultPlaybackSpeed = new SimpleDoubleProperty(prefs.getDouble(PREF_PLAYBACK_SPEED, DEF_PLAYBACK_SPEED));
    private final BooleanProperty autoFitGraph = new SimpleBooleanProperty(prefs.getBoolean(PREF_AUTO_FIT, DEF_AUTO_FIT));
    private final BooleanProperty showAlgorithmTracker = new SimpleBooleanProperty(prefs.getBoolean(PREF_SHOW_TRACKER, DEF_SHOW_TRACKER));
    private final BooleanProperty trackerExpanded = new SimpleBooleanProperty(prefs.getBoolean(PREF_TRACKER_EXPANDED, DEF_TRACKER_EXPANDED));

    public AppSettings() {
        motionEnabled.addListener((obs, o, n) -> prefs.putBoolean(PREF_MOTION, n));
        compactMode.addListener((obs, o, n) -> prefs.putBoolean(PREF_COMPACT, n));
        showRawTraces.addListener((obs, o, n) -> prefs.putBoolean(PREF_RAW_TRACES, n));
        highDensity.addListener((obs, o, n) -> prefs.putBoolean(PREF_HIGH_DENSITY, n));
        defaultPlaybackSpeed.addListener((obs, o, n) -> prefs.putDouble(PREF_PLAYBACK_SPEED, n.doubleValue()));
        autoFitGraph.addListener((obs, o, n) -> prefs.putBoolean(PREF_AUTO_FIT, n));
        showAlgorithmTracker.addListener((obs, o, n) -> prefs.putBoolean(PREF_SHOW_TRACKER, n));
        trackerExpanded.addListener((obs, o, n) -> prefs.putBoolean(PREF_TRACKER_EXPANDED, n));
    }

    // ── Existing properties ─────────────────────────────────

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

    // ── New properties ──────────────────────────────────────

    public DoubleProperty defaultPlaybackSpeedProperty() { return defaultPlaybackSpeed; }
    public double getDefaultPlaybackSpeed() { return defaultPlaybackSpeed.get(); }
    public void setDefaultPlaybackSpeed(double v) { defaultPlaybackSpeed.set(v); }

    public BooleanProperty autoFitGraphProperty() { return autoFitGraph; }
    public boolean isAutoFitGraph() { return autoFitGraph.get(); }
    public void setAutoFitGraph(boolean v) { autoFitGraph.set(v); }

    public BooleanProperty showAlgorithmTrackerProperty() { return showAlgorithmTracker; }
    public boolean isShowAlgorithmTracker() { return showAlgorithmTracker.get(); }
    public void setShowAlgorithmTracker(boolean v) { showAlgorithmTracker.set(v); }

    public BooleanProperty trackerExpandedProperty() { return trackerExpanded; }
    public boolean isTrackerExpanded() { return trackerExpanded.get(); }
    public void setTrackerExpanded(boolean v) { trackerExpanded.set(v); }

    // ── Restore defaults ────────────────────────────────────

    public void restoreDefaults() {
        setMotionEnabled(DEF_MOTION);
        setCompactMode(DEF_COMPACT);
        setShowRawTraces(DEF_RAW_TRACES);
        setHighDensity(DEF_HIGH_DENSITY);
        setDefaultPlaybackSpeed(DEF_PLAYBACK_SPEED);
        setAutoFitGraph(DEF_AUTO_FIT);
        setShowAlgorithmTracker(DEF_SHOW_TRACKER);
        setTrackerExpanded(DEF_TRACKER_EXPANDED);
    }
}
