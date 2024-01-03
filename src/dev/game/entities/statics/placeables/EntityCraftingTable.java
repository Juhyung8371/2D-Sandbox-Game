
package dev.game.entities.statics.placeables;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import dev.game.Handler;
import dev.game.crafting.CraftingManager;
import dev.game.crafting.CraftingRecipe;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.entities.creatures.Creature;
import dev.game.gfx.Assets;
import dev.game.items.Item;
import dev.game.items.ItemId;
import dev.game.items.ItemManager;
import dev.game.tiles.Tile;
import dev.game.utils.Utils;

/**
 * EntityCraftingTable.java - Crafting table entity with crafting level of
 * CraftingRecipe.TABLE_LEVEL.
 *
 * @author Juhyung Kim
 */
public class EntityCraftingTable extends PlaceableEntity {

	public final static int DEFAULT_HEALTH = 8;

	/**
	 * crafting level when opening the crafting screen.
	 */
	protected int craftingLevel = CraftingRecipe.TABLE_LEVEL;

	private boolean opened;

	private CraftingManager craftingManager;

	/**
	 * Crafting table entity with crafting level of CraftingRecipe.TABLE_LEVEL.
	 *
	 * @param handler
	 * @param x
	 * @param y
	 * @param name
	 * @param health
	 */
	protected EntityCraftingTable(Handler handler, float x, float y, int health,
			String name) {

		super(handler, EntityId.CRAFTING_TABLE, x, y, Tile.TILE_SIZE, Tile.TILE_SIZE,
				health, name);

		texture = Assets.crafting_table;

	}

	/**
	 * Crafting table entity with crafting level of CraftingRecipe.TABLE_LEVEL.
	 * <p>
	 * Health is 8.
	 *
	 * @param handler
	 * @param x
	 * @param y
	 */
	public EntityCraftingTable(Handler handler, float x, float y) {

		this(handler, x, y, DEFAULT_HEALTH, EntityName.CRAFTING_TABLE);

	}

	@Override
	public void tick() {

		if (player == null) {

			this.player = handler.getWorld().getEntityManager().getPlayer();

			craftingManager = player.getCraftingManager();

		}

		if (opened) {

			if (!craftingManager.isOpened()) {
				opened = false;
			}

		}

		followPlayer();

		updateSize();

		openCraftingScreen();

	}

	@Override
	public void render(Graphics gfx) {

		if (isCarried && updateTexture) {

			updateTexture = false;
			texture = Utils.setAlpha(texture, 150);

		}

		gfx.drawImage(texture, (int) (x - camera.getXOffset()),
				(int) (y - camera.getYOffset()), width, height, null);

	}

	@Override
	public void followPlayer() {

		if (!isCarried)
			return;

		int dir = player.getDirection();

		switch (dir) {
		case Creature.UP:
			setX(player.getX());
			setY(player.getY() - 64);
			break;
		case Creature.LEFT:
			setX(player.getX() - 64);
			setY(player.getY());
			break;
		case Creature.DOWN:
			setX(player.getX());
			setY(player.getY() + 64);
			break;
		case Creature.RIGHT:
			setX(player.getX() + 64);
			setY(player.getY());
			break;
		default:
			break;
		}

	}

	@Override
	public void updateSize() {
		// no size update needed
	}

	@Override
	public void place() {

		isCarried = false;

		texture = Utils.setAlpha(texture, 255);

		noCollision = false;

	}

	@Override
	public void die() {

		Item loot = Item.getItemEntity(ItemId.CRAFTING_TABLE, 0, 0);

		loot.setPositionInBlock(getCenterX(), getCenterY());

		ItemManager.addItem(loot);

	}

	@Override
	protected Rectangle getNewBounds() {

		return new Rectangle(4, 4, 56, 56);

	}

	// class stuff ///
	/**
	 * Open crafting table, when the player have pressed space key near the table.
	 */
	public void openCraftingScreen() {

		if (isCarried || opened || player.getInventory().isOpened()) {
			return;
		}

		if (handler.getGame().getKeyManager().keyJustPressed(KeyEvent.VK_SPACE)) {

			int dir = player.getDirection();

			Rectangle rangeBounds = new Rectangle();

			rangeBounds.width = 48;
			rangeBounds.height = 48;

			int px = (int) player.getX();
			int py = (int) player.getY();

			if (dir == Creature.UP) {

				rangeBounds.x = px + 8;
				rangeBounds.y = py - 56;

			} else if (dir == Creature.LEFT) {

				rangeBounds.x = px - 56;
				rangeBounds.y = py + 8;

			} else if (dir == Creature.DOWN) {

				rangeBounds.x = px + 8;
				rangeBounds.y = py + 72;

			} else if (dir == Creature.RIGHT) {

				rangeBounds.x = px + 72;
				rangeBounds.y = py + 8;

			}

			// open!
			if (getBounds(0, 0).intersects(rangeBounds)) {

				opened = true;
				craftingManager.openWithTable(craftingLevel);

			}

		}

	}

	// getter////////
	/**
	 * Is this table opened.
	 *
	 * @return
	 */
	public boolean isOpened() {

		return opened;

	}

	/**
	 * @return the craftingLevel
	 */
	public int getCraftingLevel() {

		return craftingLevel;
	}

}
