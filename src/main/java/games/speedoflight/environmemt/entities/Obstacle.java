package games.speedoflight.environmemt.entities;

import games.speedoflight.Bullet;
import games.speedoflight.LightPlayer;

import java.util.function.Consumer;

public abstract class Obstacle extends MapEntity
{
    protected Consumer<MoveEvent> onMoveEvent;
    protected byte[][] storedCollisionMask;

    public Obstacle(float xPos, float yPos)
    {
        super(xPos, yPos, true);
    }

    public abstract void resolvePlayerCollision(LightPlayer player);
    public abstract void resolveBulletCollision(Bullet bullet);
    public abstract byte[][] getCollisionMask(int resolution);

    public byte[][] getPreviousCollisionMask()
    {
        return storedCollisionMask;
    }

    public void setOnMoveUpdate(Consumer<MoveEvent> event)
    {
        this.onMoveEvent = event;
    }

    public class MoveEvent
    {
        public float xPosNew;
        public float yPosNew;
        public float xPosOld;
        public float yPosOld;

        public MoveEvent(float xPosOld, float yPosOld, float xPosNew, float yPosNew)
        {
            this.xPosOld = xPosOld;
            this.yPosOld = yPosOld;
            this.xPosNew = xPosNew;
            this.yPosNew = yPosNew;
        }
    }

    @Override
    public void setX(float x)
    {
        float xOld = xPos;
        this.xPos = x;
        onMoveEvent.accept(new MoveEvent(xOld, yPos, xPos, yPos));
    }

    @Override
    public void setY(float y)
    {
        float yOld = yPos;
        this.yPos = y;
        onMoveEvent.accept(new MoveEvent(xPos, yOld, xPos, yPos));
    }
}
