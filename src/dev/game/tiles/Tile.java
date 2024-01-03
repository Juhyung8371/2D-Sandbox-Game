
package dev.game.tiles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import dev.game.Handler;
import dev.game.gfx.GameCamera;
import dev.game.tiles.tiles.AirTile;
import dev.game.tiles.tiles.DirtTile;
import dev.game.tiles.tiles.GrassTile;
import dev.game.tiles.tiles.GrassyDirtTile;
import dev.game.tiles.tiles.GrassyGravelTile;
import dev.game.tiles.tiles.GrassyMudTile;
import dev.game.tiles.tiles.GravelTile;
import dev.game.tiles.tiles.MudTile;
import dev.game.tiles.tiles.OcherTile;
import dev.game.tiles.tiles.SandTile;
import dev.game.tiles.tiles.SnowTile;
import dev.game.tiles.tiles.StoneTile;
import dev.game.tiles.tiles.TopsoilTile;
import dev.game.tiles.tiles.WaterTile;

/**
 * Tile.java - tile
 *
 * @author j.kim3
 */
public class Tile {

	public static final int TILE_SIZE = 64;

	public static final int DEFAULT_BRIGHTNESS = 254;
	public static final int LOWEST_BRIGHTNESS = 32;

	// class stuff
	private boolean isSolid;

	private int brightness;

	protected BufferedImage texture;
	private final int id;
	private String name;

	/**
	 * in block
	 */
	private int x, y;

	private Handler handler;

	protected GameCamera camera;

	public Tile(BufferedImage texture, int id, String name, boolean isSolid) {

		this.texture = texture;
		this.id = id;
		this.isSolid = isSolid;
		this.name = name;

		this.brightness = DEFAULT_BRIGHTNESS;

	}

	/**
	 * Nothing really
	 */
	public void tick() {

	}

	public void render(Graphics gfx) {

		gfx.drawImage(texture, (int) (x * Tile.TILE_SIZE - camera.getXOffset()),
				(int) (y * Tile.TILE_SIZE - camera.getYOffset()), TILE_SIZE,
				TILE_SIZE, null);
	}

	/**
	 * Create new tile object by referencing the instances of tiles already made
	 *
	 * @param ID tile id
	 * @param x  position x
	 * @param y  position y
	 * @return Tile
	 */
	public static Tile createNew(int id, int x, int y) {

		Tile tile = null;

		switch (id) {

		case TileId.STONE:
			tile = new StoneTile();
			break;

		case TileId.GRASS:
			tile = new GrassTile();
			break;

		case TileId.DIRT:
			tile = new DirtTile();
			break;

		case TileId.GRASSY_DIRT:
			tile = new GrassyDirtTile();
			break;

		case TileId.TOPSOIL:
			tile = new TopsoilTile();
			break;

		case TileId.GRAVEL:
			tile = new GravelTile();
			break;

		case TileId.GRASSY_GRAVEL:
			tile = new GrassyGravelTile();
			break;

		case TileId.WATER:
			tile = new WaterTile();
			break;

		case TileId.SAND:
			tile = new SandTile();
			break;

		case TileId.OCHER:
			tile = new OcherTile();
			break;

		case TileId.MUD:
			tile = new MudTile();
			break;

		case TileId.GRASSY_MUD:
			tile = new GrassyMudTile();
			break;

		case TileId.SNOW:
			tile = new SnowTile();
			break;

		}

		if (tile == null) {

			tile = new AirTile();

		}

		tile.setX(x);
		tile.setY(y);

		return tile;

	}

	public boolean isSolid() {

		return isSolid;

	}

	public int getId() {

		return id;
	}

	public String getName() {

		return name;
	}

	public Handler getHandler() {

		return handler;
	}

	public void setHandler(Handler hand) {

		this.handler = hand;
		this.camera = hand.getGameCamera();
	}

	/**
	 * @return the x in block
	 */
	public int getX() {

		return x;
	}

	/**
	 * @param x the x to set in block
	 */
	public void setX(int x) {

		this.x = x;
	}

	/**
	 * @return the y in block
	 */
	public int getY() {

		return y;
	}

	/**
	 * @param y the y to set in block
	 */
	public void setY(int y) {

		this.y = y;
	}

	/**
	 * @return the brightness
	 */
	public int getBrightness() {

		return brightness;
	}

	/**
	 * @param brightness the brightness to set
	 */
	public void setBrightness(int brightness) {

		this.brightness = brightness;
	}

}
