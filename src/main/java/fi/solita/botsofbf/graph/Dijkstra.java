package fi.solita.botsofbf.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Dijkstra {

	public static Node findPath(Node target, Node from) {
		System.out.println("starting pathfinding: " + System.currentTimeMillis());
		Map<Node, Integer> distances = new HashMap<Node, Integer>();
		distances.put(from, 0);
		
		List<Node> Q = new ArrayList<Node>();
		Map<String, Node> mapCache = GraphReader.getMapCache();
		Map<Node, Node> previous = new HashMap<Node, Node>();
		
		System.out.println("1: " + System.currentTimeMillis());
		for(Node n : mapCache.values()) {
			if(!n.id.equals(from.id)) {
				distances.put(n, Integer.MAX_VALUE);
			}
			Q.add(n);
		}
		
		System.out.println("2: " + System.currentTimeMillis());
		for(Route r : from.routes) {
			int alt = distances.get(from) + r.price;
			Node neighbor = mapCache.get(r.to);
			if(alt < distances.get(neighbor)) {
				distances.put(neighbor, alt);
				previous.put(from, neighbor);
			}
		}
		
		Q.remove(from);
		
		while(!Q.isEmpty()) {
			System.out.println("4: " + System.currentTimeMillis());
			Entry<Node, Integer> entry = distances.entrySet().stream().filter(t -> Q.contains(t.getKey())).min(Map.Entry.comparingByValue()).get();
			Q.remove(entry.getKey());
			System.out.println("5: " + System.currentTimeMillis());
			
			for(Route r : entry.getKey().routes) {
				int alt = distances.get(entry.getKey()) + r.price;
				Node neighbor = mapCache.get(r.to);
				if(alt < distances.get(neighbor)) {
					distances.put(neighbor, alt);
					previous.put(entry.getKey(), neighbor);
				}
			}
			System.out.println("6: " + System.currentTimeMillis());
		}
		
		
		return previous.get(from);
	}
}
