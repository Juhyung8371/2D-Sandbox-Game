
package dev.game.entities.statics;

import java.awt.Graphics;

import dev.game.Handler;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.gfx.Assets;
import dev.game.items.Item;
import dev.game.items.ItemId;
import dev.game.items.ItemManager;
import dev.game.tiles.Tile;

/**
 * EntityStone.java - stone
 *
 * @author j.kim3
 */
public class EntityStone extends StaticEntity {

	public EntityStone(Handler handler, float x, float y) {
		super(handler, EntityId.STONE, x, y, Tile.TILE_SIZE, Tile.TILE_SIZE, 12,
				EntityName.STONE);

		setBounds(12, 12, 40, 40);
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics gfx) {

		// set opacity depend on surrounding
		gfx.drawImage(Assets.itemStone, (int) (x - camera.getXOffset()),
				(int) (y - camera.getYOffset()), width, height, null);

	}

	@Override
	public void die() {

		Item loot = Item.getItemEntity(ItemId.STONE, 0, 0);

		loot.setPositionInBlock(getCenterX(), getCenterY());

		ItemManager.addItem(loot);

	}

}
