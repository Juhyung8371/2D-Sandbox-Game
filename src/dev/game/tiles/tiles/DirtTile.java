
package dev.game.tiles.tiles;

import dev.game.gfx.Assets;
import dev.game.tiles.Tile;
import dev.game.tiles.TileId;

/**
 * DirtTile.java - dirt
 *
 * @author Juhyung Kim
 */
public class DirtTile extends Tile {

	public DirtTile() {

		super(Assets.dirtTile, TileId.DIRT, "Dirt", false);

	}

}
