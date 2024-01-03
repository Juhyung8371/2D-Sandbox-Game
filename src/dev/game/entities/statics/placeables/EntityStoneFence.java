
package dev.game.entities.statics.placeables;

import java.awt.Graphics;

import dev.game.Handler;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.gfx.Assets;
import dev.game.items.Item;
import dev.game.items.ItemId;
import dev.game.items.ItemManager;
import dev.game.utils.Utils;

/**
 * EntityStoneFence.java - Stone fence
 *
 * @author Juhyung Kim
 */
public class EntityStoneFence extends EntityFence {

	public EntityStoneFence(Handler handler, float x, float y) {

		super(handler, EntityId.STONE_FENCE, x, y, 100, EntityName.STONE_FENCE);

		texture = Assets.fence_stone[UP_DOWN]; // default
	}

	@Override
	public void render(Graphics gfx) {

		texture = (alignment == UP_DOWN) ? Assets.fence_stone[UP_DOWN]
				: Assets.fence_stone[LEFT_RIGHT];

		if (isCarried)
			texture = Utils.setAlpha(texture, 150);

		gfx.drawImage(texture, (int) (x - camera.getXOffset()),
				(int) (y - camera.getYOffset()), width, height, null);

	}

	@Override
	public void die() {

		Item loot = Item.getItemEntity(ItemId.STONE_FENCE, 0, 0);

		loot.setPositionInBlock(getCenterX(), getCenterY());

		ItemManager.addItem(loot);

	}

}
