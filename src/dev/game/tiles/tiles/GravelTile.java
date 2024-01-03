
package dev.game.tiles.tiles;

import dev.game.gfx.Assets;
import dev.game.tiles.Tile;
import dev.game.tiles.TileId;

/**
 * GravelTile.java - gravel
 *
 * @author Juhyung Kim
 */
public class GravelTile extends Tile {

	public GravelTile() {

		super(Assets.gravelTile, TileId.GRAVEL, "Gravel", false);

	}

}
