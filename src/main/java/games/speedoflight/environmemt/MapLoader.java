package games.speedoflight.environmemt;

import games.speedoflight.TextureLoader;
import games.speedoflight.environmemt.*;
import games.speedoflight.environmemt.entities.GroundPlane;
import games.speedoflight.environmemt.entities.RectObstacle;
import games.speedoflight.environmemt.entities.RoundObstacle;
import games.speedoflight.environmemt.entities.SpawnPoint;
import javafx.scene.paint.Color;

import java.util.Random;

public class MapLoader
{
    public static Map load(String path)
    {
        loadTextures();

        return createTestMap();
    }

    private static void loadTextures()
    {
        TextureLoader.addTexture("barrel", "speedoflight/img/barrel-top-small-shadow.png");
        TextureLoader.addTexture("crate", "speedoflight/img/wood-crate-shadow.png");
        TextureLoader.addTexture("grass", "speedoflight/img/grass-large-2k.png");
        TextureLoader.addTexture("crack", "speedoflight/img/cracked-cement-shadow.png");
    }

    private static Map createTestMap()
    {
        int width = 2048;
        int height = 2048;

        Map map = new Map(width, height);

        SpawnPoint spawnPoint0 = new SpawnPoint(width / 2, height / 2);
        SpawnPoint spawnPoint1 = new SpawnPoint(width / 3, height / 3);
        map.addMapEntity(spawnPoint0);
        map.addMapEntity(spawnPoint1);

        GroundPlane plane = new GroundPlane(width / 2,height / 2, width, height);
        plane.setTexture(TextureLoader.getTexture("grass"));
        map.addMapEntity(plane);

        RectObstacle crate = new RectObstacle(800,500, 200, 200, (float)Math.PI/8);
        crate.setTexture(TextureLoader.getTexture("crate"));
        crate.setTextureScale(1.5f);
        map.addMapEntity(crate);

        RoundObstacle barrel = new RoundObstacle(1000, 800, 50, 0.1f);
        barrel.setTexture(TextureLoader.getTexture("barrel"));
        barrel.setTextureScale(1.5f);
        map.addMapEntity(barrel);

        RoundObstacle barrel2 = new RoundObstacle(100, 100, 50, 0.1f);
        barrel2.setTexture(TextureLoader.getTexture("barrel"));
        barrel2.setTextureScale(1.5f);
        map.addMapEntity(barrel2);


        return map;
    }

    private static Map createMap()
    {
        Map map = new Map(2048 * 3, 2048 * 3);
        Random random = new Random(123456789L);

        float width = 1920;
        float height = 1027;

        for(int y = -1; y < 2; y++)
            for(int x = -1; x < 2; x++)
            {
                GroundPlane groundPlane = new GroundPlane(2048*x, 2048*y, 2048, 2048);
                groundPlane.setTexture(TextureLoader.getTexture("grass"));
                map.addMapEntity(groundPlane);
            }

        map.addMapEntity(new SpawnPoint(30, 30));
        map.addMapEntity(new SpawnPoint(width - 30,30));
        map.addMapEntity(new SpawnPoint(width - 30,height - 30));
        map.addMapEntity(new SpawnPoint(30,height - 30));
        map.addMapEntity(new SpawnPoint(width / 2 ,height - 30));
        map.addMapEntity(new SpawnPoint(width / 2 ,30));

        RectObstacle singleCrate = new RectObstacle(800,500, 200, 200, (float) Math.PI/8);
        singleCrate.setTexture(TextureLoader.getTexture("crate"));
        singleCrate.setTextureScale(1.5f);
        map.addMapEntity(singleCrate);

        RectObstacle block = new RectObstacle(1000,1000, 2, 400, (float) Math.PI/10);
        block.setTexture(TextureLoader.getTexture("crack"));
        block.setBaseColor(Color.rgb(180,180,180));
        block.setTextureScale(1.5f);
        map.addMapEntity(block);

        RoundObstacle singleBarrel = new RoundObstacle(1300, 500, 150, 0.1f);
        singleBarrel.setTexture(TextureLoader.getTexture("barrel"));
        singleBarrel.setTextureScale(1.5f);
        map.addMapEntity(singleBarrel);

        for(int i = 0; i < 50; i++)
        {
            float x = -2048 + random.nextInt(2048 * 3);
            float y = -2048 + random.nextInt(2048 * 3);

            RoundObstacle barrel = new RoundObstacle(x, y, 50, 0.01f);
            barrel.setTexture(TextureLoader.getTexture("barrel"));
            barrel.setTextureScale(1.5f);
            map.addMapEntity(barrel);

            x = -2048 + random.nextInt(2048 * 3);
            y = -2048 + random.nextInt(2048 * 3);
            float size = 50 + random.nextInt(100);
            float rotation = random.nextFloat() * (float) Math.PI * 2;

            RectObstacle crate = new RectObstacle(x,y, size, size, rotation);
            crate.setTexture(TextureLoader.getTexture("crate"));
            crate.setTextureScale(1.5f);
            map.addMapEntity(crate);
        }

        return map;
    }

    public static Map generateMap()
    {
        float minWidth = 50;
        float maxWidth = 200;
        float minHeight = 100;
        float maxHeight = 400;
        float maxSpread = 1500;
        int maxObstacles = 30;
        int minObstacles = 20;

        Random random = new Random(123456789L);

        Map map = new Map(6000,6000);
        map.addMapEntity(new GroundPlane(-3000, -3000, 6000, 6000));
/*
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
            map.addMapEntity(new RectObstacle(xPos, yPos, width, height, rot));
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
        */
        return map;
    }



}
