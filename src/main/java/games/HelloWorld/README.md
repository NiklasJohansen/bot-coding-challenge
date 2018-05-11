# Hello World!

This project shows the small amount of code you have to write in order to get 
communication going between the server and its clients.

 ## Server
 Setting up a simple server requires few lines of code. Firstly a GameServer object will have to 
 be instantiated. The server is responsible for handling interaction with all connected players.
 
``` java
GameServer<PlayerImpl> server = new GameServer(PlayerImpl.class);
```

The server is made generic to allow for different player implementations in different games.
An implementation of the player class, like the one shown below, is specified in the 
instantiation 
of the GameServer.

``` java
public class PlayerImpl extends Player<ClientResponse>
{
    public PlayerImpl()
    {
        super(ClientResponse.class);
    }
}
```

The player implementation have to extend the Player class, and supply a ClientResponse class
to the super constructor. This response class defines what data is expected to be sent from the clients,
and is in this example defined as follows.
 
``` java
public class ClientResponse 
{
    public String clientName;
    public String message;
}
```
 
A server set up like this will accept new connections and be able to receive responses from its connected 
clients. But we have to define some behaviour in order for any communication to be going back 
and forth.

``` java
server.setGameLoop(1, () ->
{
    GameState gameState = new GameState();
    gameState.serverName = "GameServer";
    gameState.timestamp = new Date().toString();
    server.broadcast(gameState);
    ...
});
```

The GameServer has built in game loop functionality. The above code loops every second and 
broadcasts a GameState object to all players. What data the GameState object should contain will
depend on the game implementing it. In this example the servers name and a timestamp is used. In a 
real game this could be a map, information about other players, game entities, etc.

``` java
public class GameState
{
    public String serverName;
    public String timestamp;
}
```

When a player has received the game state and responded with the defined ClientResponse object,
that objects data can be accessed like shown below. The getResponse method will return null if
no response has been received.

``` java
for(PlayerImpl player : server.getPlayers())
{
    ClientResponse response = player.getResponse();
    if(response != null)
        System.out.println("[" + response.clientName + "]: " + response.message);
}
```
 
 A game may want to keep track of when players connect or disconnect. This behaviour can be
 defined as follows.
 
``` java
server.setOnNewPlayerConnection(player -> 
{
    // ... //
});

server.setOnPlayerDisconnect(player -> 
{
    // ... //
});
```

The last and final step is simply to start the server on a given port number.

``` java
server.start(55500);
```

 ## Client

The client code can be written in many languages and is not required to use the any of the 
functionality available in the core library. As an example, a simple client in JavaScript can be
coded like shown bellow. NOTE that connecting to the server with a web socket is different from 
regular sockets, and the port number is by rule one higher than defined in the server code.

``` js
var connection = new WebSocket('ws://127.0.0.1:55501');

connection.onmessage = function(event) 
{
    var gameState = JSON.parse(event.data);
    
    console.log("[" + gameState.serverName + "]: " + gameState.timestamp);
    
    connection.send(JSON.stringify(
    {
        clientName: "JS_Client",
        message: "Hello " + gameState.serverName + "!"
    }));
}
```

If you have cloned down the games source code, writing a bot in Java requires very few steps. 
By using the games defined GameState and ClientResponse classes directly, setting up a connection
can be done like this:

``` java
import core.client.GameClient;
import games.HelloWorld.HelloWorldServer.ClientResponse;
import games.HelloWorld.HelloWorldServer.GameState;

...

GameClient client = new GameClient("127.0.0.1", 55500);
client.setOnServerUpdate(GameState.class, gameState ->
{
    System.out.println("[" + gameState.serverName + "]: " + gameState.timestamp);

    ClientResponse response = new ClientResponse();
    response.clientName = "Java_Client";
    response.message = "Hello " + gameState.serverName + "!";
    client.send(response);
});
```

A client can of course be written in Java without using parts of the source code. 
The HelloWorldClientStandalone class in the *example* folder shows how this can be done. 




