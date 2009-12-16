package search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import pacman.Game;
import pacman.Location;
import pacman.LocationSet;
import pacman.Move;
import pacman.State;
import util.PriorityQueue;

public class AStarSearch implements Search {
	
	Heuristic aStarHeuristic;
	
	public AStarSearch(Heuristic aStarHeuristic) {
		this.aStarHeuristic = aStarHeuristic;
	}

	@Override
	public List<Move> searchForState(State starting, Location goal) {
		PriorityQueue<Location> aStarPQ = new PriorityQueue<Location>();
		HashMap<Location, Move> moveToLocation = new HashMap<Location, Move>();
		HashMap<Location, Integer> costToLocation = new HashMap<Location, Integer>();
		HashMap<Location, State> locationState = new HashMap<Location, State>();
		
		aStarPQ.add(starting.getPacManLocation(), aStarHeuristic.calculateHeuristicCost(starting.getPacManLocation(), goal));
		moveToLocation.put(starting.getPacManLocation(), Move.NONE); // We made no moves.
		costToLocation.put(starting.getPacManLocation(), 0); // We paid nothing to go to the same place.
		locationState.put(starting.getPacManLocation(), starting);
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
					aStarPQ.add(nextState.getPacManLocation(), costToLocation.get(nextState.getPacManLocation()) +
							aStarHeuristic.calculateHeuristicCost(nextState.getPacManLocation(), goal));
				}
			}
		}
		
		return null;
	}
	
	
	
}
