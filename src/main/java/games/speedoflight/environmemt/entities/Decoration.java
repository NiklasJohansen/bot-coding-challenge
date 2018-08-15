package games.speedoflight.environmemt.entities;

import games.speedoflight.Camera;
import games.speedoflight.environmemt.MapUtil;
import javafx.scene.canvas.GraphicsContext;

public class Decoration extends MapEntity
{
    private float width;
    private float height;

    public Decoration(float xPos, float yPos, float width, float height)
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
            double width = this.width * xScale;
            double height = this.height * yScale;

            gc.save();
            gc.translate(xPos, yPos);
            gc.rotate(Math.toDegrees(rotation));
            gc.translate(-(xPos+width/2), -(yPos+height/2));
            gc.drawImage(texture, xPos, yPos, width, height);
            gc.restore();
        }
    }

    @Override
    public boolean inside(float xPos, float yPos)
    {
        return xPos > this.xPos - width / 2 && xPos < this.xPos + width / 2 &&
                yPos > this.yPos - height / 2 && yPos < this.yPos + height / 2;
    }

    @Override
    public String getSaveString()
    {
        return super.getSaveString() + ",w=" + width + ",h=" + height;
    }

    @Override
    public void applySaveString(String saveString)
    {
        this.width = MapUtil.getFloatValue(saveString, "w");
        this.height = MapUtil.getFloatValue(saveString, "h");
        super.applySaveString(saveString);
    }

}
