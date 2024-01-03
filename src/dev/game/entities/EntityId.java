
package dev.game.entities;

/**
 * EntityId.java - Id's for every entities
 *
 * @author Juhyung Kim
 */
public final class EntityId {

	public static final int PLAYER = 1;
	public static final int WOLF = 2;
	public static final int TREE = 3;
	public static final int FIRE = 4;
	public static final int STONE = 5;
	public static final int TALL_GRASS = 6;
	public static final int WOODEN_FENCE = 7;
	public static final int STONE_FENCE = 8;
	public static final int TORCH = 9;
	public static final int CRAFTING_TABLE = 10;
	public static final int ADVANCED_CRAFTING_TABLE = 11;
	public static final int TREANT = 12;
	public static final int CAVE = 13;
	public static final int GOLEM = 14;
	public static final int CAMPFIRE = 15;
	public static final int PERSON = 16;

	private EntityId() {
		// prohibiting this constructor to be called
		// no one should manipulate these id's
		throw new AssertionError("Don't touch this constructor!");

	}

}
