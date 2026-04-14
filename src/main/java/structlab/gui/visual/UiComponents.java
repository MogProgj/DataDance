package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Shared UI factory methods for building visual components.
 *
 * <p>Extracted from the original {@code MainWindowController} so that
 * all page builders, visual panes, and controllers can reuse the
 * same styled primitives.  This prevents every builder from
 * re-inventing label/card/button helpers.</p>
 */
public final class UiComponents {

    private UiComponents() {}

    // ── Labels ──────────────────────────────────────────────

    /**
     * Creates a {@link Label} with one or more CSS style classes applied.
     */
    public static Label styledLabel(String text, String... styleClasses) {
        Label l = new Label(text);
        l.getStyleClass().addAll(styleClasses);
        return l;
    }

    /**
     * Creates a section header label (convenience for {@code styledLabel(text, "section-header")}).
     */
    public static Label sectionHeader(String text) {
        return styledLabel(text, "section-header");
    }

    // ── Text Areas ──────────────────────────────────────────

    /**
     * Creates a non-editable, word-wrapping {@link TextArea} styled as a
     * monospaced output area with the given prompt text.
     */
    public static TextArea monoArea(String prompt) {
        TextArea ta = new TextArea();
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.getStyleClass().add("mono-area");
        ta.setPromptText(prompt);
        return ta;
    }

    // ── Cards ───────────────────────────────────────────────

    /**
     * Creates a card container with a header row and an empty body.
     * Retrieve the body with {@link #cardBody(VBox)}.
     */
    public static VBox card(String title) {
        VBox card = new VBox();
        card.getStyleClass().add("card");

        HBox header = new HBox();
        header.getStyleClass().add("card-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().add(styledLabel(title, "card-title"));

        VBox body = new VBox(4);
        body.getStyleClass().add("card-body");

        card.getChildren().addAll(header, body);
        return card;
    }

    /**
     * Returns the body node of a card built with {@link #card(String)}.
     */
    public static VBox cardBody(VBox card) {
        return (VBox) card.getChildren().get(1);
    }

    /**
     * Creates a settings-style card with a title/description header and empty body.
     */
    public static VBox settingsCard(String title, String description) {
        VBox card = new VBox();
        card.getStyleClass().add("settings-card");

        VBox header = new VBox(4);
        header.getStyleClass().add("settings-card-header");
        header.getChildren().addAll(
                styledLabel(title, "settings-card-title"),
                styledLabel(description, "settings-card-desc"));

        VBox body = new VBox(12);
        body.getStyleClass().add("settings-card-body");

        card.getChildren().addAll(header, body);
        return card;
    }

    /**
     * Returns the body node of a settings card.
     */
    public static VBox settingsCardBody(VBox card) {
        return (VBox) card.getChildren().get(1);
    }

    // ── Buttons ─────────────────────────────────────────────

    /**
     * Creates a disabled secondary button that grows to fill horizontal space.
     */
    public static Button secondaryButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("secondary-button");
        btn.setDisable(true);
        btn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btn, Priority.ALWAYS);
        return btn;
    }

    /**
     * Wraps buttons in an evenly spaced row.
     */
    public static HBox buttonRow(Button... buttons) {
        HBox box = new HBox(6, buttons);
        box.setPadding(new Insets(6, 0, 0, 0));
        return box;
    }

    // ── Checkboxes ──────────────────────────────────────────

    /**
     * Creates a styled {@link CheckBox} with initial value.
     */
    public static CheckBox styledCheck(String text, boolean initial) {
        CheckBox cb = new CheckBox(text);
        cb.setSelected(initial);
        cb.getStyleClass().add("settings-check");
        return cb;
    }
}
