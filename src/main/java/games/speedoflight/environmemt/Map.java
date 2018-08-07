package games.speedoflight.environmemt;

import games.speedoflight.Camera;
import games.speedoflight.TextureLoader;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map
{
    private ArrayList<MapEntity> mapEntities;
    private ArrayList<SpawnPoint> spawnPoints;
    private int spawnPointIndex = 0;
    private Random random;

    public Map()
    {
        this.spawnPoints = new ArrayList<>();
        this.mapEntities = new ArrayList<>();
        this.random = new Random(123456789L);
        loadTextures();
        createMap();
        //generateMap();
    }

    private void loadTextures()
    {
        TextureLoader.addTexture("barrel", "speedoflight/img/barrel-top-small-shadow.png");
        TextureLoader.addTexture("crate", "speedoflight/img/wood-crate-shadow.png");
        TextureLoader.addTexture("grass", "speedoflight/img/grass-large.png");
        TextureLoader.addTexture("crack", "speedoflight/img/cracked-cement-shadow.png");
    }

    public void reloadMap()
    {
        createMap();
    }

    private void createMap()
    {
        float width = 1920;
        float height = 1027;

        for(int y = -1; y < 2; y++)
            for(int x = -1; x < 2; x++)
            {
                GroundPlane groundPlane = new GroundPlane(1024*x, 1024*y, 1024, 1024);
                groundPlane.setTexture(TextureLoader.getTexture("grass"));
                mapEntities.add(groundPlane);
            }


/*
        mapEntities.add(new RectObstacle(100,100,280,280));
        mapEntities.add(new RectObstacle(width - 380,100,280,280));
        mapEntities.add(new RectObstacle(width - 380,height - 380,280,280));
        mapEntities.add(new RectObstacle(100,height - 380,280,280));
        mapEntities.add(new RectObstacle(0,height/2 - 30,450,60));
        mapEntities.add(new RectObstacle(width - 450,height/2 - 30,450,60));
        mapEntities.add(new RectObstacle(width / 2 - 150, 150,300,70));
        mapEntities.add(new RectObstacle(width / 2 - 150, height - 150 - 70,300,70));
        mapEntities.add(new RectObstacle(width / 2 - 150, height / 2 - 150,300,300));
        mapEntities.add(new RectObstacle(width / 2 - 35 - 350, 0,70,320));
        mapEntities.add(new RectObstacle(width / 2 - 35 + 350, 0,70,320));
        mapEntities.add(new RectObstacle(width / 2 - 35 - 350, height-320,70,320));
        mapEntities.add(new RectObstacle(width / 2 - 35 + 350, height-320,70,320));
*/
        //spawnPoints.add(new SpawnPoint(0, 0));
        spawnPoints.add(new SpawnPoint(30, 30));
        spawnPoints.add(new SpawnPoint(width - 30,30));
        spawnPoints.add(new SpawnPoint(width - 30,height - 30));
        spawnPoints.add(new SpawnPoint(30,height - 30));
        spawnPoints.add(new SpawnPoint(width / 2 ,height - 30));
        spawnPoints.add(new SpawnPoint(width / 2 ,30));

        RectObstacle crate = new RectObstacle(800,500, 200, 200, Math.PI/8);
        crate.setTexture(TextureLoader.getTexture("crate"));
        crate.setTextureScale(1.5f);
        mapEntities.add(crate);

        RectObstacle block = new RectObstacle(1000,1000, 2, 400, Math.PI/10);
        block.setTexture(TextureLoader.getTexture("crack"));
        block.setBaseColor(Color.rgb(180,180,180));
        block.setTextureScale(1.5f);
        mapEntities.add(block);


        for(int i = 0; i < 500; i++)
        {
            float x = -1000 + random.nextInt(2000);
            float y = -1000 + random.nextInt(2000);

            RoundObstacle barrel = new RoundObstacle(x, y, 50, 0.1f);
            barrel.setTexture(TextureLoader.getTexture("barrel"));
            barrel.setTextureScale(0.5f);
            //mapEntities.add(barrel);
        }



        float radius = 1500;
        float nBoxes = 36;
        for(int i = 0; i < nBoxes; i++)
        {
            float angle = (i/nBoxes) * 2 * (float) Math.PI;
            float x = (float) Math.cos(angle) * radius;
            float y = (float) Math.sin(angle) * radius;
            //mapEntities.add(new RectObstacle(x, y, 100, 400, angle+Math.PI/4));
        }
    }

    public void generateMap()
    {
        float minWidth = 50;
        float maxWidth = 200;
        float minHeight = 100;
        float maxHeight = 400;
        float maxSpread = 1500;
        int maxObstacles = 30;
        int minObstacles = 20;

        spawnPoints.clear();
        mapEntities.clear();
        mapEntities.add(new GroundPlane(-3000, -3000, 6000, 6000));

        int nObstacles = minObstacles + random.nextInt(maxObstacles - minObstacles);
        for(int i = 0; i < nObstacles; i++)
        {
            float distFromCenter = random.nextFloat() * maxSpread;
            float angle = random.nextFloat() * (float) Math.PI;
            float xPos = (float) Math.cos(angle) * distFromCenter;
            float yPos = (float) Math.sin(angle) * distFromCenter;
            float width = minWidth + random.nextFloat() * (maxWidth - minWidth);
            float height = minHeight + random.nextFloat() * (maxHeight - minHeight);
            float rot = (float) Math.PI * 2 * random.nextFloat();
            mapEntities.add(new RectObstacle(xPos, yPos, width, height, rot));
        }

        while(spawnPoints.size() < 10)
        {
            float distFromCenter = random.nextFloat() * maxSpread;
            float angle = random.nextFloat() * (float) Math.PI;
            float xPos = (float) Math.cos(angle) * distFromCenter;
            float yPos = (float) Math.sin(angle) * distFromCenter;

            boolean freeSpace = true;
            for(MapEntity entity : mapEntities)
            {
                if(entity instanceof RectObstacle)
                {
                    RectObstacle rect = (RectObstacle) entity;

                    if(rect.getRectCircleOverlap(xPos, yPos) < 200 || rect.isPointInside(xPos, yPos))
                    {
                        freeSpace = false;
                        break;
                    }
                }
            }

            if(freeSpace)
            {
                boolean toClose = false;
                for(SpawnPoint point : spawnPoints)
                {
                    float xDelta = point.getX() - xPos;
                    float yDelta = point.getY() - yPos;
                    float dist = (float) Math.sqrt(xDelta * xDelta + yDelta * yDelta);
                    if(dist < 300)
                    {
                        toClose = true;
                        break;
                    }
                }

                if(!toClose)
                    spawnPoints.add(new SpawnPoint(xPos, yPos));
            }
        }
    }

    public void draw(Camera camera)
    {
        for(MapEntity o : mapEntities)
            o.draw(camera);
    }

    public List<MapEntity> getMapEntities()
    {
        return mapEntities;
    }

    public SpawnPoint getNextSpawnPoint()
    {
        spawnPointIndex = (spawnPointIndex + 1) % spawnPoints.size();
        return spawnPoints.get(spawnPointIndex);
    }

    public float getCenterX()
    {
        return 1920 / 2;
    }

    public float getCenterY()
    {
        return 1027 / 2;
    }
}
