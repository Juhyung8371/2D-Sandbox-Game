
package dev.game.tiles.tiles;

import dev.game.gfx.Assets;
import dev.game.tiles.Tile;
import dev.game.tiles.TileId;

/**
 * StoneTile.java - stone tile
 *
 * @author j.kim3
 */
public class StoneTile extends Tile {

	public StoneTile() {

		super(Assets.stoneTile, TileId.STONE, "Stone", true);

	}

}
