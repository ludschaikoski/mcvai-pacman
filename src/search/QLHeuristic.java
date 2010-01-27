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

public class QLHeuristic implements Heuristic {

	public static int[][] allPairsSP = null;
	
	public QLHeuristic() {
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
				break;
			}
		}
		
		int cost = 1000000;
		for (Location dotLoc : locationColor.keySet()) {
			cost = Math.min(cost, allPairsSP[SearchHelpers.LOCATION_INT_MAPPING.get(state.getPacManLocation())][SearchHelpers.LOCATION_INT_MAPPING.get(dotLoc)]);
		}

		return cost + locationColor.size();
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
