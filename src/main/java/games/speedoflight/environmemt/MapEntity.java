package games.speedoflight.environmemt;

import games.speedoflight.Bullet;
import games.speedoflight.Camera;
import games.speedoflight.LightPlayer;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public abstract class MapEntity
{
    protected float xPos;
    protected float yPos;
    protected boolean collider;

    protected Image texture;
    protected float textureScale;
    protected Color baseColor;



    public MapEntity(float xPos, float yPos, boolean collider)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.collider = collider;
        this.textureScale = 1.0f;
    }

    public abstract void draw(Camera camera);
    public abstract void resolvePlayerCollision(LightPlayer player);
    public abstract void resolveBulletCollision(Bullet bullet);

    public boolean isCollider()
    {
        return collider;
    }

    public void setTexture(Image texture)
    {
        this.texture = texture;
    }

    public void setTextureScale(float scale)
    {
        this.textureScale = scale;
    }

    public void setBaseColor(Color color)
    {
        this.baseColor = color;
    }

}
