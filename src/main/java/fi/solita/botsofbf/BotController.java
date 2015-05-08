package fi.solita.botsofbf;

import fi.solita.botsofbf.graph.GraphReader;
import fi.solita.botsofbf.graph.Node;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


@RestController
public class BotController {


    // FIXME use correct server IP address
    private static final String SERVER_ADDRESS = "http://192.168.2.86:8080";

    private static final String MY_REST_API_PATH = "/move";
    private static final String MY_REST_API_ADDRESS = "http://%s:8080" + MY_REST_API_PATH;

    private UUID myBotId;


    @RequestMapping(value = "/bot", method = RequestMethod.GET)
    public void registerBot() throws UnknownHostException {
        myBotId = registerPlayer("Siemens").id;
        sendChatMessage(myBotId, "Prepare to meet your maker!");
    }

    private RegisterResponse registerPlayer(String playerName) throws UnknownHostException {
        RestTemplate restTemplate = new RestTemplate();

        String myRestApiAddress = String.format(MY_REST_API_ADDRESS, InetAddress.getLocalHost().getHostAddress());
        Registration registration = new Registration(playerName, myRestApiAddress);

        ResponseEntity<RegisterResponse> result =
                restTemplate.postForEntity(SERVER_ADDRESS + "/register", registration, RegisterResponse.class);

        return result.getBody();
    }


    @RequestMapping(value = MY_REST_API_PATH, method = RequestMethod.POST)
    public @ResponseBody
    Move move(@RequestBody GameStateChanged gameStateChanged) {
        Player myPlayer = gameStateChanged.playerState;
        Set<Item> items = gameStateChanged.gameState.items;
        Map map = gameStateChanged.gameState.map;

        final java.util.Map<String, Node> stringNodeMap = GraphReader.loadMap(map.tiles);

        System.out.println("My player is at " + myPlayer.position);
        System.out.println("The map has " + items.size() + " items");
        System.out.println("The map consists of " + map.tiles.size() + " x " + map.tiles.get(0).length() + " tiles");

        Move nextMove = Arrays.asList(Move.UP, Move.LEFT, Move.DOWN, Move.RIGHT).get(new Random().nextInt(4));
        return nextMove;
    }

    private void sendChatMessage(UUID playerId, String message) {
        new RestTemplate().postForEntity(
                String.format(SERVER_ADDRESS + "/%s/say", playerId),
                message, Void.class);
    }



    public static class Map {
        public int width;
        public int height;
        public List<String> tiles;
    }

    public static class Player {
        public Position position;
        public String name;
        public String url;
        public int score;
        public int money;
        public int health;
        public List<Item> usableItems;
    }

    public enum Move {
        UP,
        DOWN,
        RIGHT,
        LEFT,
        PICK,
        USE // valid if the item is usable
    }

    public static class Item {
        public int price;
        public Position position;
        public Type type;
        public boolean isUsable;

        public enum Type {
            JUST_SOME_JUNK,
            WEAPON
        }
    }

    public static class Position {
        public int x;
        public int y;
    }

    private static class Registration {
        public String playerName;
        public String url;

        public Registration(String playerName, String url) {
            this.playerName = playerName;
            this.url = url;
        }
    }

    public static class RegisterResponse {
        public UUID id;
        public Player player;
        public GameState gameState;
    }

    public static class GameStateChanged {
        public GameState gameState;
        public Player playerState;
    }

    public static class GameState {
        public Map map;
        public Set<Player> players;
        public Set<Item> items;
    }
}
