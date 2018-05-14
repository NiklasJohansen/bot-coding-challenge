package games.PongMP;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class GameBoard
{
    private final double PADDLE_WIDTH = 0.3;

    private GameState gameState;

    private double[][] points;
    private double xCenter;
    private double yCenter;

    public GameBoard(Canvas canvas)
    {
        this.gameState = new GameState();
        for(int i = 0; i < 1; i++)
            this.gameState.addBall(canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    public void render(List<PongPlayer> players, Canvas canvas)
    {
        if(players.size() < 2)
            return;

        if(points == null || players.size() != points.length)
            points = new double[players.size()][2];

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

        renderBalls(canvas);

        double diameter  = Math.min(canvas.getWidth(), canvas.getHeight()) * 0.9;
        xCenter = canvas.getWidth() / 2;
        yCenter = canvas.getHeight() / 2;
        int nPlayers = players.size();

        double radius = diameter / 2;
        double dist = (2 * Math.PI) / nPlayers;

        double x0 = points[0][0] = xCenter + radius * Math.cos(0);
        double y0 = points[0][1] = yCenter + radius * Math.sin(0);

        for(int i = 1; i <= nPlayers; i++)
        {
            double x1 = points[i-1][0] = xCenter + radius * Math.cos(dist * i);
            double y1 = points[i-1][1] = yCenter + radius * Math.sin(dist * i);

            gc.setStroke(Color.LIGHTGRAY);
            gc.setLineWidth(1);
            gc.strokeLine(x0, y0, x1, y1);

            double xDif = x1 - x0;
            double yDif = y1 - y0;
            double length = Math.sqrt(xDif * xDif + yDif * yDif);
            double xTangent = xDif / length;
            double yTangent = yDif / length;

            PongPlayer player = players.get(i - 1);

            double paddlePos = (player.getPaddlePos() + 1) / 2;
            paddlePos = (PADDLE_WIDTH / 2) + (1 - PADDLE_WIDTH) * paddlePos;

            double xPaddle = x0 + (xTangent * length * paddlePos);
            double yPaddle = y0 + (yTangent * length * paddlePos);
            double xOffset = xTangent * length * (PADDLE_WIDTH / 2);
            double yOffset = yTangent * length * (PADDLE_WIDTH / 2);

            double xPaddle0 = xPaddle - xOffset;
            double yPaddle0 = yPaddle - yOffset;
            double xPaddle1 = xPaddle + xOffset;
            double yPaddle1 = yPaddle + yOffset;

            double xMiddle = (x0 + x1) / 2;
            double yMiddle = (y0 + y1) / 2;
            double xNormal = xCenter - xMiddle;
            double yNormal = yCenter - yMiddle;
            double normLength = Math.sqrt(xNormal * xNormal + yNormal * yNormal);
            xNormal /= normLength;
            yNormal /= normLength;

            player.getDefenseLine().setNormal(xNormal, yNormal);
            player.getDefenseLine().setLine(x0, y0, x1, y1);
            player.getPaddle().setLine(xPaddle0, yPaddle0, xPaddle1, yPaddle1);

            gc.setStroke(Color.RED);
            gc.setLineWidth(3);
            gc.strokeLine(xPaddle0, yPaddle0, xPaddle1, yPaddle1);
            gc.setFill(Color.BLACK);

            x0 = x1;
            y0 = y1;
        }
    }

    public void renderBalls(Canvas canvas)
    {
        for(Ball b : gameState.balls)
            b.render(canvas);
    }

    public void update(List<PongPlayer> players)
    {
        for(Ball b : gameState.balls)
        {
            b.updatePosition();
            b.handleCollision(players, points);
        }
        boundaryCheck(players);
    }

    private void boundaryCheck(List<PongPlayer> players)
    {
        if(points == null)
            return;

        for(Ball b : gameState.balls)
        {
            if(!PongUtils.isPointInsidePolygon(points, b.xPosBall, b.yPosBall))
                b.setDying();
        }

        List<Ball> balls = gameState.balls;
        for(int i = 0; i < balls.size() && balls.size() > 0; i++)
        {
            if(balls.get(i).isDead())
            {
                balls.remove(balls.get(i--));
                gameState.addBall(xCenter, yCenter);
            }
        }
    }

    public GameState getGameState()
    {
        return gameState;
    }
}
