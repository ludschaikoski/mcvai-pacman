package player;

import java.util.List;

import pacman.Game;
import pacman.Location;
import pacman.LocationSet;
import pacman.Move;
import pacman.PacManPlayer;
import search.AStarSearch;
import search.DotShortestPathHeuristic;
import search.Search;

/**
 * Pacman plays with the technique of finding the closest
 * dot (ties broken randomly) and walks on the shortest path
 * to that dot.
 *
 * @author amounir, eartola
 *
 */
public class SPToDotPlayer implements PacManPlayer {

	/**
	 * Uses A* to search for a location
	 */
	Search searchTechnique = new AStarSearch(new DotShortestPathHeuristic());

	/**
	 * Store a list of moves when precalculated.
	 */
	List<Move> nextMoves = null;

	/**
	 * Takes a game state and returns a move for pacman
	 */
	@Override
	public Move chooseMove(Game game) {
		// Check if that move was precalculated
		if (nextMoves == null || nextMoves.isEmpty()) {
			nextMoves = null; // Reinitialize it

			LocationSet dotLocs = game.getCurrentState().getDotLocations();
			for (Location loc : dotLocs) {
				// A* search for the goal location.
				List<Move> tempMoves = searchTechnique.searchForLocation(
						game.getCurrentState(), loc);
				// Choose the closest dot to our location
				if (nextMoves == null || tempMoves.size() < nextMoves.size())
					nextMoves = tempMoves;
			}
		}
		return nextMoves.remove(0);
	}

}
