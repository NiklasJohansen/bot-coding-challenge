package games.speedoflight.environmemt.entities;

import games.speedoflight.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RoundObstacle extends Obstacle
{
    private float originalRadius;
    private float radius;
    private float mass;

    public RoundObstacle(float xPos, float yPos, float radius, float massDensity)
    {
        super(xPos, yPos);
        this.originalRadius = radius;
        this.radius = radius * xScale;
        this.mass = (float) Math.PI * radius * radius * massDensity;
    }

    public RoundObstacle(float xPos, float yPos, float radius)
    {
        this(xPos, yPos, radius, 1);
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
                gc.fillOval(xPos - radius, yPos - radius, 2 * radius, 2 * radius);
            }

            double width = (radius + radius) * textureScale;
            double height = (radius + radius) * textureScale;
            gc.drawImage(texture, xPos - width/2, yPos - height/2, width, height);
            gc.setEffect(null);
        }
        else
        {
            gc.setFill(Color.WHITESMOKE);
            gc.fillOval(xPos - radius, yPos - radius, 2 * radius, 2 * radius);
        }
    }

    @Override
    public boolean inside(float xPos, float yPos)
    {
        return Util.isPointInsideCircle(xPos, yPos, this.xPos, this.yPos, this.radius);
    }

    @Override
    public void resolvePlayerCollision(LightPlayer player)
    {
        float xPlayer = player.getX();
        float yPlayer = player.getY();

        float xDiff = xPos - xPlayer;
        float yDiff = yPos - yPlayer;
        float dist = (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        float xNorm = xDiff / dist;
        float yNorm = yDiff / dist;

        float playerMass = player.invMass();
        float obstacleMass = 1.0f / mass;
        float totMass = playerMass + obstacleMass;

        float penetration = (radius + player.getRadius()) - dist;
        if (penetration > 0)
        {
            float xPosOld = xPos;
            float yPosOld = yPos;
            xPos += xNorm * penetration * (obstacleMass / totMass);
            yPos += yNorm * penetration * (obstacleMass / totMass);
            player.setX(xPlayer - xNorm * penetration * (playerMass / totMass));
            player.setY(yPlayer - yNorm * penetration * (playerMass / totMass));
            onMoveEvent.accept(new MoveEvent(xPosOld, yPosOld, xPos, yPos));
        }
    }

    @Override
    public void resolveBulletCollision(Bullet bullet)
    {
        float xPosBullet = bullet.getX();
        float yPosBullet = bullet.getY();
        float xPosLastBullet = bullet.getXLast();
        float yPosLastBullet = bullet.getYLast();

        float[] intersection = Util.lineCircleIntersection(xPos, yPos, radius,
                xPosLastBullet, yPosLastBullet, xPosBullet, yPosBullet);

        if(intersection != null)
        {
            float xEdgeNormal = (intersection[0] - xPos) / radius;
            float yEdgeNormal = (intersection[1] - yPos) / radius;

            float xVel = xPosBullet - xPosLastBullet;
            float yVel = yPosBullet - yPosLastBullet;

            float velDot = xVel * xEdgeNormal + yVel * yEdgeNormal;
            float xVelNew = xVel - 2 * velDot * xEdgeNormal;
            float yVelNew = yVel - 2 * velDot * yEdgeNormal;

            bullet.setXLast(intersection[0]);
            bullet.setYLast(intersection[1]);
            bullet.setX(intersection[0] + xVelNew);
            bullet.setY(intersection[1] + yVelNew);
            bullet.decay();
        }

        if(Util.isPointInsideCircle(bullet.getX(), bullet.getY(), xPos, yPos, radius))
            bullet.setDead();
    }

    @Override
    public byte[][] getCollisionMask(int resolution)
    {
        int maskWidth = (int)radius * 2 / resolution;
        int maskHeight = (int)radius * 2 / resolution;
        byte[][] mask = new byte[maskHeight][maskWidth];

        int left = (int) (xPos - radius);
        int top = (int) (yPos - radius);

        for(int y = 0; y < maskHeight; y++)
        {
            for(int x = 0; x < maskWidth; x++)
            {
                int xWorld = left + x * resolution + (resolution / 2);
                int yWorld = top + y * resolution + (resolution / 2);

                if(Util.isPointInsideCircle(xWorld, yWorld, xPos, yPos, radius))
                    mask[y][x] = 1;
            }
        }

        super.storedCollisionMask = mask;
        return mask;
    }

    public void setScaleX(float xScale)
    {
        this.xScale = xScale;
        this.radius = originalRadius * xScale;
        this.onMoveEvent.accept(new MoveEvent(xPos, yPos, xPos, yPos));
    }
    public void setScaleY(float yScale)
    {
        this.yScale = yScale;
    }

}
