package structlab.app;

import structlab.registry.InMemoryStructureRegistry;
import structlab.registry.RegistrySeeder;
import structlab.app.shell.AppShell;

/**
 * Main entry point for the StructLab Interactive Simulator.
 */
public class StructLabApp {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("  Welcome to StructLab Data Simulator");
        System.out.println("=========================================");

        // Bootstrap data layers
        InMemoryStructureRegistry registry = new InMemoryStructureRegistry();
        RegistrySeeder.seed(registry);

        // Spin up the interactive UI shell map
        AppShell shell = new AppShell(registry);

        // Handoff to REPL loop
        shell.run();
    }
}
