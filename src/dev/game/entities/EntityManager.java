
package dev.game.entities;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dev.game.Game;
import dev.game.Handler;
import dev.game.entities.creatures.Creature;
import dev.game.entities.creatures.EntityPerson;
import dev.game.entities.creatures.Player;
import dev.game.entities.creatures.hostile.EntityGolem;
import dev.game.entities.creatures.hostile.EntityTreant;
import dev.game.entities.creatures.hostile.EntityWolf;
import dev.game.entities.statics.EntityCave;
import dev.game.entities.statics.EntityFire;
import dev.game.entities.statics.EntityStone;
import dev.game.entities.statics.EntityTallGrass;
import dev.game.entities.statics.EntityTree;
import dev.game.entities.statics.placeables.EntityAdvancedCraftingTable;
import dev.game.entities.statics.placeables.EntityCampfire;
import dev.game.entities.statics.placeables.EntityCraftingTable;
import dev.game.entities.statics.placeables.EntityStoneFence;
import dev.game.entities.statics.placeables.EntityTorch;
import dev.game.entities.statics.placeables.EntityWoodenFence;
import dev.game.gfx.GameCamera;
import dev.game.map.Chunk;
import dev.game.tiles.Tile;
import dev.game.utils.Utils;
import dev.game.worlds.World;

/**
 * EntityManager.java - Manager for entities
 *
 * @author j.kim3
 */
public class EntityManager {

	private NaturalSpawn spawnWolf, spawnPerson, spawnGolem;

	/**
	 * Entities are waiting to be spawned in this list until they get added to the
	 * chunk they belong
	 */
	private static List<Entity> addEntityList;

	private static Handler handler;
	private GameCamera camera;
	private final String WORLD_NAME;
	private Player player;

	/**
	 * Constructor
	 *
	 * @param theHandler
	 * @param player
	 * @param worldName
	 */
	public EntityManager(Handler theHandler, Player player, String worldName) {

		handler = theHandler;
		this.camera = handler.getGameCamera();
		this.player = player;
		addEntityList = new ArrayList<>();
		this.WORLD_NAME = worldName;

		addEntity(player);

		spawnWolf = new NaturalSpawn(handler, EntityId.WOLF, Game.FPS * 5, false,
				true, true, false);

		spawnPerson = new NaturalSpawn(handler, EntityId.PERSON, Game.FPS * 5, true,
				true, true, true);

		spawnGolem = new NaturalSpawn(handler, EntityId.GOLEM, Game.FPS * 5, true,
				true, false, true);
	}

	/**
	 * It Ticks
	 */
	public void tick() {

		spawnerTick();
		addEntitiesToChunk();

		for (int y = 0; y < World.map.length; y++) {
			for (int x = 0; x < World.map[0].length; x++) {

				Chunk currentChunk = World.map[y][x];

				// very unlikely to happen, but in case
				if (currentChunk == null)
					continue;

				List<Entity> ent = currentChunk.entities;

				Iterator<Entity> iter = ent.iterator();

				while (iter.hasNext()) {

					Entity entity = iter.next();

					// 16 seems good enough range
					if (!isEntityOutOfSight(entity, 16)) {

						if (currentChunk.isEntityOutOfChunk(entity)) {

							// to prevent player to "blink"
							// (player disappears for 1 tick and reappears)
							if (entity.getEntityID() == EntityId.PLAYER) {

								Chunk centerChunk = World.map[1][1];

								centerChunk.addEntityDirectly(entity);
								iter.remove();
								continue;

							} else {
								addEntity(entity);
								iter.remove();
								continue;
							}
						}

						entity.tick();

						if (entity instanceof Creature) {

							Creature cre = (Creature) entity;

							if (cre.shouldDie())
								iter.remove();

						} else if (!entity.isAlive()) {
							iter.remove();
						}
					}
				}
				Utils.quickSort(currentChunk.entities, 0,
						currentChunk.entities.size() - 1);
			}
		}
	}

	/**
	 * Update the spawning
	 */
	private void spawnerTick() {

		spawnWolf.tick();
		spawnPerson.tick();
		spawnGolem.tick();

	}

	public void render(Graphics gfx) {

		for (int y = 0; y < World.map.length; y++) {
			for (int x = 0; x < World.map[0].length; x++) {

				Chunk currentChunk = World.map[y][x];

				if (currentChunk == null)
					continue;

				List<Entity> ent = currentChunk.entities;

				Iterator<Entity> iter = ent.iterator();

				while (iter.hasNext()) {

					Entity entity = iter.next();

					if (!isEntityOutOfSight(entity))
						entity.render(gfx);

				}
			}
		}
	}

	/**
	 * Add an entity to world.
	 *
	 * @param ent Entity to add to world
	 */
	public static void addEntity(Entity ent) {

		String index = Chunk.getNewSpawnIndex(ent.getCenterX(), ent.getCenterY());

		ent.setChunkIndex(index);

		addEntityList.add(ent);

	}

	/**
	 * Find the right chunk for the entity to spawn. Else, get rid of it.
	 */
	private void addEntitiesToChunk() {

		Iterator<Entity> iter = addEntityList.iterator();

		label: while (iter.hasNext()) {

			Entity entity = iter.next();
			String index = entity.getChunkIndex();

			// check for the index in world chunks
			for (int y = 0; y < World.map.length; y++) {
				for (int x = 0; x < World.map[0].length; x++) {

					Chunk chunk = World.map[y][x];

					if (chunk == null)
						continue;

					if (chunk.getIndex().equals(index)) {

						chunk.entities.add(entity);
						iter.remove();

						continue label;
					}
				}
			}

			// entity is added to the chunk that player have not traveled
			// then it is considered unnecessary
			iter.remove();

		}
		// this should be empty, but in case
		addEntityList.clear();

	}

	/**
	 * Check if the entity out of sight
	 *
	 * @param ent
	 * @return
	 */
	public boolean isEntityOutOfSight(Entity ent) {

		return isEntityOutOfSight(ent, 2);

	}

	/**
	 * Check if the entity out of sight
	 *
	 * @param ent
	 * @param range
	 * @return
	 */
	public boolean isEntityOutOfSight(Entity ent, int range) {

		float xOffset = camera.getXOffset();
		float yOffset = camera.getYOffset();
		float xEnd = xOffset + Game.SCREEN_WIDTH;
		float yEnd = yOffset + Game.SCREEN_HEIGHT;

		int boundX = (int) ent.getX() - Tile.TILE_SIZE * range;
		int boundY = (int) ent.getY() - Tile.TILE_SIZE * range;
		int boundMaxX = (int) ent.getX() + Tile.TILE_SIZE * (range + 1);
		int boundMaxY = (int) ent.getY() + Tile.TILE_SIZE * (range + 1);

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

	/**
	 * Get the new Entity object by the id.
	 *
	 * @param id
	 * @param x
	 * @param y
	 * @param health
	 * @return
	 */
	public static Entity getEntityById(int id, int x, int y, int health) {

		Entity entity = getEntityById(id, x, y);

		entity.setHealth(health);

		return entity;

	}

	/**
	 * Get the new Entity object by the id. Summoning job could be done by
	 * {@linkplain #addEntity(Entity)}.
	 * <p>
	 * It is very important to add the case of new EntityId in this method.
	 *
	 * @param id Entity Id. See EntityId class. PLAYER is not allowed
	 * @param x  X position in pixel
	 * @param y  Y position in pixel
	 * @return A new Entity object
	 *
	 * @see dev.game.entities.EntityId EntityId
	 */
	public static Entity getEntityById(int id, int x, int y) {

		Entity entity = null;
		boolean isPlayer = false;

		switch (id) {

		// there should be only one player
		case EntityId.PLAYER:
			isPlayer = true;
			break;

		case EntityId.WOLF:
			entity = new EntityWolf(handler, x, y);
			break;

		case EntityId.TREE:
			entity = new EntityTree(handler, x, y);
			break;

		case EntityId.WOODEN_FENCE:
			entity = new EntityWoodenFence(handler, x, y);
			break;

		case EntityId.STONE_FENCE:
			entity = new EntityStoneFence(handler, x, y);
			break;

		case EntityId.FIRE:
			entity = new EntityFire(handler, x, y);
			break;

		case EntityId.STONE:
			entity = new EntityStone(handler, x, y);
			break;

		case EntityId.TALL_GRASS:
			entity = new EntityTallGrass(handler, x, y);
			break;

		case EntityId.TORCH:
			entity = new EntityTorch(handler, x, y);
			break;

		case EntityId.CRAFTING_TABLE:
			entity = new EntityCraftingTable(handler, x, y);
			break;

		case EntityId.ADVANCED_CRAFTING_TABLE:
			entity = new EntityAdvancedCraftingTable(handler, x, y);
			break;

		case EntityId.TREANT:
			entity = new EntityTreant(handler, x, y);
			break;

		case EntityId.CAVE:
			entity = new EntityCave(handler, x, y);
			break;

		case EntityId.GOLEM:
			entity = new EntityGolem(handler, x, y);
			break;

		case EntityId.CAMPFIRE:
			entity = new EntityCampfire(handler, x, y);
			break;

		case EntityId.PERSON:
			entity = new EntityPerson(handler, x, y);
			break;

		}

		if (entity == null && !isPlayer) {

			throw new NullPointerException(
					"\n[Error] You forgot to add the new EntityId in "
							+ "'EntityManager.getEntityById(int, int, int)' method!");

		}

		return entity;

	}

	//////////////// getter setter
	public Handler getHandler() {

		return handler;
	}

	public Player getPlayer() {

		return player;
	}

	public void setPlayer(Player player) {

		this.player = player;
	}

}
