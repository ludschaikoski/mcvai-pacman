package player;

import java.util.HashMap;
import java.util.List;

import pacman.Game;
import pacman.Location;
import pacman.Move;
import pacman.PacManPlayer;
import search.AStarSearch;
import search.FullBoardHeuristic;
import search.SearchHelpers;

public class OptimalFullBoardPlayer implements PacManPlayer {

	List<Move> pacmanMoves = null;

	@Override
	public Move chooseMove(Game game) {

		if (pacmanMoves != null && pacmanMoves.size() != 0)
			return pacmanMoves.get(0);

		SearchHelpers.ALL_LOCATIONS = new Location[Game.getAllLocations().size()];
		SearchHelpers.LOCATION_INT_MAPPING = new HashMap<Location, Integer>();

		SearchHelpers.DOT_LOCATIONS = new Location[game.getCurrentState().getDotLocations().size()];
		SearchHelpers.DOT_LOCATION_INT_MAPPING = new HashMap<Location, Integer>();

		int index = 0;
		for (Location loc : Game.getAllLocations()) {
			SearchHelpers.ALL_LOCATIONS[index] = loc;
			SearchHelpers.LOCATION_INT_MAPPING.put(loc, index++);
		}

		index = 0;
		for (Location loc : game.getCurrentState().getDotLocations()) {
			SearchHelpers.DOT_LOCATIONS[index] = loc;
			SearchHelpers.DOT_LOCATION_INT_MAPPING.put(loc, index++);
		}

		AStarSearch searchTechnique = new AStarSearch(new FullBoardHeuristic());
		pacmanMoves = searchTechnique.searchForEmptyBoard(game.getCurrentState());
		return pacmanMoves.get(0);
	}

}
