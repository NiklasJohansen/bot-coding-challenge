package games.speedoflight;

import games.speedoflight.environmemt.CollisionMask;
import games.speedoflight.environmemt.Map;

import java.util.List;

public class GameData
{
    public MapData map;
    public PlayerData[] players;
    public PlayerData thisPlayer;

    public void addPlayers(List<LightPlayer> playerList)
    {
        if(players == null || players.length != playerList.size())
            players = new PlayerData[playerList.size()];

        for(int i = 0; i < playerList.size(); i++)
        {
            LightPlayer player = playerList.get(i);
            players[i] = new PlayerData(player);
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
        private float x;
        private float y;
        private boolean alive;

        private PlayerData(LightPlayer player)
        {
            this.x = player.getX();
            this.y = player.getY();
            this.alive = !player.isDead();
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