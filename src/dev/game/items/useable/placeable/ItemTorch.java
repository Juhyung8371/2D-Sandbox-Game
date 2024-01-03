
package dev.game.items.useable.placeable;

import dev.game.entities.EntityId;
import dev.game.entities.statics.placeables.EntityTorch;
import dev.game.entities.statics.placeables.PlaceableEntity;
import dev.game.gfx.Assets;
import dev.game.items.ItemId;

/**
 * ItemTorch.java - A torch item for lighting area
 *
 * @author Juhyung Kim
 */
public class ItemTorch extends PlaceableItem {

	public ItemTorch() {

		super(ItemId.TORCH, "Torch", Assets.itemTorch, EntityId.TORCH);

	}

	@Override
	public boolean useItem(int invIndex, PlaceableEntity entity) {

		EntityTorch torchEntity = (EntityTorch) entity;

		boolean canPlace = torchEntity.canPlace();

		if (canPlace) {

			torchEntity.place();

			useItem_spendItem(invIndex);

		}

		return canPlace;

	}

}
