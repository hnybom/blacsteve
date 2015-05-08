package fi.solita.botsofbf.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Dijkstra {

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
		
		while(!Q.isEmpty()) {
			Entry<Node, Integer> entry = distances.entrySet().stream().filter(t -> Q.contains(t.getKey())).min(Map.Entry.comparingByValue()).get();
			Node u = entry.getKey();
			Q.remove(u);
			
			for(Route r : u.routes) {
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
}
