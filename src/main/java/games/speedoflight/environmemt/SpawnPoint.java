package games.speedoflight.environmemt;

public class SpawnPoint
{
    private float xPos;
    private float yPos;

    public SpawnPoint(float xPos, float yPos)
    {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public float getX()
    {
        return xPos;
    }

    public float getY()
    {
        return yPos;
    }
}
