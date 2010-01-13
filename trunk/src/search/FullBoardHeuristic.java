package search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import pacman.Game;
import pacman.Location;
import pacman.LocationSet;
import pacman.State;
import util.Pair;

public class FullBoardHeuristic implements Heuristic {

	public int[][] allPairsSP = null;
	
	public FullBoardHeuristic() {
		allPairsSP = new int[SearchHelpers.ALL_LOCATIONS.length][SearchHelpers.ALL_LOCATIONS.length];
		
		for (int[] arr : allPairsSP) {
			Arrays.fill(arr, -1);
		}
		
		for (int i = 0; i < allPairsSP.length; i++) {
			getAllSP(i);
		}
	}
	
	/**
	 * Gets the shortest path between the starting location and all other location.
	 * Simply applies BFS
	 *
	 * @param startLocIndex
	 */
	private void getAllSP(int startLocIndex) {
		
		HashSet<Location> allLocs = (HashSet<Location>) Game.getAllLocations();
		Queue<Pair<Integer, Integer>> locationsCostQ = new LinkedList<Pair<Integer,Integer>>();
		
		locationsCostQ.add(new Pair<Integer, Integer>(startLocIndex, 0));
		while (! locationsCostQ.isEmpty()) {
			
			Pair<Integer, Integer> locCost = locationsCostQ.poll();
			if (allPairsSP[startLocIndex][locCost.first()] != -1)
				continue;
			
			allPairsSP[startLocIndex][locCost.first()] = locCost.second();
			Location loc = SearchHelpers.ALL_LOCATIONS[locCost.first()];
			
			Location neighbour = null;
			
			try {
				neighbour = new Location(loc.getX() + 1, loc.getY());
				if (allLocs.contains(neighbour)) {
					int neighLocIndex = SearchHelpers.LOCATION_INT_MAPPING.get(neighbour);
					if (allPairsSP[startLocIndex][neighLocIndex] == -1) {
						locationsCostQ.add(new Pair<Integer, Integer> (neighLocIndex, locCost.second() + 1));
					}
				}
			} catch (Exception e) {}
			
			try {
				neighbour = new Location(loc.getX() - 1, loc.getY());
				if (allLocs.contains(neighbour)) {
					int neighLocIndex = SearchHelpers.LOCATION_INT_MAPPING.get(neighbour);
					if (allPairsSP[startLocIndex][neighLocIndex] == -1) {
						locationsCostQ.add(new Pair<Integer, Integer> (neighLocIndex, locCost.second() + 1));
					}
				}
			} catch (Exception e) {}
			
			try {
				neighbour = new Location(loc.getX(), loc.getY() + 1);
				if (allLocs.contains(neighbour)) {
					int neighLocIndex = SearchHelpers.LOCATION_INT_MAPPING.get(neighbour);
					if (allPairsSP[startLocIndex][neighLocIndex] == -1) {
						locationsCostQ.add(new Pair<Integer, Integer> (neighLocIndex, locCost.second() + 1));
					}
				}
			} catch (Exception e) {}
			
			try {
				neighbour = new Location(loc.getX(), loc.getY() - 1);
				if (allLocs.contains(neighbour)) {
					int neighLocIndex = SearchHelpers.LOCATION_INT_MAPPING.get(neighbour);
					if (allPairsSP[startLocIndex][neighLocIndex] == -1) {
						locationsCostQ.add(new Pair<Integer, Integer> (neighLocIndex, locCost.second() + 1));
					}
				}
			} catch (Exception e) {}
		}
	}

	@Override
	public double calculateHeuristicCost(Location current, Location goal) {
		// TODO Auto-generated method stub
		return 0;
	}

	HashMap<Location, Integer> locationColor = null;
	LocationSet dotLocations = null;
	
	/**
	 * In this heuristic our target is to build a heuristic that operates on the whole field.
	 */
	@Override
	public double calculateHeuristicCost(State state) {
		locationColor = new HashMap<Location, Integer>();
		dotLocations = state.getDotLocations();
		
		int color = 1;
		for (Location loc : dotLocations) {
			if (locationColor.get(loc) == null) {
				colorLocation(loc, color++);
			}
		}
		
		// This will be the adjacency matrix of the Pacman location with the dots clusters.
		int[][] adjM = new int[color][color];
		
		// In this array we will put the borders of every color of the graph. This will enable us
		// to get the shortest path between every two corresponding colors i.e. the distance
		// between every two corresponding dots clusters.
		ArrayList<ArrayList<Location>> colorBorderLocations = new ArrayList<ArrayList<Location>>();
		
		for (int i = 0; i < color; i++) {
			colorBorderLocations.add(new ArrayList<Location>());
		}
		
		// The first node in the graph is only Pacman location
		colorBorderLocations.get(0).add(state.getPacManLocation());
		
		for (Location loc : dotLocations) {
			Location neighbour = null;
			
			try {
				neighbour = new Location(loc.getX() + 1, loc.getY());
				if (!dotLocations.contains(neighbour) && Game.getAllLocations().contains(neighbour)) {
					colorBorderLocations.get(locationColor.get(loc)).add(loc);
					continue;
				}
			} catch (Exception e) {}
			
			try {
				neighbour = new Location(loc.getX() - 1, loc.getY());
				if (!dotLocations.contains(neighbour) && Game.getAllLocations().contains(neighbour)) {
					colorBorderLocations.get(locationColor.get(loc)).add(loc);
					continue;
				}
			} catch (Exception e) {}
			
			try {
				neighbour = new Location(loc.getX(), loc.getY() + 1);
				if (!dotLocations.contains(neighbour) && Game.getAllLocations().contains(neighbour)) {
					colorBorderLocations.get(locationColor.get(loc)).add(loc);
					continue;
				}
			} catch (Exception e) {}
			
			try {
				neighbour = new Location(loc.getX(), loc.getY() - 1);
				if (!dotLocations.contains(neighbour) && Game.getAllLocations().contains(neighbour)) {
					colorBorderLocations.get(locationColor.get(loc)).add(loc);
					continue;
				}
			} catch (Exception e) {}
		}
		
		for (int i = 0; i < adjM.length; i++) {
			for (int j = 0; j < adjM.length; j++) {
				if (i == j) {
					adjM[i][j] = 0;
				} else {
					adjM[i][j] = 1000000;
				}
			}
		}
		
		int edgeCount = 0;
		for (int i = 0; i < colorBorderLocations.size() - 1; i++) {
			ArrayList<Location> colors1 = colorBorderLocations.get(i);

			for (int j = i + 1; j < colorBorderLocations.size(); j++) {
				ArrayList<Location> colors2 = colorBorderLocations.get(j);
				
				int minDist = 1000000;

				for (Location loc1 : colors1) {
					for (Location loc2 : colors2) {
						minDist = Math.min(minDist, 
								allPairsSP[SearchHelpers.LOCATION_INT_MAPPING.get(loc1)][SearchHelpers.LOCATION_INT_MAPPING.get(loc2)]);
					}
				}

				adjM[i][j] = minDist;
				edgeCount++;
			}
		}

		// Now the heuristic result is the Minimum spanning tree of the nodes + number of nodes remaining
		
		class Edge implements Comparable<Edge> {

			int from, to, cost;

			public Edge(int from, int to, int cost) {
				this.from = from;
				this.to = to;
				this.cost = cost;
			}

			@Override
			public int compareTo(Edge e) {
				if (cost < e.cost) {
					return -1;
				} else if (cost > e.cost) {
					return 1;
				} else {
					return 0;
				}
			}
			
		}
		
		Edge[] edges = new Edge[edgeCount];
		
		int index = 0;
		for (int i = 0; i < adjM.length - 1; i++) {
			for (int j = i + 1; j < adjM.length; j++) {
				edges[index++] = new Edge(i, j, adjM[i][j]);
			}
		}
		
		Arrays.sort(edges);
		boolean[] visited = new boolean[adjM.length];
		int mstCost = 0;
		
		visited[edges[0].from] = visited[edges[0].to] = true;
		mstCost += edges[0].cost;
		for (int i = 0; i < edges.length; i++) {
			if (visited[edges[i].from] && !visited[edges[i].to]) {
				mstCost += edges[i].cost;
				visited[edges[i].to] = true;
				i = -1;
			} else if (!visited[edges[i].from] && visited[edges[i].to]) {
				mstCost += edges[i].cost;
				visited[edges[i].from] = true;
				i = -1;
			}
		}

		return mstCost + dotLocations.size();
	}

	/**
	 * - This method does the graph coloring.
	 * - It receives a color and a location and colors the dots adjacent to it
	 * with the corresponding color.
	 *
	 * @param loc the location to start coloring from.
	 * @param color the color to use to color this location.
	 */
	private void colorLocation(Location loc, int color) {
		locationColor.put(loc, color);
		Location neighbour = null;

		// Color the one on my right.
		try {
			neighbour = new Location(loc.getX() + 1, loc.getY());
			if (dotLocations.contains(neighbour)) {
				if (locationColor.get(neighbour) == null) {
					colorLocation(neighbour, color);
				}
			}
		} catch (Exception e) {}

		// Color the one on my left.
		try {
			neighbour = new Location(loc.getX() - 1, loc.getY());
			if (dotLocations.contains(neighbour)) {
				if (locationColor.get(neighbour) == null) {
					colorLocation(neighbour, color);
				}
			}
		} catch (Exception e) {}

		// Color the one below.
		try {
			neighbour = new Location(loc.getX(), loc.getY() + 1);
			if (dotLocations.contains(neighbour)) {
				if (locationColor.get(neighbour) == null) {
					colorLocation(neighbour, color);
				}
			}
		} catch (Exception e) {}

		// Color the one above me.
		try {
			neighbour = new Location(loc.getX(), loc.getY() - 1);
			if (dotLocations.contains(neighbour)) {
				if (locationColor.get(neighbour) == null) {
					colorLocation(neighbour, color);
				}
			}
		} catch (Exception e) {}

	}

}
