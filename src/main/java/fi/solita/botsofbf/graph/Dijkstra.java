package fi.solita.botsofbf.graph;

import fi.solita.botsofbf.BotController.Move;

import java.util.*;

public class Dijkstra {

    private static boolean lastFailRoute = false;
    public static Move directionToRecover = null;
    private static Node lastFailed = null;

    public static Node findPathSimple(Node target, Node from) {
        return GraphReader.getMapCache().get(getClosestItem(from.routes, from, target).to);
    }


    public static Node findPath(Node target, Node from) {
        if(target == null ||from == null) return null;

        GraphReader.getMapCache().values().stream().forEach(t ->  {
            t.cost = Integer.MAX_VALUE;
            t.previous = null;
        });
        final PriorityQueue<Node> Q = new PriorityQueue<>();

        from.cost = 0;
        Q.add(from);

        while(!Q.isEmpty()) {
            final Node u = Q.poll();
            if(u.equals(target)) return target;
            for(Route r : u.routes) {
                int alt = u.cost + r.price;
                Node neighbor = r.to;
                if(alt < neighbor.cost) {
                    neighbor.cost = alt;
                    neighbor.previous = u;
                    Q.add(neighbor);
                }
            }
        }

        return null;

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
                directionToRecover = null;
                closest = i;
                distance = distance2;
            }
        }

        if(distance == Integer.MAX_VALUE || lastFailRoute) {
            lastFailRoute = true;
            lastFailed = me;
            Route i = getRoute(route, me);
            setRecoveryDirection(me, MoveTranslator.translate(me, i));

            if (i != null) return i;
        }

        return closest;
    }
    
    private static Move setRecoveryDirection(final Node me, final Move direction) {
    	if(directionToRecover == null) {
	    	List<Move> directions = new ArrayList<Move>(Arrays.asList(Move.UP, Move.LEFT, Move.DOWN, Move.RIGHT));
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
