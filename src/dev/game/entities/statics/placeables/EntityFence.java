
package dev.game.entities.statics.placeables;

import java.awt.Rectangle;

import dev.game.Handler;
import dev.game.entities.creatures.Creature;
import dev.game.items.Item;
import dev.game.items.ItemId;
import dev.game.items.ItemManager;
import dev.game.tiles.Tile;
import dev.game.utils.Utils;

/**
 * EntityFence.java - fence
 *
 * @author j.kim3
 */
public abstract class EntityFence extends PlaceableEntity {

	/**
	 * Alignment of the entity is extended
	 */
	public static final int UP_DOWN = 0, LEFT_RIGHT = 1;

	// direction the fence is extended to
	protected int alignment;
	private int oldAlignment;

	public EntityFence(Handler handler, int id, float x, float y, int health,
			String name) {

		super(handler, id, x, y, Tile.TILE_SIZE, Tile.TILE_SIZE * 3, health, name);

		this.alignment = UP_DOWN; // default
		this.oldAlignment = 10; // default (something very wrong to call the
								// updateBounds())

	}

	@Override
	public void tick() {

		if (player == null) {
			this.player = handler.getWorld().getEntityManager().getPlayer();
		}

		followPlayer();

		updateSize();

		updateBounds();

	}

	@Override
	public void die() {

		Item loot = Item.getItemEntity(ItemId.WOODEN_FENCE, 0, 0);

		loot.setPositionInBlock(getCenterX(), getCenterY());

		ItemManager.addItem(loot);

	}

	/**
	 * Player is carrying fence to find the place to place this fence
	 * <p>
	 * So, fence should follow player (stay in front of player)
	 */
	@Override
	public void followPlayer() {

		if (!isCarried) {
			return;
		}

		int dir = player.getDirection();

		// fence direction is the opposite of the player direction
		if (dir == Creature.UP || dir == Creature.DOWN) {

			alignment = LEFT_RIGHT;

		} else {

			alignment = UP_DOWN;

		}

		switch (dir) {
		case Creature.UP:
			setX(player.getX() - 64);
			setY(player.getY() - 64);
			break;
		case Creature.LEFT:
			setX(player.getX() - 32);
			setY(player.getY() - 64);
			break;
		case Creature.DOWN:
			setX(player.getX() - 64);
			setY(player.getY() + 32);
			break;
		case Creature.RIGHT:
			setX(player.getX() + 32);
			setY(player.getY() - 64);
			break;
		default:
			break;
		}

	}

	/**
	 * Update the width and height of the fence depend on its direction
	 */
	@Override
	public void updateSize() {

		if (!isCarried) {
			return;
		}

		if (alignment == UP_DOWN) {

			setWidth(Tile.TILE_SIZE);
			setHeight(Tile.TILE_SIZE * 3);

		} else {

			setWidth(Tile.TILE_SIZE * 3);
			setHeight(Tile.TILE_SIZE);

		}

	}

	/**
	 * Place the fence
	 */
	@Override
	public void place() {

		updateSize();

		isCarried = false;

		updateBounds();

		noCollision = false;

		texture = Utils.setAlpha(texture, 255);

	}

	@Override
	protected Rectangle getNewBounds() {

		Rectangle rect = new Rectangle();

		if (alignment == UP_DOWN) {

			rect.x = 24;
			rect.y = 0;
			rect.width = 16;
			rect.height = Tile.TILE_SIZE * 3;

		} else {

			rect.x = 2;
			rect.y = 28;
			rect.width = Tile.TILE_SIZE * 3 - 4;
			rect.height = Tile.TILE_SIZE - 32;

		}

		return rect;

	}

	/**
	 * Update the <code>placeBounds </code>.
	 */
	private void updateBounds() {

		if (oldAlignment != alignment) {

			setBounds(getNewBounds());
			oldAlignment = alignment;

		}

	}

	// getter setter ////
	public int getAlignment() {

		return alignment;
	}

	public void setAlignment(int alignment) {

		this.alignment = alignment;
	}

}
