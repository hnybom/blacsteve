package fi.solita.botsofbf;

import fi.solita.botsofbf.graph.Dijkstra;
import fi.solita.botsofbf.graph.GraphReader;
import fi.solita.botsofbf.graph.MoveTranslator;
import fi.solita.botsofbf.graph.Node;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@RestController
public class BotController {


    // FIXME use correct server IP address
    private static final String SERVER_ADDRESS = "http://localhost:8080";
    private static final String MY_REST_API_PATH = "/move";
    private static final String MY_REST_API_ADDRESS = "http://%s:3000" + MY_REST_API_PATH;

    private UUID myBotId;

    @RequestMapping(value = "/bot", method = RequestMethod.GET)
    public void registerBot() throws UnknownHostException {
        myBotId = registerPlayer("BlacSteve").id;
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

        GraphReader.loadMap(map.tiles);

        
        Item closest = getClosestItem(items, myPlayer);
        Node from = GraphReader.getNodeByCoords(myPlayer.position.x, myPlayer.position.y);
        Node target = GraphReader.getNodeByCoords(closest.position.x, closest.position.y);

        if(from.id.equals(target.id)) {
        	return BotController.Move.PICK;
        }
        Node endNode = Dijkstra.findPath(target, from);
        while(endNode.previous != null) {
            System.out.println("path:" + endNode);
            if(endNode.previous == from) break;
            endNode = endNode.previous;
        }

    	System.out.println("next" + endNode);
    	System.out.println("closest: " + target);
    	System.out.println("my pos" + from);
        System.out.println("My player is at " + myPlayer.position);
        System.out.println("The map has " + items.size() + " items");
        System.out.println("The map consists of " + map.tiles.size() + " x " + map.tiles.get(0).length() + " tiles");

        return MoveTranslator.translate(from, endNode);
    }

    private void sendChatMessage(UUID playerId, String message) {
        new RestTemplate().postForEntity(
                String.format(SERVER_ADDRESS + "/%s/say", playerId),
                message, Void.class);
    }

    private boolean canGetThisItem (final Player me, final Item i) {
        final long round = Math.round(i.price * (1 - (i.discountPercent / 100d)));
        if(round <= me.money) return true;
        return false;
    }

    private Item getClosestItem(final Set<Item> items, final Player me) {
        Item closest = items.iterator().next();
        double distance = Integer.MAX_VALUE;
        for(Item i : items) {
            int x1 = me.position.x;
            int x2 = i.position.x;
            int y1 = me.position.y;
            int y2 = i.position.y;

            double distance2 = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
            if(distance2 < distance && canGetThisItem(me, i)) {
                closest = i;
                distance = distance2;
            }
        }

        if(!canGetThisItem(me, closest)) {
            System.out.println("No Items we can get going home!");
            Item item = new Item();
            item.position = new Position();
            item.position.x = GraphReader.exitNode.x;
            item.position.y = GraphReader.exitNode.y;
            return item;
        }

        return closest;
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
        public int discountPercent;
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
		@Override
		public String toString() {
			return "Position [x=" + x + ", y=" + y + "]";
		}
        
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
