package games.Game;

import javafx.animation.AnimationTimer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class Controller
{
    @FXML private Canvas canvas;
    @FXML private AnchorPane anchorPane;

    @FXML
    public void initialize()
    {
        addEventListeners();
        startGameLoop();
    }

    public void closeRequest()
    {
        Platform.exit();
        System.exit(0);
    }

    private void startGameLoop()
    {
        new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                updateGame();
                renderGame();
            }
        }.start();
    }

    private void updateGame()
    {

    }

    private void renderGame()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.GRAY);
        gc.fillRect(100,100,300,300);
    }

    private void addEventListeners()
    {
        Platform.runLater(() ->
        {
            canvas.setOnMouseMoved(event ->
            {
                System.out.println("Mouse moved: " + event.getX() + " " + event.getY());
            });

            anchorPane.getScene().setOnKeyPressed(event ->
            {
                System.out.println("Key pressed: " + event.getCode());
            });

            anchorPane.getScene().setOnKeyReleased(event ->
            {
                System.out.println("Key released: " + event.getCode());
            });
        });
    }

}
