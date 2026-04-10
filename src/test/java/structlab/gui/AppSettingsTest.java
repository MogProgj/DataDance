package structlab.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppSettingsTest {

    @Test
    void defaultValues() {
        AppSettings s = new AppSettings();
        assertTrue(s.isMotionEnabled());
        assertFalse(s.isCompactMode());
        assertTrue(s.isShowRawTraces());
        assertFalse(s.isHighDensity());
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
