
package dev.game.items.useable.placeable;

import dev.game.entities.EntityId;
import dev.game.entities.statics.placeables.EntityCraftingTable;
import dev.game.entities.statics.placeables.PlaceableEntity;
import dev.game.gfx.Assets;
import dev.game.items.ItemId;

/**
 * ItemCraftingTable.java - Crafting table item.
 *
 * @author Juhyung Kim
 */
public class ItemCraftingTable extends PlaceableItem {

	public ItemCraftingTable() {

		super(ItemId.CRAFTING_TABLE, "Crafting Table", Assets.crafting_table,
				EntityId.CRAFTING_TABLE);

	}

	@Override
	public boolean useItem(int invIndex, PlaceableEntity entity) {

		EntityCraftingTable table = (EntityCraftingTable) entity;

		boolean canPlace = table.canPlace();

		if (canPlace) {

			table.place();

			useItem_spendItem(invIndex);

		}

		return canPlace;

	}

}
