package fi.solita.botsofbf.graph;

import fi.solita.botsofbf.BotController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by henriny on 08/05/15.
 */
public class GraphReader {

    private static Map<String, Node> mapCache = new HashMap<>();

    public static Node exitNode;

    public static Map<String, Node>  loadMap(final List<String> tiles) {
        int x = 0;
        int y = 0;

        for(String tile : tiles) {
           for(char c : tile.toCharArray()) {
               if(c == BotController.WALL_TILE) continue;
               if(c == BotController.FLOOR_TILE) {
                   final Node n = new Node();
                   n.x = x;
                   mapCache.put()                   
               }
               x++;

           }
            y++;
        }

    }


    private String getId(final Node node) {
        return node.x + ":" + node.y;
    }

}
