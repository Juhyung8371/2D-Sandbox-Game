
package dev.game.items.useable.placeable;

import dev.game.entities.EntityId;
import dev.game.entities.statics.placeables.EntityFence;
import dev.game.entities.statics.placeables.PlaceableEntity;
import dev.game.gfx.Assets;
import dev.game.items.ItemId;

/**
 * ItemWoodenFence.java - A fence that can be used to make barrier
 *
 * @author Juhyung Kim
 */
public class ItemWoodenFence extends PlaceableItem {

	public ItemWoodenFence() {

		super(ItemId.WOODEN_FENCE, "Wooden Fence", Assets.itemFence_wood,
				EntityId.WOODEN_FENCE);

	}

	@Override
	public boolean useItem(int invIndex, PlaceableEntity entity) {

		EntityFence fenceEntity = (EntityFence) entity;

		boolean canPlace = fenceEntity.canPlace();

		if (canPlace) {

			fenceEntity.place();

			useItem_spendItem(invIndex);

		}

		return canPlace;

	}

}
