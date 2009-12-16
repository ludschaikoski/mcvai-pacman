package search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import pacman.Game;
import pacman.Location;
import pacman.Move;
import pacman.State;
import util.PriorityQueue;

/**
 * <p>
 *  In this class we implement the A* search technique for a Pacman game.
 *  </p>
 *
 * <p>
 *  The A* search need a heuristic and it calculates the function (f) from
 * a certain node as the cost from the starting node to that node + the heuristic
 * function from that node to the goal
 * </p>
 *
 * @author amounir, eartola
 *
 */
public class AStarSearch implements Search {
	
	/**
	 * The heuristic we are going to use
	 */
	Heuristic aStarHeuristic;
	
	/**
	 * The constructor should read the appropriate heuristic that should be used.
	 *
	 * @param aStarHeuristic the AStar heuristic used in the construction.
	 */
	public AStarSearch(Heuristic aStarHeuristic) {
		this.aStarHeuristic = aStarHeuristic;
	}

	/**
	 *  Overrides the search for location state using the A* technique.
	 *
	 *  - The function (f) =
	 *      cost to the current node (g) + heuristic cost to goal node (h)
	 *
	 *  We use some hash tables that do the following:
	 *    - Get the move that brought me to this location
	 *    - Get the cost till I came to this location
	 *    - Get the state I am in on that location
	 *
	 *  To get the next node to be traversed we used a priority queue where priority
	 * is the function (f)
	 *    
	 *  In the end of the function we return the list of moves that brought us to
	 * that location
	 */
	@Override
	public List<Move> searchForLocation(State starting, Location goal) {
		// Initialize our priority queue
		PriorityQueue<Location> aStarPQ = new PriorityQueue<Location>();
		
		// Initialize the hashmaps that we are using
		HashMap<Location, Move> moveToLocation = new HashMap<Location, Move>();
		HashMap<Location, Integer> costToLocation = new HashMap<Location, Integer>();
		HashMap<Location, State> locationState = new HashMap<Location, State>();
		
		aStarPQ.add(starting.getPacManLocation(), aStarHeuristic.calculateHeuristicCost(starting.getPacManLocation(), goal));
		moveToLocation.put(starting.getPacManLocation(), Move.NONE); // We made no moves.
		costToLocation.put(starting.getPacManLocation(), 0); // We paid nothing to go to the same place.
		locationState.put(starting.getPacManLocation(), starting); // The state is the starting state.
		while (!aStarPQ.isEmpty()) {
			Location current = aStarPQ.removeFirst();

			// Are we at the goal state?
			if (current.equals(goal)) {

				// Calculate the path
				ArrayList<Move> path = new ArrayList<Move>();
				while (moveToLocation.get(current) != Move.NONE) {
					path.add(moveToLocation.get(current));
					current = Game.getNextLocation(
							current, moveToLocation.get(current).getOpposite());
				}
				Collections.reverse(path);

				return path;
			}

			// Now traverse the state
			State currentState = locationState.get(current);
			List<Move> legalMoves = Game.getLegalPacManMoves(currentState);
			for (Move move : legalMoves) {
				State nextState = Game.getNextState(currentState, move);
				
				// The state isn't already traversed
				if (moveToLocation.get(nextState.getPacManLocation()) == null) {
					moveToLocation.put(nextState.getPacManLocation(), move);
					locationState.put(nextState.getPacManLocation(), nextState);
					costToLocation.put(nextState.getPacManLocation(), costToLocation.get(current) + 1);
					aStarPQ.add(nextState.getPacManLocation(), -1.0 * (costToLocation.get(nextState.getPacManLocation()) +
							aStarHeuristic.calculateHeuristicCost(nextState.getPacManLocation(), goal)));
				}
			}
		}
		
		return null; // Return null if we cannot come to that location.
	}
}
