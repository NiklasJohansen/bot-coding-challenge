package games.speedoflight;

import games.speedoflight.environmemt.CollisionMask;
import games.speedoflight.environmemt.Map;

import java.util.List;

public class GameData
{
    public MapData map;
    public PlayerData[] otherPlayers;
    public PlayerData thisPlayer;

    public void addPlayers(List<LightPlayer> playerList)
    {
        int nPlayers = playerList.size() - (thisPlayer != null ? 1 : 0);

        if(otherPlayers == null || otherPlayers.length != nPlayers)
            otherPlayers = new PlayerData[nPlayers];

        for(int i = 0, j = 0; i < playerList.size(); i++)
        {
            LightPlayer player = playerList.get(i);

            if(thisPlayer != null && player == thisPlayer.player)
                continue;

            otherPlayers[j++] = new PlayerData(player);
        }
    }

    public void addMapData(Map map)
    {
        this.map = new MapData(map);
    }

    public void setThisPlayer(LightPlayer player)
    {
        this.thisPlayer = new PlayerData(player);
    }

    private class PlayerData
    {
        private transient LightPlayer player;

        private float xPos;
        private float yPos;
        private float xVel;
        private float yVel;
        private float life;
        private float rotation;
        private boolean alive;

        private PlayerData(LightPlayer player)
        {
            this.player = player;
            this.xPos = player.getX();
            this.yPos = player.getY();
            this.xVel = xPos - player.getLastX();
            this.yVel = yPos - player.getLastY();
            this.alive = !player.isDead();
            this.life = player.getLife();
            this.rotation = player.getRotation();
        }
    }

    private static class MapData
    {
        private int[] mapBitSequence;
        private int width;
        private int height;
        private int cellSize;

        private MapData(Map map)
        {
            CollisionMask mask = map.getCollisionMask();
            this.mapBitSequence = mask.getIntegerSequenceMask();
            this.width = mask.getWidth();
            this.height = mask.getHeight();
            this.cellSize = mask.getCellSize();
        }
    }
}