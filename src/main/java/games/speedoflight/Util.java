package games.speedoflight;


public class Util
{
    public static double[] lineSegmentIntersection(double x0, double y0, double x1, double y1,
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

    /**
     * PNPOLY - https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
     */
    public static boolean isPointInsidePolygon(float xPoint, float yPoint, double[] xVertices, double[] yVertices)
    {
        boolean inside = false;
        for (int i = 0, j = xVertices.length - 1; i < xVertices.length; j = i++)
        {
            if (((yVertices[i] > yPoint) != (yVertices[j] > yPoint)) &&
                    (xPoint < (xVertices[j] - xVertices[i]) * (yPoint - yVertices[i]) / (yVertices[j] - yVertices[i]) + xVertices[i]))
                inside = !inside;
        }
        return inside;
    }

    public static boolean isPointInsideCircle(float xPoint, float yPoint, float xCircle, float yCircle, float radius)
    {
        float xDelta = xPoint - xCircle;
        float yDelta = yPoint - yCircle;
        float distSquared = xDelta * xDelta + yDelta * yDelta;
        return distSquared < radius * radius;
    }

    public static double pointEdgeDistance(double xPoint, double yPoint, double x0, double y0, double x1, double y1)
    {
        double xPointEdgeVec = xPoint - x0;
        double yPointEdgeVec = yPoint - y0;

        double xEdgeVec = x1 - x0;
        double yEdgeVec = y1 - y0;
        double edgeLength = Math.sqrt(xEdgeVec * xEdgeVec + yEdgeVec * yEdgeVec);

        double dot = xPointEdgeVec * xEdgeVec + yPointEdgeVec * yEdgeVec;
        double projectedDistance = Math.max(0, Math.min(edgeLength, dot / edgeLength));

        double xEdgeNorm = xEdgeVec / edgeLength;
        double yEdgeNorm = yEdgeVec / edgeLength;

        double xProjection = x0 + xEdgeNorm * projectedDistance;
        double yProjection = y0 + yEdgeNorm * projectedDistance;

        double xPointProjVec = xPoint - xProjection;
        double yPointProjVec = yPoint - yProjection;

        return Math.sqrt(xPointProjVec * xPointProjVec + yPointProjVec * yPointProjVec);
    }

    /**
     * https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection
     */
    public static float[] lineCircleIntersection(float xCircle, float yCircle, float radius,
                                                 float x0, float y0, float x1, float y1)
    {
        float xDir = x1 - x0;
        float yDir = y1 - y0;
        float lineLength = (float) Math.sqrt(xDir * xDir + yDir * yDir);
        xDir /= lineLength;
        yDir /= lineLength;

        float xL = xCircle - x0;
        float yL = yCircle - y0;

        float tca = xL * xDir + yL * yDir;
        if(tca < 0)
            return null;

        float d2 = (xL * xL + yL * yL) - tca * tca;
        if(d2 > radius * radius)
            return null;

        float thc = (float) Math.sqrt((radius * radius) - d2);
        float t0 = tca - thc;
        float t1 = tca + thc;

        if(t0 > t1)
        {
            float temp = t0;
            t0 = t1;
            t1 = temp;
        }

        if(t0 < 0)
        {
            t0 = t1;
            if(t0 < 0)
                return null;
        }

        if(lineLength < t0)
            return null;

        return new float[] {x0 + xDir * t0, y0 + yDir * t0};
    }
}
