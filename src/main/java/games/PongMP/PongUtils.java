package games.PongMP;

public class PongUtils
{
    /**
     * isPointInsidePolygon
     * https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
     */
    public static boolean isPointInsidePolygon(double[][] points, double xTest, double yTest)
    {
        boolean inside = false;
        for (int i = 0, j = points.length - 1; i < points.length; j = i++)
        {
            if (((points[i][1] > yTest) != (points[j][1] > yTest)) &&
                    (xTest < (points[j][0] - points[i][0]) * (yTest - points[i][1]) / (points[j][1] - points[i][1]) + points[i][0]))
                inside = !inside;
        }
        return inside;
    }

    /**
     * Finds and returns the intersection point between two line segments.
     * Based on code from the book "Tricks of the Windows Game Programming Gurus" by Andrè LaMothe
     * @param x0 x in first point in first line
     * @param y0 y in first point in first line
     * @param x1 x in second point in first line
     * @param y1 y in second point in first line
     * @param x2 x in first point in second line
     * @param y2 y in first point in second line
     * @param x3 x in second point in second line
     * @param y3 x in second point in second line
     * @return an array containing the x and y coordinate of the intersection point. Null if no intersection.
     */
    public static double[] getLineIntersection(double x0, double y0, double x1, double y1,
                                         double x2, double y2, double x3, double y3)
    {
        double s1x = x1 - x0;
        double s1y = y1 - y0;
        double s2x = x3 - x2;
        double s2y = y3 - y2;

        double s = (-s1y * (x0 - x2) + s1x * (y0 - y2)) / (-s2x * s1y + s1x * s2y);
        double t = ( s2x * (y0 - y2) - s2y * (x0 - x2)) / (-s2x * s1y + s1x * s2y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
            return new double[] {x0 + (t * s1x), y0 + (t * s1y)};
        else
            return null;
    }
}
