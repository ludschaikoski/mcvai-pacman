package search;

import pacman.Location;
import pacman.State;

/**
 * This is the interface for any heuristic.
 *
 * @author amounir, eartola
 *
 */
public interface Heuristic {
	
	/**
	 * Calculates the heuristic by just reading the current state of the game.
	 *
	 * @param state
	 * @return
	 */
	public double calculateHeuristicCost(State state);
	
	/**
	 * Calculates the heuristic cost from a location to another ignoring
	 * any external effective like ghosts.
	 *
	 * @param current current pacman location 
	 * @param goal pacman goal loation
	 * @return heuristic value
	 */
	public double calculateHeuristicCost(Location current, Location goal);

}
