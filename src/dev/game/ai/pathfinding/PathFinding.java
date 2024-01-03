
package dev.game.ai.pathfinding;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dev.game.Handler;
import dev.game.entities.Entity;
import dev.game.tiles.Tile;
import dev.game.worlds.World;

/**
 * PathFinding.java - A* algorithm
 *
 * @author Juhyung Kim
 */
public class PathFinding {

	private Handler handler;
	private Entity finder;
	private Entity targetEntity = null;

	// to prevent infinite search for path
	private final int MAX_SEARCH = 50;
	private int searchAmount; // amount of node searching done

	private Node start, target;

	private ArrayList<Node> openList; // nodes that have not checked
	private ArrayList<Node> closeList; // nodes that have checked

	private boolean noPath; // true if no path is found

	private ArrayList<Node> path; // the final path

	private Comparator<Node> sorter = new Comparator<Node>() {

		public int compare(Node a, Node b) {

			// if statement is true, -1, else return 1
			// -1 sort in ascending order
			return (a.getMoveCost() < b.getMoveCost()) ? -1 : 1;

		}

	};

	/**
	 * Find the path from the entity to the given target.
	 *
	 * @param handler
	 * @param finder
	 * @param targetEntity
	 */
	public PathFinding(Handler handler, Entity finder, Entity targetEntity) {

		this.finder = finder;
		this.handler = handler;
		this.targetEntity = targetEntity;

		searchAmount = 0;

		noPath = false;

		int startX = finder.getCenterX();
		int startY = finder.getCenterY();
		int targetX = targetEntity.getCenterX();
		int targetY = targetEntity.getCenterY();

		start = new Node(startX, startY, 0);

		target = new Node(targetX, targetY, 0);

		int cost = getDistanceFromStart(target);

		/// the two nodes are away from each other in same distance
		target.setMoveCost(cost);
		start.setMoveCost(cost);

		openList = new ArrayList<>();
		closeList = new ArrayList<>();

		openList.add(start); // add the starting node to open list to start searching

		findPath();

	}

	/**
	 * Connect the nodes to make the path
	 *
	 * @param target
	 * @return
	 */
	private ArrayList<Node> makePath(Node target) {

		ArrayList<Node> thePath = new ArrayList<>();

		Node current = target;

		while (current.getParentPath() != null) {

			thePath.add(current);

			current = current.getParentPath();

		}

		return thePath;

	}

	/**
	 * Find / update the path
	 * <p>
	 */
	private void findPath() {

		while (!openList.isEmpty()) {

			Node current = openList.get(0); // lowest move cost

			// path is found
			if (target.getX() == current.getX() && target.getY() == current.getY()) {

				path = makePath(current);

				noPath = false;

				return;
			}

			closeList.add(current); // now this node is checked

			openList.remove(current); // off from unchecked-list

			checkNeighbors(current); // fill in the openList with neighbors

		}

		noPath = true;
		path = null; // no path

	}

	/**
	 * Check the neighboring tiles (8 adjacent tiles) of a tile
	 * <p>
	 * Unaccessible diagonal path will get omitted
	 *
	 * @param current Current node
	 */
	private void checkNeighbors(Node current) {

		if (noPath)
			return;

		searchAmount++;

		// if the entity try to search for the path too much, stop it. (lag issue)
		if (searchAmount > MAX_SEARCH) {

			searchAmount = 0;
			noPath = true;
			return;

		}

		int currentX = current.getX();
		int currentY = current.getY();

		// Check the neighboring tiles (8 adjacent tiles) of a tile
		for (int y = -1; y < 2; y++) {

			for (int x = -1; x < 2; x++) {

				int newX = currentX + x;
				int newY = currentY + y;

				// setting moveCost and parent is done after checking if
				// this is considerable spot to check
				Node node = new Node(newX, newY, 0);

				Tile tile = World.getTile(newX, newY);

				boolean isInOpenList = false;
				boolean isInCloseList = false;

				for (Node open : openList) {

					if (open.equals(node))
						isInOpenList = true;

				}

				for (Node close : closeList) {

					if (close.equals(node))
						isInCloseList = true;

				}

				// the new neighbor should be something was not checked yet,
				/// and is not solid tile
				if (tile.isSolid())
					continue;

				if (isInCloseList)
					continue;

				if (isInOpenList)
					continue;

				int moveCost = getMoveCost(node);

				node.setMoveCost(moveCost);
				node.setParentPath(current);

				boolean isStraight = (x == 0 || y == 0);

				// true if the path is still considerable
				// after checking path depending on the tile
				boolean consider;

				// Straight path doesn't need to check its adjacent obstacle
				if (isStraight) {

					consider = true;

				} // else checking in case where the diagonal path is unavailable
				else {

					// booleans of obstacles
					boolean up = World.getTile(currentX, currentY - 1).isSolid();
					boolean down = World.getTile(currentX, currentY + 1).isSolid();
					boolean left = World.getTile(currentX - 1, currentY).isSolid();
					boolean right = World.getTile(currentX + 1, currentY).isSolid();

					// checking if target diagonal tile has adjacent obstacle tile to
					// avoid
					if ((x == -1 && y == -1) && (up || left)) {
						continue;
					}
					if ((x == 1 && y == -1) && (up || right)) {
						continue;
					}
					if ((x == -1 && y == 1) && (down || left)) {
						continue;
					}
					if ((x == 1 && y == 1) && (down || right)) {
						continue;
					}

					consider = true;

				}

				// now check for the entity on the path
				if (consider) {

					// true an entity is on the way of path
					boolean collided = false;

					Rectangle pathRect = new Rectangle(newX * Tile.TILE_SIZE,
							newY * Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE);

					// check all entities
					for (int yy = 0; yy < World.map.length; yy++) {
						for (int xx = 0; xx < World.map[0].length; xx++) {

							if (World.map[yy][xx] == null)
								continue;

							for (Entity ent : World.map[yy][xx].entities) {

								if (ent.getNoCollision())
									continue;

								if (ent.equals(finder))
									continue;

								// checking, not to skip the tile that collided with
								// possible target entity
								if (pathRect.intersects(ent.getBounds(0, 0))) {

									if (targetEntity != null && ent == targetEntity)
										break;

									collided = true;
									break;

								}
							}
						}
					}

					if (!collided)
						openList.add(node);

				}
			}
		}

		Collections.sort(openList, sorter);

	}

	/**
	 * H cost
	 * <p>
	 * Get the distance from the target tile 10 points each straight path, 14 points
	 * for each diagonal path Value determined by "Diagonal Distance" which has 8
	 * directions to move
	 * <p>
	 * Target node should be valid already
	 *
	 * @param current The current tile
	 * @return The distance from the target tile
	 */
	private int getDistanceFromTarget(Node current) {

		int cost = 10;
		int diagonalCost = 14;

		int xDis = Math.abs(target.getX() - current.getX());
		int yDis = Math.abs(target.getY() - current.getY());

		int diagonal = Math.min(xDis, yDis); // amount of diagonal paths

		int straight = Math.max(xDis, yDis) - diagonal; // amount of straight paths

		return (diagonal * diagonalCost + straight * cost);

	}

	/**
	 * G cost
	 * <p>
	 * Get the distance from starting tile Including both straight and diagonal path
	 * <p>
	 * To make math easy, straight path is 10, and diagonal path is 14 Because
	 * sqrt(10^2 + 10^2) is about 14
	 * <p>
	 * Target node should be valid already
	 *
	 * @param current
	 * @return
	 */
	private int getDistanceFromStart(Node current) {

		int cost = 10;
		int diagonalCost = 14;

		int xDis = Math.abs(start.getX() - current.getX());
		int yDis = Math.abs(start.getY() - current.getY());

		int diagonal = Math.min(xDis, yDis); // amount of diagonal paths

		int straight = Math.max(xDis, yDis) - diagonal; // amount of straight paths

		return (diagonal * diagonalCost + straight * cost);

	}

	/**
	 * F value H value + G value (distance from target + distance from start)
	 * <p>
	 * Lower the closer to the target
	 *
	 * @param current
	 * @return
	 */
	private int getMoveCost(Node current) {

		return getDistanceFromTarget(current) + getDistanceFromStart(current);

	}

	// getter
	/**
	 * True if there's no path found
	 *
	 * @return
	 */
	public boolean getNoPath() {

		return this.noPath;

	}

	/**
	 * Get the found path
	 *
	 * @return
	 */
	public ArrayList<Node> getPath() {

		return path;

	}

}
