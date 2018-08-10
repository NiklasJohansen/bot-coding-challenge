package games.speedoflight.environmemt.entities;

import games.speedoflight.Camera;
import games.speedoflight.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpawnPoint extends MapEntity
{
    public SpawnPoint(float xPos, float yPos)
    {
        super(xPos, yPos, false);
    }

    public float getX()
    {
        return xPos;
    }

    public float getY()
    {
        return yPos;
    }

    @Override
    public void draw(Camera camera)
    {
        GraphicsContext gc = camera.getGraphicsContext();
        gc.setFill(Color.rgb(255,255,255,0.5));
        gc.fillOval(xPos - 10, yPos - 10, 20, 20);
    }

    @Override
    public boolean inside(float xPos, float yPos)
    {
        return Util.isPointInsideCircle(xPos, yPos, this.xPos, this.yPos, 20);
    }
}
