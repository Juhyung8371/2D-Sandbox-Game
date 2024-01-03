
package dev.game.ai.pathfinding;

import dev.game.tiles.Tile;

/**
 * Node.java - The position node for path-finding
 *
 * @author j.kim3
 */
public class Node {

	private Node parentPath; // the parent to track the path later

	private int x, y; // in blocks

	// F value
	// H value + G value
	// Lower the closer to the target
	private int moveCost;

	public Node(int x, int y, int moveCost) {

		parentPath = null;

		this.x = x;
		this.y = y;

		this.moveCost = moveCost;

	}

	@Override
	public String toString() {
		return x + ":" + y;
	}

	// getter setter
	public Node getParentPath() {
		return parentPath;
	}

	public void setParentPath(Node parentPath) {
		this.parentPath = parentPath;
	}

	public int getX() {
		return x;
	}

	public int getXInPixel() {
		return x * Tile.TILE_SIZE;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public int getYInPixel() {
		return y * Tile.TILE_SIZE;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getMoveCost() {
		return moveCost;
	}

	public void setMoveCost(int moveCost) {
		this.moveCost = moveCost;
	}

	public boolean equals(Node obj) {

		int ox = obj.x;
		int oy = obj.y;

		return ((this.x == ox) && (this.y == oy));

	}

}
