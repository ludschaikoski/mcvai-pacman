package search;

import java.util.List;

import pacman.Location;
import pacman.Move;
import pacman.State;

public interface Search {
	
	/**
	 * The methods takes a current state and returns the sequence
	 * to get to the goal state from the current state.
	 *
	 * @param current current state.
	 * @param goal goal state.
	 * @return an optimal sequence of moves from the current to the goal.
	 */
	List<Move> searchForState(State current, Location goal);

}
