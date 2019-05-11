package games.speedoflight;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Effect;

import java.util.List;

public class Camera
{
    private static final double TRANSLATION_SMOOTHING = 0.05;
    private static final double SCALING_SMOOTHING = 0.03;

    private Canvas canvas;

    private float xPos;
    private float yPos;
    private float xPosTarget;
    private float yPosTarget;

    private float scale;
    private float scaleTarget;

    public Camera(Canvas canvas, float xPos, float yPos)
    {
        this.canvas = canvas;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xPosTarget = xPos;
        this.yPosTarget = yPos;
        this.scale = 1.0f;
        this.scaleTarget = 1.0f;
    }

    public void startCapture()
    {
        double xCenter = -xPos + (canvas.getWidth() / 2 / scale) ;
        double yCenter = -yPos + (canvas.getHeight() / 2 / scale);
        canvas.getGraphicsContext2D().scale(scale, scale);
        canvas.getGraphicsContext2D().translate(xCenter, yCenter);
    }

    public void endCapture()
    {
        double xCenter = xPos - (canvas.getWidth() / 2 / scale) ;
        double yCenter = yPos - (canvas.getHeight() / 2 / scale) ;
        canvas.getGraphicsContext2D().translate(xCenter, yCenter);
        canvas.getGraphicsContext2D().scale(1 / scale, 1 / scale);
    }

    public void update()
    {
        scale += (scaleTarget - scale) * SCALING_SMOOTHING;
        xPos += (xPosTarget - xPos) * TRANSLATION_SMOOTHING * scale;
        yPos += (yPosTarget - yPos) * TRANSLATION_SMOOTHING * scale;
    }

    public void setTargetZoom(float zoom)
    {
        this.scaleTarget = (float) Math.max(0.1, Math.min(1.5, zoom));
    }

    public void setZoom(float zoom)
    {
        this.scale = (float) Math.max(0.1, Math.min(1.5, zoom));
    }

    public void zoom(double dir)
    {
        scale = (float) Math.max(0.1, Math.min(1.5, scale + dir));
    }

    public void setTarget(float x, float y)
    {
        this.xPosTarget = x;
        this.yPosTarget = y;
    }

    public void followAlivePlayers(List<LightPlayer> players)
    {
        if(players.size() == 0)
        {
            setTargetZoom(1);
            setTarget(0,0);
            return;
        }

        float left = 0;
        float right = 0;
        float bottom = 0;
        float top = 0;
        float xCenterPlayers = 0;
        float yCenterPlayers = 0;
        int nPlayers = 0;
        for(LightPlayer player: players)
        {
            if(!player.isDead())
            {
                if(nPlayers == 0)
                {
                    left = right = player.getX();
                    bottom = top = player.getY();
                }
                xCenterPlayers += player.getX();
                yCenterPlayers += player.getY();
                left = Math.min(left, player.getX());
                top = Math.min(top, player.getY());
                right = Math.max(right, player.getX());
                bottom = Math.max(bottom, player.getY());
                nPlayers++;
            }
        }
        xCenterPlayers /= nPlayers;
        yCenterPlayers /= nPlayers;

        float xScale = (float) canvas.getWidth() / ((right - left) * 1.5f);
        float yScale = (float) canvas.getHeight() / ((bottom - top) * 1.5f);

        setTargetZoom(Math.min(xScale, yScale));
        setTarget(xCenterPlayers, yCenterPlayers);
    }

    public float getX()
    {
        return xPos;
    }

    public float getY()
    {
        return yPos;
    }

    public float screenToWorldPositionX(float xPosScreen)
    {
        return (xPosScreen / scale) + (xPos - ((float) canvas.getWidth() / 2 / scale));
    }

    public float screenToWorldPositionY(float yPosScreen)
    {
        return (yPosScreen / scale) + (yPos - ((float) canvas.getHeight() / 2 / scale));
    }

    public float getZoom()
    {
        return scale;
    }

    public float getViewportWidth()
    {
        return (float) canvas.getWidth();
    }

    public float getViewportHeight()
    {
        return (float )canvas.getHeight();
    }

    public GraphicsContext getGraphicsContext()
    {
        return canvas.getGraphicsContext2D();
    }
}
