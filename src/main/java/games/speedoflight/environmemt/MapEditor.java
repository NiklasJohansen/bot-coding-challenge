package games.speedoflight.environmemt;

import games.speedoflight.Camera;
import games.speedoflight.environmemt.entities.MapEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class MapEditor
{
    private final float CAMERA_SPEED = 400;
    private final float ZOOM_SPEED = 0.5f;
    private final float ROTATION_SPEED = (float)Math.PI / 100;

    private boolean up, down, left, right, shift, space, leftMouse, rKey, ctrl;
    private float xMouse, yMouse;
    private float xMousePinned, yMousePinned;

    private MapEntity selectedEntity;
    private float selectedEntituScaleX;
    private float selectedEntituScaleY;
    private float selectedEntityRotation;

    public MapEditor()
    {

    }

    public Map edit(Map map, Camera camera)
    {
        float xWorld = camera.screenToWorldPositionX(xMouse);
        float yWorld = camera.screenToWorldPositionY(yMouse);

        if(selectedEntity == null && leftMouse)
        {
            for(MapEntity entity : map.getObstacles())
            {
                if(entity.inside(xWorld, yWorld))
                {
                    selectedEntity = entity;
                    selectedEntituScaleX = entity.getScaleX();
                    selectedEntituScaleY = entity.getScaleY();
                    //selectedEntityRotation = entity.getRotation();
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
                    float xScale = selectedEntituScaleX + (xMousePinned - xMouse) / 100;
                    float yScale = selectedEntituScaleY + (yMousePinned - yMouse) / 100;
                    selectedEntity.setScaleX((float) Math.max(0.1, xScale));
                    selectedEntity.setScaleY((float) Math.max(0.1, yScale));
                    //selectedEntity.setRotation(0);
                }
            }
            else
            {
                selectedEntity.setX(xWorld);
                selectedEntity.setY(yWorld);
            }

            //if(!ctrl && leftMouse)
            //    selectedEntity.setRotation(selectedEntityRotation);

            if(rKey)
            {
                float rot = selectedEntity.getRotation() + (rKey ? ROTATION_SPEED : 0);
                selectedEntity.setRotation(rot);
            }

            if(!leftMouse)
                selectedEntity = null;
        }

        return map;
    }

    public void draw(Camera camera)
    {
        float x = camera.getX() + (left ? -CAMERA_SPEED : 0) + (right ? CAMERA_SPEED : 0);
        float y = camera.getY() + (up ? -CAMERA_SPEED : 0)   + (down ? CAMERA_SPEED : 0);
        float zoom = camera.getZoom() + (space ? -ZOOM_SPEED : 0) + (shift ? ZOOM_SPEED : 0);
        camera.setTarget(x, y);
        camera.setTargetZoom(zoom);

        GraphicsContext gc = camera.getGraphicsContext();
        //gc.fillText("MAP EDITOR!!!!", camera.getViewportWidth() / 2, camera.getViewportHeight() / 2);
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
            case R: rKey = pressed; break;
            case SHIFT: shift = pressed; break;
            case SPACE: space = pressed; break;
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

    private void setPinnedMouse()
    {
        xMousePinned = xMouse;
        yMousePinned = yMouse;
    }

}
