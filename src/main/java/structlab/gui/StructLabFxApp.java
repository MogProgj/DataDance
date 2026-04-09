package structlab.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import structlab.app.service.StructLabService;
import structlab.gui.controller.MainWindowController;

public class StructLabFxApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        StructLabService service = StructLabService.createDefault();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-window.fxml"));
        Parent root = loader.load();

        MainWindowController controller = loader.getController();
        controller.initService(service);

        Scene scene = new Scene(root, 1200, 750);
        primaryStage.setTitle("StructLab — Data Structure Simulator");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
