
package dev.game.tiles;

import java.awt.Color;
import java.awt.Graphics;

import dev.game.Game;
import dev.game.Handler;
import dev.game.gfx.Assets;
import dev.game.gfx.GameCamera;
import dev.game.gfx.Text;
import dev.game.map.Chunk;
import dev.game.tiles.tiles.AirTile;
import dev.game.worlds.World;

/**
 * TileManager.java - The tile manager of the world
 *
 * @author j.kim3
 */
public class TileManager {

	private Handler handler;
	private World world;
	private GameCamera camera;

	/**
	 * The tile manager of the world
	 *
	 * @param handler handler
	 */
	public TileManager(Handler handler) {

		this.handler = handler;
		this.camera = handler.getGameCamera();

	}

	public void tick() {

		if (world == null) {
			if (handler.getWorld() != null) {
				this.world = handler.getWorld();
			} else {
				return;
			}
		}

		float xOffset = camera.getXOffset();
		float yOffset = camera.getYOffset();

		int xStart = (int) (xOffset / Tile.TILE_SIZE) - 1;
		int xEnd = (int) ((xOffset + Game.SCREEN_WIDTH) / Tile.TILE_SIZE) + 1;
		int yStart = (int) (yOffset / Tile.TILE_SIZE) - 1;
		int yEnd = (int) ((yOffset + Game.SCREEN_HEIGHT) / Tile.TILE_SIZE) + 1;

		int chunkHalf = Chunk.SIZE / 2;

		// adding extra room to tick
		// place outside of screen should tick but do not need to render
		xStart -= chunkHalf;
		xEnd += chunkHalf;
		yStart -= chunkHalf;
		yEnd += chunkHalf;

		int bri = world.getWorldBrightness();

		for (int y = 0; y < World.map.length; y++) {

			for (int x = 0; x < World.map[0].length; x++) {

				Chunk currentChunk = World.map[y][x];

				if (currentChunk == null)
					continue;

				Tile[][] tiles = currentChunk.tiles;

				for (int yy = 0; yy < tiles.length; yy++) {
					for (int xx = 0; xx < tiles[0].length; xx++) {

						Tile tile = tiles[yy][xx];

						if (!isTileOutOfRange(tile, xStart, yStart, xEnd, yEnd)) {
							tile.tick();
							tile.setBrightness(bri);
						}
					}
				}
			}
		}
	}

	/**
	 * Render tiles in the world
	 *
	 * @param gfx
	 */
	public void render(Graphics gfx) {

		float xOffset = camera.getXOffset();
		float yOffset = camera.getYOffset();

		int xStart = (int) (xOffset / Tile.TILE_SIZE) - 1;
		int xEnd = (int) ((xOffset + Game.SCREEN_WIDTH) / Tile.TILE_SIZE) + 1;
		int yStart = (int) (yOffset / Tile.TILE_SIZE) - 1;
		int yEnd = (int) ((yOffset + Game.SCREEN_HEIGHT) / Tile.TILE_SIZE) + 1;

		for (int y = 0; y < World.map.length; y++) {
			for (int x = 0; x < World.map[0].length; x++) {

				Chunk currentChunk = World.map[y][x];

				if (currentChunk == null)
					continue;

				Tile[][] tiles = currentChunk.tiles;

				for (int yy = 0; yy < tiles.length; yy++) {
					for (int xx = 0; xx < tiles[0].length; xx++) {

						Tile tile = tiles[yy][xx];

						if (!isTileOutOfRange(tile, xStart, yStart, xEnd, yEnd)) {

							tile.render(gfx);

						}
					}
				}
			}
		}
	}

	/**
	 * Get tile at the coordinate.
	 * <p>
	 * If the position is out of 3 * 3 surrounding Chunks, it return an AirTile
	 *
	 * @param x pos in block
	 * @param y pos in block
	 * @return Tile in the spot
	 */
	public Tile getTile(int x, int y) {

		for (int yy = 0; yy < World.map.length; yy++) {
			for (int xx = 0; xx < World.map[0].length; xx++) {

				Chunk currentChunk = World.map[yy][xx];

				if (currentChunk == null)
					continue;

				Tile[][] tiles = currentChunk.tiles;

				Tile leftTop = tiles[0][0];
				Tile rightBottom = tiles[Chunk.SIZE - 1][Chunk.SIZE - 1];
				int xStart = leftTop.getX();
				int xEnd = rightBottom.getX();
				int yStart = leftTop.getY();
				int yEnd = rightBottom.getY();

				// if it's found in this chunk, return it
				if (x >= xStart || y >= yStart || x <= xEnd || y <= yEnd) {

					for (int i = 0; i < tiles.length; i++) {
						for (int j = 0; j < tiles[0].length; j++) {

							Tile tile = tiles[i][j];

							if (tile.getX() == x && tile.getY() == y) {
								return tile;
							}
						}
					}
				}
			}
		}

		// in case it was not in any chunk
		// (probably coordinate too far from the player)
		return new AirTile();

	}

	/**
	 * Check if the tile is out of the range.
	 *
	 * @param tile to check
	 * @param minX in block
	 * @param minY in block
	 * @param maxX in block
	 * @param maxY in block
	 * @return
	 */
	public boolean isTileOutOfRange(Tile tile, int minX, int minY, int maxX,
			int maxY) {

		int tileX = tile.getX();
		int tileY = tile.getY();

		return (tileX < minX || tileY < minY || tileX > maxX || tileY > maxY);

	}

	/**
	 * For developing purpose, show the tile coordinate on tile
	 *
	 * @param gfx
	 * @param tile
	 * @param camera
	 */
	public void showTileCoordinate(Graphics gfx, Tile tile, GameCamera camera) {

		Text.drawString(gfx, tile.getX() + "," + tile.getY(),
				(int) (tile.getX() * Tile.TILE_SIZE - camera.getXOffset())
						+ Tile.TILE_SIZE / 2,
				(int) (tile.getY() * Tile.TILE_SIZE - camera.getYOffset())
						+ Tile.TILE_SIZE / 2,
				Color.WHITE, Assets.cordFont, true);

	}

	// getter
	public Handler getHandler() {

		return handler;
	}

}
