package games.speedoflight.environmemt.entities;

import games.speedoflight.Camera;
import games.speedoflight.environmemt.Map;
import games.speedoflight.environmemt.MapUtil;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public abstract class MapEntity
{
    protected float xPos;
    protected float yPos;
    protected float xScale;
    protected float yScale;

    protected float rotation;
    protected boolean collider;

    protected String assetName;
    protected Image texture;
    protected Color baseColor;
    protected float textureScale;

    public MapEntity(float xPos, float yPos, boolean collider)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.collider = collider;
        this.textureScale = 1.0f;
        this.xScale = 1.0f;
        this.yScale = 1.0f;
    }

    public abstract void draw(Camera camera);
    public abstract boolean inside(float xPos, float yPos);

    public String getSaveString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("assetName=").append(assetName);
        sb.append(",xPos=").append(xPos);
        sb.append(",yPos=").append(yPos);
        sb.append(",rot=").append(rotation);
        sb.append(",xScale=").append(xScale);
        sb.append(",yScale=").append(yScale);
        return sb.toString();
    }

    public void applySaveString(String saveString)
    {
        setX(MapUtil.getFloatValue(saveString, "xPos"));
        setY(MapUtil.getFloatValue(saveString, "yPos"));
        setScaleX(MapUtil.getFloatValue(saveString, "xScale"));
        setScaleY(MapUtil.getFloatValue(saveString, "yScale"));
        setRotation(MapUtil.getFloatValue(saveString, "rot"));
        setAssetName(MapUtil.getStringValue(saveString, "assetName"));
    }

    public void setAssetName(String name)
    {
        this.assetName = name;
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

    public float getX() { return xPos; }
    public float getY() { return yPos; }
    public float getScaleX() { return xScale; }
    public float getScaleY() { return yScale; }
    public float getRotation() { return rotation; }

    public void setX(float x) { this.xPos = x; }
    public void setY(float y) { this.yPos = y; }
    public void setScaleX(float xScale) { this.xScale = xScale; }
    public void setScaleY(float yScale) { this.yScale = yScale; }
    public void setRotation(float rotation) { this.rotation = rotation; }

}
