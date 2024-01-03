
package dev.game.entities;

import java.util.Random;

import dev.game.Handler;
import dev.game.entities.creatures.Player;
import dev.game.tiles.Tile;
import dev.game.worlds.World;

/**
 * NaturalSpawn.java - Allow entity to spawn naturally in world
 *
 * @author Juhyung Kim
 */
public class NaturalSpawn {

	// around player
	private int spawnRadius = 40;

	private int entityId;

	private Handler handler;

	private int period; // the time to wait for each spawn period

	// keep increases until it reaches the period,
	// then reset to 0 for next period
	private int coolTime;

	private boolean daySpawn, nightSpawn, worldSpawn, caveSpawn;

	private final Random RNG = new Random();

	private Player player;
	private World world;

	/**
	 * Spawn entity periodically in world
	 *
	 * @param handler
	 * @param entityId   Entity's id to spawn
	 * @param period     In tick
	 * @param daySpawn   Spawn only at day
	 * @param nightSpawn Spawn only at night
	 * @param worldSpawn Spawn only in world
	 * @param caveSpawn  Spawn only in cave
	 */
	public NaturalSpawn(Handler handler, int entityId, int period, boolean daySpawn,
			boolean nightSpawn, boolean worldSpawn, boolean caveSpawn) {

		this.handler = handler;
		this.period = period;
		this.coolTime = 0;

		this.daySpawn = daySpawn;
		this.nightSpawn = nightSpawn;
		this.worldSpawn = worldSpawn;
		this.caveSpawn = caveSpawn;

		this.entityId = entityId;

	}

	/**
	 * Spawn anywhere, any time...
	 *
	 * @param handler
	 * @param entityId
	 * @param period
	 */
	public NaturalSpawn(Handler handler, int entityId, int period) {

		this(handler, entityId, period, true, true, true, true);

	}

	public void tick() {

		if (player == null) {
			this.world = handler.getWorld();
			this.player = world.getEntityManager().getPlayer();
		}

		if (coolTime >= period) {
			coolTime = 0;
			spawn();
			return;
		}

		int worldTime = world.getWorldTime();
		int dimension = world.getDimension();

		// check the time to spawn
		if (isSpawnableTime(worldTime)) {
			if (worldSpawn && dimension == World.DIMENSION_WORLD) {
				coolTime++;
			} else if (caveSpawn && dimension == World.DIMENSION_CAVE) {
				coolTime++;
			}
		}
	}

	/**
	 * Time to Spawn!
	 */
	private void spawn() {

		// try reasonable times to spawn the entity
		for (int i = 0; i < 20; i++) {

			int x = player.getCenterX() - spawnRadius + RNG.nextInt(spawnRadius * 2);
			int y = player.getCenterY() - spawnRadius + RNG.nextInt(spawnRadius * 2);

			if (World.getTile(x, y).isSolid())
				continue;

			// block coordinate into pixel coordinate
			int xx = x * Tile.TILE_SIZE;
			int yy = y * Tile.TILE_SIZE;

			Entity ent = EntityManager.getEntityById(entityId, xx, yy);

			// do not spawn on top of each other
			if (ent.isOverlappedWithEntity(Entity.CHECK_ALL_ENTITIES)) {
				continue;
			}

			EntityManager.addEntity(ent);
			break;

		}

	}

	/**
	 * Check if the current time is good to count the cool-time for spawn.
	 *
	 * @param worldTime
	 * @return
	 */
	private boolean isSpawnableTime(int worldTime) {

		if (daySpawn && isDay(worldTime))
			return true;

		if (nightSpawn && isNight(worldTime))
			return true;

		return false;

	}

	/**
	 * Check if the time is daytime
	 *
	 * @param worldTime
	 * @return
	 */
	private boolean isDay(int worldTime) {

		return (worldTime >= World.DAY && worldTime < World.NIGHT);
	}

	/**
	 * Check if the time is nighttime
	 *
	 * @param worldTime
	 * @return
	 */
	private boolean isNight(int worldTime) {

		return (worldTime < World.DAY || worldTime >= World.NIGHT);
	}

	//// getter setter
	/**
	 * @return the spawnRadius
	 */
	public int getSpawnRadius() {

		return spawnRadius;
	}

	/**
	 * @param spawnRadius the spawnRadius to set
	 */
	public void setSpawnRadius(int spawnRadius) {

		this.spawnRadius = spawnRadius;
	}

	/**
	 * @return the entityId
	 */
	public int getEntityId() {

		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(int entityId) {

		this.entityId = entityId;
	}

	/**
	 * True if it only spawns at day
	 *
	 * @return the daySpawn
	 */
	public boolean isDaySpawn() {

		return daySpawn;
	}

	/**
	 * @param daySpawn the daySpawn to set
	 */
	public void setDaySpawn(boolean daySpawn) {

		this.daySpawn = daySpawn;
	}

	/**
	 * True if it only spawns at night
	 *
	 * @return the nightSpawn
	 */
	public boolean isNightSpawn() {

		return nightSpawn;
	}

	/**
	 * @param nightSpawn the nightSpawn to set
	 */
	public void setNightSpawn(boolean nightSpawn) {

		this.nightSpawn = nightSpawn;
	}

}
