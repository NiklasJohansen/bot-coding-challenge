package games.speedoflight.environmemt;

import games.speedoflight.Bullet;
import games.speedoflight.Camera;
import games.speedoflight.Controller;
import games.speedoflight.LightPlayer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RoundObstacle extends MapEntity
{
    private float radius;
    private float mass;

    public RoundObstacle(float xPos, float yPos, float radius, float massDensity)
    {
        super(xPos, yPos, true);
        this.radius = radius;
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

            double width = texture.getWidth() * textureScale;
            double height = texture.getHeight() * textureScale;
            gc.drawImage(texture, xPos - width/2, yPos - height/2, width, height);
            gc.setEffect(null);
            gc.setFill(Color.rgb(255,255,255,0.8f));
            gc.fillOval(xPos - radius, yPos - radius, 2 * radius, 2 * radius);
        }
        else
        {
            gc.setFill(Color.WHITESMOKE);
            gc.fillOval(xPos - radius, yPos - radius, 2 * radius, 2 * radius);
        }
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
            xPos += xNorm * penetration * (obstacleMass / totMass);
            yPos += yNorm * penetration * (obstacleMass / totMass);
            player.setX(xPlayer - xNorm * penetration * (playerMass / totMass));
            player.setY(yPlayer - yNorm * penetration * (playerMass / totMass));
        }
    }

    @Override
    public void resolveBulletCollision(Bullet bullet)
    {
        float xPosBullet = bullet.getX();
        float yPosBullet = bullet.getY();
        float xDelta = xPos - xPosBullet;
        float yDelta = yPos - yPosBullet;
        double dist_sq = xDelta * xDelta + yDelta * yDelta;
        if(dist_sq < radius * radius)
        {
            float dist = (float) Math.sqrt(dist_sq);
            float penetration = radius - dist;
            float xNorm = xDelta / dist;
            float yNorm = yDelta / dist;

            float bulletMass = 1.0f / bullet.getMass();
            float obstacleMass = 1.0f / mass;
            float totMass = bulletMass + obstacleMass;

            xPos -= xNorm * penetration * (obstacleMass / totMass);
            yPos -= yNorm * penetration * (obstacleMass / totMass);

            // https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection

        }

    }
}
