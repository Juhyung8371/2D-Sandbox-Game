
package dev.game.entities.statics;

import java.awt.Graphics;
import java.util.Random;

import dev.game.Handler;
import dev.game.entities.EntityId;
import dev.game.entities.EntityManager;
import dev.game.entities.EntityName;
import dev.game.entities.creatures.hostile.EntityTreant;
import dev.game.gfx.Assets;
import dev.game.items.Item;
import dev.game.items.ItemId;
import dev.game.items.ItemManager;
import dev.game.tiles.Tile;

/**
 * EntityTree.java - Tree
 *
 * @author j.kim3
 */
public class EntityTree extends StaticEntity {

	/**
	 * True if this tree turns into EntityTreant when attacked.
	 */
	public boolean isTreant;

	public EntityTree(Handler handler, float x, float y) {

		super(handler, EntityId.TREE, x, y, Tile.TILE_SIZE, Tile.TILE_SIZE * 2, 4,
				EntityName.TREE);

		setBounds(16, 80, 32, 48);

		// 10% chance of becoming a monster!
		isTreant = (new Random().nextInt(10) == 0);

	}

	/**
	 * Drop and apple and a wood
	 */
	@Override
	public void die() {

		if (isTreant)
			return;

		Item loot = Item.getItemEntity(ItemId.WOOD, 0, 0);

		loot.setPositionInBlock(getCenterX(), getCenterY());

		ItemManager.addItem(loot);

		Item loot2 = Item.getItemEntity(ItemId.APPLE, 0, 0);
		loot2.setPositionInBlock(getCenterX(), getCenterY() - 1);

		ItemManager.addItem(loot2);

	}

	@Override
	public void tick() {

		if (!isTreant)
			return;

		// someone have angered the Treant!
		if (health != maxHealth) {

			kill();
			// Treant is bigger than normal tree
			EntityTreant treant = new EntityTreant(handler, x - 24, y - 26);
			EntityManager.addEntity(treant);

		}

	}

	@Override
	public void render(Graphics gfx) {

		gfx.drawImage(Assets.tree, (int) (x - camera.getXOffset()),
				(int) (y - camera.getYOffset()), width, height, null);

	}

}
