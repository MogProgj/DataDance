package structlab.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;

class AppSettingsPersistenceTest {

    @BeforeEach
    void clearPreferences() throws Exception {
        Preferences prefs = Preferences.userNodeForPackage(AppSettings.class);
        prefs.clear();
        prefs.flush();
    }

    @Test
    void settingIsSavedToPreferences() {
        AppSettings s = new AppSettings();
        s.setCompactMode(true);
        Preferences prefs = Preferences.userNodeForPackage(AppSettings.class);
        assertTrue(prefs.getBoolean("compactMode", false));
    }

    @Test
    void settingIsLoadedFromPreferences() {
        // First: persist a value
        Preferences prefs = Preferences.userNodeForPackage(AppSettings.class);
        prefs.putBoolean("highDensity", true);
        // Second: new instance should load it
        AppSettings s = new AppSettings();
        assertTrue(s.isHighDensity());
    }

    @Test
    void togglePersistsLatestValue() {
        AppSettings s = new AppSettings();
        s.setMotionEnabled(false);
        s.setMotionEnabled(true);
        Preferences prefs = Preferences.userNodeForPackage(AppSettings.class);
        assertTrue(prefs.getBoolean("motionEnabled", false));
    }
}
