package fi.solita.botsofbf.graph;

import java.util.*;

/**
 * Created by henriny on 08/05/15.
 */
public class GraphReader {

    private static Map<String, Node> mapCache = new HashMap<>();
    private static Set<Route> routes = new HashSet<>();

    public static Node exitNode;


    public static Map<String, Node> getMapCache () {
        return mapCache;
    }

    public static void initPaths() {
        synchronized (mapCache) {
            mapCache.values().stream().forEach(t -> {
                t.cost = Integer.MAX_VALUE;
                t.previous = null;
            });
        }
    }

    public static Map<String, Node> loadMap (final List<String> tiles) {
        synchronized (mapCache) {
            if (!mapCache.isEmpty()) return mapCache;

            int x = 0;
            int maxX = 0;
            int y = 0;

            for (String tile : tiles) {
                for (char c : tile.toCharArray()) {
                    final Node n = new Node();
                    n.x = x;
                    n.y = y;
                    n.type = c;
                    n.id = getId(n);
                    mapCache.put(n.id, n);
                    if (c == Node.EXIT_TILE) exitNode = n;
                    x++;
                }
                maxX = x;
                x = 0;
                y++;
            }

            for (final Node n : mapCache.values()) {
                for (String ids : getNeighbourIds(n, maxX, y)) {
                    Route r = new Route();
                    r.from = mapCache.get(n.id);
                    r.to = mapCache.get(ids);
                    r.price = 1;
                    n.routes.add(r);
                    routes.add(r);
                }
            }

            return mapCache;
        }
    }

    public static Set<Route> getRoutes() {
        return routes;
    }

    public static Set<Route> getRoutes(final List<String> tiles) {
        if(routes.isEmpty()) loadMap(tiles);
        return routes;
    }

    public static List<String> getNeighbourIds(final Node n, final int maxX, final int maxY) {
    	if(n.type == Node.WALL_TILE) return Collections.emptyList();
    	
        final List<String> ids = new ArrayList<>();

        int xMinus = n.x - 1;
        int xPlus = n.x + 1;

        int yMinus = n.y - 1;
        int yPlus = n.y + 1;

        if(xMinus >= 0) {
            final String id = getId(xMinus, n.y);
            addIfNotWall(ids, id);
        }

        if(xPlus <= maxX) {
            final String id = getId(xPlus, n.y);
            addIfNotWall(ids, id);
        }

        if(yMinus >= 0) {
            final String id = getId(n.x, yMinus);
            addIfNotWall(ids, id);
        }

        if(yPlus <= maxY) {
            final String id = getId(n.x, yPlus);
            addIfNotWall(ids, id);
        }

        return ids;
    }

    public static Node getNodeByCoords(final int x, final int y) {
        return mapCache.get(getId(x,y));
    }

    private static void addIfNotWall (final List<String> ids, final String id) {
        if(mapCache.get(id).type != Node.WALL_TILE) ids.add(id);
    }

    public static String getId (final Node node) {
        return node.x + ":" + node.y;
    }

    public static String getId (final int x , final int y) {
        return x + ":" + y;
    }

}
