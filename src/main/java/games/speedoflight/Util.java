package games.speedoflight;

import javafx.scene.paint.Color;

public class Util {


    public static double[] lineIntersection(double x0, double y0, double x1, double y1,
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



}
