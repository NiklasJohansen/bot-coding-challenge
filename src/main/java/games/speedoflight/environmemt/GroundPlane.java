package games.speedoflight.environmemt;

import games.speedoflight.Bullet;
import games.speedoflight.Camera;
import games.speedoflight.LightPlayer;
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
            gc.drawImage(texture, xPos, yPos, width, height);
        }
        else
        {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(xPos, yPos, width, height);
        }
    }

    @Override
    public void resolvePlayerCollision(LightPlayer player){}

    @Override
    public void resolveBulletCollision(Bullet bullet){}
}
