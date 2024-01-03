
package dev.game.gfx;

import dev.game.Game;
import dev.game.Handler;
import dev.game.entities.Entity;

/**
 * GameCamera.java - GameCamera to show the player
 *
 * @author j.kim3
 */
public class GameCamera {

	private float xOffset, yOffset;
	private Handler handler;

	public GameCamera(Handler handler, float xOffset, float yOffset) {

		this.handler = handler;
		this.xOffset = xOffset;
		this.yOffset = yOffset;

	}

	/**
	 * Center on entity.
	 * <p>
	 * When teleporting player, this is required to continue ticking player
	 *
	 * @param ent
	 */
	public void centerOnEntity(Entity ent) {

		// first, set the offset of camera same as the entity
		// second, move it left (screen width / 2) so the entity's left top corner is
		// at the center of the camera
		// third, move it right (entity's width / 2) so the entity is at the true
		// center
		xOffset = ent.getX() - Game.SCREEN_WIDTH / 2 + ent.getWidth() / 2;
		yOffset = ent.getY() - Game.SCREEN_HEIGHT / 2 + ent.getHeight() / 2;

	}

	/**
	 * Move the camera
	 *
	 * @param xAmount
	 * @param yAmount
	 */
	public void move(float xAmount, float yAmount) {

		xOffset += xAmount;
		yOffset += yAmount;

	}

	// getter setter
	public float getXOffset() {

		return xOffset;
	}

	public void setXOffset(float xOffset) {

		this.xOffset = xOffset;
	}

	public float getYOffset() {

		return yOffset;
	}

	public void setYOffset(float yOffset) {

		this.yOffset = yOffset;
	}

}
