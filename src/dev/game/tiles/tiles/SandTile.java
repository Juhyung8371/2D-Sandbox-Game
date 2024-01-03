
package dev.game.tiles.tiles;

import dev.game.gfx.Assets;
import dev.game.tiles.Tile;
import dev.game.tiles.TileId;

/**
 * SandTile.java - sand tile
 *
 * @author j.kim3
 */
public class SandTile extends Tile {

	public SandTile() {
		super(Assets.sandTile, TileId.SAND, "Sand", false);
	}

}
