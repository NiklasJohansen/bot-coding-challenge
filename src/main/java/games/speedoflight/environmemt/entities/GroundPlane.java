package games.speedoflight.environmemt.entities;

import games.speedoflight.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GroundPlane extends MapEntity
{
    private float width;
    private float height;

    public GroundPlane(float xPos, float yPos, float width, float height)
    {
        super(xPos, yPos, false);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Camera camera)
    {
        GraphicsContext gc = camera.getGraphicsContext();
        if(texture != null)
        {
            gc.drawImage(texture, xPos - width / 2, yPos - height / 2, width, height);
        }
        else
        {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(xPos, yPos, width, height);
        }
    }

    @Override
    public boolean inside(float xPos, float yPos)
    {
        return xPos > this.xPos - width / 2 && xPos < this.xPos + width / 2 &&
                yPos > this.yPos - height / 2 && yPos < this.yPos + height / 2;
    }
}
