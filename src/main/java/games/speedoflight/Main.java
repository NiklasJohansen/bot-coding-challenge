package games.speedoflight;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/speedoflight/userinterface.fxml"));
        AnchorPane root = loader.load();

        primaryStage.setTitle("Speed Of Light");
        primaryStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
        primaryStage.setOnCloseRequest(event -> ((Controller)loader.getController()).closeRequest());
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
