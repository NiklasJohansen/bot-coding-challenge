package games.PongMP;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class Ball
{
    private static transient double DEATH_THRESHOLD = 0.001;
    private static transient double HEALTH_DECAY_RATE = 0.85;
    private static transient double SPEED_INCREASE_ON_CONTACT = 1.1;
    private static transient double MAX_SPEED = 30;

    public double speed;
    public double radius;
    public double xPosBall;
    public double yPosBall;
    public double xPosBallLast;
    public double yPosBallLast;

    private transient PongPlayer ballInContactWith;
    private transient double health;

    public Ball(double xStart, double yStart)
    {
        this.health = 1;
        this.radius = 8;
        this.speed = 3 + Math.random() * 3;
        this.xPosBall = xStart;
        this.yPosBall = yStart;
        this.xPosBallLast = xStart + speed * Math.cos(Math.random() * Math.PI * 2);
        this.yPosBallLast = yStart + speed * Math.sin(Math.random() * Math.PI * 2);
    }

    public void updatePosition()
    {
        double xVel = xPosBall - xPosBallLast;
        double yVel = yPosBall - yPosBallLast;

        this.xPosBallLast = xPosBall;
        this.yPosBallLast = yPosBall;

        this.xPosBall += xVel;
        this.yPosBall += yVel;

        if(health < 1.0)
            health *= HEALTH_DECAY_RATE;
    }

    public void handleCollision(List<PongPlayer> playerList, double[][] arenaCorners)
    {
        for(PongPlayer p : playerList)
        {
            LineSegment defLine = p.getDefenseLine();
            LineSegment paddle = p.getPaddle();

            double xVel = xPosBall - xPosBallLast;
            double yVel = yPosBall - yPosBallLast;
            double speed = Math.sqrt(xVel * xVel + yVel * yVel);

            // Calculates the edge point of the ball in the direction of travel
            double xPosEdge = xPosBall + (xVel / speed) * radius;
            double yPosEdge = yPosBall + (yVel / speed) * radius;

            // Extends the paddle reach with the radius of the ball
            double xPaddleOffset = paddle.xDirection * radius;
            double yPaddleOffset = paddle.yDirection * radius;

            double[] intersection = PongUtils.getLineIntersection(
                    xPosEdge,
                    yPosEdge,
                    xPosBallLast,
                    yPosBallLast,
                    paddle.x0 + xPaddleOffset,
                    paddle.y0 + yPaddleOffset,
                    paddle.x1 - xPaddleOffset,
                    paddle.y1 - yPaddleOffset);

            if(intersection != null && PongUtils.isPointInsidePolygon(arenaCorners, xPosBallLast, yPosBallLast))
            {
                if(speed < MAX_SPEED)
                {
                    xVel *= SPEED_INCREASE_ON_CONTACT;
                    yVel *= SPEED_INCREASE_ON_CONTACT;
                }

                // Dot product between velocity vector and defence line normal
                double velDot = xVel * defLine.xNormal + yVel * defLine.yNormal;

                // Reflection the velocity vector along the normal
                double xVelNew = xVel - 2 * velDot * defLine.xNormal;
                double yVelNew = yVel - 2 * velDot * defLine.yNormal;

                // Move the ball inside the arena
                // TODO: move the ball back with the velocity vector
                xPosBall = intersection[0] + defLine.xNormal * radius;
                yPosBall = intersection[1] + defLine.yNormal * radius;

                // Set the new "last" position to alter the direction of movement
                xPosBallLast = xPosBall - xVelNew;
                yPosBallLast = yPosBall - yVelNew;
            }
        }
    }

    public void render(Canvas canvas)
    {
        if(health > DEATH_THRESHOLD)
        {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.rgb(255,255,255, health));
            gc.fillOval(xPosBall - radius, yPosBall - radius, 2 * radius, 2 * radius);
        }
    }

    public boolean isDead()
    {
        return health <= DEATH_THRESHOLD;
    }

    public void setDying()
    {
        if(health == 1.0)
            health *= 0.99;
    }
}
