
package dev.game.map;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import dev.game.Handler;
import dev.game.entities.EntityId;
import dev.game.states.State;
import dev.game.tiles.Tile;
import dev.game.tiles.TileId;

/**
 * MapGenerator.java - Generate a random map with size and seeds.
 *
 * @author Juhyung Kim
 */
public class MapGenerator {

	public static final int RANDOM_SEED = -1;

	private Tile[][] map;
	private boolean[][] worldEntityMap, caveEntityMap;
	private int[][] worldEntityIdMap, caveEntityIdMap;
	private int elevationSeed, moistureSeed;
	private final String WORLD_NAME;
	private SimplexNoise elevationNoise, moistureNoise, treeNoise, grassNoise,
			caveNoise, stoneNoise;
	private Handler handler;

	/**
	 * Generate a random map with specific size and specific seeds.
	 * <p>
	 * RANDOM_SEED for random
	 *
	 * @param handler
	 * @param worldName name
	 */
	public MapGenerator(Handler handler, String worldName) {

		this.WORLD_NAME = worldName;

		int[] loadSeed = loadSeedsFromFile();
		elevationSeed = loadSeed[0];
		moistureSeed = loadSeed[1];

		this.handler = handler;

		this.elevationSeed = (elevationSeed == RANDOM_SEED)
				? new Random().nextInt(Integer.MAX_VALUE)
				: elevationSeed;

		this.moistureSeed = (moistureSeed == RANDOM_SEED)
				? new Random().nextInt(Integer.MAX_VALUE)
				: moistureSeed;

		map = new Tile[Chunk.SIZE][Chunk.SIZE];
		elevationNoise = new SimplexNoise(this.elevationSeed);
		moistureNoise = new SimplexNoise(this.moistureSeed);
		treeNoise = new SimplexNoise((this.elevationSeed + this.moistureSeed) / 2);
		stoneNoise = new SimplexNoise(
				(this.elevationSeed + this.moistureSeed) * 2 / 3);

		caveNoise = new SimplexNoise((this.elevationSeed + this.moistureSeed) / 3);
		grassNoise = new SimplexNoise(
				(this.elevationSeed + this.moistureSeed) / 3 * 2);

		saveSeedsInFile();

	}

	/**
	 * Map generation for Cave world
	 *
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	public Tile[][] generateCaveMap(int xPos, int yPos) {

		xPos *= Chunk.SIZE;
		yPos *= Chunk.SIZE;

		// reset
		map = new Tile[Chunk.SIZE][Chunk.SIZE];

		int yMax = yPos + Chunk.SIZE;
		int xMax = xPos + Chunk.SIZE;

		for (int y = yPos; y < yMax; y++) {

			for (int x = xPos; x < xMax; x++) {

				int arrayIndexX = (x - xPos);
				int arrayIndexY = (y - yPos);

				// various octaves
				double elevation = 2 * elevationNoise.noise(0.1 * x, 0.1 * y)
						+ 0.8 * elevationNoise.noise(0.05 * x, 0.05 * y);

				elevation = ((elevation < 0.3) ? TileId.GRAVEL : TileId.STONE);

				map[arrayIndexY][arrayIndexX] = Tile.createNew((int) elevation, x,
						y);

				map[arrayIndexY][arrayIndexX].setHandler(handler);

			}
		}
		return map;

	}

	/**
	 * Generate the map using elevation noise and moisture noise. Also, generate the
	 * tree map too (call generateTreeMap(int, int) for access).
	 *
	 * @param xPos in block, left top corner of the chunk
	 * @param yPos in block, left top corner of the chunk
	 * @return map tile array
	 */
	public Tile[][] generateMap(int xPos, int yPos) {

		xPos *= Chunk.SIZE;
		yPos *= Chunk.SIZE;

		// reset
		map = new Tile[Chunk.SIZE][Chunk.SIZE];

		int yMax = yPos + Chunk.SIZE;
		int xMax = xPos + Chunk.SIZE;

		for (int y = yPos; y < yMax; y++) {

			for (int x = xPos; x < xMax; x++) {

				int arrayIndexX = (x - xPos);
				int arrayIndexY = (y - yPos);

				// various octaves
				double elevation = (0.05 * elevationNoise.noise(x, y)
						+ 0.05 * elevationNoise.noise(0.5 * x, 0.5 * y)
						+ 0.2 * elevationNoise.noise(0.1 * x, 0.1 * y)
						+ 2 * elevationNoise.noise(0.05 * x, 0.05 * y)
						+ 7 * elevationNoise.noise(0.01 * x, 0.01 * y)
						+ 14 * elevationNoise.noise(0.005 * x, 0.005 * y)
						+ 18 * elevationNoise.noise(0.001 * x, 0.001 * y));

				// magic number
				elevation /= 32;

				// various octaves
				double moisture = (0.1 * moistureNoise.noise(x, y)
						+ 0.2 * moistureNoise.noise(0.5 * x, 0.5 * y)
						+ 1 * moistureNoise.noise(0.1 * x, 0.1 * y)
						+ 2 * moistureNoise.noise(0.05 * x, 0.05 * y)
						+ 7 * moistureNoise.noise(0.01 * x, 0.01 * y)
						+ 12 * moistureNoise.noise(0.005 * x, 0.005 * y)
						+ 16 * moistureNoise.noise(0.001 * x, 0.001 * y));

				// another magic number
				moisture /= 27.5;

				map[arrayIndexY][arrayIndexX] = Tile
						.createNew(getTileIdByBiome(elevation, moisture), x, y);

				map[arrayIndexY][arrayIndexX].setHandler(handler);
			}
		}

		return map;

	}

	/**
	 *
	 * @param xPos in chunk position
	 * @param yPos in chunk position
	 * @param size of map
	 * @return
	 */
	public Tile[][] generateMapForImage(int xPos, int yPos, int size) {

		xPos *= Chunk.SIZE;
		yPos *= Chunk.SIZE;

		// reset
		Tile[][] theMap = new Tile[size][size];

		int yMax = yPos + size;
		int xMax = xPos + size;

		for (int y = yPos; y < yMax; y++) {

			for (int x = xPos; x < xMax; x++) {

				int arrayIndexX = (x - xPos);
				int arrayIndexY = (y - yPos);

				// various octaves
				double elevation = (0.05 * elevationNoise.noise(x, y)
						+ 0.05 * elevationNoise.noise(0.5 * x, 0.5 * y)
						+ 0.2 * elevationNoise.noise(0.1 * x, 0.1 * y)
						+ 2 * elevationNoise.noise(0.05 * x, 0.05 * y)
						+ 7 * elevationNoise.noise(0.01 * x, 0.01 * y)
						+ 14 * elevationNoise.noise(0.005 * x, 0.005 * y)
						+ 18 * elevationNoise.noise(0.001 * x, 0.001 * y));

				// magic number
				elevation /= 32;

				// various octaves
				double moisture = (0.1 * moistureNoise.noise(x, y)
						+ 0.2 * moistureNoise.noise(0.5 * x, 0.5 * y)
						+ 1 * moistureNoise.noise(0.1 * x, 0.1 * y)
						+ 2 * moistureNoise.noise(0.05 * x, 0.05 * y)
						+ 7 * moistureNoise.noise(0.01 * x, 0.01 * y)
						+ 12 * moistureNoise.noise(0.005 * x, 0.005 * y)
						+ 16 * moistureNoise.noise(0.001 * x, 0.001 * y));

				// another magic number
				moisture /= 27.5;

				theMap[arrayIndexY][arrayIndexX] = Tile
						.createNew(getTileIdByBiome(elevation, moisture), x, y);

				theMap[arrayIndexY][arrayIndexX].setHandler(handler);
			}
		}

		return theMap;

	}

	/**
	 * Generate map for the entities that should spawn in the world.
	 * <p>
	 * <b>Run this method AFTER the gererateMap(float, float)</b>
	 * <p>
	 * This should be called for getWorldEntityMap() and getWorldEntityIdMap()
	 *
	 * @param xPos in chunk pos
	 * @param yPos in chunk pos
	 */
	public void generateWorldEntitiesMap(int xPos, int yPos) {

		xPos *= Chunk.SIZE;
		yPos *= Chunk.SIZE;

		// reset
		worldEntityMap = new boolean[Chunk.SIZE][Chunk.SIZE];
		worldEntityIdMap = new int[Chunk.SIZE][Chunk.SIZE];

		int yMax = yPos + Chunk.SIZE;
		int xMax = xPos + Chunk.SIZE;

		// priority: cave, tree, grass
		for (int y = yPos; y < yMax; y++) {

			for (int x = xPos; x < xMax; x++) {

				int arrayIndexX = x - xPos;
				int arrayIndexY = y - yPos;

				double cave = (0.1 * caveNoise.noise(x, y)
						+ 0.1 * caveNoise.noise(0.5 * x, 0.5 * y));

				cave /= (0.2);

				double caveR = getRByTile(EntityId.CAVE,
						map[arrayIndexY][arrayIndexX].getId());

				if (caveR > cave) {
					worldEntityMap[arrayIndexY][arrayIndexX] = true;
					worldEntityIdMap[arrayIndexY][arrayIndexX] = EntityId.CAVE;
					continue;
				}

				/////////////////////////
				double tree = (0.1 * treeNoise.noise(x, y)
						+ 0.1 * treeNoise.noise(0.5 * x, 0.5 * y));
				tree /= (0.2);

				double treeR = getRByTile(EntityId.TREE,
						map[arrayIndexY][arrayIndexX].getId());

				if (treeR > tree) {
					worldEntityMap[arrayIndexY][arrayIndexX] = true;
					worldEntityIdMap[arrayIndexY][arrayIndexX] = EntityId.TREE;
					continue;
				}

				//////////////////////////
				double grass = (0.1 * grassNoise.noise(x, y)
						+ 0.1 * grassNoise.noise(0.5 * x, 0.5 * y)
						+ 0.05 * grassNoise.noise(0.01 * x, 0.01 * y));

				grass /= (0.2);

				double grassR = getRByTile(EntityId.TALL_GRASS,
						map[arrayIndexY][arrayIndexX].getId());

				if (grassR > grass) {

					int newIndex = arrayIndexY - 1;

					// if 1 tile up is tree, don't place grass so it won't overlap
					if (newIndex > 0
							&& worldEntityIdMap[newIndex][arrayIndexX] != EntityId.TREE) {
						worldEntityMap[arrayIndexY][arrayIndexX] = true;
						worldEntityIdMap[arrayIndexY][arrayIndexX] = EntityId.TALL_GRASS;
					}
				}
			}
		}
	}

	/**
	 * For testing purpose, draw the noise map out.
	 * <p>
	 * This should be called for getCaveEntityMap() and getCaveEntityIdMap()
	 *
	 * @param noise
	 * @param fileName
	 */
	public void drawNoise(boolean[][] noise, String fileName) {

		BufferedImage image = new BufferedImage(noise[0].length, noise.length,
				BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < noise.length; y++) {

			for (int x = 0; x < noise[0].length; x++) {

				int c = (noise[y][x]) ? 0 : 255;

				Color color = new Color(c, c, c);

				int rgb = color.getRGB();

				image.setRGB(x, y, rgb);

			}
		}

		try {
			File outputfile = new File(fileName + ".png");
			outputfile.createNewFile();

			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			throw new RuntimeException("I didn't handle this very well");
		}

	}

	/**
	 * Generate the entity map for the cave map.
	 *
	 * @param xPos
	 * @param yPos
	 */
	public void generateCaveEntitiesMap(int xPos, int yPos) {

		xPos *= Chunk.SIZE;
		yPos *= Chunk.SIZE;

		// reset
		caveEntityMap = new boolean[Chunk.SIZE][Chunk.SIZE];
		caveEntityIdMap = new int[Chunk.SIZE][Chunk.SIZE];

		int yMax = yPos + Chunk.SIZE;
		int xMax = xPos + Chunk.SIZE;

		for (int y = yPos; y < yMax; y++) {

			for (int x = xPos; x < xMax; x++) {

				int arrayIndexX = (x - xPos);
				int arrayIndexY = (y - yPos);

				double stone = (0.1 * stoneNoise.noise(x, y)
						+ 0.1 * stoneNoise.noise(0.5 * x, 0.5 * y));

				stone /= (0.1 + 0.1);

				double r = getRByTile(EntityId.STONE,
						map[arrayIndexY][arrayIndexX].getId());

				if (r > stone) {
					caveEntityMap[arrayIndexY][arrayIndexX] = true;
					caveEntityIdMap[arrayIndexY][arrayIndexX] = EntityId.STONE;
				}
			}
		}
	}

	/**
	 * Get the right tile id for given biome (elevation and moisture).
	 *
	 * @param elevation -1 ~ 1
	 * @param moisture  -1 ~ 1
	 * @return
	 */
	private int getTileIdByBiome(double elevation, double moisture) {

		if (elevation < -0.6) {
			return TileId.WATER; // lake
		}
		if (elevation < -0.45) {
			return TileId.SAND; // beach
		}
		if (elevation > 0.96) {
			return TileId.SNOW; // snowy mountain-top
		}
		if (elevation > 0.85 && moisture > 0) {
			return TileId.SNOW; // snowy mountain-top
		}

		if (elevation > 0.75) {
			if (moisture > 0.5) {
				return TileId.GRASSY_GRAVEL; // mt
			}
			return TileId.GRAVEL; // mt
		}

		if (elevation > 0.45) {

			if (moisture < -0.5) {
				return TileId.OCHER; // scorched
			}
			if (moisture < -0.2) {
				return TileId.DIRT; // bare
			}
			if (moisture < 0.3) {
				return TileId.GRAVEL; // tundra (dry)
			}
			if (moisture < 0.65) {
				return TileId.GRASSY_GRAVEL; // tundra (wet)
			}
		}

		if (elevation > 0.1) {

			if (moisture < -0.5) {
				return TileId.SAND; // TEMPERATE_DESERT;
			}
			if (moisture < 0.2) {
				return TileId.DIRT; // SHRUBLAND;
			}
			if (moisture < 0.6) {
				return TileId.GRASSY_DIRT; // SHRUBLAND;
			}
			return TileId.TOPSOIL;// TAIGA;

		}

		if (elevation > -0.3) {

			if (moisture < -0.5) {
				return TileId.SAND; // TEMPERATE_DESERT;
			}
			if (moisture < 0) {
				return TileId.GRASS; // GRASSLAND;
			}
			if (moisture < 0.3) {
				return TileId.TOPSOIL; // TEMPERATE_DECIDUOUS_FOREST;
			}
			if (moisture < 0.6) {
				return TileId.GRASSY_MUD; // TROPICAL_RAIN_FOREST;
			}
			return TileId.MUD; // swamp

		}

		if (moisture < -0.5) {
			return TileId.SAND; // SUBTROPICAL_DESERT;
		}
		if (moisture < 0.2) {
			return TileId.GRASS; // GRASSLAND;
		}
		if (moisture < 0.5) {
			return TileId.TOPSOIL; // TROPICAL_SEASONAL_FOREST;
		}
		return TileId.GRASSY_MUD; // TROPICAL_RAIN_FOREST;

	}

	/**
	 * R is for dispersed entity-placing. If the value of noise is bigger than
	 * returned R, that is the appropriate place to spawn the entity.
	 * <p>
	 * Higher the R, higher the tree density. < -1 is no spawn.
	 * <p>
	 * 
	 * @param enti
	 *
	 *
	 *
	 *
	 *
	 *               tyId Entity id to span
	 * @param tileId Tile id to check
	 * @return
	 */
	private double getRByTile(int entityId, int tileId) {

		double r = 0;

		if (entityId == EntityId.TREE) {

			switch (tileId) {

			case TileId.GRASS:
				r = -0.6;
				break;

			case TileId.DIRT:
				r = -0.65;
				break;

			case TileId.GRASSY_DIRT:
				r = -0.65;
				break;

			case TileId.TOPSOIL:
				r = -0.6;
				break;

			case TileId.GRAVEL:
				r = -0.8;
				break;

			case TileId.GRASSY_GRAVEL:
				r = -0.8;
				break;

			case TileId.WATER:
				r = -10;
				break;

			case TileId.SAND:
				r = -10;
				break;

			case TileId.OCHER:
				r = -0.8;
				break;

			case TileId.GRASSY_MUD:
				r = -0.5;
				break;

			case TileId.MUD:
				r = -0.65;
				break;

			case TileId.SNOW: // no trees on snow
				r = -10;
				break;

			}

		} else if (entityId == EntityId.CAVE) {

			// let the cave be "equally" distributed
			r = -0.85;

		} else if (entityId == EntityId.TALL_GRASS) {

			switch (tileId) {

			case TileId.GRASS:
				r = -0.5;
				break;

			case TileId.DIRT:
				r = -0.55;
				break;

			case TileId.GRASSY_DIRT:
				r = -0.55;
				break;

			case TileId.TOPSOIL:
				r = -0.5;
				break;

			case TileId.GRAVEL:
				r = -0.7;
				break;

			case TileId.GRASSY_GRAVEL:
				r = -0.7;
				break;

			case TileId.WATER:
			case TileId.SAND:
				r = -10;
				break;

			case TileId.OCHER:
				r = -0.7;
				break;

			case TileId.GRASSY_MUD:
				r = -0.4;
				break;

			case TileId.MUD:
				r = -0.55;
				break;

			case TileId.SNOW: // no grass on snow
				r = -10;
				break;

			}

		} else if (entityId == EntityId.STONE) {

			switch (tileId) {

			case TileId.STONE:
				r = -10;
				break;

			case TileId.GRAVEL:
				r = -0.5;
				break;
			}

		}

		return r;

	}

	/**
	 * Save the seeds data.
	 * <p>
	 * Format: seed1 seed2
	 */
	private void saveSeedsInFile() {

		String path = State.SAVEFILE_DIR + "/" + WORLD_NAME + "/" + State.SEEDS_NAME
				+ State.EXTENSION;

		File file = new File(path);

		BufferedWriter bw = null;

		try {

			if (!file.exists())
				file.getParentFile().mkdirs();

			file.createNewFile();

			FileOutputStream fos = null;

			fos = new FileOutputStream(file);

			bw = new BufferedWriter(new OutputStreamWriter(fos));

			bw.write(elevationSeed + " " + moistureSeed);

		} catch (IOException ex) {

			Logger.getLogger(MapGenerator.class.getName()).log(Level.SEVERE, null,
					ex);

		} finally {

			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ex) {
					Logger.getLogger(MapGenerator.class.getName()).log(Level.SEVERE,
							null, ex);
				}
			}
		}
	}

	/**
	 * Load the seed for this world. If this world is new, get new RANDOM_SEED's
	 *
	 * @return {seed1, seed2}
	 */
	public int[] loadSeedsFromFile() {

		String path = State.SAVEFILE_DIR + "/" + WORLD_NAME + "/" + State.SEEDS_NAME
				+ State.EXTENSION;

		File file = new File(path);

		if (!file.exists())
			return new int[] { RANDOM_SEED, RANDOM_SEED };

		String line = "";
		BufferedReader br = null;

		try {

			br = new BufferedReader(new FileReader(path));

			line = br.readLine();

		} catch (IOException ex) {

			Logger.getLogger(MapGenerator.class.getName()).log(Level.SEVERE, null,
					ex);

		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException ex) {
					Logger.getLogger(MapGenerator.class.getName()).log(Level.SEVERE,
							null, ex);
				}
			}
		}

		String[] seedData = line.split(" ");

		int seed1 = Integer.parseInt(seedData[0]);
		int seed2 = Integer.parseInt(seedData[1]);

		return new int[] { seed1, seed2 };

	}

	// getter //////////////////
	/**
	 * get world name
	 *
	 * @return
	 */
	public String getWorldName() {

		return this.WORLD_NAME;
	}

	public int getSeed1() {

		return this.elevationSeed;
	}

	public int getSeed2() {

		return this.moistureSeed;
	}

	/**
	 * call generateWorldEntitiesMap(int xPos, int yPos) first
	 *
	 * @return the worldEntityMap
	 */
	public boolean[][] getWorldEntityMap() {

		return worldEntityMap;
	}

	/**
	 * call generateWorldEntitiesMap(int xPos, int yPos) first
	 *
	 * @return the caveEntityMap
	 */
	public boolean[][] getCaveEntityMap() {

		return caveEntityMap;
	}

	/**
	 * call generateCaveEntitiesMap(int xPos, int yPos) first
	 *
	 * @return the worldEntityIdMap
	 */
	public int[][] getWorldEntityIdMap() {

		return worldEntityIdMap;
	}

	/**
	 * call generateCaveEntitiesMap(int xPos, int yPos) first
	 *
	 * @return the caveEntityIdMap
	 */
	public int[][] getCaveEntityIdMap() {

		return caveEntityIdMap;
	}

}
