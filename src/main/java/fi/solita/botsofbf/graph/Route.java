package fi.solita.botsofbf.graph;

/**
 * Created by henriny on 08/05/15.
 */
public class Route {

    public int price;

    public Node from;
    public Node to;

    @Override
    public String toString () {
        return "Route{" +
                "price=" + price +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }

}
