
package dev.game.lights;

import dev.game.entities.Entity;

/**
 * Light.java - Let there be Light!
 *
 * @author j.kim3
 */
public class Light {

	// light need source to emit
	private Entity source;

	private int x, y, range; // in block

	private int brightness;

	// true is light is this available
	private boolean isAlive = true;

	public Light(Entity source, int x, int y, int range, int brightness) {

		this.source = source;

		this.x = x;
		this.y = y;
		this.brightness = brightness;

		this.range = range;

	}

	public void tick() {

		// light need source to exist
		if (source == null || !source.isAlive()) {
			isAlive = false;
		}

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
	 * @param brightness the brightness to set (0 ~ 254)
	 */
	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	/**
	 * @return the range
	 */
	public int getRange() {
		return range;
	}

	/**
	 * @param range the range to set
	 */
	public void setRange(int range) {
		this.range = range;
	}

	public boolean isAlive() {
		return isAlive;
	}

}
