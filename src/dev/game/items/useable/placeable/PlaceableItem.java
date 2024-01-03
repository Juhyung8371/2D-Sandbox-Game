
package dev.game.items.useable.placeable;

import java.awt.image.BufferedImage;

import dev.game.items.useable.UseableItem;

/**
 * PlaceableItem.java - item that can be placed
 *
 * @author Juhyung Kim
 */
public abstract class PlaceableItem extends UseableItem {

	/**
	 * The id of the entity that gets summoned when the item is used (placed).
	 */
	private int correspondingEntityId;

	public PlaceableItem(int id, String name, BufferedImage texture,
			int correspondingEntityId) {

		super(id, name, texture, true);

		this.correspondingEntityId = correspondingEntityId;

	}

	/**
	 * @return the correspondingEntityId
	 */
	public int getCorrespondingEntityId() {

		return correspondingEntityId;
	}

}
