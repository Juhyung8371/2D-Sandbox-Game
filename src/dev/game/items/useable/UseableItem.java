
package dev.game.items.useable;

import java.awt.image.BufferedImage;

import dev.game.entities.statics.placeables.PlaceableEntity;
import dev.game.items.Item;

/**
 * UseableItem.java - Items that can be used by player
 *
 * @author Juhyung Kim
 */
public abstract class UseableItem extends Item {

	public UseableItem(int id, String name, BufferedImage texture,
			boolean placeable) {

		super(id, name, texture, placeable, true);

	}

	/**
	 * Use the item in the index of quick-slot. {@linkplain #useItem_spendItem(int)}
	 * method should be included.
	 *
	 * @param invIndex The index of the quick-slot in the inventory that the item is
	 *                 used (Inventory.QUICK_SLOT_INDEX ~ +4)
	 * @param entity   The entity to place. It could be null in case it does not
	 *                 involve PlaceableEntity
	 *
	 * @return true if the is used (use PlaceableEntity.canPlace()).
	 */
	public abstract boolean useItem(int invIndex, PlaceableEntity entity);

	/**
	 * The method that should be included in the
	 * {@linkplain #useItem(int, PlaceableEntity) useItem} method.
	 *
	 * @param invIndex Use the index variable from useItem method
	 */
	protected void useItem_spendItem(int invIndex) {

		player.getInventory().getItems()[invIndex].spendOneItem();

	}

}
