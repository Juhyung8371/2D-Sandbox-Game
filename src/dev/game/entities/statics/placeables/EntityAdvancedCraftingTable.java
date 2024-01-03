
package dev.game.entities.statics.placeables;

import dev.game.Handler;
import dev.game.crafting.CraftingRecipe;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.gfx.Assets;
import dev.game.items.Item;
import dev.game.items.ItemId;
import dev.game.items.ItemManager;

/**
 * EntityAdvancedCraftingTable.java - Advanced crafting table entity with
 * crafting level of CraftingRecipe.ADVANCED_TABLE_LEVEL.
 *
 * @author Juhyung Kim
 */
public class EntityAdvancedCraftingTable extends EntityCraftingTable {

	/**
	 * Advanced crafting table entity with crafting level of
	 * CraftingRecipe.ADVANCED_TABLE_LEVEL.
	 *
	 * @param handler
	 * @param x
	 * @param y
	 * @param health
	 */
	public EntityAdvancedCraftingTable(Handler handler, float x, float y,
			int health) {

		super(handler, x, y, health, EntityName.ADVANCED_CRAFTING_TABLE);

		entityID = EntityId.ADVANCED_CRAFTING_TABLE;
		craftingLevel = CraftingRecipe.ADVANCED_TABLE_LEVEL;
		texture = Assets.advanced_crafting_table;

	}

	/**
	 * Crafting table entity with crafting level of
	 * CraftingRecipe.ADVANCED_TABLE_LEVEL.
	 * <p>
	 * Health is twice the normal crafting table.
	 *
	 * @param handler
	 * @param x
	 * @param y
	 */
	public EntityAdvancedCraftingTable(Handler handler, float x, float y) {

		this(handler, x, y, DEFAULT_HEALTH * 2);

	}

	@Override
	public void die() {

		Item loot = Item.getItemEntity(ItemId.ADVANCED_CRAFTING_TABLE, 0, 0);

		loot.setPositionInBlock(getCenterX(), getCenterY());

		ItemManager.addItem(loot);

	}

}
