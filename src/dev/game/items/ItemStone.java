
package dev.game.items;

import dev.game.gfx.Assets;

/**
 * ItemStone.java - stone
 *
 * @author j.kim3
 */
public class ItemStone extends Item {

	public ItemStone() {
		super(ItemId.STONE, "Stone", Assets.itemStone, false, false);
	}

}
