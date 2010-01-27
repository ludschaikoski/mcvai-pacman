package search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import pacman.Game;
import pacman.Location;
import pacman.LocationSet;
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

	/**
	 * The optimized state optimizes a state in terms of memory.
	 * It uses bitwise operations to represent every location in one bit.
	 *
	 * @author amounir
	 *
	 */
	 class OptimizedState {
		 
		 private int[] dotsLocs = new int[8];
		 private Move from;
		 private OptimizedState fromState;
		 private int pacmanLocIndex;
		 private int cost;
		
		/**
		 * This builds the optimized state given the real state of the game
		 *
		 * @param current
		 */
		public OptimizedState(State current, Move from, OptimizedState fromState, int cost) {
			this.setFrom(from);
			this.setCost(cost);
			this.fromState = fromState;
			setPacmanLocIndex(SearchHelpers.LOCATION_INT_MAPPING.get(current.getPacManLocation()));
			
			for (Location dotLoc : current.getDotLocations()) {
				int dotLocIndex = SearchHelpers.DOT_LOCATION_INT_MAPPING.get(dotLoc);
				dotsLocs[dotLocIndex / 32] |= 1 << (dotLocIndex % 32);
			}
		}
		
		public OptimizedState(OptimizedState current) {
			from = current.from;
			setFromState(current.getFromState());
			pacmanLocIndex = current.pacmanLocIndex;
			cost = current.cost;
			dotsLocs = new int[current.dotsLocs.length];
			for (int i = 0; i < dotsLocs.length; i++) {
				dotsLocs[i] = current.dotsLocs[i];
			}
		}
		
		/**
		 * This builds the game state from an optimized state.
		 *
		 * @return the game state in the format understandable with the game.
		 */
		public State toState() {
			Location pacmanLocation = SearchHelpers.ALL_LOCATIONS[pacmanLocIndex];

			int locCount = SearchHelpers.DOT_LOCATIONS.length;
			LocationSet dotsLocations = new LocationSet();
			for (int i = 0; i < locCount; i++) {
				int dotsLocIndex = SearchHelpers.DOT_LOCATION_INT_MAPPING.get(SearchHelpers.DOT_LOCATIONS[i]);
				if (((1 << (dotsLocIndex % 32)) & (dotsLocs[dotsLocIndex / 32])) == 0) {
					continue;
				}
				dotsLocations.add(SearchHelpers.DOT_LOCATIONS[dotsLocIndex]);
			}
			
			State res = new State(pacmanLocation, new ArrayList<Location>(), dotsLocations, new ArrayList<Move>(), null);
			return res;
		}

		/**
		 * This method returns whether the two states are similar or no
		 */
		public boolean equals(Object o) {
			OptimizedState os = (OptimizedState)o;

			if (os.pacmanLocIndex != pacmanLocIndex) {
				return false;
			}

			for (int i = 0; i < dotsLocs.length; i++) {
				if (dotsLocs[i] != os.dotsLocs[i])
					return false;
			}

			return true;
		}
		
		/**
		 * This method returns the hashcode of the element.
		 * it only depends on the pacman location and the state of the field.
		 */
		public int hashCode() {
		    long result = 17;

		    result = result*37 + pacmanLocIndex;
		    for (int dotLoc : dotsLocs) {
		    	result += dotLoc;
		    }

		    return ((int)result < 0 ? -1 * (int)result : (int)result);
		}
		
		public boolean containsDot(int locationID) {
			return ((dotsLocs[locationID / 32] & 1 << (locationID % 32)) != 0);
		}
		
		public void movePacman(Move move) {
			Location pacmanLoc = SearchHelpers.ALL_LOCATIONS[pacmanLocIndex];
			if (move.equals(Move.LEFT)) {
				pacmanLocIndex = SearchHelpers.LOCATION_INT_MAPPING.get(new Location(pacmanLoc.getX() - 1, pacmanLoc.getY()));
			}
			if (move.equals(Move.RIGHT)) {
				pacmanLocIndex = SearchHelpers.LOCATION_INT_MAPPING.get(new Location(pacmanLoc.getX() + 1, pacmanLoc.getY()));
			}
			if (move.equals(Move.UP)) {
				pacmanLocIndex = SearchHelpers.LOCATION_INT_MAPPING.get(new Location(pacmanLoc.getX(), pacmanLoc.getY() + 1));
			}
			if (move.equals(Move.DOWN)) {
				pacmanLocIndex = SearchHelpers.LOCATION_INT_MAPPING.get(new Location(pacmanLoc.getX(), pacmanLoc.getY() - 1));
			}
		}
		
		/**
		 * If the location contains a dot it removes the dot.
		 * Otherwise it puts a dot
		 *
		 * @param locationID
		 */
		public void flipDotAtLocation(int locationID) {
			dotsLocs[locationID / 32] ^= 1 << (locationID % 32);
		}

		void setDotsLocs(int[] dotsLocs) {
			this.dotsLocs = dotsLocs;
		}

		/*int[] getDotsLocs() {
			return dotsLocs;
		}*/

		void setFrom(Move from) {
			this.from = from;
		}

		Move getFrom() {
			return from;
		}

		void setPacmanLocIndex(int pacmanLocIndex) {
			this.pacmanLocIndex = pacmanLocIndex;
		}

		int getPacmanLocIndex() {
			return pacmanLocIndex;
		}

		void setCost(int cost) {
			this.cost = cost;
		}

		int getCost() {
			return cost;
		}

		private void setFromState(OptimizedState fromState) {
			this.fromState = fromState;
		}

		private OptimizedState getFromState() {
			return fromState;
		}
		
	}
	
	 /**
	  * 
	  * @param locationIndex
	  * @return
	  */
	
	 public List<Move> getPossibleMoves(int locationIndex) {
		 Location loc = SearchHelpers.ALL_LOCATIONS[locationIndex];
		 
		 Location u = new Location(loc.getX(), loc.getY() - 1);
		 Location d = new Location(loc.getX(), loc.getY() + 1);
		 Location l = new Location(loc.getX() - 1, loc.getY());
		 Location r = new Location(loc.getX() + 1, loc.getY());
		 
		 ArrayList<Move> ret = new ArrayList<Move>();

		 if (SearchHelpers.LOCATION_INT_MAPPING.get(u) != null) {
			 ret.add(Move.UP);
		 }
		 if (SearchHelpers.LOCATION_INT_MAPPING.get(d) != null) {
			 ret.add(Move.DOWN);
		 }
		 if (SearchHelpers.LOCATION_INT_MAPPING.get(l) != null) {
			 ret.add(Move.LEFT);
		 }
		 if (SearchHelpers.LOCATION_INT_MAPPING.get(r) != null) {
			 ret.add(Move.RIGHT);
		 }
		 
		 return ret;
	 }
	
	/**
	 * In this method we read a starting state and return a list of moves to reach with this
	 * starting state into an empty set where all dots are eaten by pacman
	 *
	 * @param starting the state where all dots are available on the field.
	 * @return list of moves to reach into the empty state.
	 */
	public List<Move> searchForEmptyBoard(State startingState) {
		
		int iterDeepInit = (int) aStarHeuristic.calculateHeuristicCost(startingState);
		for (int iterDeepCost = 292; ; iterDeepCost++) {
			
			System.out.println("Iteration cost max = " + iterDeepCost);
		
			PriorityQueue<OptimizedState> statesPQ = new PriorityQueue<OptimizedState>();
			HashSet<OptimizedState> visitedState = new HashSet<OptimizedState>();

			statesPQ.add(new OptimizedState(startingState, Move.NONE, null, 0), -1 * aStarHeuristic.calculateHeuristicCost(startingState));
			
			for (int iter = 1; ! statesPQ.isEmpty(); iter++) {

				OptimizedState current = statesPQ.removeFirst();
				
				if (visitedState.contains(current)) {
					continue;
				}	
				visitedState.add(current);

				if (isGoalState(current)) { // Construct the path
					ArrayList<Move> ret = new ArrayList<Move>();
					ret.add(Move.RIGHT);
					Collections.reverse(ret);
					return ret;
				}
	
				////
				// Display
				////
				if (iter % 100000 == 0) {
					System.gc();
					System.out.println("Iteration: " + iter);
				}
	
				////
				// End of Display
				////
	
				State currentState = current.toState();
				List<Move> possibleMoves = Game.getLegalPacManMoves(currentState);
				for (Move pmanMove : possibleMoves) {
					State nextState = Game.getNextState(currentState, pmanMove);
					OptimizedState opNextState = new OptimizedState(nextState, pmanMove, current, current.getCost() + 1);

					if (visitedState.contains(opNextState)) {
						continue;
					}

					if (Game.isWinning(nextState)) {
						statesPQ.relaxPriority(opNextState, -1 * opNextState.getCost());
					} else {
						double nextStateCost = (opNextState.getCost() + aStarHeuristic.calculateHeuristicCost(nextState));
						if (nextStateCost <= iterDeepCost) {
							statesPQ.relaxPriority(opNextState, -1 * nextStateCost);
						}
					}
				}
	
			}
		}
	}

	private boolean isGoalState(OptimizedState current) {
		int res = 0;
		for (int dloc : current.dotsLocs) {
			res |= dloc;
		}
		return res == 0 ? true : false;
	}
}
