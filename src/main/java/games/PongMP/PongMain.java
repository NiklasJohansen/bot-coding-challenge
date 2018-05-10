package games.PongMP;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class PongMain extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        AnchorPane root = FXMLLoader.load(getClass().getResource("/PongMP/userinterface.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
