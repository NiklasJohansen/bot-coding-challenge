package games.PongMP;

import core.server.Player;

public class PongPlayer extends Player<PongPlayer.ClientResponse>
{
    private static final double PADDLE_SPEED = 0.05;

    private double paddlePos;
    private LineSegment paddle;
    private LineSegment defenseLine;

    public PongPlayer()
    {
        super(ClientResponse.class);
        this.paddlePos = 0;
        this.paddle = new LineSegment();
        this.defenseLine = new LineSegment();
    }

    public void update()
    {
        ClientResponse response = super.getResponse();
        if(response != null)
        {
            if(response.moveLeft)
                paddlePos = Math.max(-1, paddlePos - PADDLE_SPEED);

            if(response.moveRight)
                paddlePos = Math.min(1, paddlePos + PADDLE_SPEED);
        }
    }

    public double getPaddlePos()
    {
        return paddlePos;
    }

    public LineSegment getPaddle()
    {
        return paddle;
    }

    public LineSegment getDefenseLine()
    {
        return defenseLine;
    }

    public static class ClientResponse
    {
        public boolean moveRight;
        public boolean moveLeft;
    }
}
