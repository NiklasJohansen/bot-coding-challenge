package games.PongMP;

import core.server.Player;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class GameBoard
{
    public void render(List<Player> players, Canvas canvas)
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double diameter  = Math.min(canvas.getWidth(), canvas.getHeight()) * 0.9;
        double xCenter = canvas.getWidth() / 2;
        double yCenter = canvas.getHeight() / 2;
        int nPlayers = 10;

        //gc.setFill(Color.WHITE);
        //gc.setStroke(Color.BLACK);
        //gc.strokeOval(xCenter - diameter / 2, yCenter - diameter / 2, diameter, diameter);

        double radius = diameter / 2;
        double dist = (2 * Math.PI) / nPlayers;

        double x1 = xCenter + radius * Math.cos(0);
        double y1 = yCenter + radius * Math.sin(0);

        for(int i = 1; i <= nPlayers; i++)
        {
            double x2 = xCenter + radius * Math.cos(dist * i);
            double y2 = yCenter + radius * Math.sin(dist * i);

            gc.strokeLine(x1, y1, x2, y2);
            gc.setFill(Color.BLACK);
            gc.fillOval(x1 - 5, y1 - 5, 10, 10);

            x1 = x2;
            y1 = y2;
        }
    }
}
