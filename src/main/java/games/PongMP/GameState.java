package games.PongMP;


import java.util.ArrayList;

public class GameState
{
    public ArrayList<Ball> balls;
    public LineSegment playerPaddle;
    public LineSegment playerDefenseLine;
    public double paddlePos;

    public GameState()
    {
        this.balls = new ArrayList<>();
    }

    public void addBall(double xStart, double yStart)
    {
        this.balls.add(new Ball(xStart, yStart));
    }
}