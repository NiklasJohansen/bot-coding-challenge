package games.speedoflight.environmemt;

import games.speedoflight.Camera;
import games.speedoflight.environmemt.entities.Obstacle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class CollisionMask
{
    private byte[][] collisionMask;
    private int maskCellSize;

    private int width;
    private int height;

    public CollisionMask(int width, int height, int maskCellSize)
    {
        this.width = width;
        this.height = height;
        this.maskCellSize = maskCellSize;
        this.collisionMask = new byte[height][width];
    }

    public void draw(Camera camera)
    {
        GraphicsContext gc = camera.getGraphicsContext();
        gc.setFill(Color.rgb(0,0,0,0.3));
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                if(collisionMask[y][x] > 0)
                    gc.fillRect(x * maskCellSize, y * maskCellSize, maskCellSize, maskCellSize);
    }

    public void updateAll(List<Obstacle> obstacles)
    {
        this.collisionMask = new byte[height][width];
        obstacles.forEach(this::apply);
    }

    public void apply(Obstacle obstacle)
    {
        int xPos = (int) obstacle.getX();
        int yPos = (int) obstacle.getY();

        byte[][] mask = obstacle.getCollisionMask(maskCellSize);
        int xGrid = xPos / maskCellSize;
        int yGrid = yPos / maskCellSize;

        if(xGrid >= 0 && xGrid < width && yGrid >= 0 && yGrid < height)
            fillMask(mask, (byte) 1, xGrid, yGrid);
    }

    public void remove(Obstacle obstacle, float xPos, float yPos)
    {
        byte[][] mask = obstacle.getPreviousCollisionMask();

        int xGrid = (int) xPos / maskCellSize;
        int yGrid = (int) yPos / maskCellSize;

        if(xGrid >= 0 && xGrid < width && yGrid >= 0 && yGrid < height)
            fillMask(mask, (byte) 0, xGrid, yGrid);
    }

    private void fillMask(byte[][] mask, byte value, int xGridCenter, int yGridCenter)
    {
        for(int y = 0; y < mask.length; y++)
        {
            for (int x = 0; x < mask[y].length; x++)
            {
                if(mask[y][x] > 0)
                {
                    int xMap = xGridCenter - mask[y].length / 2 + x;
                    int yMap = yGridCenter - mask.length / 2 + y;
                    if(xMap >= 0 && xMap < collisionMask[0].length && yMap >= 0 && yMap < collisionMask.length)
                        collisionMask[yMap][xMap] = value;
                }
            }
        }
    }

    public byte[][] getByteMask()
    {
        return collisionMask;
    }

    public int[] getIntegerSequenceMask()
    {
        int nLongs = (int) Math.ceil((double)(width * height) / Integer.SIZE);

        int[] newArray = new int[nLongs];
        int bitCounter = 0;
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                int bitIndex = (Integer.SIZE - 1) - (bitCounter % Integer.SIZE);
                newArray[bitCounter / Integer.SIZE] |= (collisionMask[i][j] > 0) ? (1 << bitIndex) : 0L;
                bitCounter++;
            }
        }
        return newArray;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getCellSize()
    {
        return maskCellSize;
    }
}
