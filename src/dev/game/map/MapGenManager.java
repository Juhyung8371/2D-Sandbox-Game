
package dev.game.map;

import dev.game.Handler;
import dev.game.entities.creatures.Player;
import dev.game.worlds.World;

/**
 * MapGenManager.java - The runnable that manages the map chunk loading.
 *
 * @author Juhyung Kim
 */
public class MapGenManager {

	private Handler handler;
	private Player player;

	private final String WORLD_NAME;

	private World world = null;

	public MapGenManager(Handler handler, String worldName) {

		this.handler = handler;
		this.WORLD_NAME = worldName;

	}

	public void tick() {

		if (player == null && handler.getWorld() != null) {

			world = handler.getWorld();

			player = world.getEntityManager().getPlayer();

		}

		updateChunks();
		generateChunks();

	}

	/**
	 * Fill in the empty spot in World.map made by {@linkplain #updateChunk()}
	 * method.
	 */
	private void generateChunks() {

		Chunk centerChunk = World.map[1][1];

		// if there are any empty spot in the 3*3 map, fill it in
		for (int y = 0; y < World.map.length; y++) {

			for (int x = 0; x < World.map[0].length; x++) {

				if (World.map[y][x] == null) {

					int newChunkX = centerChunk.x + (x - 1);
					int newChunkY = centerChunk.y + (y - 1);

					// check from file or create one
					Chunk newChunk = Chunk.getNewChunk(newChunkX, newChunkY,
							WORLD_NAME, world.getDimension());

					World.map[y][x] = newChunk;

				}
			}
		}
	}

	/**
	 * Check the player's position and update the World.map (by removing the Chunks
	 * that are not near player anymore)
	 */
	private void updateChunks() {

		Chunk centerChunk = World.map[1][1];

		// this should not happen
		if (centerChunk == null)
			return;

		boolean north = centerChunk.isAwayNorth(player);
		boolean south = centerChunk.isAwaySouth(player);
		boolean east = centerChunk.isAwayEast(player);
		boolean west = centerChunk.isAwayWest(player);

		// check the player's position to update the surrounding Chunks
		// checking clockwise
		if (north) {

			// save the Chunks that are not going to be part
			// of the map anymore (south row)
			if (World.map[2][2] != null)
				World.map[2][2].saveInFile();
			if (World.map[2][1] != null)
				World.map[2][1].saveInFile();
			if (World.map[2][0] != null)
				World.map[2][0].saveInFile();

			// east to south-east
			if (World.map[1][2] != null) {
				World.map[2][2] = World.map[1][2];
			}
			// center to south
			if (World.map[1][1] != null) {
				World.map[2][1] = World.map[1][1];
			}
			// west to south-west
			if (World.map[1][0] != null) {
				World.map[2][0] = World.map[1][0];
			}
			// north-east to east
			if (World.map[0][2] != null) {
				World.map[1][2] = World.map[0][2];
			}
			// north to center
			if (World.map[0][1] != null) {
				World.map[1][1] = World.map[0][1];
			}
			// north-west to east
			if (World.map[0][0] != null) {
				World.map[1][0] = World.map[0][0];
			}

			// removing the north row for the new row to generate
			World.map[0][0] = null;
			World.map[0][1] = null;
			World.map[0][2] = null;

		}
		if (east) {

			// save the Chunks that are not going to be part
			// of the map anymore (west column)
			if (World.map[0][0] != null)
				World.map[0][0].saveInFile();
			if (World.map[1][0] != null)
				World.map[1][0].saveInFile();
			if (World.map[2][0] != null)
				World.map[2][0].saveInFile();

			// north to north-west
			if (World.map[0][1] != null) {
				World.map[0][0] = World.map[0][1];
			}
			// north-east to north
			if (World.map[0][2] != null) {
				World.map[0][1] = World.map[0][2];
			}
			// center to west
			if (World.map[1][1] != null) {
				World.map[1][0] = World.map[1][1];
			}
			// east to center
			if (World.map[1][2] != null) {
				World.map[1][1] = World.map[1][2];
			}
			// south to south-west
			if (World.map[2][1] != null) {
				World.map[2][0] = World.map[2][1];
			}
			// south-east to south
			if (World.map[2][2] != null) {
				World.map[2][1] = World.map[2][2];
			}

			// removing the east column for the new column to generate
			World.map[0][2] = null;
			World.map[1][2] = null;
			World.map[2][2] = null;

		}
		if (south) {

			// save the Chunks that are not going to be part
			// of the map anymore (north row)
			if (World.map[0][0] != null)
				World.map[0][0].saveInFile();
			if (World.map[0][1] != null)
				World.map[0][1].saveInFile();
			if (World.map[0][2] != null)
				World.map[0][2].saveInFile();

			// east to north-east
			if (World.map[1][2] != null) {
				World.map[0][2] = World.map[1][2];
			}
			// center to north
			if (World.map[1][1] != null) {
				World.map[0][1] = World.map[1][1];
			}
			// west to north-west
			if (World.map[1][0] != null) {
				World.map[0][0] = World.map[1][0];
			}
			// south-east to east
			if (World.map[2][2] != null) {
				World.map[1][2] = World.map[2][2];
			}
			// south to center
			if (World.map[2][1] != null) {
				World.map[1][1] = World.map[2][1];
			}
			// south-west to east
			if (World.map[2][0] != null) {
				World.map[1][0] = World.map[2][0];
			}

			// removing the south row for the new row to generate
			World.map[2][0] = null;
			World.map[2][1] = null;
			World.map[2][2] = null;

		}
		if (west) {

			// save the Chunks that are not going to be part of
			// the map anymore (east column)
			if (World.map[0][2] != null)
				World.map[0][2].saveInFile();
			if (World.map[1][2] != null)
				World.map[1][2].saveInFile();
			if (World.map[2][2] != null)
				World.map[2][2].saveInFile();

			// north to north-east
			if (World.map[0][1] != null) {
				World.map[0][2] = World.map[0][1];
			}
			// north-east to north
			if (World.map[0][0] != null) {
				World.map[0][1] = World.map[0][0];
			}
			// center to east
			if (World.map[1][1] != null) {
				World.map[1][2] = World.map[1][1];
			}
			// west to center
			if (World.map[1][0] != null) {
				World.map[1][1] = World.map[1][0];
			}
			// south to south-east
			if (World.map[2][1] != null) {
				World.map[2][2] = World.map[2][1];
			}
			// south-west to south
			if (World.map[2][0] != null) {
				World.map[2][1] = World.map[2][0];
			}

			// removing the west column for the new column to generate
			World.map[0][0] = null;
			World.map[1][0] = null;
			World.map[2][0] = null;

		}

	}

}
