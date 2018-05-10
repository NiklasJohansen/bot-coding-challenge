package games.PongMP;

import core.server.GameServer;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;

public class Controller
{
    @FXML
    private Canvas canvas;
    private GameServer server;
    private GameState gameState;
    private GameBoard gameBoard;

    @FXML
    public void initialize()
    {
        this.gameBoard = new GameBoard();

        openServer();
        startGameLoop();
    }

    private void openServer()
    {
        this.server = new GameServer(55500);
        this.server.setClientResponseClass(ClientResponse.class);
        this.server.start();
    }

    private void startGameLoop()
    {
        new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                updateGame();
            }
        }.start();
    }

    private void updateGame()
    {
        gameBoard.render(server.getPlayers(), canvas);
    }

    public static class ClientResponse
    {
        public boolean moveRight;
        public boolean moveLeft;
    }

    public static class GameState
    {
        public double xPosBall;
        public double yPosBall;
        public double xVelBall;
        public double yVelBall;
    }
}
