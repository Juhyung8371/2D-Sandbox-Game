
package dev.game.tiles;

/**
 * TileId.java - Tiles
 *
 * @author j.kim3
 */
public final class TileId {

	public static final int AIR = 0;
	public static final int STONE = 1;
	public static final int GRASS = 2;
	public static final int DIRT = 3;
	public static final int GRASSY_DIRT = 4;
	public static final int TOPSOIL = 5;
	public static final int GRAVEL = 6;
	public static final int GRASSY_GRAVEL = 7;
	public static final int WATER = 8;
	public static final int SAND = 9;
	public static final int OCHER = 10;
	public static final int MUD = 11;
	public static final int GRASSY_MUD = 12;
	public static final int SNOW = 13;

	private TileId() {

		// prohibiting this constructor to be called
		// no one should manipulate these id's
		throw new AssertionError("Don't touch this constructor!");

	}

}
