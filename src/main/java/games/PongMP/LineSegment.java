package games.PongMP;

public class LineSegment
{
    public double x0, y0; // Start
    public double x1, y1; // Stop
    public double xNormal, yNormal;
    public double xDirection, yDirection;
    public double length;

    public void setLine(double x0, double y0, double x1, double y1)
    {
        this.x0 =  x0;
        this.y0 =  y0;
        this.x1 =  x1;
        this.y1 =  y1;
        calculateLength();
        calculateDirection();
    }

    public void setNormal(double xn, double yn)
    {
        this.xNormal = xn;
        this.yNormal = yn;
    }

    private void calculateLength()
    {
        double xDelta = x0 - x1;
        double yDelta = y0 - y1;
        this.length = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
    }

    private void calculateDirection()
    {
        this.xDirection = (x0 - x1) / length;
        this.yDirection = (y0 - y1) / length;
    }

}
