
package dev.game.entities.statics;

import java.awt.Graphics;

import dev.game.Handler;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.gfx.Assets;
import dev.game.tiles.Tile;

/**
 * EntityTallGrass.java - Tall grass
 *
 * @author j.kim3
 */
public class EntityTallGrass extends StaticEntity {

	public EntityTallGrass(Handler handler, float x, float y) {

		super(handler, EntityId.TALL_GRASS, x, y, Tile.TILE_SIZE, Tile.TILE_SIZE, 1,
				EntityName.TALL_GRASS);

		setBounds(12, 22, 40, 40);

		noCollision = true;

	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics gfx) {

		gfx.drawImage(Assets.tallGrass, (int) (x - camera.getXOffset()),
				(int) (y - camera.getYOffset()), width, height, null);

	}

	@Override
	public void die() {

		// nothing
	}

}
