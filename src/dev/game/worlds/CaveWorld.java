
package dev.game.worlds;

import java.util.List;

import dev.game.entities.EntityId;
import dev.game.entities.creatures.Player;
import dev.game.entities.statics.EntityCave;
import dev.game.map.Chunk;
import dev.game.sounds.Sound;
import dev.game.states.State;
import dev.game.tiles.Tile;
import dev.game.utils.Utils;

/**
 * The underground world in the cave.
 * <p>
 * This does not extend World. But requires World to load.
 *
 * @author Juhyung Kim
 */
public class CaveWorld {

	/**
	 * Load the cave world
	 *
	 * @param world
	 */
	public static void loadCaveWorld(World world) {

		System.out.println("Enter the cave");

		Sound.stopAll();
		Sound.CAVE_BGM.loop();

		saveWorld(world); // Superman, help!!!!
		loadChunks(world);
		spawnPlayer(world);
		spawnCaveEntity(world);

	}

	/**
	 * Save the world's data before going to cave world.
	 *
	 * @param world
	 */
	private static void saveWorld(World world) {

		for (int yy = 0; yy < World.map.length; yy++) {
			for (int xx = 0; xx < World.map[0].length; xx++) {
				if (World.map[xx][yy] != null)
					World.map[yy][xx].saveInFile();
			}
		}

		world.savePlayerData();

	}

	/**
	 * Load the chunks for the cave world. And set the brightness to lowest.
	 *
	 * @param world
	 */
	private static void loadChunks(World world) {

		// now change to cave world
		world.getLightManager().clear();
		world.setDimension(World.DIMENSION_CAVE);
		world.loadChunks();
		world.setWorldBrightness(Tile.LOWEST_BRIGHTNESS - 12);

	}

	/**
	 * Spawn the player to the center chunk.
	 *
	 * @param world
	 */
	private static void spawnPlayer(World world) {

		Player player = world.getEntityManager().getPlayer();

		World.map[1][1].addEntityDirectly(player);

		int py = player.getCenterY();

		// adjust y pos so player do not get stuck in wall
		while (World.getTile(player.getCenterX(), py).isSolid()) {
			py++;
		}

		player.setYInBlock(py);

		world.getHandler().getGameCamera().centerOnEntity(player);

	}

	/**
	 * Check if cave entity to go back to the world is needed to be spawned.
	 *
	 * @param world
	 */
	private static void spawnCaveEntity(World world) {

		Player player = world.getEntityManager().getPlayer();

		int[] newPos = Chunk.posToChunckPos(player.getCenterX(),
				player.getCenterY());

		String path = State.SAVEFILE_DIR + "/" + world.getWorldName() + "/"
				+ State.CHUNKS_DIR + "/"
				+ World.dimensionToName(World.DIMENSION_CAVE) + "/" + newPos[0]
				+ Chunk.CHUNK_NAME_DIVIDER + newPos[1] + State.EXTENSION;

		// the reason why it's checking the file directly is because
		// the EntityManager.addEntity(Entity) needs one more tick to
		// spawn the CaveEntity, but this method is called immediately after
		// the load of chunks, so the EntityManager could not have added
		// the CaveEntity yet even though it might exist.
		List<String> data = Utils.loadFileAsArrays(path);

		int cx = player.getCenterX() * Tile.TILE_SIZE;
		int cy = player.getCenterY() * Tile.TILE_SIZE;

		if (data != null) {

			for (String line : data) {

				String[] entityData = line.split(" ");

				int entId = Utils.parseInt(entityData[0]);

				// if there is cave, don't spawn it
				if (entId == EntityId.CAVE) {

					int entX = Utils.parseInt(entityData[1]);
					int entY = Utils.parseInt(entityData[2]);

					// cave is already spawned
					if (entX == cx && entY == cy)
						return;

				}
			}
		}

		// if it doesn't exist, spawn one
		EntityCave cave = new EntityCave(world.getHandler(), cx, cy);
		World.map[1][1].addEntityDirectly(cave);

	}

}
