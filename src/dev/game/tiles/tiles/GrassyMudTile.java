
package dev.game.tiles.tiles;

import dev.game.gfx.Assets;
import dev.game.tiles.Tile;
import dev.game.tiles.TileId;

/**
 * GrassyMudTile.java - mud with grass
 *
 * @author Juhyung Kim
 */
public class GrassyMudTile extends Tile {

	public GrassyMudTile() {
		super(Assets.grassyMudTile, TileId.GRASSY_MUD, "Grassy Mud", false);
	}

}
