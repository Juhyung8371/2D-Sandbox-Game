
package dev.game.items;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dev.game.Game;
import dev.game.Handler;
import dev.game.gfx.GameCamera;
import dev.game.map.Chunk;
import dev.game.tiles.Tile;
import dev.game.worlds.World;

/**
 * ItemManager.java - Manages items exist in the world (no business for
 * inventory items)
 *
 * @author j.kim3
 */
public class ItemManager {

	private Handler handler;

	private GameCamera camera;

	// items in queue to be spawned
	private static ArrayList<Item> addItemList;

	public ItemManager(Handler handler) {

		this.handler = handler;
		this.camera = handler.getGameCamera();

		addItemList = new ArrayList<Item>();

	}

	public void tick() {

		addItemsToChunk();

		addItemList.clear();

		for (int y = 0; y < World.map.length; y++) {

			for (int x = 0; x < World.map[0].length; x++) {

				Chunk currentChunk = World.map[y][x];

				if (currentChunk == null)
					continue;

				List<Item> items = currentChunk.items;

				Iterator<Item> iter = items.iterator();

				while (iter.hasNext()) {

					Item item = iter.next();

					if (!isItemOutOfSight(item)) {

						item.tick();

						if (item.isPickedUp()) {

							handler.getWorld().getEntityManager().getPlayer()
									.getInventory().addItem(item);

							iter.remove();

						}
					}
				}
			}
		}

	}

	/**
	 * Render items in the world
	 *
	 * @param gfx
	 */
	public void render(Graphics gfx) {

		for (int y = 0; y < World.map.length; y++) {
			for (int x = 0; x < World.map[0].length; x++) {

				Chunk currentChunk = World.map[y][x];

				if (currentChunk == null)
					continue;

				List<Item> items = currentChunk.items;

				Iterator<Item> iter = items.iterator();

				while (iter.hasNext()) {

					Item item = iter.next();

					if (!isItemOutOfSight(item)) {

						item.render(gfx);

					}
				}
			}
		}

	}

	/**
	 * Add item to queue so it could be spawned in world in order safely
	 *
	 * @param item
	 */
	public static void addItem(Item item) {

		String pos = Chunk.getNewSpawnIndex(item.getXInBlock(), item.getYInBlock());

		item.setSpawnChunkIndex(pos);

		addItemList.add(item);

	}

	/**
	 * Find the right chunk for the item to spawn.
	 */
	private void addItemsToChunk() {

		Iterator<Item> iter = addItemList.iterator();

		label: while (iter.hasNext()) {

			Item item = iter.next();

			String index = item.getSpawnChunkIndex();

			// check for the index in world chunks
			for (int y = 0; y < World.map.length; y++) {
				for (int x = 0; x < World.map[0].length; x++) {

					Chunk chunk = World.map[y][x];

					if (chunk == null)
						continue;

					if (chunk.getIndex().equals(index)) {

						item.setHandler(handler);

						chunk.items.add(item);

						iter.remove();

						continue label;

					}
				}
			}
		}
	}

	/**
	 * Check if item is within screen
	 *
	 * @param item
	 * @return
	 */
	public boolean isItemOutOfSight(Item item) {

		float xOffset = camera.getXOffset();
		float yOffset = camera.getYOffset();
		float xEnd = xOffset + Game.SCREEN_WIDTH;
		float yEnd = yOffset + Game.SCREEN_HEIGHT;

		int boundX = (int) item.getX() - Tile.TILE_SIZE;
		int boundY = (int) item.getY() - Tile.TILE_SIZE;
		int boundMaxX = (int) item.getX() + Tile.TILE_SIZE * 2;
		int boundMaxY = (int) item.getY() + Tile.TILE_SIZE * 2;

		boolean result = false;

		if (boundMaxY < yOffset) { // if entity is away to up side
			result = true;
		} else if (boundY > yEnd) { // if entity is away to down side
			result = true;
		} else if (boundMaxX < xOffset) { // if entity is away to left side
			result = true;
		} else if (boundX > xEnd) { // if entity is away to right side
			result = true;
		}

		return result;

	}

	// getter
	public Handler getHandler() {

		return this.handler;
	}

}
