package fi.solita.botsofbf.graph;

import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {

    public static Node findPath(Node target, Node from) {
        if(target == null ||from == null) return null;

        synchronized (GraphReader.getMapCache()) {
            GraphReader.initPaths();
        }
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

        for(Route i : route) {
            int x2 = GraphReader.getMapCache().get(i.to).x;

            int y2 = GraphReader.getMapCache().get(i.to).y;

            double distance2 = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

            if(distance2 < distance) {

                closest = i;
                distance = distance2;
            }
        }

        if(distance == Integer.MAX_VALUE) {
            Route i = getRoute(route, me);
            if (i != null) return i;
        }

        return closest;
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
