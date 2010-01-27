package ghosts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import pacman.Game;
import pacman.GhostPlayer;
import pacman.Location;
import pacman.Move;
import pacman.State;
import search.QLGState;
import search.QLHeuristic;
import search.QLState;
import search.SearchHelpers;

public class QLGhost extends GhostPlayer {
	
	public QLGhost() {
		SearchHelpers.ALL_LOCATIONS = new Location[Game.getAllLocations().size()];
		SearchHelpers.LOCATION_INT_MAPPING = new HashMap<Location, Integer>();

		int index = 0;
		for (Location loc : Game.getAllLocations()) {
			SearchHelpers.ALL_LOCATIONS[index] = loc;
			SearchHelpers.LOCATION_INT_MAPPING.put(loc, index++);
		}
	}

	@Override
	public Move chooseMove(Game game, int ghostIndex) {
		State current = game.getCurrentState();
		
		List<Move> possibleMoves = game.getLegalGhostMoves(ghostIndex);
		double maxState = -1000000;

		List<Move> nextMoves = new ArrayList<Move>();

		for (Move move : possibleMoves) {
			if (move.equals(Move.NONE)) {
				continue;
			}
			double cost = SearchHelpers.QLG_TABLE.getStateValue(new QLGState(game.getCurrentState(), move, ghostIndex));
			if (cost > maxState) {
				maxState = cost;
			}
		}
		
		for (Move move : possibleMoves) {
			if (move.equals(Move.NONE)) {
				continue;
			}
			double cost = SearchHelpers.QLG_TABLE.getStateValue(new QLGState(game.getCurrentState(), move, ghostIndex));
			if (cost == maxState) {
				nextMoves.add(move);
			}
		}

		return nextMoves.size() == 0 ? Move.NONE : chooseBest(game.getCurrentState(), nextMoves, ghostIndex);
	}

	private Move chooseBest(State currentState, List<Move> nextMoves, int ghostIndex) {
		double minCost = 1000000;
		Move nextMove = Move.NONE;
		for (Move move : nextMoves) {
			Location gLoc = currentState.getGhostLocations().get(ghostIndex);
			Location pLoc = currentState.getPacManLocation();
			
			Location nextLoc = null;
			
			if (move.equals(Move.RIGHT)) {
				if (gLoc.getX() == 25 && gLoc.getY() == 15) {
					nextLoc = new Location(0, 15);
				} else {
					nextLoc = new Location(gLoc.getX() + 1, gLoc.getY());
				}
			} else if (move.equals(Move.LEFT)) {
				if (gLoc.getX() == 0 && gLoc.getY() == 15) {
					nextLoc = new Location(25, 15);
				} else {
					nextLoc = new Location(gLoc.getX() - 1, gLoc.getY());
				}
			} else if (move.equals(Move.UP)) {
				nextLoc = new Location(gLoc.getX(), gLoc.getY() + 1);
			} else if (move.equals(Move.DOWN)) {
				nextLoc = new Location(gLoc.getX(), gLoc.getY() - 1);
			}
			
			if (nextLoc == null) {
				continue;
			}
			
			if (QLHeuristic.allPairsSP != null) {

				double cost = QLHeuristic.allPairsSP[SearchHelpers.LOCATION_INT_MAPPING.get(nextLoc)][SearchHelpers.LOCATION_INT_MAPPING.get(pLoc)];
				if (cost < minCost) {
					minCost = cost;
					nextMove = move;
				}

			} else {

				double cost = Location.manhattanDistance(nextLoc, pLoc);
				if (cost < minCost) {
					minCost = cost;
					nextMove = move;
				}

			}
		}
		
		Random rand = new Random();
		int next = Math.abs(rand.nextInt());
		if (next % 100 >= 30) {
			return nextMoves.get(Math.abs(rand.nextInt()) % nextMoves.size());
		}
		return nextMove;
		
	}

	private static double alpha = 0.9;
	
	public static void updateQLTable(State currentState, State nextState) {

		for (int gi = 0; gi < currentState.getGhostLocations().size(); gi++) {
			
			Location gLoc = currentState.getGhostLocations().get(gi);
			Location ngLoc = nextState.getGhostLocations().get(gi);
			Move gMove = Move.NONE;
			
			if (ngLoc.getX() == (gLoc.getX() + 1)) {
				gMove = Move.RIGHT;
			}
			else if (ngLoc.getX() == (gLoc.getX() - 1)) {
				gMove = Move.LEFT;			
			}
			else if (ngLoc.getY() == (gLoc.getY() + 1)) {
				gMove = Move.UP;
			}
			else if (ngLoc.getY() == (gLoc.getY() + 1)) {
				gMove = Move.DOWN;
			}
			else if (ngLoc.getX() > gLoc.getX()) {
				gMove = Move.LEFT;
			} else {
				gMove = Move.RIGHT;
			}
			

			QLGState qstate = new QLGState(currentState, gMove, gi);

			double qScore = 0;
	
			if (Game.isWinning(nextState)) {
				qScore -= 1000 * alpha;
				SearchHelpers.QLG_TABLE.updateStateValue(qstate, qScore);
				return;
			} else if (Game.isLosing(nextState)) {
				qScore += 1000 * alpha;
				SearchHelpers.QLG_TABLE.updateStateValue(qstate, qScore);
				return;
			}

			List<Move> possibleMoves = Game.getLegalGhostMoves(nextState, gi);
			
			double maxNextScore = -1000000;
	
			for (Move move : possibleMoves) {
				double nextStateScore = SearchHelpers.QLG_TABLE.getStateValue(new QLGState(nextState, move, gi));
				if (nextStateScore > maxNextScore) {
					maxNextScore = nextStateScore;
				}
			}
			
			qScore += alpha * maxNextScore;
			SearchHelpers.QLG_TABLE.updateStateValue(qstate, qScore);
		}

	}

}
