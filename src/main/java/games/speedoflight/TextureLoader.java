package games.speedoflight;

import javafx.scene.image.Image;

import java.util.HashMap;

public class TextureLoader
{
    private static HashMap<String, Image> textureMap = new HashMap<>();

    public static void addTexture(String name, String path)
    {
        textureMap.put(name, new Image(path));
    }

    public static Image getTexture(String name)
    {
        return textureMap.get(name);
    }
}
