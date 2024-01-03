
package dev.game.tiles.tiles;

import dev.game.gfx.Assets;
import dev.game.tiles.Tile;
import dev.game.tiles.TileId;

/**
 * AirTile.java - default tile for error case
 *
 * @author j.kim3
 */
public class AirTile extends Tile {

	public AirTile() {

		super(Assets.airTile, TileId.AIR, "Air", true);

	}

}
