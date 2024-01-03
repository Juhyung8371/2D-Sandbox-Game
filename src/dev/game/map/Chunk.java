
package dev.game.map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dev.game.entities.Entity;
import dev.game.entities.EntityId;
import dev.game.entities.EntityManager;
import dev.game.entities.statics.placeables.EntityCampfire;
import dev.game.entities.statics.placeables.EntityCraftingTable;
import dev.game.entities.statics.placeables.EntityFence;
import dev.game.entities.statics.placeables.EntityTorch;
import dev.game.entities.statics.placeables.PlaceableEntity;
import dev.game.items.Item;
import dev.game.items.ItemManager;
import dev.game.states.State;
import dev.game.tiles.Tile;
import dev.game.utils.Utils;
import dev.game.worlds.World;

/**
 * Chunk.java - A portion of the map in a 32 * 32 grid.
 *
 * @author Juhyung Kim
 */
public class Chunk {

	public static final String WORLD_CHUNK_NAME = "WORLD";
	public static final String CAVE_CHUNK_NAME = "CAVE";

	public final int dimension;

	/**
	 * The divider for Chunk name, to divide (x,y).
	 */
	public static final String CHUNK_NAME_DIVIDER = ",";
	/**
	 * A divider to separate data in Chunk file.
	 */
	public static final String DATA_DIVIDER = "!";
	/**
	 * Chunk width and hieght size.
	 */
	public static final int SIZE = 32;

	/**
	 * In chunkpos
	 */
	public int x, y;

	// TODO make private maybe
	public List<Entity> entities;
	public List<Item> items;
	public Tile[][] tiles;
	private static MapGenerator mapGenerator; // assigned in World constructor
	private final String WORLD_NAME;

	/**
	 * Format: "x,y"
	 */
	private final String index;

	/**
	 * A portion of the map in a 32 * 32 grid.
	 *
	 * @param x          left-top X coordinate of this Chunk in chunkpos
	 * @param y          left-top Y coordinate of this Chunk in chunkpos
	 * @param isNewChunk to check if initializing should be done
	 */
	private Chunk(int x, int y, String worldName, boolean isNewChunk,
			int dimension) {

		this.x = x;
		this.y = y;

		this.dimension = dimension;

		this.WORLD_NAME = worldName;
		this.index = x + CHUNK_NAME_DIVIDER + y;
		this.entities = new ArrayList<>();
		this.items = new ArrayList<>();
		this.tiles = new Tile[SIZE][SIZE];

		init(isNewChunk);

	}

	/**
	 * Place the entities according to the spawn map (from
	 * {@link dev.game.map.MapGenerator MapGenerator}).
	 */
	private void placeEntities(boolean[][] spawnMap, int[][] idMap) {

		for (int yy = 0; yy < spawnMap.length; yy++) {

			for (int xx = 0; xx < spawnMap[0].length; xx++) {

				if (!spawnMap[yy][xx]) {
					continue;
				}

				int xPos = (x * Chunk.SIZE * Tile.TILE_SIZE) + (xx * Tile.TILE_SIZE);
				int yPos = (y * Chunk.SIZE * Tile.TILE_SIZE) + (yy * Tile.TILE_SIZE);

				Entity ent = EntityManager.getEntityById(idMap[yy][xx], xPos, yPos);

				entities.add(ent);

			}

		}

	}

	/**
	 * Fill in the Chunk with data.
	 *
	 * @param isNewChunk to check if trees should be newly placed
	 */
	private void init(boolean isNewChunk) {

		if (dimension == World.DIMENSION_WORLD) {

			this.tiles = mapGenerator.generateMap(x, y);

			if (isNewChunk) {
				mapGenerator.generateWorldEntitiesMap(x, y);
				placeEntities(mapGenerator.getWorldEntityMap(),
						mapGenerator.getWorldEntityIdMap());
			}

		} else if (dimension == World.DIMENSION_CAVE) {

			this.tiles = mapGenerator.generateCaveMap(x, y);

			if (isNewChunk) {
				mapGenerator.generateCaveEntitiesMap(x, y);
				placeEntities(mapGenerator.getCaveEntityMap(),
						mapGenerator.getCaveEntityIdMap());
			}

		}

	}

	/**
	 * Save the chunk as a file. Save entities and items (not map, it can be
	 * created).
	 */
	public void saveInFile() {

		String path = State.SAVEFILE_DIR + "/" + WORLD_NAME + "/" + State.CHUNKS_DIR
				+ "/" + World.dimensionToName(dimension) + "/" + index
				+ State.EXTENSION;

		BufferedWriter bw = null;

		try {

			File file = new File(path);

			if (!file.exists()) {

				file.getParentFile().mkdirs();

				file.createNewFile();

			}

			FileOutputStream fos = new FileOutputStream(file);

			bw = new BufferedWriter(new OutputStreamWriter(fos));

			// save Entities
			for (int i = 0; i < entities.size(); i++) {

				Entity ent = entities.get(i);

				int entId = ent.getEntityID();

				// player data is saved separately by World
				if (entId == EntityId.PLAYER)
					continue;

				if (ent instanceof PlaceableEntity) {

					PlaceableEntity place = (PlaceableEntity) ent;

					// temporary entity shouldn't be saved
					if (place.isCarried())
						continue;

				}

				int entX = (int) ent.getX();
				int entY = (int) ent.getY();
				int entHealth = ent.getHealth();

				// exception for fence who needs more info
				if (entId == EntityId.WOODEN_FENCE
						|| entId == EntityId.STONE_FENCE) {

					EntityFence fence = (EntityFence) ent;
					int fenceAlign = fence.getAlignment();

					bw.write(entId + " " + entX + " " + entY + " " + entHealth + " "
							+ fenceAlign);

				} else {

					bw.write(entId + " " + entX + " " + entY + " " + entHealth);

				}

				bw.newLine();

			}

			// data separated by DATA_DIVIDER
			bw.write(DATA_DIVIDER);
			bw.newLine();

			// save Item entities
			for (int i = 0; i < items.size(); i++) {

				Item item = items.get(i);

				int itemId = item.getID();
				int itemX = item.getX();
				int itemY = item.getY();

				bw.write(itemId + " " + itemX + " " + itemY);

				bw.newLine();

			}

		} catch (IOException ex) {
			Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
		} finally {

			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ex) {
					Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null,
							ex);
				}
			}

		}
	}

	/**
	 * Find the Chunk file among files and if it doesn't exist, return a new Chunk.
	 * <p>
	 * Chunk contains Entity and Item(not inventory item) data.
	 *
	 * @param xPos      in chunk pos
	 * @param yPos
	 * @param worldName
	 * @param dimension
	 * @return
	 */
	public static Chunk getNewChunk(int xPos, int yPos, String worldName,
			int dimension) {

		return getNewChunk(Chunk.posToChunkIndex(xPos, yPos), worldName, dimension);

	}

	/**
	 * Find the Chunk file among files and if it doesn't exist, return a new Chunk.
	 * <p>
	 * Chunk contains Entity and Item(not inventory item) data.
	 *
	 * @param chunkIndex "x,y"
	 * @param worldName
	 * @param dimension
	 * @return
	 */
	public static Chunk getNewChunk(String chunkIndex, String worldName,
			int dimension) {

		String[] xy = chunkIndex.split(CHUNK_NAME_DIVIDER);
		int xx = Integer.parseInt(xy[0]);
		int yy = Integer.parseInt(xy[1]);

		Chunk chunk = getChunkFromFiles(xx, yy, worldName, dimension);

		if (chunk != null)
			return chunk;
		else
			return new Chunk(xx, yy, worldName, true, dimension);

	}

	/**
	 * Find the Chunk file among files and if it doesn't exist, return null. Chunk
	 * contains Entity and Item data.
	 *
	 * @param xPos      of chunk finding, in chunkpos
	 * @param yPos      of chunk finding, in chunkpos
	 * @param worldName
	 * @param dimension
	 * @return the Chunk or null if it doesn't exist
	 */
	public static Chunk getChunkFromFiles(int xPos, int yPos, String worldName,
			int dimension) {

		String path = State.SAVEFILE_DIR + "/" + worldName + "/" + State.CHUNKS_DIR
				+ "/" + World.dimensionToName(dimension) + "/" + xPos
				+ CHUNK_NAME_DIVIDER + yPos + State.EXTENSION;

		File file = new File(path);

		if (!file.exists())
			return null;

		Chunk newChunk = null;
		BufferedReader br = null;

		try {

			br = new BufferedReader(new FileReader(path));

			// removing the extension from the whole name, and separate "x,y" into x
			// and y
			String name = file.getName();
			String[] a = name.split("\\.");
			String b = a[0];
			String[] nameXY = b.split(CHUNK_NAME_DIVIDER);

			int xx = Integer.parseInt(nameXY[0]);
			int yy = Integer.parseInt(nameXY[1]);

			newChunk = new Chunk(xx, yy, worldName, false, dimension);

			String line;
			int index = 0;

			while ((line = br.readLine()) != null) {

				// find the data divider
				if (line.equals(DATA_DIVIDER)) {
					index++;
					continue;
				}

				// entities
				if (index == 0) {

					String[] entityData = line.split(" ");

					int entId = Integer.parseInt(entityData[0]);
					int entX = Integer.parseInt(entityData[1]);
					int entY = Integer.parseInt(entityData[2]);
					int entHealth = Integer.parseInt(entityData[3]);

					Entity newEntity = EntityManager.getEntityById(entId, entX, entY,
							entHealth);

					// PlacableEntities
					switch (entId) {

					case EntityId.WOODEN_FENCE:
					case EntityId.STONE_FENCE:

						int fenceAlign = Utils.parseInt(entityData[4]);
						EntityFence fence = (EntityFence) newEntity;
						fence.setAlignment(fenceAlign);
						fence.place();
						EntityManager.addEntity(fence);

						break;

					case EntityId.TORCH:

						EntityTorch torch = (EntityTorch) newEntity;
						torch.place();
						EntityManager.addEntity(torch);
						break;

					case EntityId.CRAFTING_TABLE:
					case EntityId.ADVANCED_CRAFTING_TABLE:

						EntityCraftingTable table = (EntityCraftingTable) newEntity;
						table.place();
						EntityManager.addEntity(table);
						break;

					case EntityId.CAMPFIRE:

						EntityCampfire camp = (EntityCampfire) newEntity;
						camp.place();
						EntityManager.addEntity(camp);
						break;

					default:

						EntityManager.addEntity(newEntity);

					}

				} else if (index == 1) { // items

					String[] itemData = line.split(" ");

					int itemId = Utils.parseInt(itemData[0]);
					int itemY = Utils.parseInt(itemData[1]);
					int itemX = Utils.parseInt(itemData[2]);

					// Somehow only 2 parameter is svaed
					switch (itemId) {

					// in case of error...
					case 0:
						break;

					default:

						Item item = Item.getItemEntity(itemId, itemY, itemX);

						ItemManager.addItem(item);
					}

				}

			}

		} catch (IOException e) {

			Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, e);

		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException ex) {
					Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null,
							ex);
				}
			}
		}

		return newChunk;

	}

	/**
	 * Get the left top corner coordinate of the chunk based on the coordinate
	 * given.
	 *
	 * @param xPos in block
	 * @param yPos
	 * @return
	 */
	public static int[] posToChunckPos(int xPos, int yPos) {

		int newX = Utils.quickFloor((float) xPos / (float) Chunk.SIZE);
		int newY = Utils.quickFloor((float) yPos / (float) Chunk.SIZE);

		return new int[] { newX, newY };

	}

	/**
	 * Convert the chunk position to its index. Format
	 * "x+Chunk.CHUNK_NAME_DIVIDER+y"
	 *
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	public static String posToChunkIndex(int xPos, int yPos) {

		return xPos + Chunk.CHUNK_NAME_DIVIDER + yPos;

	}

	/**
	 * Get the new spawnIndex
	 *
	 * @param xPos in block
	 * @param yPos in block
	 * @return
	 */
	public static String getNewSpawnIndex(int xPos, int yPos) {

		float xx = (float) xPos / (float) Chunk.SIZE;
		float yy = (float) yPos / (float) Chunk.SIZE;

		return Utils.quickFloor(xx) + CHUNK_NAME_DIVIDER + Utils.quickFloor(yy);

	}

	/**
	 * This method does not physically teleport the entity to other Chunk. This only
	 * move the entity data to the other Chunk, so the this Chunk do not have to
	 * take care of the Entity anymore.
	 * <p>
	 * This is used when the Entity move out of this Chunk (physically left this
	 * Chunk).
	 *
	 * @param entity
	 * @param chunkToMove
	 * @return If entity moving was successful, return true.
	 */
	public boolean moveEntityToChunk(Entity entity, Chunk chunkToMove) {

		int entityIndex = entities.indexOf(entity);

		// if the Entity is not in this Chunk's entity list,
		// or the Chunk to move to is null
		if (entityIndex == -1 || chunkToMove == null) {
			return false;
		}

		entities.remove(entityIndex);

		chunkToMove.entities.add(entity);

		return true;

	}

	/**
	 * Add the entity directely to this chunk.
	 *
	 * @param entity
	 * @return
	 */
	public boolean addEntityDirectly(Entity entity) {

		return entities.add(entity);

	}

	/**
	 * This method does not physically teleport the entity to other Chunk. This only
	 * move the entity data to the other Chunk, so the this Chunk do not have to
	 * take care of the Entity anymore.
	 * <p>
	 * This is used when the Entity move out of this Chunk (physically left this
	 * Chunk).
	 * <p>
	 * If World.map does not contain Chunk with given index, look thorugh files for
	 * right Chunk.
	 *
	 * @param entity
	 * @param chunkIndex
	 * @return If entity moving was successful, return true.
	 */
	public boolean moveEntityToChunk(Entity entity, String chunkIndex) {

		int entityIndex = entities.indexOf(entity);

		// if the Entity is not in this Chunk's entity list
		if (entityIndex == -1)
			return false;

		// let EntityManager take care of it
		EntityManager.addEntity(entity);

		entities.remove(entityIndex);

		return true;

	}

	/**
	 * Check if the Chunk with given index exists in file.
	 *
	 * @param index
	 * @param worldName
	 * @return
	 */
	public static boolean chunkExistsInFile(String index, String worldName) {

		String path = State.SAVEFILE_DIR + "/" + worldName + "/" + State.CHUNKS_DIR
				+ "/" + index + State.EXTENSION;

		File file = new File(path);

		return file.exists();

	}

	//////// getter
	/**
	 * Index of the Chunk, aka the name of it.
	 * <p>
	 * Format: "x,y"
	 *
	 * @return the index
	 */
	public String getIndex() {

		return this.index;
	}

	/**
	 * Check if the Entity is not in this Chunk by being away at the North of the
	 * Chunk.
	 *
	 * @param entity
	 * @return
	 */
	public boolean isAwayNorth(Entity entity) {

		return (entity.getCenterY() < this.y * Chunk.SIZE);

	}

	/**
	 * Check if the Entity is not in this Chunk by being away at the South of the
	 * Chunk.
	 *
	 * @param entity
	 * @return
	 */
	public boolean isAwaySouth(Entity entity) {

		return (entity.getCenterY() > this.y * Chunk.SIZE + Chunk.SIZE);

	}

	/**
	 * Check if the Entity is not in this Chunk by being away at the West of the
	 * Chunk.
	 *
	 * @param entity
	 * @return
	 */
	public boolean isAwayWest(Entity entity) {

		return (entity.getCenterX() < this.x * Chunk.SIZE);

	}

	/**
	 * Check if the Entity is not in this Chunk by being away at the East of the
	 * Chunk.
	 *
	 * @param entity
	 * @return
	 */
	public boolean isAwayEast(Entity entity) {

		return (entity.getCenterX() > this.x * Chunk.SIZE + Chunk.SIZE);

	}

	/**
	 * Check if the Entity if out of the Chunk in any direction.
	 *
	 * @param entity
	 * @return
	 */
	public boolean isEntityOutOfChunk(Entity entity) {

		return (isAwayEast(entity) || isAwayWest(entity) || isAwayNorth(entity)
				|| isAwaySouth(entity));

	}

	/**
	 * Set the map generator for the Chunks.
	 *
	 * @param generator
	 */
	public static void setGenerator(MapGenerator generator) {

		Chunk.mapGenerator = generator;
	}

}
