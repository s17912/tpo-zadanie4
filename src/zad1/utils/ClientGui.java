package zad1.utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientGui extends Application {
    private static final String APP_TITLE = "TPO 4 - Client GUIClient";
    private static final String MAIN_VIEW_PATH = "../resources/v2ClientGuiView.fxml";

    private static final int SCENE_WIDTH = 620;
    private static final int SCENE_HEIGHT = 480;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(MAIN_VIEW_PATH));
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}
