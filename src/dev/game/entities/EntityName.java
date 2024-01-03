
package dev.game.entities;

/**
 * EntityName.java - name of all entities
 *
 * @author Juhyung Kim
 */
public class EntityName {

	public static final String PLAYER = "Player";
	public static final String WOLF = "Wolf";
	public static final String TREE = "Tree";
	public static final String FIRE = "Fire";
	public static final String STONE = "Stone";
	public static final String TALL_GRASS = "Tall Grass";
	public static final String WOODEN_FENCE = "Wooden Fence";
	public static final String STONE_FENCE = "Stone Fence";
	public static final String TORCH = "Torch";
	public static final String CRAFTING_TABLE = "Crafting Table";
	public static final String ADVANCED_CRAFTING_TABLE = "Advanced Crafting Table";
	public static final String TREANT = "Treant";
	public static final String CAVE = "Cave";
	public static final String GOLEM = "Golem";
	public static final String CAMPFIRE = "Campfire";
	public static final String PERSON = "Person";

	private EntityName() {

		// prohibiting this constructor to be called
		// no one should manipulate these id's
		throw new AssertionError("Don't touch this constructor!");

	}

}
