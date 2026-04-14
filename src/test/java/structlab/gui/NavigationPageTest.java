package structlab.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NavigationPageTest {

    @Test
    void allPagesHaveTitleAndSubtitle() {
        for (NavigationPage page : NavigationPage.values()) {
            assertNotNull(page.title(), page.name() + " should have a title");
            assertFalse(page.title().isBlank(), page.name() + " title should not be blank");
            assertNotNull(page.subtitle(), page.name() + " should have a subtitle");
            assertFalse(page.subtitle().isBlank(), page.name() + " subtitle should not be blank");
        }
    }

    @Test
    void sixPagesExist() {
        assertEquals(6, NavigationPage.values().length);
    }

    @Test
    void valueOfWorksForAllPages() {
        assertEquals(NavigationPage.EXPLORE, NavigationPage.valueOf("EXPLORE"));
        assertEquals(NavigationPage.COMPARE, NavigationPage.valueOf("COMPARE"));
        assertEquals(NavigationPage.LEARN, NavigationPage.valueOf("LEARN"));
        assertEquals(NavigationPage.ACTIVITY, NavigationPage.valueOf("ACTIVITY"));
        assertEquals(NavigationPage.SETTINGS, NavigationPage.valueOf("SETTINGS"));
    }

    @Test
    void exploreTitleIsExplore() {
        assertEquals("Explore", NavigationPage.EXPLORE.title());
    }
}
