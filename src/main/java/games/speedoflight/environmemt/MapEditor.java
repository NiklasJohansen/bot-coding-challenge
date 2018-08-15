package games.speedoflight.environmemt;

import games.speedoflight.Camera;
import games.speedoflight.environmemt.entities.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class MapEditor
{
    private final float CAMERA_SPEED = 400;
    private final float ZOOM_SPEED = 0.08f;
    private final float ROTATION_SPEED = (float)Math.PI / 200;

    private boolean up, down, left, right, shift, delete, leftMouse,
            qKey, eKey, rKey, mKey, oKey, cKey, ctrl, pgUp, pgDown;

    private float xMouse, yMouse;
    private float xMousePinned, yMousePinned;
    private float scroll;

    private MapEntity selectedEntity;
    private float selectedEntityScaleX;
    private float selectedEntityScaleY;
    private float selectedOffsetX;
    private float selectedOffsetY;

    private String mapFolderPath;

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
                MapEntity entity = MapUtil.createNewEntity(selectedAsset, xWorld, yWorld);
                entity.setTextureScale(selectedAsset.getTextureScale());
                map.addMapEntity(entity);
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
            List<MapEntity> entities = map.getEntities();
            for(int i = entities.size() - 1; i >= 0; i--)
            {
                MapEntity entity = entities.get(i);
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
                    selectedOffsetX = selectedEntity.getX() - xWorld;
                    selectedOffsetY = selectedEntity.getY() - yWorld;
                }
            }
            else
            {
                selectedEntity.setX(xWorld + selectedOffsetX);
                selectedEntity.setY(yWorld + selectedOffsetY);
                selectedEntityScaleX = selectedEntity.getScaleX();
                selectedEntityScaleY = selectedEntity.getScaleY();
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

            if(pgUp || pgDown)
            {
                List<MapEntity> entities = map.getEntities();
                int index = entities.indexOf(selectedEntity) + (pgUp ? 1 : 0) + (pgDown ? -1 : 0);
                if(index >= 0 && index < entities.size())
                {
                    entities.remove(selectedEntity);
                    entities.add(index, selectedEntity);
                }
                pgUp = pgDown = false;
            }

            if(delete)
            {
                map.removeMapEntity(selectedEntity);
                selectedEntity.setX(-1000);
                selectedEntity = null;
                delete = false;
            }

            if(!leftMouse)
                selectedEntity = null;
        }

        if(ctrl && down)
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Speed of Light Save", "*.SLS"));
            File file = fileChooser.showSaveDialog(null);
            if(file != null)
            {
                MapUtil.save(map, file.toString());
                mapFolderPath = file.toString().replace("\\" + file.getName(), "");
                loadAssets(mapFolderPath);
            }

            ctrl = down = false;
        }

        if(ctrl && oKey)
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Speed of Light Save", "*.SLS"));
            File file = fileChooser.showOpenDialog(null);
            if(file != null)
            {
                map = MapUtil.load(file.toString());
                mapFolderPath = file.toString().replace("\\" + file.getName(), "");
                loadAssets(mapFolderPath);
            }
            ctrl = oKey = false;
        }

        if(ctrl && rKey)
        {
            if(mapFolderPath != null)
                loadAssets(mapFolderPath);
            ctrl = rKey = false;
        }

        if(cKey)
        {
            map.toggleShowCollision();
            cKey = false;
        }

        if(mKey)
        {
            map.toggleShowMapArea();
            mKey = false;
        }

        return map;
    }

    public void loadAssets(String folderPath)
    {
        assetHandler.load(new File(folderPath));
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

        assetHandler.draw(camera);
    }

    public void handleKeyEvents(KeyEvent event, boolean pressed)
    {
        ctrl = event.isControlDown();
        if(ctrl && leftMouse)
            setPinnedMouse();

        switch(event.getCode())
        {
            case W: up = pressed; break;
            case A: left = pressed; break;
            case S: down = pressed; break;
            case D: right = pressed; break;
            case Q: qKey = pressed; break;
            case E: eKey = pressed; break;
            case O: oKey = pressed; break;
            case R: rKey = pressed; break;
            case C: cKey = pressed; break;
            case M: mKey = pressed; break;
            case PAGE_UP: pgUp = pressed; break;
            case PAGE_DOWN: pgDown = pressed; break;
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
