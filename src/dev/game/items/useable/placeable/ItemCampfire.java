
package dev.game.items.useable.placeable;

import dev.game.entities.EntityId;
import dev.game.entities.statics.placeables.EntityCampfire;
import dev.game.entities.statics.placeables.PlaceableEntity;
import dev.game.gfx.Assets;
import dev.game.items.ItemId;

/**
 * ItemCampfire.java - camp fire item
 *
 * @author Juhyung Kim
 */
public class ItemCampfire extends PlaceableItem {

	public ItemCampfire() {

		super(ItemId.CAMPFIRE, "Campfire", Assets.campfire, EntityId.CAMPFIRE);

	}

	@Override
	public boolean useItem(int invIndex, PlaceableEntity entity) {

		EntityCampfire campEntity = (EntityCampfire) entity;

		boolean canPlace = campEntity.canPlace();

		if (canPlace) {

			campEntity.place();

			useItem_spendItem(invIndex);

		}

		return canPlace;

	}

}
