package search;

import pacman.Location;
import pacman.State;

public class DotShortestPathHeuristic implements Heuristic {

	@Override
	public double calculateHeuristicCost(Location current, Location goal) {
		// Lets start with the Manhattan distance heuristic
		return Location.manhattanDistance(current, goal);
	}

	@Override
	public double calculateHeuristicCost(State state) {
		// TODO Auto-generated method stub
		return 0;
	}

}
