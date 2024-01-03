
package dev.game.tiles.tiles;

import dev.game.gfx.Assets;
import dev.game.tiles.Tile;
import dev.game.tiles.TileId;

/**
 * GrassTile.java - grass tile
 *
 * @author j.kim3
 */
public class GrassTile extends Tile {

	public GrassTile() {

		super(Assets.grassTile, TileId.GRASS, "Grass", false);
	}

}
