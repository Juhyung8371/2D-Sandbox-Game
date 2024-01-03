
package dev.game.items.useable.placeable;

import dev.game.entities.EntityId;
import dev.game.entities.statics.placeables.EntityCraftingTable;
import dev.game.entities.statics.placeables.PlaceableEntity;
import dev.game.gfx.Assets;
import dev.game.items.ItemId;

/**
 * ItemAdvancedCraftingTable.java - Advanced crafting table item.
 *
 * @author Juhyung Kim
 */
public class ItemAdvancedCraftingTable extends PlaceableItem {

	public ItemAdvancedCraftingTable() {

		super(ItemId.ADVANCED_CRAFTING_TABLE, "Advanced Crafting Table",
				Assets.advanced_crafting_table, EntityId.ADVANCED_CRAFTING_TABLE);

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
