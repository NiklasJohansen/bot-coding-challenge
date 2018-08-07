package games.speedoflight;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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

    private LightPlayer spawner;

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
    }

    public void update()
    {
        float xVel = xPos - xPosLast;
        float yVel = yPos - yPosLast;

        if(life != START_LIFE)
        {
            xPosLastLast = xPosLast;
            yPosLastLast = yPosLast;
        }

        xPosLast = xPos;
        yPosLast = yPos;
        xPos += xVel;
        yPos += yVel;
        life--;
    }

    public void draw(Camera camera)
    {
        GraphicsContext gc = camera.getGraphicsContext();
        gc.setStroke(Color.color(1,1,0.8, life / 100.0f));
        gc.strokeLine(xPosLastLast, yPosLastLast, xPosLast, yPosLast);
        gc.strokeLine(xPosLast, yPosLast, xPos, yPos);
    }

    public LightPlayer getOwner()
    {
        return spawner;
    }

    public void setDead()
    {
        life = 0;
    }

    public boolean isDead()
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

