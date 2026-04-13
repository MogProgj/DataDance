package structlab.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;

class AppSettingsTest {

    @BeforeEach
    void clearPreferences() throws Exception {
        Preferences prefs = Preferences.userNodeForPackage(AppSettings.class);
        prefs.clear();
        prefs.flush();
    }

    @Test
    void defaultValues() {
        AppSettings s = new AppSettings();
        assertTrue(s.isMotionEnabled());
        assertFalse(s.isCompactMode());
        assertTrue(s.isShowRawTraces());
        assertFalse(s.isHighDensity());
        assertEquals(1.0, s.getDefaultPlaybackSpeed());
        assertTrue(s.isAutoFitGraph());
        assertTrue(s.isShowAlgorithmTracker());
        assertTrue(s.isTrackerExpanded());
    }

    @Test
    void setAndGetMotionEnabled() {
        AppSettings s = new AppSettings();
        s.setMotionEnabled(false);
        assertFalse(s.isMotionEnabled());
        s.setMotionEnabled(true);
        assertTrue(s.isMotionEnabled());
    }

    @Test
    void setAndGetCompactMode() {
        AppSettings s = new AppSettings();
        s.setCompactMode(true);
        assertTrue(s.isCompactMode());
    }

    @Test
    void setAndGetShowRawTraces() {
        AppSettings s = new AppSettings();
        s.setShowRawTraces(false);
        assertFalse(s.isShowRawTraces());
    }

    @Test
    void setAndGetHighDensity() {
        AppSettings s = new AppSettings();
        s.setHighDensity(true);
        assertTrue(s.isHighDensity());
    }

    @Test
    void propertiesAreNotNull() {
        AppSettings s = new AppSettings();
        assertNotNull(s.motionEnabledProperty());
        assertNotNull(s.compactModeProperty());
        assertNotNull(s.showRawTracesProperty());
        assertNotNull(s.highDensityProperty());
        assertNotNull(s.defaultPlaybackSpeedProperty());
        assertNotNull(s.autoFitGraphProperty());
        assertNotNull(s.showAlgorithmTrackerProperty());
        assertNotNull(s.trackerExpandedProperty());
    }

    @Test
    void setAndGetNewProperties() {
        AppSettings s = new AppSettings();
        s.setDefaultPlaybackSpeed(2.5);
        assertEquals(2.5, s.getDefaultPlaybackSpeed());

        s.setAutoFitGraph(false);
        assertFalse(s.isAutoFitGraph());

        s.setShowAlgorithmTracker(false);
        assertFalse(s.isShowAlgorithmTracker());

        s.setTrackerExpanded(false);
        assertFalse(s.isTrackerExpanded());
    }

    @Test
    void restoreDefaultsResetsAll() {
        AppSettings s = new AppSettings();
        s.setMotionEnabled(false);
        s.setCompactMode(true);
        s.setShowRawTraces(false);
        s.setHighDensity(true);
        s.setDefaultPlaybackSpeed(3.0);
        s.setAutoFitGraph(false);
        s.setShowAlgorithmTracker(false);
        s.setTrackerExpanded(false);

        s.restoreDefaults();

        assertTrue(s.isMotionEnabled());
        assertFalse(s.isCompactMode());
        assertTrue(s.isShowRawTraces());
        assertFalse(s.isHighDensity());
        assertEquals(1.0, s.getDefaultPlaybackSpeed());
        assertTrue(s.isAutoFitGraph());
        assertTrue(s.isShowAlgorithmTracker());
        assertTrue(s.isTrackerExpanded());
    }

    @Test
    void propertyReflectsSetter() {
        AppSettings s = new AppSettings();
        s.setCompactMode(true);
        assertTrue(s.compactModeProperty().get());
        s.setCompactMode(false);
        assertFalse(s.compactModeProperty().get());
    }
}
