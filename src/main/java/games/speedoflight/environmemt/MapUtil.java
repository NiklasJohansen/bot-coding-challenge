package games.speedoflight.environmemt;

import games.speedoflight.environmemt.entities.*;
import javafx.scene.image.Image;

import java.io.*;

public class MapUtil
{
    public static void save(Map map, String fileName)
    {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))
        {
            writer.write("width="+map.getWidth() + ", height=" + map.getHeight());
            writer.newLine();
            for(MapEntity entity : map.getEntities())
            {
                writer.write(entity.getSaveString());
                writer.newLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static Map load(String path)
    {
        try(BufferedReader reader = new BufferedReader(new FileReader(path)))
        {
            Map map = new Map(2048, 2048);

            String mapSizeLine = reader.readLine();
            if(mapSizeLine != null)
            {
                float width = getFloatValue(mapSizeLine, "width");
                float height = getFloatValue(mapSizeLine, "height");
                map = new Map(width, height);
            }

            AssetHandler assetHandler = new AssetHandler();
            if(path.contains("\\"))
                path = path.substring(0, path.lastIndexOf("\\"));
            else if(path.contains("/"))
                path = path.substring(0, path.lastIndexOf("/"));
            File folder = new File(path);
            assetHandler.load(folder);

            String line;
            while((line = reader.readLine()) != null)
            {
                String assetName = getStringValue(line, "assetName");
                for(AssetHandler.Asset asset : assetHandler.getAssets())
                {
                    if(asset.getName().equals(assetName))
                    {
                        MapEntity entity = createNewEntity(asset, 0, 0);
                        map.addMapEntity(entity);
                        entity.applySaveString(line);
                    }
                }
            }

            map.getCollisionMask().updateAll(map.getObstacles());
            return map;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return  null;
    }

    public static MapEntity createNewEntity(AssetHandler.Asset asset, float xPos, float yPos)
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

        entity.setAssetName(asset.getName());
        entity.setTexture(texture);
        entity.setTextureScale(asset.getTextureScale());
        return entity;
    }

    public static float getFloatValue(String saveString, String key)
    {
        String strValue = getStringValue(saveString, key);
        if(strValue != null && strValue.matches("[-+]?[0-9]*\\.?[0-9]+"))
            return Float.parseFloat(strValue);
        else
            return -1;
    }

    public static String getStringValue(String saveString, String key)
    {
        String[] pairs = saveString.split(",");
        if(pairs.length == 0)
            pairs = new String[]{saveString};

        for(String pair : pairs)
        {
            pair = pair.trim();
            String[] elements = pair.split("=");
            if(elements.length == 2)
            {
                if(elements[0].trim().equals(key))
                    return elements[1].trim();
            }
        }
        return null;
    }

}
