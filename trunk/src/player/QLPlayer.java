package player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import pacman.Game;
import pacman.Location;
import pacman.Move;
import pacman.PacManPlayer;
import pacman.State;
import search.DotShortestPathHeuristic;
import search.FullBoardHeuristic;
import search.QLHeuristic;
import search.QLState;
import search.QLTable;
import search.SearchHelpers;

public class QLPlayer implements PacManPlayer {

	public static ArrayList<Move> MOVE_LIST = new ArrayList<Move>();
	public static State STARTING = null;
	
	public QLPlayer() {
		SearchHelpers.ALL_LOCATIONS = new Location[Game.getAllLocations().size()];
		SearchHelpers.LOCATION_INT_MAPPING = new HashMap<Location, Integer>();

		int index = 0;
		for (Location loc : Game.getAllLocations()) {
			SearchHelpers.ALL_LOCATIONS[index] = loc;
			SearchHelpers.LOCATION_INT_MAPPING.put(loc, index++);
		}
	}
	
	@Override
	public Move chooseMove(Game game) {
		State current = game.getCurrentState();
		
		List<Move> possibleMoves = getPacmanPossibleMoves(current);
		double maxState = -1000000;
		List<Move> nextMoves = new ArrayList<Move>();

		for (Move move : possibleMoves) {
			double cost = SearchHelpers.QL_TABLE.getStateValue(new QLState(game.getCurrentState(), move));
			if (cost > maxState) {
				maxState = cost;
			}
		}
		
		for (Move move : possibleMoves) {
			double cost = SearchHelpers.QL_TABLE.getStateValue(new QLState(game.getCurrentState(), move));
			if (cost == maxState) {
				nextMoves.add(move);
			}
		}

		return chooseBest(game.getCurrentState(), nextMoves);
		//return nextMoves.get(Math.abs(r.nextInt()) % nextMoves.size());
		
		/*ArrayList<ArrayList<Move>> allGhostLocations = new ArrayList<ArrayList<Move>>();
		for (int i = 0; i < 4; i++) {
			allGhostLocations.add(new ArrayList<Move>());
		}
		
		for (int i = 0; i < 4; i++) {
			allGhostLocations.get(i).addAll(game.getLegalGhostMoves(i));
		}
		
		HashMap<State, Move> nextStatesMove = new HashMap<State, Move>();
		
		
		for (Move move : possibleMoves) {
			for (int g1 = 0; g1 < allGhostLocations.get(0).size(); g1++) {
				for (int g2 = 0; g2 < allGhostLocations.get(1).size(); g2++) {
					for (int g3 = 0; g3 < allGhostLocations.get(2).size(); g3++) {
						for (int g4 = 0; g4 < allGhostLocations.get(3).size(); g4++) {
							ArrayList<Move> ghostMoves = new ArrayList<Move>();
							ghostMoves.add(allGhostLocations.get(0).get(g1));
							ghostMoves.add(allGhostLocations.get(1).get(g2));
							ghostMoves.add(allGhostLocations.get(2).get(g3));
							ghostMoves.add(allGhostLocations.get(3).get(g4));
							
							State nextState = Game.getNextState(game.getCurrentState(), move, ghostMoves);
							
						}
					}
				}
			}
		}
		
		HashMap<Move, Integer> moveMinScore = new HashMap<Move, Integer>();
		for (Move move : possibleMoves) {
			moveMinScore.put(move, 1000000);
		}
		
		for (State nextState : nextStatesMove.keySet()) {
			
			allGhostLocations = new ArrayList<ArrayList<Move>>();
			for (int i = 0; i < 4; i++) {
				allGhostLocations.add(new ArrayList<Move>());
			}
			
			for (int i = 0; i < 4; i++) {
				allGhostLocations.get(i).addAll(game.getLegalGhostMoves(i));
			}
			
			if (Game.isWinning(nextState)) {
				moveMinScore.put(nextStatesMove.get(nextState), Math.min(1000, moveMinScore.get(nextStatesMove.get(nextState))));
				continue;
			}
			
			if (Game.isLosing(nextState)) {
				moveMinScore.put(nextStatesMove.get(nextState), Math.min(-1000, moveMinScore.get(nextStatesMove.get(nextState))));
				continue;
			}
			
			possibleMoves = getPacmanPossibleMoves(nextState);
			
			for (Move move : possibleMoves) {
				for (int g1 = 0; g1 < allGhostLocations.get(0).size(); g1++) {
					for (int g2 = 0; g2 < allGhostLocations.get(1).size(); g2++) {
						for (int g3 = 0; g3 < allGhostLocations.get(2).size(); g3++) {
							for (int g4 = 0; g4 < allGhostLocations.get(3).size(); g4++) {
								ArrayList<Move> ghostMoves = new ArrayList<Move>();
								ghostMoves.add(allGhostLocations.get(0).get(g1));
								ghostMoves.add(allGhostLocations.get(1).get(g2));
								ghostMoves.add(allGhostLocations.get(2).get(g3));
								ghostMoves.add(allGhostLocations.get(3).get(g4));
								
								State secondNextState = Game.getNextState(nextState, move, ghostMoves);
								
								
							}
						}
					}
				}
			}
			
		}
		
		Move nextMove = Move.NONE;
		int nextMoveCost = -1000000;
		for (Move move : moveMinScore.keySet()) {
			if (moveMinScore.get(move) > nextMoveCost) {
				nextMoveCost = moveMinScore.get(move);
				nextMove = move;
			}
		}
		
		
		State nextState = Game.getNextState(game.getCurrentState(), nextMove);
		Game.getNextState(s, pacManMove, ghostMoves)
		Game.getl
		Game.getNextState(s, pacManMove, ghostMoves)
		if (Game.isWinning(nextState)) {
			
		}*/
	}

	QLHeuristic qlh = null;

	private Move chooseBest(State currentState, List<Move> nextMoves) {

		if (qlh == null) {
			qlh = new QLHeuristic();
		}

		double minCost = 1000000;
		Move retMove = Move.NONE;
		for (Move nextMove : nextMoves) {
			State nextState = Game.getNextState(currentState, nextMove);
			double stateCost = 1000000;

			try {
				stateCost = qlh.calculateHeuristicCost(nextState);
			} catch (Exception e) {
				continue;
			}

			if (minCost > stateCost) {
				minCost = stateCost;
				retMove = nextMove;
			}
		}
		
		if (retMove.equals(Move.NONE)) {
			Random r = new Random();
			return nextMoves.get(Math.abs(r.nextInt()) % nextMoves.size());
		} else {
			return retMove;
		}
	}

	private static List<Move> getPacmanPossibleMoves(State current) {
		Location pLoc = current.getPacManLocation();
		List<Move> ret = new ArrayList<Move>();
		
		Location neigh = null;

		neigh = new Location(pLoc.getX() - 1, pLoc.getY());
		if (SearchHelpers.LOCATION_INT_MAPPING.get(neigh) != null) {
			ret.add(Move.LEFT);
		}
		neigh = new Location(pLoc.getX() + 1, pLoc.getY());
		if (SearchHelpers.LOCATION_INT_MAPPING.get(neigh) != null) {
			ret.add(Move.RIGHT);
		}
		neigh = new Location(pLoc.getX(), pLoc.getY() - 1);
		if (SearchHelpers.LOCATION_INT_MAPPING.get(neigh) != null) {
			ret.add(Move.DOWN);
		}
		neigh = new Location(pLoc.getX(), pLoc.getY() + 1);
		if (SearchHelpers.LOCATION_INT_MAPPING.get(neigh) != null) {
			ret.add(Move.UP);
		}
		
		if (pLoc.getX() == 0 && pLoc.getY() == 15) {
			ret.add(Move.LEFT);
		}
		
		if (pLoc.getX() == 25 && pLoc.getY() == 15) {
			ret.add(Move.RIGHT);
		}

		return ret;
	}

	private static double alpha = 0.9;
	
	public static void updateQLTable(State currentState, State nextState, Move pacManMove) {
		QLState qstate = new QLState(currentState, pacManMove);
		
		double qScore = 0;

		if (Game.isWinning(nextState)) {
			qScore += 1000 * alpha;
			SearchHelpers.QL_TABLE.updateStateValue(qstate, qScore);
			return;
		} else if (Game.isLosing(nextState)) {
			qScore += -1000 * alpha;
			SearchHelpers.QL_TABLE.updateStateValue(qstate, qScore);
			return;
		}
		
		qScore = currentState.getDotLocations().size() - nextState.getDotLocations().size();
		List<Move> possibleMoves = getPacmanPossibleMoves(nextState);
		
		double maxNextScore = -1000000;

		for (Move move : possibleMoves) {
			double nextStateScore = SearchHelpers.QL_TABLE.getStateValue(new QLState(nextState, move));
			if (nextStateScore > maxNextScore) {
				maxNextScore = nextStateScore;
			}
		}
		
		qScore += alpha * maxNextScore;
		SearchHelpers.QL_TABLE.updateStateValue(qstate, qScore);
	}

}
