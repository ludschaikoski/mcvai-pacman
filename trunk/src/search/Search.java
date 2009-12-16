package search;

import java.util.List;

import pacman.Location;
import pacman.Move;
import pacman.State;

/**
 * The interface describes the search technique for pacman.
 *
 * @author amounir, eartola
 *
 */
public interface Search {
	
	/**
	 * The methods takes a current state and returns the sequence
	 * to get pacman to a certain location from the current state.
	 *
	 * @param current current state.
	 * @param goal goal location.
	 * @return an optimal sequence of moves from the current to the goal.
	 */
	List<Move> searchForLocation(State current, Location goal);

}
