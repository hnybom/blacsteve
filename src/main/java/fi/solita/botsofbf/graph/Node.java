package fi.solita.botsofbf.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henriny on 08/05/15.
 */
public class Node implements Comparable<Node> {
    // The map consists of tiles with one of the following type:
    public static final char WALL_TILE = 'x';
    public static final char FLOOR_TILE = '_';
    public static final char EXIT_TILE = 'o';

    public String id;

    public int x;
    public int y;

    public char type;

    public Node previous;

    public int cost;

    public List<Route> routes = new ArrayList<>();

	@Override
	public String toString() {
        return id;
	}

    @Override
    public int compareTo (final Node o) {
        return Integer.compare(cost, o.cost);
    }
}
