package games.speedoflight.environmemt;

import games.speedoflight.Camera;
import games.speedoflight.environmemt.entities.Decoration;
import games.speedoflight.environmemt.entities.MapEntity;
import games.speedoflight.environmemt.entities.Obstacle;
import games.speedoflight.environmemt.entities.SpawnPoint;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Map
{
    private ArrayList<MapEntity> mapEntities;
    private ArrayList<Decoration> decorations;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<SpawnPoint> spawnPoints;

    private float width;
    private float height;
    private int spawnPointIndex = 0;

    private boolean showCollisionMask;
    private boolean showMapArea;
    private boolean isUpdated;
    private CollisionMask collisionMask;

    public Map(float width, float height)
    {
        this.width = width;
        this.height = height;
        this.mapEntities = new ArrayList<>();
        this.spawnPoints = new ArrayList<>();
        this.decorations = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.isUpdated = true;
        this.collisionMask = new CollisionMask((int)width/10, (int)height/10, 10);
    }

    public void draw(Camera camera)
    {
        GraphicsContext gc = camera.getGraphicsContext();
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, width, height);

        for(MapEntity o : mapEntities)
            o.draw(camera);

        if(showCollisionMask)
            collisionMask.draw(camera);

        if(showMapArea)
        {
            System.out.println("rendering");
            gc.setFill(Color.rgb(100,100,100,0.5f));
            gc.fillRect(0, 0, width, height);
        }
    }

    public void addMapEntity(MapEntity mapEntity)
    {
        mapEntities.add(mapEntity);

        if(mapEntity instanceof Obstacle)
        {
            Obstacle obstacle = (Obstacle) mapEntity;
            obstacles.add(obstacle);
            collisionMask.apply(obstacle);
            obstacle.setOnMoveUpdate(moveEvent ->
            {
                collisionMask.remove(obstacle, moveEvent.xPosOld, moveEvent.yPosOld);
                collisionMask.apply(obstacle);
                isUpdated = true;
            });
        }

        else if(mapEntity instanceof SpawnPoint)
            spawnPoints.add((SpawnPoint) mapEntity);

        else if(mapEntity instanceof Decoration)
            decorations.add((Decoration) mapEntity);
    }

    public void removeMapEntity(MapEntity mapEntity)
    {
        mapEntities.remove(mapEntity);
        obstacles.remove(mapEntity);
        decorations.remove(mapEntity);
        spawnPoints.remove(mapEntity);
    }

    public List<MapEntity> getEntities()
    {
        return mapEntities;
    }

    public List<Decoration> getDecorations()
    {
        return decorations;
    }

    public List<Obstacle> getObstacles()
    {
        return obstacles;
    }

    public SpawnPoint getNextSpawnPoint()
    {
        if(spawnPoints.size() == 0)
            return new SpawnPoint(0, 0);

        spawnPointIndex = (spawnPointIndex + 1) % spawnPoints.size();
        return spawnPoints.get(spawnPointIndex);
    }

    public float getCenterX()
    {
        return width / 2;
    }

    public float getCenterY()
    {
        return height / 2;
    }

    public CollisionMask getCollisionMask()
    {
        return collisionMask;
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public void toggleShowCollision()
    {
        showCollisionMask = !showCollisionMask;
    }

    public void toggleShowMapArea()
    {
        showMapArea = !showMapArea;
    }


    public boolean isUpdated()
    {
        boolean state = isUpdated;
        isUpdated = false;
        return state;
    }
}
