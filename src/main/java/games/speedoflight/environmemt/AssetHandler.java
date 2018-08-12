package games.speedoflight.environmemt;

import games.speedoflight.Camera;
import games.speedoflight.TextureLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;

public class AssetHandler
{
    private String RESOURCE_PATH = "/speedoflight/img";

    private float xPos;
    private float yPos;
    private float width;
    private float height;
    private float scroll;

    private ArrayList<Asset> assets;
    private Asset selectedAsset;

    public AssetHandler()
    {
        this.xPos = 0;
        this.yPos = 0;
        this.width = 100;
        this.height = 600;
        this.assets = new ArrayList<>();
        load();
    }

    private void load()
    {
        File folder = new File(getClass().getResource(RESOURCE_PATH).getPath());
        File[] files = folder.listFiles();
        for(File f : files)
        {
            if(f.isFile())
            {
                boolean exists = false;
                for(Asset a : assets)
                {
                    if(a.name == f.getName())
                    {
                        exists = true;
                        break;
                    }
                }

                if(!exists)
                    assets.add(new Asset(f));
            }
        }
    }

    public void draw(Camera camera)
    {
        this.height = camera.getViewportHeight();

        GraphicsContext gc = camera.getGraphicsContext();
        gc.setFill(Color.rgb(0,0,0,0.6));
        gc.fillRect(xPos, yPos, width, height);

        float y = scroll + 10;
        for(int i = 0; i < assets.size(); i++)
        {
            Asset asset = assets.get(i);
            asset.width = width * 0.8f;
            asset.height = (float)(asset.texture.getHeight() * (asset.width / asset.texture.getWidth()));
            asset.xPos = xPos + 10;
            asset.yPos = yPos + y;
            gc.drawImage(asset.texture, xPos + 10, yPos + y, asset.width, asset.height);
            y += asset.height + 10;
        }
    }

    public void update(float xMouse, float yMouse)
    {
        for(Asset asset : assets)
        {
            if(asset.inside(xMouse, yMouse))
            {
                selectedAsset = asset;
                break;
            }
        }
    }

    public void scroll(float dir)
    {
        scroll = Math.min(0, scroll + Math.signum(dir) * 20);
    }

    public boolean mouseInside(float xMouse, float yMouse)
    {
        return xMouse >= xPos && xMouse < xPos + width && yMouse > yPos && yMouse < yPos + height;
    }

    public Asset getSelectedAsset()
    {
        return selectedAsset;
    }

    public class Asset
    {
        private float xPos;
        private float yPos;
        private float width;
        private float height;
        private Image texture;
        private String name;
        private String type;
        private float textureScale;

        private Asset(File file)
        {
            String fileName = file.getName();
            System.out.println("File: " + fileName);
            this.name = fileName.split("\\.")[0];
            if(name.indexOf("]") > 1)
                name = name.split("]")[1];
            System.out.println(name);
            TextureLoader.addTexture(name, RESOURCE_PATH + "/" + fileName);
            this.texture = TextureLoader.getTexture(this.name);
            this.type = "";
            this.textureScale = 1.0f;
            parseParamters(fileName);
        }

        private void parseParamters(String fileName)
        {
            if(fileName.startsWith("[") && fileName.contains("]"))
            {
                String parameterString = fileName.substring(1).split("\\]")[0];
                if(parameterString != null && parameterString.length() > 0)
                {
                    parameterString = parameterString.replaceAll("\\s+","").toLowerCase().trim();

                    String[] parameters;
                    if(parameterString.contains(","))
                    {
                        parameters = parameterString.split(",");
                    }
                    else parameters = new String[]{parameterString};

                    for(String parameter : parameters)
                    {
                        String[] elements = parameter.split("=");
                        if(elements.length == 2)
                        {
                            String key = elements[0];
                            String value = elements[1];

                            switch(key)
                            {
                                case "type":
                                    this.type = value;
                                    break;

                                case "texturescale":
                                    this.textureScale = Float.parseFloat(value);
                                    break;

                                default:
                                    System.err.println("Parameter: " + key + " not recognized in " + fileName);
                                    break;
                            }
                        }
                    }
                }
            }
        }

        private boolean inside(float x, float y)
        {
            return x >= xPos && x < xPos + width && y > yPos && y < yPos + height;
        }

        public Image getTexture()
        {
            return texture;
        }

        public String getType()
        {
            return type;
        }

        public float getTextureScale()
        {
            return textureScale;
        }

    }


}
