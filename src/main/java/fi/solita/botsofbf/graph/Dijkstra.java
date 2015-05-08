package fi.solita.botsofbf.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import fi.solita.botsofbf.BotController.Move;

public class Dijkstra {

    private static boolean lastFailRoute = false;
    private static Move directionToRecover = null;
    private static Node lastFailed = null;

    public static Node findPathSimple(Node target, Node from) {
        return GraphReader.getMapCache().get(getClosestItem(from.routes, from, target).to);
    }


	public static Node findPath(Node target, Node from) {
		Map<Node, Integer> distances = new HashMap<Node, Integer>();
		distances.put(from, 0);
		
		List<Node> Q = new ArrayList<>();
		Map<String, Node> mapCache = GraphReader.getMapCache();
		Map<Node, Node> previous = new HashMap<Node, Node>();
		
		Q.add(from);
		
		for(Node n : mapCache.values()) {
			if(!n.id.equals(from.id)) {
				distances.put(n, Integer.MAX_VALUE);
			}
		}

        final List<Node> alreadyCalculcated = new ArrayList<>();

		while(!Q.isEmpty()) {
			Entry<Node, Integer> entry = distances.entrySet().stream().filter(t -> Q.contains(t.getKey())).min(Map.Entry.comparingByValue()).get();
			Node u = entry.getKey();
			Q.remove(u);
            alreadyCalculcated.add(u);
			for(Route r : u.routes.stream().filter(t -> !alreadyCalculcated.contains(mapCache.get(t.to))).collect(Collectors.toList()) ) {
				int alt = distances.get(from) + r.price;
				Node neighbor = mapCache.get(r.to);
				if(alt < distances.get(neighbor)) {
					distances.put(neighbor, alt);
					previous.put(u, neighbor);
					Q.add(neighbor);
				}
			}
		}
		
		
		return previous.get(from);
	}

    private static Route getClosestItem(final List<Route> route, final Node me, final Node target) {
        Route closest = route.iterator().next();
        double distance = Integer.MAX_VALUE;

        int x1 = target.x;
        int y1 = target.y;

        double distance3 = Math.sqrt((x1 - me.x) * (x1 - me.x) + (y1 - me.y) * (y1 - me.y));

        for(Route i : route) {
            int x2 = GraphReader.getMapCache().get(i.to).x;

            int y2 = GraphReader.getMapCache().get(i.to).y;

            double distance2 = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

            if(distance2 < distance && distance2 < distance3 &&
                    !GraphReader.getMapCache().get(i.to).equals(lastFailed)) {
                lastFailRoute = false;
                lastFailed = null;
                closest = i;
                distance = distance2;
            }
        }

        if(distance == Integer.MAX_VALUE || lastFailRoute) {
            lastFailRoute = true;
            lastFailed = me;
            Route i = getRoute(route, me);

            if (i != null) return i;
        }

        return closest;
    }
    
    private static Move getRecoveryDirection(final Node me, final Move direction) {
    	if(directionToRecover == null) {
	    	List<Move> directions = Arrays.asList(Move.UP, Move.LEFT, Move.DOWN, Move.RIGHT);
	    	directions.remove(direction);
	    	Move newDirection = directions.get(new Random().nextInt(3));
	    	directionToRecover = newDirection;
    	}
    	
    	return directionToRecover;
    }

    private static Route getRoute (final List<Route> route, final Node me) {
        for(Route i : route) {
            if(me.y < GraphReader.getMapCache().get(i.to).y) return i;
            if(me.y > GraphReader.getMapCache().get(i.to).y) return i;
            if(me.x < GraphReader.getMapCache().get(i.to).x) return i;
            if(me.x > GraphReader.getMapCache().get(i.to).x) return i;
        }
        return null;
    }
}
