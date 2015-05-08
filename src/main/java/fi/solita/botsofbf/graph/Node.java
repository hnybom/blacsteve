package fi.solita.botsofbf.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henriny on 08/05/15.
 */
public class Node {
    // The map consists of tiles with one of the following type:
    public static final char WALL_TILE = 'x';
    public static final char FLOOR_TILE = '_';
    public static final char EXIT_TILE = 'o';

    public String id;

    public int x;
    public int y;

    public char type;

    public List<Route> routes = new ArrayList<>();


}
