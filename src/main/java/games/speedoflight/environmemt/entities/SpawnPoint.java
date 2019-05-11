package games.speedoflight.environmemt.entities;

import games.speedoflight.Camera;
import games.speedoflight.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpawnPoint extends MapEntity
{
    private float radius;

    public SpawnPoint(float xPos, float yPos)
    {
        super(xPos, yPos, false);
        this.radius = 100;
    }

    public float getX()
    {
        return xPos;
    }

    public float getY()
    {
        return yPos;
    }

    public float getRadius()
    {
        return radius;
    }

    @Override
    public void draw(Camera camera)
    {
        GraphicsContext gc = camera.getGraphicsContext();
        gc.setFill(Color.rgb(255,255,255,0.5));
        gc.fillOval(xPos - 10, yPos - 10, 20, 20);

        double width = texture.getWidth() * xScale;
        double height = texture.getHeight() * yScale;

        gc.save();
        gc.translate(xPos, yPos);
        gc.rotate(Math.toDegrees(rotation));
        gc.translate(-(xPos+width/2), -(yPos+height/2));
        gc.drawImage(texture, xPos, yPos, width, height);
        gc.restore();

    }

    @Override
    public boolean inside(float xPos, float yPos)
    {
        return Util.isPointInsideCircle(xPos, yPos, this.xPos, this.yPos, 20);
    }
}
