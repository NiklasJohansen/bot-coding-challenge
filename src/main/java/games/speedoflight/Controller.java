package games.speedoflight;

import core.server.GameServer;
import games.speedoflight.environmemt.Map;
import games.speedoflight.environmemt.MapEditor;
import games.speedoflight.environmemt.MapUtil;
import games.speedoflight.environmemt.entities.Obstacle;
import games.speedoflight.environmemt.entities.SpawnPoint;
import javafx.animation.AnimationTimer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Controller
{
    private GameState gameState;
    private enum GameState
    {
        LOBBY, ROUND_STARTED, ROUND_OVER, MAP_EDITOR
    }

    private final int ROUND_BEGIN_COUNTDOWN = 10;

    @FXML private Canvas canvas;
    @FXML private AnchorPane anchorPane;

    private Map map;
    private Camera camera;
    private GameServer<LightPlayer> server;
    private ArrayList<Bullet> bullets;
    private MapEditor mapEditor;

    private float animationCounter;
    private int countDown = 10;
    private long countDownTimer;
    private int fps;
    private int fpsCounter;
    private long fpsTimer;

    @FXML
    public void initialize()
    {
        addEventListeners();
        openServer();
        startGameLoop();

        String defaultMap = Controller.class.getResource("/speedoflight/map/default.SLS").getPath();
        this.map = MapUtil.load(defaultMap);
        this.mapEditor = new MapEditor();
        this.mapEditor.loadAssets(defaultMap.replace("/default.SLS", ""));

        this.camera = new Camera(canvas, map.getCenterX(), map.getCenterY());
        this.camera.setTarget(map.getCenterX(), map.getCenterY());
        this.countDown = ROUND_BEGIN_COUNTDOWN;
        this.gameState = GameState.LOBBY;

        this.bullets = new ArrayList<>();
    }

    public void closeRequest()
    {
        Platform.exit();
        System.exit(0);
    }

    private void startGameLoop()
    {
        new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                updateGame();
                renderGame();
            }
        }.start();
    }

    private void openServer()
    {
        this.server = new GameServer<>(LightPlayer.class);
        this.server.setOnNewPlayerConnection(player ->
        {
            SpawnPoint point = map.getNextSpawnPoint();
            player.setX(point.getX());
            player.setY(point.getY());
            player.setLastX(point.getX());
            player.setLastY(point.getY());
            GameData gameData = createGameData(true);
            gameData.setThisPlayer(player);
            player.send(gameData);
        });

        this.server.setGameLoop(10, () ->
        {
            if(map != null)
            {
                GameData gameData = createGameData(false);

                for(LightPlayer player : server.getPlayers())
                {
                    gameData.setThisPlayer(player);
                    gameData.addPlayers(server.getPlayers());
                    player.send(gameData);
                }
            }
        });

        this.server.start(55500);
    }

    private GameData createGameData(boolean addMapData)
    {
        GameData gameData = new GameData();

        if(map.isUpdated() || addMapData)
            gameData.addMapData(map);

        return gameData;
    }

    private void updateGame()
    {
        int playersAlive = 0;
        for(LightPlayer player: server.getPlayers())
            if(!player.isDead())
                playersAlive++;

        switch(gameState)
        {
            case ROUND_STARTED:
                if(playersAlive < 2 && server.getPlayers().size() > 1)
                    gameState = GameState.ROUND_OVER;

                updatePlayers();
                break;

            case ROUND_OVER:
                if(countDown == ROUND_BEGIN_COUNTDOWN)
                {
                    Collections.sort(server.getPlayers());
                    countDown--;
                    countDownTimer = System.currentTimeMillis();
                    server.allowNewConnections(true);
                }
                else if(countDown == 0)
                {
                    gameState = GameState.ROUND_STARTED;
                    countDown = ROUND_BEGIN_COUNTDOWN;
                    server.allowNewConnections(true); // false
                    respawn();
                }
                else if(System.currentTimeMillis() > countDownTimer + 1000)
                {
                    countDown--;
                    countDownTimer = System.currentTimeMillis();
                }
                break;

            case MAP_EDITOR:
                map = mapEditor.edit(map, camera);
                updatePlayers();
                break;
        }

    }

    private void updatePlayers()
    {
        for (int i = 0; i < bullets.size() && bullets.size() > 0; i++)
        {
            Bullet b = bullets.get(i);
            b.update();

            for (Obstacle o : map.getObstacles())
                o.resolveBulletCollision(b);

            if (b.isActive()) {
                bullets.remove(b);
                i--;
            }
        }

        server.getPlayers().forEach(player -> player.update(bullets));
        resolveCollisions();
    }

    private void resolveCollisions()
    {
        List<LightPlayer> players = server.getPlayers();
        for (int  i = 0; i < players.size() - 1; i++)
            for (int j = i + 1; j < players.size(); j++)
                players.get(i).resolvePlayerCollision(players.get(j));

        for(Obstacle entity : map.getObstacles())
            for(LightPlayer player : players)
                entity.resolvePlayerCollision(player);
    }

    private void renderGame()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        camera.startCapture();
        map.draw(camera);

        for(Bullet b : bullets)
            b.draw(camera);

        for(LightPlayer player: server.getPlayers())
            player.draw(camera);

        camera.endCapture();
        camera.update();

        switch (gameState)
        {
            case LOBBY:
                renderStartScreen(gc);
                camera.setTarget(map.getCenterX(), map.getCenterY());
                camera.setTargetZoom(1.0f);
                break;

            case ROUND_STARTED:
                camera.followAlivePlayers(server.getPlayers());
                break;

            case ROUND_OVER:
                renderScoreBoard(gc);
                break;

            case MAP_EDITOR:
                mapEditor.draw(camera);
                break;
        }

        renderFPS(camera);
        animationCounter = (animationCounter + 1) % 256;
    }


    private void renderFPS(Camera camera)
    {
        GraphicsContext gc = camera.getGraphicsContext();
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(20));
        gc.fillText(String.valueOf(fps), 10, 20);

        fpsCounter++;
        if(System.currentTimeMillis() > fpsTimer + 1000)
        {
            fps = fpsCounter;
            fpsCounter = 0;
            fpsTimer = System.currentTimeMillis();
        }
    }

    private void renderStartScreen(GraphicsContext gc)
    {
        List<LightPlayer> players = server.getPlayers();

        float xCenter = (float) canvas.getWidth() / 2;

        gc.setFill(Color.color(0f,0f,0f,0.6));
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFont(Font.font(40));
        gc.fillText("PLAYERS (" + players.size() + ")", xCenter, canvas.getHeight() * 0.2);

        int index = 1;
        for (LightPlayer player : players)
        {
            float fontSize = 30 + 2 * (float)Math.sin((animationCounter / 256) * Math.PI * 2 + index * (Math.PI));
            gc.setFont(Font.font(fontSize));
            gc.fillText(player.getName(), xCenter, canvas.getHeight() * 0.2 + 50 * index);
            index++;
        }

        float fontSize = 30 + 5 * (float)Math.sin((animationCounter / 256) * Math.PI * 2);
        gc.setFont(Font.font(fontSize));
        gc.fillText("PRESS SPACE TO START GAME", xCenter, canvas.getHeight() * 0.9);
    }

    private void renderScoreBoard(GraphicsContext gc)
    {
        gc.setFill(Color.color(0f,0f,0f,0.8));
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font(40));
        gc.fillText("--------- SCOREBOARD ---------", canvas.getWidth() / 2, canvas.getHeight() * 0.2);

        int index = 1;
        for(LightPlayer player: server.getPlayers())
        {
            gc.setFont(Font.font(30));
            gc.fillText(player.getName() + ":    " +
                    player.getKills() + "  /  " + player.getDeaths(),
                    canvas.getWidth() / 2, canvas.getHeight() * 0.2 + 50 * index);
            index++;
        }

        gc.setFont(Font.font(30));
        gc.fillText("NEW ROUND BEGINS IN", canvas.getWidth() / 2, canvas.getHeight() * 0.8);
        gc.setFont(Font.font(60));
        gc.fillText(String.valueOf(countDown), canvas.getWidth() / 2, canvas.getHeight() * 0.9);
    }

    public void respawn()
    {
        Collections.shuffle(server.getPlayers());
        for(LightPlayer player: server.getPlayers())
        {
            while(true)
            {
                SpawnPoint point = map.getNextSpawnPoint();
                player.setX(point.getX() - 10 +  (float)Math.random() * 20);
                player.setY(point.getY() - 10 +  (float)Math.random() * 20);
                player.setLastX(point.getX());
                player.setLastY(point.getY());
                player.setAlive();

                boolean spawnOk = true;
                for(LightPlayer otherPlayer: server.getPlayers())
                {
                    if(player != otherPlayer)
                    {
                        float xDelta = player.getX() - otherPlayer.getX();
                        float yDelta = player.getY() - otherPlayer.getY();
                        float dist = (float) Math.sqrt(xDelta * xDelta + yDelta * yDelta);
                        if(dist < point.getRadius())
                            spawnOk = false;
                    }
                }
                if(spawnOk)
                    break;
            }
        }
    }

    private void addEventListeners()
    {
        Platform.runLater(() ->
        {
            canvas.setOnScroll(event ->
            {
                if(gameState == GameState.MAP_EDITOR)
                    mapEditor.handleScrollEvent(event);
            });

            canvas.setOnMouseDragged(event ->
            {
                if(gameState == GameState.MAP_EDITOR)
                    mapEditor.handleMouseMoveEvents(event);
            });

            canvas.setOnMouseMoved(event ->
            {
                if(gameState == GameState.MAP_EDITOR)
                    mapEditor.handleMouseMoveEvents(event);
            });

            canvas.setOnMousePressed(event ->
            {
                if(gameState == GameState.MAP_EDITOR)
                    mapEditor.handleMousePressEvents(event, true);
            });

            canvas.setOnMouseReleased(event ->
            {
                if(gameState == GameState.MAP_EDITOR)
                    mapEditor.handleMousePressEvents(event, false);
            });

            anchorPane.getScene().setOnKeyPressed(event ->
            {
                switch (gameState)
                {
                    case MAP_EDITOR:
                        mapEditor.handleKeyEvents(event, true);
                        if(event.getCode() == KeyCode.ESCAPE)
                            gameState = GameState.LOBBY;
                        break;

                    case LOBBY:
                        if(event.getCode() == KeyCode.SPACE && server.getPlayers().size() > 0)
                        {
                            gameState = GameState.ROUND_STARTED;
                            server.allowNewConnections(true); // false
                            respawn();
                        }
                        else if(event.getCode() == KeyCode.E)
                        {
                            gameState = GameState.MAP_EDITOR;
                        }
                        break;

                    case ROUND_STARTED:
                        if(event.getCode() == KeyCode.ESCAPE)
                        {
                            gameState = GameState.LOBBY;
                            server.allowNewConnections(true);
                        }
                        break;
                }
            });

            anchorPane.getScene().setOnKeyReleased(event ->
            {
                if(gameState == GameState.MAP_EDITOR)
                    mapEditor.handleKeyEvents(event, false);
            });

            anchorPane.getScene().widthProperty().addListener((o, oldValue, newValue) ->
                    canvas.setWidth(newValue.doubleValue()));

            anchorPane.getScene().heightProperty().addListener((o, oldValue, newValue) ->
                    canvas.setHeight(newValue.doubleValue()));
        });
    }
}
