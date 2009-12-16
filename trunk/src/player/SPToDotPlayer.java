package player;

import java.util.List;

import pacman.Game;
import pacman.Move;
import pacman.PacManPlayer;
import search.AStarSearch;
import search.DotShortestPathHeuristic;
import search.Search;

public class SPToDotPlayer implements PacManPlayer {

	Search searchTechnique = new AStarSearch(new DotShortestPathHeuristic());
	List<Move> nextMoves = null;

	@Override
	public Move chooseMove(Game game) {
		if (nextMoves == null || nextMoves.isEmpty()) {
			nextMoves = searchTechnique.searchForState(
					game.getCurrentState(), game.getCurrentState(
							).getDotLocations().iterator().next());
		}
		return nextMoves.remove(0);
	}

}
