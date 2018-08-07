package games.speedoflight;

import core.server.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class LightPlayer extends Player<LightPlayer.ClientResponse> implements Comparable
{
    private final float ACCELERATION = 0.4f;
    private final float ROT_ACCELERATION = 0.08f;
    private final float FRICTION = 0.97f;

    private float radius;
    private float xPos;
    private float yPos;
    private float xPosLast;
    private float yPosLast;
    private float rotation;
    private float life;
    private float mass;

    private static int unknownNameCounter;
    private String unknownName;

    // Score
    private int kills = 0;
    private int deaths = 0;

    public LightPlayer()
    {
        super(ClientResponse.class);
        this.xPos = 400;
        this.yPos = 400;
        this.xPosLast = 400;
        this.yPosLast = 400;
        this.radius = 20;
        this.life = 100;
        this.mass = (float)(Math.PI * Math.pow(radius, 2));
        this.unknownName = "UNKNOWN_" + unknownNameCounter++;
    }

    public void update(List<Bullet> bullets)
    {
        ClientResponse response = getResponse();
        updatePosition(response);
        handleAction(response, bullets);
    }

    private void handleAction(ClientResponse response, List<Bullet> bullets)
    {
        if(response != null && response.fire && !isDead())
        {
            response.fire = false;
            bullets.add(new Bullet(this,
                    xPos + (float)Math.cos(rotation) * (radius + 2),
                    yPos + (float)Math.sin(rotation) * (radius + 2),
                    rotation, 50));
        }

        for (int i = 0; i < bullets.size(); i++)
        {
            Bullet b = bullets.get(i);

            if(b.getOwner() == this)
                continue;

            float xDiff = xPos - b.getX();
            float yDiff = yPos - b.getY();
            float dist = (float)Math.sqrt(xDiff * xDiff + yDiff * yDiff);

            if(dist < radius * 2)
            {
                float xBulletDiff = b.getX() - b.getXLast();
                float yBulletDiff = b.getY() - b.getYLast();

                for(int j = 0; j < 10; j++)
                {
                    float xBullet = b.getXLast() + ((float)j / 10f) * xBulletDiff;
                    float yBullet = b.getYLast() + ((float)j / 10f) * yBulletDiff;

                    float xDiffInter = xPos - xBullet;
                    float yDistInter = yPos - yBullet;
                    float interpolatedDist = (float)Math.sqrt(xDiffInter * xDiffInter + yDistInter * yDistInter);

                    if(interpolatedDist < radius)
                    {
                        float bulletSpeed = b.getSpeed();
                        float bulletMass = 1.0f / b.getMass();
                        float playerMass = 1.0f / mass;
                        float totMass = playerMass + bulletMass;

                        float xNorm = xBulletDiff / bulletSpeed;
                        float yNorm = yBulletDiff / bulletSpeed;

                        float penetration = radius - interpolatedDist;
                        xPos += xNorm * penetration * (playerMass / totMass);
                        yPos += yNorm * penetration * (playerMass / totMass);

                        float prevLife = life;
                        life -= b.getSpeed() * b.getLife();
                        b.setDead();

                        if(life <= 0 && prevLife > 0)
                        {
                            b.getOwner().increaseKills();
                            deaths++;
                        }
                        break;
                    }
                }
            }
        }
    }

    private void updatePosition(ClientResponse response)
    {
        float xVel = xPos - xPosLast;
        float yVel = yPos - yPosLast;
        xPosLast = xPos;
        yPosLast = yPos;

        if(!isDead() && response != null)
        {
            if(response.up)
            {
                xVel -= Math.cos(rotation + Math.PI) * ACCELERATION;
                yVel -= Math.sin(rotation + Math.PI) * ACCELERATION;
            }

            if(response.down)
            {
                xVel += Math.cos(rotation + Math.PI) * ACCELERATION;
                yVel += Math.sin(rotation + Math.PI) * ACCELERATION;
            }

            if(response.left)
            {
                xVel -= Math.cos(rotation + Math.PI * 0.5) * ACCELERATION;
                yVel -= Math.sin(rotation + Math.PI * 0.5) * ACCELERATION;
            }

            if(response.right)
            {
                xVel += Math.cos(rotation + Math.PI * 0.5) * ACCELERATION;
                yVel += Math.sin(rotation + Math.PI * 0.5) * ACCELERATION;
            }

            if(response.rotLeft)
                rotation -= ROT_ACCELERATION;

            if(response.rotRight)
                rotation += ROT_ACCELERATION;
        }

        if(rotation > 2 * Math.PI)
            rotation = 0;
        else if(rotation < 0)
            rotation = 2.0f * (float)Math.PI;

        xVel *= FRICTION;
        yVel *= FRICTION;

        xPos += xVel;
        yPos += yVel;
    }

    public void draw(Camera camera)
    {
        float c = Math.max(0, life/100f);
        float opacity = isDead() ? 0.3f : 1f;
        Color playerColor = Color.color(0.3627451f, 0.3627451f * c, 0.3627451f * c, opacity);

        GraphicsContext gc = camera.getGraphicsContext();

        gc.setEffect(new DropShadow(30, Color.rgb(0,0,0,0.5)));
        gc.setFill(playerColor);
        gc.fillOval(xPos - radius, yPos - radius, radius * 2, radius * 2);
        gc.setStroke(Color.BLACK);
        gc.strokeLine(xPos, yPos, xPos + Math.cos(rotation) * radius, yPos + Math.sin(rotation) * radius);
        gc.setEffect(null);

        gc.setFill(Color.color(1, 1, 1, opacity));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font(16));
        gc.fillText(getName(),  xPos, yPos - radius - 10);
    }

    public void resolvePlayerCollision(LightPlayer player)
    {
        float xDiff = xPos - player.xPos;
        float yDiff = yPos - player.yPos;
        float dist = (float)Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        float xNorm = xDiff / dist;
        float yNorm = yDiff / dist;

        float playerMass = 1.0f / mass;
        float otherPlayerMass = 1.0f / player.mass;
        float totMass = playerMass + otherPlayerMass;

        float penetration = (radius + player.radius) - dist;
        if (penetration > 0)
        {
            xPos += xNorm * penetration * (playerMass / totMass);
            yPos += yNorm * penetration * (playerMass / totMass);
            player.xPos -= yNorm * penetration * (otherPlayerMass / totMass);
            player.yPos -= yNorm * penetration * (otherPlayerMass / totMass);
        }
    }

    public int getKills()
    {
        return kills;
    }

    public int getDeaths()
    {
        return deaths;
    }

    public void increaseKills()
    {
        kills++;
    }

    public void setY(float y)
    {
        this.yPos = y;
    }

    public void setX(float x)
    {
        this.xPos = x;
    }

    public void setYLast(float y)
    {
        this.yPosLast = y;
    }

    public void setXLast(float x)
    {
        this.xPosLast = x;
    }

    public float invMass()
    {
        return 1.0f / mass;
    }

    public boolean isDead()
    {
        return life <= 0;
    }

    public float getX()
    {
        return xPos;
    }

    public float getY()
    {
        return yPos;
    }

    public float getRadius()
    {
        return radius;
    }

    public String getName()
    {
        ClientResponse response = getResponse();
        if(response != null && !response.username.equals(""))
            return response.username;
        else
            return this.unknownName;
    }

    public void setAlive()
    {
        life = 100;
    }

    @Override
    public int compareTo(Object player) {
        int killDiff = ((LightPlayer)player).getKills() - kills;
        if (killDiff != 0)
            return killDiff;
        else return deaths - ((LightPlayer)player).getKills();
    }

    public static class ClientResponse
    {
        public String username;
        public boolean up;
        public boolean down;
        public boolean left;
        public boolean right;
        public boolean rotLeft;
        public boolean rotRight;
        public boolean fire;
    }
}
