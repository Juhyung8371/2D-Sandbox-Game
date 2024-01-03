
package dev.game.lights;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import dev.game.Game;
import dev.game.Handler;
import dev.game.entities.creatures.Player;
import dev.game.gfx.GameCamera;
import dev.game.tiles.Tile;
import dev.game.tiles.TileManager;
import dev.game.utils.Utils;
import dev.game.worlds.World;

/**
 * LightManager.java - manages lights
 *
 * @author j.kim3
 */
public class LightManager {

	private Handler handler;

	private ArrayList<Light> lights; // all the lights
	private static ArrayList<Light> addLightList; // lights in queue to spawn

	private TileManager tileManager;

	public LightManager(Handler handler, TileManager tileManager) {

		this.handler = handler;
		this.tileManager = tileManager;

		lights = new ArrayList<>();

		addLightList = new ArrayList<>();
	}

	public void tick() {

		for (Light l : addLightList) {
			lights.add(l);
		}

		addLightList.clear();

		for (int i = 0; i < lights.size(); i++) {

			Light light = lights.get(i);

			if (!light.isAlive()) {

				lights.remove(light);
				continue;

			}

			if (isOutOfSight(light))
				continue;

			light.tick();

			lightEffect(light.getX(), light.getY(), light.getRange(),
					light.getBrightness());

		}

	}

	public void render(Graphics gfx) {

		GameCamera cam = handler.getGameCamera();

		float xOffset = cam.getXOffset();
		float yOffset = cam.getYOffset();

		// only rendering tiles on screen
		// rendering range is from the offset of the camera(left-top of the screen),
		// to the right-bottom of the screen
		int xStart = (int) (xOffset / Tile.TILE_SIZE) - 1;
		int xEnd = (int) ((xOffset + Game.SCREEN_WIDTH) / Tile.TILE_SIZE) + 1;
		int yStart = (int) (yOffset / Tile.TILE_SIZE) - 1;
		int yEnd = (int) ((yOffset + Game.SCREEN_HEIGHT) / Tile.TILE_SIZE) + 1;

		for (int yy = yStart; yy < yEnd; yy++) {

			for (int xx = xStart; xx < xEnd; xx++) {

				Tile tile = World.getTile(xx, yy);

				int bri = tile.getBrightness();
				int ren = Tile.DEFAULT_BRIGHTNESS - bri;

				if (ren < Tile.LOWEST_BRIGHTNESS)
					ren = Tile.LOWEST_BRIGHTNESS;

				// dark layer
				Color color = new Color(0, 0, 0, ren);

				gfx.setColor(color);

				gfx.fillRect((int) (xx * Tile.TILE_SIZE - cam.getXOffset()),
						(int) (yy * Tile.TILE_SIZE - cam.getYOffset()),
						Tile.TILE_SIZE, Tile.TILE_SIZE);

			}
		}

	}

	/**
	 * Let there be light!
	 *
	 * @param centerX
	 * @param centerY
	 * @param range
	 * @param brightness
	 */
	private void lightEffect(int centerX, int centerY, int range, int brightness) {

		// no light for non-emitting light
		if (brightness < Tile.LOWEST_BRIGHTNESS)
			return;

		int topY = centerY - range;
		int btmY = centerY + range;

		// width for the diamond shaped for loop
		int dis = 0;

		// diamond shaped for loop (shape of light)
		for (int y = topY; y <= btmY; y++) {
			for (int x = centerX - dis; x <= centerX + dis; x++) {
				ray(centerX, centerY, x, y, range, brightness);
			}

			if (y < centerY)
				dis++;
			else
				dis--;

		}

	}

	/**
	 * Shine a ray of light from the starting point to the ending point. If it hits
	 * a solid block, stop.
	 *
	 * @param xStart     in block
	 * @param yStart     in bloc
	 * @param xEnd       in block
	 * @param yEnd       in block
	 * @param range      light might not reach the end if range is too far
	 * @param brightness Tile.LOWEST_BRIGHTNESS ~ Tile.DEFAULT_BRIGHTNESS
	 */
	private void ray(int xStart, int yStart, int xEnd, int yEnd, int range,
			int brightness) {

		// delta of exact value and rounded value of the dependent variable
		int d = 0;

		int dx = Math.abs(xEnd - xStart);
		int dy = Math.abs(yEnd - yStart);

		int dx2 = 2 * dx; // slope scaling factors to
		int dy2 = 2 * dy; // avoid floating point (integer is more efficient too)

		int ix = xStart < xEnd ? 1 : -1; // increment direction (in Cartesian plane)
		int iy = yStart < yEnd ? 1 : -1;

		int x = xStart;
		int y = yStart;

		int newBrightness = brightness / (range);

		int rangeCounter = 0;
		boolean breakNext = false;

		// way(order) of computing is different for each case
		if (dx >= dy) {

			while (rangeCounter <= range) {

				Tile tile = tileManager.getTile(x, y);
				boolean isSolid = tile.isSolid();

				if (breakNext)
					break;

				if (isSolid)
					breakNext = true;

				rangeCounter++;

				int tileBrightness = tile.getBrightness();

				// averaging brightness
				int temp = newBrightness + tileBrightness;

				if (temp > Tile.DEFAULT_BRIGHTNESS)
					temp = Tile.DEFAULT_BRIGHTNESS;

				tile.setBrightness((int) temp);

				if (x == xEnd)
					break;
				x += ix;
				d += dy2;
				if (d > dx) {
					y += iy;
					d -= dx2;
				}
			}
		} else {

			while (rangeCounter <= range) {

				Tile tile = tileManager.getTile(x, y);
				boolean isSolid = tile.isSolid();

				if (breakNext)
					break;

				if (isSolid)
					breakNext = true;

				rangeCounter++;

				int tileBrightness = tile.getBrightness();

				// averaging brightness
				int temp = newBrightness + tileBrightness;

				if (temp > Tile.DEFAULT_BRIGHTNESS)
					temp = Tile.DEFAULT_BRIGHTNESS;

				tile.setBrightness((int) temp);

				if (y == yEnd)
					break;
				y += iy;
				d += dx2;
				if (d > dy) {
					x += ix;
					d -= dy2;
				}
			}
		}
	}

	/**
	 * Light up the surrounding blocks within the range
	 *
	 * @deprecated
	 *
	 * @param centerX    light's x pos
	 * @param centerY    light's y pos
	 * @param range      range of light in block
	 * @param brightness 0 ~ 254 (bigger the brighter, 0 is technically not a light)
	 */
	private void lightEffect_NoShadow(int centerX, int centerY, int range,
			int brightness) {

		// no light for non-emitting light
		if (brightness == 0) {
			return;
		}

		// check the surrounding blocks by using double loops
		// and deduce the intensity of light by distance from the center position
		for (int y = centerY - range; y <= centerY + range; y++) {

			for (int x = centerX - range; x <= centerX + range; x++) {

				int distance = Utils.getDistanceManhattan(centerX, centerY, x, y);

				// the part of the square that's out of range
				if (distance > range)
					continue;

				double newBrightness;

				if (distance == 0) {
					newBrightness = brightness; // max brightness at center
				} else {
					newBrightness = (brightness / distance);
				}

				Tile tile = tileManager.getTile(x, y);
				int tileBrightness = tile.getBrightness();

				// averaging brightness
				newBrightness = newBrightness + tileBrightness;

				if (newBrightness > Tile.DEFAULT_BRIGHTNESS)
					newBrightness = Tile.DEFAULT_BRIGHTNESS;

				tile.setBrightness((int) newBrightness);

			}
		}
	}

	/**
	 * Flood-fill the surrounding tiles to get brighter area.
	 * <p>
	 * A recursive method
	 *
	 * @deprecated
	 *
	 * @param x
	 * @param y
	 * @param range
	 * @param rangeCounter
	 * @param lastBrightness
	 */
	private void lightNeighbors(int x, int y, double range, double rangeCounter,
			int lastBrightness) {

		double newBrightness = lastBrightness * (rangeCounter / range);

		Tile tile = tileManager.getTile(x, y);

		if (newBrightness <= tile.getBrightness()) {
			return;
		}

		tile.setBrightness((int) newBrightness);

		// give reduced ranged to neigbors (so it doesn't continue forever)
		rangeCounter--;

		if (rangeCounter <= 0)
			return;

		lightNeighbors(x + 1, y, range, rangeCounter, lastBrightness);
		lightNeighbors(x - 1, y, range, rangeCounter, lastBrightness);
		lightNeighbors(x, y + 1, range, rangeCounter, lastBrightness);
		lightNeighbors(x, y - 1, range, rangeCounter, lastBrightness);

	}

	/**
	 * Add a light
	 *
	 * @param light
	 */
	public static void add(Light light) {

		addLightList.add(light);

	}

	/**
	 * Remove the light
	 *
	 * @param light
	 */
	public void remove(Light light) {

		lights.remove(light);

	}

	/**
	 * Clear all the lights
	 */
	public void clear() {

		lights.clear();

	}

	/**
	 * No need to render when its out of sight
	 *
	 * @param light
	 * @return
	 */
	private boolean isOutOfSight(Light light) {

		World world = handler.getWorld();

		Player player = world.getEntityManager().getPlayer();

		int px = player.getCenterX();
		int py = player.getCenterY();
		int lx = light.getX();
		int ly = light.getY();
		int ran = light.getRange();

		int distance = Utils.getDistance(px, py, lx, ly);

		// 6 is the amount of blocks showing in the screen
		// (from player to the screen frame of the game)
		int theRange = ran + 6;

		return (distance > theRange);

	}

}
