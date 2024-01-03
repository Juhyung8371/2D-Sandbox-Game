
package dev.game.items.useable;

import dev.game.entities.statics.placeables.PlaceableEntity;
import dev.game.gfx.Assets;
import dev.game.items.ItemId;

/**
 * ItemApple.java - An apple item, heals 10 hp when used
 *
 * @author Juhyung Kim
 */
public class ItemApple extends UseableItem {

	public final static int HEAL_AMOUNT = 10;

	public ItemApple() {
		super(ItemId.APPLE, "Apple", Assets.itemApple, false);
	}

	@Override
	public boolean useItem(int invIndex, PlaceableEntity entity) {

		if (player.getHealth() != player.getMaxHealth()) {

			player.heal(ItemApple.HEAL_AMOUNT);

			useItem_spendItem(invIndex);

			return true;

		}

		return false;

	}

}
