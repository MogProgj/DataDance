package structlab.gui;

public enum NavigationPage {
    EXPLORE("Explore", "Browse and simulate data structures"),
    COMPARE("Compare", "Side-by-side implementation analysis"),
    LEARN("Learn", "Data structure reference library"),
    ACTIVITY("Activity", "Session history and recent actions"),
    SETTINGS("Settings", "Application preferences");

    private final String title;
    private final String subtitle;

    NavigationPage(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public String title() { return title; }
    public String subtitle() { return subtitle; }
}
