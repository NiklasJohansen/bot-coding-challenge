package games.speedoflight.environmemt;

import games.speedoflight.Camera;
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
    private ArrayList<Obstacle> obstacles;
    private ArrayList<SpawnPoint> spawnPoints;

    private float width;
    private float height;
    private int spawnPointIndex = 0;

    private boolean isUpdated;
    private CollisionMask collisionMask;

    public Map(float width, float height)
    {
        this.width = width;
        this.height = height;
        this.spawnPoints = new ArrayList<>();
        this.mapEntities = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.isUpdated = true;
        this.collisionMask = new CollisionMask((int)width/10, (int)height/10, 10);
    }

    public void draw(Camera camera)
    {
        GraphicsContext gc = camera.getGraphicsContext();
        gc.setFill(Color.PINK);
        gc.fillRect(0, 0, width, height);

        for(MapEntity o : mapEntities)
            o.draw(camera);

        for(MapEntity o : obstacles)
            o.draw(camera);

        collisionMask.draw(camera);
    }

    public void addMapEntity(MapEntity mapEntity)
    {
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
        else
            mapEntities.add(mapEntity);
    }

    public List<MapEntity> getMapEntities()
    {
        return mapEntities;
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

    public boolean isUpdated()
    {
        boolean state = isUpdated;
        isUpdated = false;
        return state;
    }
}
