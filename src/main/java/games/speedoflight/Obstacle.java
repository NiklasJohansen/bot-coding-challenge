package games.speedoflight;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class Obstacle
{
    private float xPos;
    private float yPos;
    private float width;
    private float height;
    private float[][] sides;
    private float[][] vertices;

    public Obstacle(float x, float y, float width, float height)
    {
        this.xPos = x;
        this.yPos = y;
        this.width = width;
        this.height = height;
        this.sides = new float[][] {
            {xPos, yPos, xPos + width, yPos},
            {xPos+width, yPos, xPos + width, yPos+height},
            {xPos+width, yPos+height, xPos, yPos+height},
            {xPos, yPos+height, xPos, yPos}
        };
        this.vertices = new float[][] {
            {xPos, yPos},
            {xPos + width, yPos},
            {xPos + width, yPos + height},
            {xPos, yPos + height}
        };
    }

    public void draw(GraphicsContext gc)
    {
        gc.setFill(Color.GRAY);
        gc.fillRect(xPos, yPos, width, height);
    }

    public void resolveCollision(LightPlayer player)
    {
        float xPlayer = player.getX();
        float yPlayer = player.getY();
        float radius = player.getRadius();
        float xCenter = getCenterX();
        float yCenter = getCenterY();

        float xDelta = xPlayer - Math.max(xPos, Math.min(xPlayer, xPos + width));
        float yDelta = yPlayer - Math.max(yPos, Math.min(yPlayer, yPos + height));
        float dist = (float)Math.sqrt(xDelta * xDelta + yDelta * yDelta);

        if(dist < radius)
        {
            float penetration = radius - dist;

            for(int i = 0; i < sides.length; i++)
            {
                float[] side = sides[i];
                float[] intersection = Util.lineIntersection(
                        xPlayer, yPlayer, xCenter, yCenter,
                        side[0],side[1],side[2],side[3]);

                if(intersection != null)
                {
                    switch (i)
                    {
                        case 0: player.setY(yPlayer - penetration); break;
                        case 1: player.setX(xPlayer + penetration); break;
                        case 2: player.setY(yPlayer + penetration); break;
                        case 3: player.setX(xPlayer - penetration); break;
                    }
                    break;
                }
            }
        }
    }

    public void resolveBulletCollision(Bullet bullet)
    {
        for(int i = 0; i < sides.length; i++)
        {
            float[] side = sides[i];
            float[] intersection = Util.lineIntersection(
                    bullet.getX(), bullet.getY(), bullet.getXLast(), bullet.getYLast(),
                    side[0],side[1],side[2],side[3]);

            if(intersection != null)
            {
                bullet.setDead();

//                if(Util.isPointInsidePolygon(vertices, bullet.getX(), bullet.getY())) {
//                    bullet.setXLast(bullet.getX());
//                    bullet.setYLast(bullet.getY());
//                    bullet.setX(intersection[0]);
//                    bullet.setY(intersection[1]);
//                    break;
//                }
            }
        }
    }

    public float getCenterX()
    {
        return xPos + width / 2;
    }

    public float getCenterY()
    {
        return yPos + height / 2;
    }
}
