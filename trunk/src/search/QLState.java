package search;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import pacman.Location;
import pacman.Move;
import pacman.State;

public class QLState implements Serializable {

	/**
	 * Pacman location is represented by an integer whose value is obtained from the map.
	 */
	int pacmanLoc;

	/**
	 * Surroundings are an array of integers.
	 *
	 * The values are:
	 *     1 -> DOT
	 *     2 -> GHOST
	 *     3 -> EMPTY
	 *
	 *   To construct the surroundings array we perform a BFS for a
	 * depth of 3. We get the visited nodes and put their values.
	 * 
	 *   The reason for this is we want only to have the relevant dots
	 * not to just choose a surrounding square.
	 *
	 *   In this case the maximum number of elements in the surrounding
	 * array will be 12. The minimum will be 3.
	 *
	 */
	int[] surroundings = null;
	
	/**
	 * The maximum depth for the BFS that gets the neighbouring locations.
	 */
	int MAX_DEPTH = 2;
	
	/**
	 * The action of the state.
	 */
	Move move;

	/**
	 * Construct a QL State given a game state and an action.
	 *
	 * @param s the current state of the game.
	 * @param move the action performed at this state.
	 */
	public QLState(State s, Move move) {
		this.move = move;

		Location loc = s.getPacManLocation();
		pacmanLoc = SearchHelpers.LOCATION_INT_MAPPING.get(loc);

		HashSet<Location> visited = new HashSet<Location>();
		Queue<Location> bfsQ = new LinkedList<Location>();
		bfsQ.add(loc);
		while (! bfsQ.isEmpty()) {
			Location curr = bfsQ.poll();

			if (Location.manhattanDistance(loc, curr) > MAX_DEPTH) {
				continue;
			}
			if (visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			
			Location neigh = null;
			neigh = new Location(curr.getX() - 1, curr.getY());
			if (SearchHelpers.LOCATION_INT_MAPPING.get(neigh) != null) {
				bfsQ.add(neigh);
			}
			neigh = new Location(curr.getX() + 1, curr.getY());
			if (SearchHelpers.LOCATION_INT_MAPPING.get(neigh) != null) {
				bfsQ.add(neigh);
			}
			neigh = new Location(curr.getX(), curr.getY() - 1);
			if (SearchHelpers.LOCATION_INT_MAPPING.get(neigh) != null) {
				bfsQ.add(neigh);
			}
			neigh = new Location(curr.getX(), curr.getY() + 1);
			if (SearchHelpers.LOCATION_INT_MAPPING.get(neigh) != null) {
				bfsQ.add(neigh);
			}
		}

		surroundings = new int[visited.size() - 1];
		int index = 0;
		for (Location visLoc : visited) {
			if (visLoc.equals(loc)) {
				continue;
			}

			if (s.getGhostLocations().contains(visLoc)) {
				surroundings[index++] = 2;
			} else if (s.getDotLocations().contains(visLoc)) {
				surroundings[index++] = 1;
			} else {
				surroundings[index++] = 3;
			}
		}
	}

	/**
	 * Simple check on all fields.
	 */
	public boolean equals(Object o) {
		QLState qls = (QLState)o;

		// Check pacman location is similar.
		if (pacmanLoc != qls.pacmanLoc) {
			return false;
		}
		
		if (surroundings.length != qls.surroundings.length) {
			return false;
		}
		
		if (! move.equals(qls.move))
			return false;

		// Check surroundings are similar.
		for (int i = 0; i < surroundings.length; i++) {
			if (surroundings[i] != qls.surroundings[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Hashcode for the QLState.
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + pacmanLoc;
		return result;
	}

	/**
	 * May be useless here.
	 */
	public String toString() {
		return "";
	}
	
}
