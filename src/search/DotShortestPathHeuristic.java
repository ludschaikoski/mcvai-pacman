package search;

import pacman.Location;

public class DotShortestPathHeuristic implements Heuristic {

	@Override
	public double calculateHeuristicCost(Location current, Location goal) {
		// Lets start with the Manhattan distance heuristic
		return Location.manhattanDistance(current, goal);
	}

}
