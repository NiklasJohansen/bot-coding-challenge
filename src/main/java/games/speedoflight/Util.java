package games.speedoflight;

public class Util {


    public static float[] lineIntersection(float x0, float y0, float x1, float y1,
                                      float x2, float y2, float x3, float y3)
    {
        float s1x = x1 - x0;
        float s1y = y1 - y0;
        float s2x = x3 - x2;
        float s2y = y3 - y2;

        float s = (-s1y * (x0 - x2) + s1x * (y0 - y2)) / (-s2x * s1y + s1x * s2y);
        float t = ( s2x * (y0 - y2) - s2y * (x0 - x2)) / (-s2x * s1y + s1x * s2y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
            return new float[] {x0 + (t * s1x), y0 + (t * s1y)};
        else
            return null;
    }

    public static boolean isPointInsidePolygon(float[][] points, float xTest, float yTest)
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

}
