package games.speedoflight.environmemt;

import games.speedoflight.Camera;
import games.speedoflight.environmemt.entities.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.List;

public class MapEditor
{
    private final float CAMERA_SPEED = 400;
    private final float ZOOM_SPEED = 0.08f;
    private final float ROTATION_SPEED = (float)Math.PI / 200;

    private boolean up, down, left, right, shift, delete, leftMouse, rightMouse, qKey, eKey, ctrl;
    private float xMouse, yMouse;
    private float xMousePinned, yMousePinned;
    private float scroll;

    private MapEntity selectedEntity;
    private float selectedEntityScaleX;
    private float selectedEntityScaleY;
    private float selectedOffsetX;
    private float selectedOffsetY;

    private AssetHandler assetHandler;
    private AssetHandler.Asset selectedAsset;

    public MapEditor()
    {
        this.assetHandler = new AssetHandler();
    }

    public Map edit(Map map, Camera camera)
    {
        float xWorld = camera.screenToWorldPositionX(xMouse);
        float yWorld = camera.screenToWorldPositionY(yMouse);

        if(selectedAsset != null)
        {
            if(!leftMouse)
            {
                map.addMapEntity(createNewEntity(selectedAsset, xWorld, yWorld));
                selectedAsset = null;
            }
            return map;
        }
        else if(assetHandler.mouseInside(xMouse, yMouse))
        {
            assetHandler.update(xMouse, yMouse);
            if(leftMouse)
            {
                AssetHandler.Asset asset = assetHandler.getSelectedAsset();
                if(asset != null)
                {
                    selectedAsset = asset;
                }
            }
            return map;
        }

        if(selectedEntity == null && leftMouse)
        {
            List<Obstacle> obstacles = map.getObstacles();
            for(int i = obstacles.size() - 1; i >= 0; i--)
            {
                MapEntity entity = obstacles.get(i);
                if(entity.inside(xWorld, yWorld))
                {
                    selectedEntity = entity;
                    selectedEntityScaleX = entity.getScaleX();
                    selectedEntityScaleY = entity.getScaleY();
                    selectedOffsetX = entity.getX() - xWorld;
                    selectedOffsetY = entity.getY() - yWorld;
                    break;
                }
            }
        }

        if(selectedEntity != null)
        {
            if(ctrl)
            {
                if(leftMouse)
                {
                    float xScale = selectedEntityScaleX + (xMouse - xMousePinned) / 100;
                    float yScale = selectedEntityScaleY + (yMouse - yMousePinned) / 100;
                    selectedEntity.setScaleX((float) Math.max(0.1, xScale));
                    selectedEntity.setScaleY((float) Math.max(0.1, yScale));
                }
            }
            else
            {
                selectedEntity.setX(xWorld + selectedOffsetX);
                selectedEntity.setY(yWorld + selectedOffsetY);
            }

            float rot = selectedEntity.getRotation();
            float speed = ROTATION_SPEED;

            if(shift && (eKey || qKey))
            {
                speed = (float) (Math.PI / 4);
                rot = (int)(rot / speed) * speed;
                rot += (eKey ? speed : 0) + (qKey ? -speed : 0);
                selectedEntity.setRotation(rot);
                eKey = qKey = false;
            }
            else
            {
                rot += (eKey ? speed : 0) + (qKey ? -speed : 0);
                selectedEntity.setRotation(rot);
            }

            if(delete)
            {
                map.getObstacles().remove(selectedEntity);
                selectedEntity.setX(-1000);
            }

            if(!leftMouse)
                selectedEntity = null;
        }

        return map;
    }

    private MapEntity createNewEntity(AssetHandler.Asset asset, float xPos, float yPos)
    {
        MapEntity entity;
        Image texture = asset.getTexture();
        float texScale = asset.getTextureScale();
        float width = (float)texture.getWidth();
        float height = (float)texture.getHeight();
        String entityType = asset.getType();

        if(entityType.contains("obstacle"))
        {
            if(entityType.contains("round"))
                entity = new RoundObstacle(xPos, yPos, width/2/texScale);
            else
                entity = new RectObstacle(xPos, yPos, width, height);
        }
        else if(entityType.contains("spawnpoint"))
        {
            entity = new SpawnPoint(xPos, yPos);
        }
        else entity = new Decoration(xPos, yPos, width, height);

        entity.setTexture(texture);
        entity.setTextureScale(selectedAsset.getTextureScale());
        return entity;
    }

    public void draw(Camera camera)
    {
        float x = camera.getX() + (left ? -CAMERA_SPEED : 0) + (right ? CAMERA_SPEED : 0);
        float y = camera.getY() + (up ? -CAMERA_SPEED : 0)   + (down ? CAMERA_SPEED : 0);
        float zoom = camera.getZoom() + Math.signum(scroll) * ZOOM_SPEED;
        scroll = 0;

        camera.setTarget(x, y);
        camera.setZoom(zoom);
        camera.setTargetZoom(zoom);

        assetHandler.draw(camera);
        if(selectedAsset != null)
        {
            Image texture = selectedAsset.getTexture();
            float texWidth = (float) texture.getWidth() * camera.getZoom();
            float texHeight = (float) texture.getHeight() * camera.getZoom();
            GraphicsContext gc = camera.getGraphicsContext();
            gc.drawImage(selectedAsset.getTexture(),
                    xMouse - texWidth / 2,
                    yMouse - texHeight / 2,
                    texWidth,
                    texHeight);
        }
    }

    public void handleKeyEvents(KeyEvent event, boolean pressed)
    {
        ctrl = event.isControlDown();
        if(ctrl && leftMouse)
        {
            setPinnedMouse();
        }
        else selectedEntity = null;
        switch(event.getCode())
        {
            case W: up = pressed; break;
            case A: left = pressed; break;
            case S: down = pressed; break;
            case D: right = pressed; break;
            case Q: qKey = pressed; break;
            case E: eKey = pressed; break;
            case SHIFT: shift = pressed; break;
            case DELETE: delete = pressed; break;
        }
    }

    public void handleMousePressEvents(MouseEvent event, boolean pressed)
    {
        switch(event.getButton())
        {
            case PRIMARY:
                leftMouse = pressed;
                if(ctrl && pressed)
                    setPinnedMouse();
                break;

            case SECONDARY:
                rightMouse = pressed;
                setPinnedMouse();
                break;
        }
    }

    public void handleMouseMoveEvents(MouseEvent event)
    {
        xMouse = (float) event.getX();
        yMouse = (float) event.getY();
    }

    public void handleScrollEvent(ScrollEvent event)
    {
        if(assetHandler.mouseInside(xMouse, yMouse))
            assetHandler.scroll((float)event.getDeltaY());
        else
            this.scroll += event.getDeltaY();
    }

    private void setPinnedMouse()
    {
        xMousePinned = xMouse;
        yMousePinned = yMouse;
    }

}
