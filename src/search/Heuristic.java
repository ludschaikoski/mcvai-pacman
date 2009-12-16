package search;

import pacman.Location;

public interface Heuristic {
	
	public double calculateHeuristicCost(Location current, Location goal);

}
