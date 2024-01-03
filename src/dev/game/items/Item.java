
package dev.game.items;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import dev.game.Game;
import dev.game.Handler;
import dev.game.entities.creatures.Player;
import dev.game.gfx.GameCamera;
import dev.game.inventory.Inventory;
import dev.game.items.useable.ItemApple;
import dev.game.items.useable.placeable.ItemAdvancedCraftingTable;
import dev.game.items.useable.placeable.ItemCampfire;
import dev.game.items.useable.placeable.ItemCraftingTable;
import dev.game.items.useable.placeable.ItemStoneFence;
import dev.game.items.useable.placeable.ItemTorch;
import dev.game.items.useable.placeable.ItemWoodenFence;
import dev.game.tiles.Tile;

/**
 * Item.java - Base Item class
 *
 * @author Juhyung Kim
 */
public class Item {

	/**
	 * Max item Id possible, just in case of error
	 */
	public static final int MAX_ITEMID = 100;

	public static final int MAX_STACK = 99;

	public static final int ITEM_WIDTH = 40, ITEM_HEIGHT = 40;

	protected Handler handler;
	protected GameCamera camera;
	private BufferedImage texture;
	private String name;
	private int id;

	// in pixel
	private int x, y;

	private int itemCount;
	private boolean pickedUp = false;

	private boolean placeable;

	// bound to check if player is close enough
	private Rectangle bounds = null;

	// for pickup checking and interactions
	protected Player player;

	private int idleIndex = -1; // index for item idling animation

	// true for the item that can be used (spent, consumed etc)
	protected boolean isUseable = false;

	/**
	 * When item is spawned, it's index of chunk to spawn. The value is assigned in
	 * ItemManager.addItem(Item)
	 */
	protected String spawnChunkIndex;

	/**
	 * Default count is 1.
	 *
	 * @param id
	 * @param name
	 * @param texture
	 * @param placeable
	 * @param isUseable
	 */
	protected Item(int id, String name, BufferedImage texture, boolean placeable,
			boolean isUseable) {

		this.texture = texture;
		this.name = name;
		this.id = id;
		this.itemCount = 1;
		this.placeable = placeable;
		this.isUseable = isUseable;

	}

	/**
	 * Idle the item in world
	 * <p>
	 * To make it look lively
	 */
	private void itemIdle() {

		idleIndex++;

		// calling only 5 times a sec
		if (idleIndex % (Game.FPS / 5) != 0)
			return;

		if (idleIndex <= Game.FPS) {
			y++;
		} else if (idleIndex > Game.FPS) {
			y--;
		}

		if (idleIndex > Game.FPS * 2)
			idleIndex = -1;

	}

	/**
	 * check if the item is picked up
	 */
	private void checkPickUp() {

		// checking bound here because in case this item is not a physical item,
		// which will not have x, y to get bounds.
		if (bounds == null) {
			setBounds();
		}

		if (player == null)
			player = handler.getWorld().getEntityManager().getPlayer();

		// creating the collision box
		// and check if player is within the pickup range
		if (bounds.intersects(player.getBounds(0, 0))) {

			Item[] theItems = player.getInventory().getItems();

			// only check inventory slot and quick-slot part of inventory
			for (int i = 2; i < Inventory.INV_SIZE + Inventory.QUICK_SIZE + 2; i++) {

				Item theItem = theItems[i];

				if (theItem == null) {

					setPickedUp(true);

					return;

				}

				// if there is already a same item in the inventory,
				// just add them together
				if (theItem.getID() == id) {

					setPickedUp(true);
					return;

				}
			}
		}
	}

	public void tick() {

		checkPickUp();
		itemIdle();

	}

	/**
	 * Render the item in the game (world)
	 *
	 * @param gfx
	 */
	public void render(Graphics gfx) {

		if (getHandler() == null) {
			return;
		}

		gfx.drawImage(texture, (int) (x - camera.getXOffset()),
				(int) (y - camera.getYOffset()), null);

	}

	/**
	 * Get the item with the id > 0, invalid id gives null.
	 *
	 * @see dev.game.items.ItemId
	 *
	 * @param id
	 * @return
	 */
	public static Item getItem(int id) {

		if (id < 1)
			return null;

		switch (id) {

		case ItemId.WOOD:
			return new ItemWood();

		case ItemId.STONE:
			return new ItemStone();

		case ItemId.APPLE:
			return new ItemApple();

		case ItemId.WOODEN_FENCE:
			return new ItemWoodenFence();

		case ItemId.STONE_FENCE:
			return new ItemStoneFence();

		case ItemId.TORCH:
			return new ItemTorch();

		case ItemId.CRAFTING_TABLE:
			return new ItemCraftingTable();

		case ItemId.ADVANCED_CRAFTING_TABLE:
			return new ItemAdvancedCraftingTable();

		case ItemId.CAMPFIRE:
			return new ItemCampfire();

		}
		return null;
	}

	/**
	 * Get an Item object that does not effect the world but exist as data. DO NOT
	 * ADD THIS TO THE WORLD!
	 *
	 * @param theId
	 * @param count
	 * @return
	 */
	public static Item getItem(int theId, int count) {

		Item item = Item.getItem(theId);

		if (item != null)
			item.setItemCount(count);

		return item;
	}

	/**
	 * Create item that's going to go right into the inventory
	 *
	 * @param theId
	 * @param count
	 * @param thePlayer
	 * @return
	 */
	public static Item getItemForInv(int theId, int count, Player thePlayer) {

		Item item = Item.getItem(theId, count);

		if (item != null) {
			item.setPickedUp(true);
			item.setPlayer(thePlayer);
		}

		return item;
	}

	/**
	 * Create item to spawn in world, add this to world with ItemManager to spawn.
	 *
	 * @param theId could use ItemId class
	 * @param theX  in pixel
	 * @param theY  in pixel
	 * @return
	 */
	public static Item getItemEntity(int theId, int theX, int theY) {

		Item item = Item.getItem(theId);

		item.setPosition(theX, theY);

		return item;
	}

	/**
	 * Decrease the item count by one
	 */
	public void spendOneItem() {

		setItemCount(itemCount - 1);

	}

	////////// getter setter/////////
	private void setBounds() {

		bounds = new Rectangle((int) x, (int) y, Item.ITEM_WIDTH, Item.ITEM_HEIGHT);
	}

	/**
	 * Set pos in pixel
	 *
	 * @param x in pixel
	 * @param y in pixel
	 */
	public void setPosition(int x, int y) {

		this.x = x;
		this.y = y;

	}

	/**
	 * Set pos in block
	 *
	 * @param x
	 * @param y
	 */
	public void setPositionInBlock(int x, int y) {

		this.x = (x * Tile.TILE_SIZE) + (Tile.TILE_SIZE / 2) - (Item.ITEM_WIDTH / 2);
		this.y = (y * Tile.TILE_SIZE) + (Tile.TILE_SIZE / 2)
				- (Item.ITEM_HEIGHT / 2);
	}

	/**
	 * @return the handler
	 */
	public Handler getHandler() {

		return handler;
	}

	/**
	 * Set the handler for the item. Item start with no handler, so this is required
	 * when spawning item in world.
	 *
	 * @param handler the handler to set
	 */
	public void setHandler(Handler handler) {

		this.handler = handler;
		this.camera = handler.getGameCamera();

	}

	/**
	 * @return the texture
	 */
	public BufferedImage getTexture() {

		return texture;
	}

	/**
	 * @param texture the texture to set
	 */
	public void setTexture(BufferedImage texture) {

		this.texture = texture;
	}

	/**
	 * @return the name
	 */
	public String getName() {

		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * @return the ID
	 */
	public int getID() {

		return id;
	}

	/**
	 * @param ID the ID to set
	 */
	public void setID(int ID) {

		this.id = ID;
	}

	/**
	 * @return the x in pixel
	 */
	public int getX() {

		return x;
	}

	/**
	 * @param x the x to set in pixel
	 */
	public void setX(int x) {

		this.x = x;
	}

	public int getXInBlock() {

		return (int) (x / Tile.TILE_SIZE);
	}

	public void setXInBlock(int x) {

		this.x = (int) (x * Tile.TILE_SIZE);
	}

	/**
	 * @return the y in pixel
	 */
	public int getY() {

		return y;
	}

	/**
	 * @param y the y to set in pixel
	 */
	public void setY(int y) {

		this.y = y;
	}

	public int getYInBlock() {

		return (int) (y / Tile.TILE_SIZE);
	}

	public void setYInBlock(int y) {

		this.y = (int) (y * Tile.TILE_SIZE);
	}

	/**
	 * @return the itemCount
	 */
	public int getItemCount() {

		return itemCount;

	}

	/**
	 * @param itemCount the itemCount to set
	 */
	public void setItemCount(int itemCount) {

		if (itemCount < 0) {
			itemCount = 0;
		}

		this.itemCount = itemCount;
	}

	public boolean isPickedUp() {

		return pickedUp;
	}

	/**
	 * @param pickedUp the pickedUp to set
	 */
	public void setPickedUp(boolean pickedUp) {

		this.pickedUp = pickedUp;
	}

	/**
	 * True if this item extends
	 * {@linkplain #dev.juhyung.game.items.useable.placeable.PlaceableItem(int, String, BufferedImage, int)}
	 *
	 * @return
	 */
	public boolean isPlaceable() {

		return placeable;
	}

	/**
	 * Check if item count is zero or lower
	 *
	 * @return
	 */
	public boolean isEmpty() {

		return (itemCount < 1);

	}

	/**
	 * @return the isUseable
	 */
	public boolean isUseable() {

		return isUseable;
	}

	public void setPlayer(Player player) {

		this.player = player;
	}

	/**
	 * When Item is spawned, it's index of chunk to spawn. The value is assigned in
	 * ItemManager.addItem(Item)
	 *
	 * @return
	 */
	public String getSpawnChunkIndex() {
		return spawnChunkIndex;
	}

	/**
	 * When Item is spawned, it's index of chunk to spawn. The value is assigned in
	 * ItemManager.addItem(Item)
	 *
	 * @param spawnChunkIndex the chunkIndex to set
	 */
	public void setSpawnChunkIndex(String spawnChunkIndex) {
		this.spawnChunkIndex = spawnChunkIndex;
	}

}
