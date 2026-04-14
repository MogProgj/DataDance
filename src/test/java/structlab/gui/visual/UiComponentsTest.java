package structlab.gui.visual;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class UiComponentsTest {

    @Test
    void styledLabelAppliesClasses() {
        Label l = UiComponents.styledLabel("Hello", "info-label", "detail-name");
        assertEquals("Hello", l.getText());
        assertTrue(l.getStyleClass().contains("info-label"));
        assertTrue(l.getStyleClass().contains("detail-name"));
    }

    @Test
    void sectionHeaderUsesClass() {
        Label l = UiComponents.sectionHeader("OPERATIONS");
        assertEquals("OPERATIONS", l.getText());
        assertTrue(l.getStyleClass().contains("section-header"));
    }

    @Test
    void monoAreaIsNonEditable() {
        TextArea ta = UiComponents.monoArea("prompt text");
        assertFalse(ta.isEditable());
        assertTrue(ta.isWrapText());
        assertEquals("prompt text", ta.getPromptText());
        assertTrue(ta.getStyleClass().contains("mono-area"));
    }

    @Test
    void cardHasHeaderAndBody() {
        VBox card = UiComponents.card("TEST TITLE");
        assertTrue(card.getStyleClass().contains("card"));
        assertEquals(2, card.getChildren().size());

        VBox body = UiComponents.cardBody(card);
        assertNotNull(body);
        assertTrue(body.getStyleClass().contains("card-body"));
    }

    @Test
    void settingsCardHasHeaderAndBody() {
        VBox card = UiComponents.settingsCard("TITLE", "desc");
        assertTrue(card.getStyleClass().contains("settings-card"));
        assertEquals(2, card.getChildren().size());

        VBox body = UiComponents.settingsCardBody(card);
        assertNotNull(body);
        assertTrue(body.getStyleClass().contains("settings-card-body"));
    }

    @Test
    void secondaryButtonIsDisabledByDefault() {
        Button btn = UiComponents.secondaryButton("Cancel");
        assertEquals("Cancel", btn.getText());
        assertTrue(btn.isDisable());
        assertTrue(btn.getStyleClass().contains("secondary-button"));
    }

    @Test
    void buttonRowWrapsButtons() {
        Button a = new Button("A");
        Button b = new Button("B");
        HBox row = UiComponents.buttonRow(a, b);
        assertEquals(2, row.getChildren().size());
    }

    @Test
    void styledCheckInitialState() {
        CheckBox cb = UiComponents.styledCheck("Enable", true);
        assertTrue(cb.isSelected());
        assertEquals("Enable", cb.getText());
        assertTrue(cb.getStyleClass().contains("settings-check"));
    }
}
