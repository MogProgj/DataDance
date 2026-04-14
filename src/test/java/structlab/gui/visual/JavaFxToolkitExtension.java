package structlab.gui.visual;

import javafx.application.Platform;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * JUnit 5 extension that initialises the JavaFX toolkit once for
 * all test classes that need it.  Uses the global store so the
 * toolkit is started at most once per JVM, regardless of how many
 * test classes carry {@code @ExtendWith(JavaFxToolkitExtension.class)}.
 *
 * <p>On headless CI the extension relies on {@code xvfb-run} providing
 * a virtual display.  If {@link Platform#startup} still fails or hangs,
 * a bounded timeout prevents the JVM from stalling indefinitely.</p>
 */
public class JavaFxToolkitExtension implements BeforeAllCallback {

    private static final ExtensionContext.Namespace NS =
            ExtensionContext.Namespace.create(JavaFxToolkitExtension.class);

    private static final int STARTUP_TIMEOUT_SECONDS = 5;

    @Override
    public void beforeAll(ExtensionContext context) {
        ExtensionContext.Store store = context.getRoot().getStore(NS);
        store.getOrComputeIfAbsent("toolkit-init", key -> {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.startup(latch::countDown);
                if (!latch.await(STARTUP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    System.err.println("[JavaFxToolkitExtension] Platform.startup() timed out after "
                            + STARTUP_TIMEOUT_SECONDS + "s — JavaFX visual tests may fail.");
                }
            } catch (IllegalStateException ignored) {
                // Toolkit already initialised — fine.
            } catch (UnsupportedOperationException e) {
                System.err.println("[JavaFxToolkitExtension] JavaFX not supported: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[JavaFxToolkitExtension] Interrupted during toolkit startup.");
            }
            return true;
        }, Boolean.class);
    }
}
