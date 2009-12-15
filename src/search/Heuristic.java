package search;

import pacman.State;

public interface Heuristic {
	
	public double calculateHeuristicCost(State current, State goal);

}
