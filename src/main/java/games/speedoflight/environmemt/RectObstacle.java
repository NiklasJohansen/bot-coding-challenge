package games.speedoflight.environmemt;

import games.speedoflight.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class RectObstacle extends MapEntity
{
    private double[] xVertices;
    private double[] yVertices;
    private double angle;
    private float width;
    private float height;

    public RectObstacle(float xPos, float yPos, float width, float height)
    {
        this(xPos, yPos, width, height, 0);
    }

    public RectObstacle(float xPos, float yPos, float width, float height, double angle)
    {
        super(xPos, yPos, true);
        this.angle = angle;
        this.width = width;
        this.height = height;
        this.xVertices = new double[4];
        this.yVertices = new double[4];

        calculateVertices(angle);
    }

    @Override
    public void draw(Camera camera)
    {
        GraphicsContext gc = camera.getGraphicsContext();

        if(texture != null)
        {
            if(baseColor != null)
            {
                gc.setFill(baseColor);
                gc.fillPolygon(xVertices, yVertices, xVertices.length);
            }

            double width = this.width * textureScale;
            double height = this.height * textureScale;
            gc.save();
            gc.translate(xPos, yPos);
            gc.rotate(Math.toDegrees(angle));
            gc.translate(-(xPos+width/2), -(yPos+height/2));
            gc.drawImage(texture, xPos, yPos, width, height);
            gc.restore();
        }
        else
        {
            gc.setFill(Color.WHITESMOKE);
            gc.fillPolygon(xVertices, yVertices, xVertices.length);
        }
    }

    private void calculateVertices(double angle)
    {
        float w = width / 2;
        float h = height / 2;

        double x0 = Math.cos(angle) * w;
        double y0 = Math.sin(angle) * w;
        double x1 = -Math.sin(angle) * h;
        double y1 =  Math.cos(angle) * h;

        xVertices[0] = (xPos - x0) - x1;
        yVertices[0] = (yPos - y0) - y1;
        xVertices[1] = (xPos - x0) + x1;
        yVertices[1] = (yPos - y0) + y1;
        xVertices[2] = (xPos + x0) + x1;
        yVertices[2] = (yPos + y0) + y1;
        xVertices[3] = (xPos + x0) - x1;
        yVertices[3] = (yPos + y0) - y1;
    }

    @Override
    public void resolvePlayerCollision(LightPlayer player)
    {
        float xPlayer = player.getX();
        float yPlayer = player.getY();
        float radius = player.getRadius();

        double lowestDist = Double.MAX_VALUE;
        int vertexIndex0 = 0;
        int vertexIndex1 = 0;
        for(int i = 0, j = xVertices.length - 1; i < xVertices.length; j = i++)
        {
            double dist = Util.pointEdgeDistance(xPlayer, yPlayer,
                    xVertices[i], yVertices[i], xVertices[j], yVertices[j]);

            if(dist < lowestDist)
            {
                lowestDist = dist;
                vertexIndex0 = j;
                vertexIndex1 = i;
            }
        }

        if(lowestDist < radius)
        {
            double penetration = radius - lowestDist;
            double xEdge = xVertices[vertexIndex0] - xVertices[vertexIndex1];
            double yEdge = yVertices[vertexIndex0] - yVertices[vertexIndex1];
            double edgeLength = Math.sqrt(xEdge * xEdge + yEdge * yEdge);

            double xNorm = xEdge / edgeLength;
            double yNorm = yEdge / edgeLength;

            xPlayer -= -yNorm * penetration;
            yPlayer -=  xNorm * penetration;

            player.setX(xPlayer);
            player.setY(yPlayer);
        }
    }

    public float getRectCircleOverlap(float xCircle, float yCircle)
    {
        float lowestDist = Float.MAX_VALUE;
        for(int i = 0, j = xVertices.length - 1; i < xVertices.length; j = i++)
        {
            float dist = (float) Util.pointEdgeDistance(xCircle, yCircle,
                    xVertices[i], yVertices[i], xVertices[j], yVertices[j]);

            if(dist < lowestDist)
                lowestDist = dist;
        }
        return lowestDist;
    }

    public boolean isPointInside(float xPos, float yPos)
    {
        return Util.isPointInsidePolygon(xPos, yPos, xVertices, yVertices);
    }


    @Override
    public void resolveBulletCollision(Bullet bullet)
    {
        float xPosBullet = bullet.getX();
        float yPosBullet = bullet.getY();
        float xPosLastBullet = bullet.getXLast();
        float yPosLastBullet = bullet.getYLast();

        double intersectionDist = Float.MAX_VALUE;
        double[] closestIntersection = null;
        int firstVertexIndex = 0;
        int secondVertexIndex = 0;

        for(int i = 0, j = xVertices.length - 1; i < xVertices.length; j = i++)
        {
            double[] intersection = Util.lineIntersection(xPosBullet, yPosBullet, xPosLastBullet, yPosLastBullet,
                    xVertices[i], yVertices[i], xVertices[j], yVertices[j]);

            if(intersection != null)
            {
                double dist = Util.pointEdgeDistance(xPosLastBullet, yPosLastBullet,
                        xVertices[i], yVertices[i], xVertices[j], yVertices[j]);

                if(dist < intersectionDist)
                {
                    intersectionDist = dist;
                    closestIntersection = intersection;
                    firstVertexIndex = i;
                    secondVertexIndex = j;
                }
            }
        }

        if(closestIntersection != null)
        {
            float xVel = xPosBullet - xPosLastBullet;
            float yVel = yPosBullet - yPosLastBullet;

            double xEdgeVec = xVertices[firstVertexIndex] - xVertices[secondVertexIndex];
            double yEdgeVec = yVertices[firstVertexIndex] - yVertices[secondVertexIndex];
            double edgeDistance = Math.sqrt(xEdgeVec * xEdgeVec + yEdgeVec * yEdgeVec);
            double xEdgeNormal = -(yEdgeVec / edgeDistance);
            double yEdgeNormal =  (xEdgeVec / edgeDistance);

            double velDot = xVel * xEdgeNormal + yVel * yEdgeNormal;
            double xVelNew = xVel - 2 * velDot * xEdgeNormal;
            double yVelNew = yVel - 2 * velDot * yEdgeNormal;

            bullet.setXLast( (float) closestIntersection[0]);
            bullet.setYLast( (float) closestIntersection[1]);
            bullet.setX( (float) (closestIntersection[0] + xVelNew));
            bullet.setY( (float) (closestIntersection[1] + yVelNew));
            bullet.decay();
        }

        if(Util.isPointInsidePolygon(bullet.getX(), bullet.getY(), xVertices, yVertices))
            bullet.setDead();
    }
}
