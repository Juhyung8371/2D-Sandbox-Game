
package dev.game.items;

/**
 * ItemId.java - Id's for every items
 *
 * @author j.kim3
 */
public final class ItemId {

	public static final int WOOD = 1;
	public static final int STONE = 2;
	public static final int APPLE = 3;
	public static final int WOODEN_FENCE = 4;
	public static final int STONE_FENCE = 5;
	public static final int TORCH = 6;
	public static final int CRAFTING_TABLE = 7;
	public static final int ADVANCED_CRAFTING_TABLE = 8;
	public static final int CAMPFIRE = 9;

	private ItemId() {
		// prohibiting this constructor to be called
		// no one should manipulate these id's
		throw new AssertionError("Don't touch this constructor!");

	}

}
