package games.speedoflight;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class Bullet
{
    private final float START_LIFE = 100;

    private float xPos;
    private float yPos;
    private float xPosLast;
    private float yPosLast;
    private float xPosLastLast;
    private float yPosLastLast;
    private float life;
    private float mass;
    private int framesAlive;
    private LightPlayer spawner;

    private static LinearGradient gradient;

    public Bullet(LightPlayer spawner, float xPos, float yPos, float direction, float speed)
    {
        this.spawner = spawner;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xPosLast = xPos - (float) Math.cos(direction) * speed;
        this.yPosLast = yPos - (float) Math.sin(direction) * speed;
        this.xPosLastLast = xPos;
        this.yPosLastLast = yPos;
        this.life = START_LIFE;
        this.mass = 1000;

        if(gradient == null)
            gradient = new LinearGradient(0, 0, 1, 1, true,
                    CycleMethod.REFLECT,
                    new Stop(1.0, Color.YELLOW),
                    new Stop(0.0, Color.TRANSPARENT));

    }

    public void update()
    {
        float xVel = xPos - xPosLast;
        float yVel = yPos - yPosLast;
        xPosLastLast = xPosLast;
        yPosLastLast = yPosLast;
        xPosLast = xPos;
        yPosLast = yPos;
        xPos += xVel;
        yPos += yVel;
        life--;
    }

    public void draw(Camera camera)
    {
        GraphicsContext gc = camera.getGraphicsContext();
        gc.setStroke(gradient);

        if(framesAlive > 0)
        {
            gc.beginPath();
            gc.setLineWidth(2);
            gc.moveTo(xPos, yPos);
            gc.lineTo(xPosLast, yPosLast);

            if(framesAlive < 2)
                gc.lineTo(spawner.getX(), spawner.getY());
            else
                gc.lineTo(xPosLastLast, yPosLastLast);

            gc.stroke();
            gc.setLineWidth(1);
        }

        framesAlive++;
    }

    public LightPlayer getOwner()
    {
        return spawner;
    }

    public void setInactive()
    {
        life = 0;
    }

    public boolean isActive()
    {
        return life <= 0;
    }

    public void setX(float x)
    {
        this.xPos = x;
    }

    public void setY(float y)
    {
        this.yPos = y;
    }

    public void setXLast(float x)
    {
        this.xPosLast = x;
    }

    public void setYLast(float y)
    {
        this.yPosLast = y;
    }

    public float getX()
    {
        return xPos;
    }

    public float getY()
    {
        return yPos;
    }

    public float getXLast()
    {
        return xPosLast;
    }

    public float getYLast()
    {
        return yPosLast;
    }

    public float getMass()
    {
        return mass;
    }

    public float getSpeed()
    {
        float xDiff = xPos - xPosLast;
        float yDiff = yPos - yPosLast;
        return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public float getLife()
    {
        return life / 100.0f;
    }

    public void decay()
    {
        this.life *= 0.8f;
    }
}

