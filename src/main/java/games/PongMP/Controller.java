package games.PongMP;

import core.server.GameServer;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;

public class Controller
{
    @FXML
    private Canvas canvas;
    private GameServer<PongPlayer> server;
    private GameBoard gameBoard;

    @FXML
    public void initialize()
    {
        this.gameBoard = new GameBoard(canvas);

        openServer();
        startGameLoop();
        addEventListeners();
    }

    private void openServer()
    {
        this.server = new GameServer<>(PongPlayer.class);
        this.server.setGameLoop(15, () ->
        {
            GameState gameState = gameBoard.getGameState();

            server.getPlayers().forEach(pongPlayer ->
            {
                gameState.paddlePos = pongPlayer.getPaddlePos();
                gameState.playerPaddle = pongPlayer.getPaddle();
                gameState.playerDefenseLine = pongPlayer.getDefenseLine();
                pongPlayer.send(gameState);
                pongPlayer.update();
            });
        });
        this.server.start(55500);
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
        gameBoard.update(server.getPlayers());
        gameBoard.render(server.getPlayers(), canvas);
    }

    private void addEventListeners()
    {
        canvas.setOnMouseMoved(event ->
        {
            for(Ball b : gameBoard.getGameState().balls)
            {
              //  b.xPosBall = event.getX();
              //  b.yPosBall = event.getY();
            }

        });


    }

}
