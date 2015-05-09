import com.fasterxml.jackson.databind.ObjectMapper;
import fi.solita.botsofbf.graph.Dijkstra;
import fi.solita.botsofbf.graph.GraphReader;
import fi.solita.botsofbf.graph.Node;

import java.util.List;

/**
 * Created by henriny on 08/05/15.
 */
public class test {

    public static class Map {
        public List<String> tiles;
    }

    @org.junit.Test
    public void test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map stateChanged =
                mapper.readValue(ClassLoader.getSystemResourceAsStream("black.json"), Map.class);

        GraphReader.loadMap(stateChanged.tiles);
        final long start = System.currentTimeMillis();
        final Node target = GraphReader.getMapCache().get("14:10");
        final Node from = GraphReader.getMapCache().get("72:12");
        final Node path = Dijkstra.findPath(target, from);
        System.out.println("Found in: " + (System.currentTimeMillis() - start));

        Node endNode = path;
        while(endNode.previous != null && endNode != from) {
            System.out.println("Next: " + endNode);
            endNode = endNode.previous;
        }



    }


}
