
package dev.game.worlds;

import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import dev.game.Game;
import dev.game.Handler;
import dev.game.entities.EntityManager;
import dev.game.entities.creatures.Player;
import dev.game.items.ItemManager;
import dev.game.lights.LightManager;
import dev.game.map.Chunk;
import dev.game.map.MapGenManager;
import dev.game.map.MapGenerator;
import dev.game.particle.ParticleManager;
import dev.game.sounds.Sound;
import dev.game.states.State;
import dev.game.tiles.Tile;
import dev.game.tiles.TileManager;
import dev.game.utils.Utils;

/**
 * The world (map).
 * <p>
 * 
 * @author Juhyung Kim
 */
public class World {

	protected Handler handler;

	/**
	 * World name
	 */
	public final String WORLD_NAME;
	/**
	 * World path, including / at the end
	 */
	public final String WORLD_PATH;

	/**
	 * Dimension
	 */
	public static final int DIMENSION_WORLD = 0, DIMENSION_CAVE = 1;

	private static int dimension;

	// managers
	protected EntityManager entityManager;
	protected ItemManager itemManager;
	protected static TileManager tileManager;
	protected LightManager lightManager;
	protected ParticleManager particleManager;

	// 5 minutes for each phase
	protected static final int TIME_PERIOD = Game.FPS * 300;

	// MIDNIGHT = the point to turn back to DANW
	public static final int DAWN = 0, DAY = TIME_PERIOD, EVENING = DAY + TIME_PERIOD,
			NIGHT = EVENING + TIME_PERIOD, MIDNIGHT = NIGHT + TIME_PERIOD;

	// how much brightness is (+/-)'ed per each minute
	public final static int BRIGHTNESS_FACTOR = (Tile.DEFAULT_BRIGHTNESS
			- Tile.LOWEST_BRIGHTNESS) / 5;

	// the current brightness in the world
	private static int worldBrightness = Tile.DEFAULT_BRIGHTNESS;

	/**
	 * time passed in world in tick
	 */
	protected static int worldTime = DAY;

	/**
	 * Counter for saving player data
	 */
	private int saveCounter = 0;

	/**
	 * Interval to save the player data in tick
	 */
	private final int SAVE_INTERVAL = Game.FPS / 2;

	/**
	 * The 3 * 3 Chunks around the player
	 */
	public static Chunk[][] map = new Chunk[3][3];

	private Player player;

	private MapGenManager mapGenManager;

	public World(Handler handler, MapGenerator generator) {

		this.handler = handler;

		// just like setting handler for the world, set the map gen for Chunks
		Chunk.setGenerator(generator);

		map[0] = new Chunk[3];
		map[1] = new Chunk[3];
		map[2] = new Chunk[3];

		itemManager = new ItemManager(handler);

		WORLD_NAME = generator.getWorldName();
		WORLD_PATH = State.SAVEFILE_DIR + "/" + WORLD_NAME + "/";

		int[] playerData = getPlayerData();

		// default
		this.dimension = World.DIMENSION_WORLD;

		Player tempPlayer = new Player(handler, 100, 100);

		if (playerData != null) {
			this.dimension = playerData[3];
			worldTime = playerData[4];

			tempPlayer.setX(playerData[0]);
			tempPlayer.setY(playerData[1]);
			tempPlayer.setHealth(playerData[2]);
		}

		if (this.dimension == World.DIMENSION_WORLD) {
			Sound.stopAll();
			if ((worldTime >= DAY && worldTime < EVENING)) {
				Sound.DAY_BGM.loop();
			} else {
				Sound.NIGHT_BGM.loop();
			}
		}

		entityManager = new EntityManager(handler, tempPlayer, WORLD_NAME);

		player = entityManager.getPlayer();

		handler.getGameCamera().centerOnEntity(player);

		// map loading is usually the mapGenManager's job,
		// but the initial map loading will be done in the game main thread
		loadChunks();

		particleManager = new ParticleManager(handler);

		// important this is under the player assignment
		mapGenManager = new MapGenManager(handler, WORLD_NAME);

	}

	public void tick() {
		// order matters
		mapGenManager.tick();
		timeCycle();
		tileManager.tick();
		lightManager.tick();
		itemManager.tick();
		entityManager.tick();
		particleManager.tick();
	}

	public void render(Graphics gfx) {

		tileManager.render(gfx);
		itemManager.render(gfx);
		particleManager.render(gfx);
		entityManager.render(gfx);

		// light manager have to be rendered over everything
		// thus render last
		lightManager.render(gfx);

		savePlayerDataPeriocially();

	}

	/**
	 * Control the day/night cycle, only on the World dimension
	 * <p>
	 * When it's DAWN, the world get gradually brighter, when it's EVENING, the
	 * world get gradually darker
	 * <p>
	 * Each period is 5 minutes long, and the light level change happen every 1
	 * minute.
	 * <p>
	 */
	private static void timeCycle() {

		// there is no day or night in cave
		if (dimension != World.DIMENSION_WORLD)
			return;

		worldTime++;

		// do brightness update each minute
		if (worldTime % (Game.FPS * 60) != 0) {
			return;
		}

		// sun is rising!
		if (worldTime <= DAY) {

			worldBrightness += BRIGHTNESS_FACTOR;

		} // bright at day
		else if ((worldTime >= DAY && worldTime < EVENING)) {

			worldBrightness = Tile.DEFAULT_BRIGHTNESS;

		} // dark at night
		else if (worldTime > NIGHT) {

			worldBrightness = Tile.LOWEST_BRIGHTNESS;

		} // sun is falling during evening
		else if (worldTime > EVENING) {

			worldBrightness -= BRIGHTNESS_FACTOR;

		}
		// back to 0
		if (worldTime >= MIDNIGHT) {

			worldTime = DAWN;

		}

	}

	/**
	 * Get a tile ID of a tile in inputed location (in blocks)
	 *
	 * @param x posX in block
	 * @param y posY in block
	 * @return Tile of tile at certain location
	 */
	public static Tile getTile(int x, int y) {

		return tileManager.getTile(x, y);

	}

	/**
	 * Called when the World is just created (loaded). This method loads the
	 * surrounding 9 chunks around the player.
	 */
	public void loadChunks() {

		int[] chunkPos = Chunk.posToChunckPos(player.getCenterX(),
				player.getCenterY());

		int centerChunkX = chunkPos[0];
		int centerChunkY = chunkPos[1];

		if (tileManager == null)
			tileManager = new TileManager(handler);

		if (lightManager == null && tileManager != null)
			lightManager = new LightManager(handler, tileManager);

		for (int y = -1; y < 2; y++) {
			for (int x = -1; x < 2; x++) {

				int cx = centerChunkX + x;
				int cy = centerChunkY + y;
				// check from file or create one
				map[y + 1][x + 1] = Chunk.getNewChunk(cx, cy, WORLD_NAME, dimension);

			}
		}
	}

	/**
	 * Get the player for world loading. Player data also includes the current
	 * dimension data. New world's spawn position is 0,0.
	 *
	 * @return null if it doesn't exist
	 */
	private int[] getPlayerData() {

		String path = getPlayerDataPath();

		// if file doesn't exist, it's probably a new world
		if (!(new File(path).exists()))
			return null;

		int[] data = new int[5];

		String[] load = Utils.loadFileAsString(path).split(" ");

		data[0] = Integer.parseInt(load[0]); // x pos
		data[1] = Integer.parseInt(load[1]); // y pos
		data[2] = Integer.parseInt(load[2]); // health
		data[3] = Integer.parseInt(load[3]); // dimension
		data[4] = Integer.parseInt(load[4]); // time

		return data;

	}

	/**
	 * Save the player's data and world data
	 */
	public void savePlayerData() {

		for (int yy = 0; yy < World.map.length; yy++) {
			for (int xx = 0; xx < World.map[0].length; xx++) {
				if (World.map[xx][yy] != null)
					World.map[yy][xx].saveInFile();
			}
		}

		String path = getPlayerDataPath();
		BufferedWriter bw = null;

		try {

			File file = new File(path);

			if (!file.exists()) {

				file.getParentFile().mkdirs();
				file.createNewFile();

			}

			FileOutputStream fos = new FileOutputStream(file);

			bw = new BufferedWriter(new OutputStreamWriter(fos));

			int entX = (int) player.getX();
			int entY = (int) player.getY();
			int entHealth = player.getHealth();

			bw.write(entX + " " + entY + " " + entHealth + " " + dimension + " "
					+ worldTime);

		} catch (IOException ex) {

		} finally {

			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ex) {
					Logger.getLogger(World.class.getName()).log(Level.SEVERE, null,
							ex);
				}
			}

		}
	}

	/**
	 * Save the player's data periodically
	 */
	private void savePlayerDataPeriocially() {

		if (saveCounter >= SAVE_INTERVAL) {

			savePlayerData();
			saveCounter = 0;

		} else {

			saveCounter++;

		}

	}

	/**
	 * Load the world, typically used for dimension travel.
	 *
	 * @param world
	 */
	public static void loadWorld(World world) {

		Sound.stopAll();

		if ((worldTime >= DAY && worldTime < EVENING)) {
			Sound.DAY_BGM.loop();
		} else {
			Sound.NIGHT_BGM.loop();
		}

		// save chunks
		for (int yy = 0; yy < World.map.length; yy++) {
			for (int xx = 0; xx < World.map[0].length; xx++) {
				if (World.map[xx][yy] != null)
					World.map[yy][xx].saveInFile();
			}
		}

		world.savePlayerData();

		// now change to cave world
		world.getLightManager().clear();
		world.setDimension(World.DIMENSION_WORLD);
		world.loadChunks();

		world.setWorldBrightness(Tile.DEFAULT_BRIGHTNESS);

		int time = worldTime;
		worldTime = 0;

		for (int i = 0; i < time; i++) {
			timeCycle();
		}

		Player player = world.getEntityManager().getPlayer();

		World.map[1][1].addEntityDirectly(player);

		world.getHandler().getGameCamera().centerOnEntity(player);

	}

	// getters
	public Chunk getCenterChunk() {

		return map[1][1];

	}

	public Handler getHandler() {

		return this.handler;
	}

	public ItemManager getItemManager() {

		return itemManager;
	}

	public EntityManager getEntityManager() {

		return entityManager;
	}

	public TileManager getTileManager() {

		return tileManager;
	}

	public LightManager getLightManager() {

		return lightManager;
	}

	/**
	 * @return the worldTime
	 */
	public int getWorldTime() {

		return worldTime;
	}

	/**
	 * @param worldTime the worldTime to set
	 */
	public void setWorldTime(int worldTime) {

		this.worldTime = worldTime;
	}

	/**
	 * @return the worldBrightness
	 */
	public int getWorldBrightness() {

		return worldBrightness;
	}

	/**
	 * Recommended brightness range is in Tile class
	 *
	 * @param brightness
	 */
	public void setWorldBrightness(int brightness) {

		worldBrightness = brightness;
	}

	/**
	 * Get the world path, including / at the end
	 *
	 * @return
	 */
	public String getWorldPath() {

		return this.WORLD_PATH;
	}

	/**
	 * the name
	 *
	 * @return
	 */
	public String getWorldName() {

		return this.WORLD_NAME;
	}

	/**
	 * Get the last chunk file's full path
	 *
	 * @return
	 */
	private String getPlayerDataPath() {

		return this.WORLD_PATH + State.PLAYER_FILE_NAME + State.EXTENSION;

	}

	/**
	 * Get the current dimension
	 *
	 * @return
	 */
	public int getDimension() {

		return dimension;
	}

	/**
	 * Get the name for the dimension
	 *
	 * @param dimension
	 * @return
	 */
	public static String dimensionToName(int dimension) {

		switch (dimension) {
		case World.DIMENSION_WORLD:
			return "World";
		case World.DIMENSION_CAVE:
			return "Cave";
		}

		throw new Error("Dimension Id " + dimension + " does not exist!");

	}

	/**
	 * set the dimension
	 *
	 * @param dimension
	 */
	public void setDimension(int dimension) {

		this.dimension = dimension;

	}

}
