
package dev.game.particle;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import dev.game.Handler;
import dev.game.gfx.Animation;
import dev.game.gfx.GameCamera;
import dev.game.utils.Utils;

/**
 * Particle.java - PRETTY PRETTY PARTICLES
 *
 * @author Juhyung Kim
 */
public class Particle implements Cloneable {

	public static int NO_EFFECT = -1;

	private Handler handler;
	private GameCamera camera;
	private int x, y; // in pixel
	protected int width, height; // in pixel

	protected Animation animation;
	private boolean alive;
	// true if the particle should be rendered with its x,y as its middle, not
	// left-top.
	private boolean isCentered;

	// TODO some particle should be render on top of entity, some don't
	private boolean isOverEntity;

	/**
	 * Create a particle
	 *
	 * @param x        in pixel
	 * @param y        in pixel
	 * @param width    in pixel
	 * @param height   in pixel
	 * @param duration in milliseconds
	 * @param frames
	 */
	public Particle(Handler handler, int x, int y, int width, int height,
			int duration, BufferedImage[] frames) {

		this.handler = handler;
		this.camera = handler.getGameCamera();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.animation = new Animation(duration, frames, true);

		this.setCentered(false);

		this.alive = true;

	}

	/**
	 * Create a particle. Width and height are double of the image's.
	 *
	 * @param x        in pixel
	 * @param y        in pixel
	 * @param duration in milliseconds
	 * @param frames
	 */
	public Particle(Handler handler, int x, int y, int duration,
			BufferedImage[] frames) {

		this(handler, x, y, 0, 0, duration, frames);

		this.width = frames[0].getWidth() * 2;
		this.height = frames[0].getHeight() * 2;

	}

	/**
	 *
	 * @param handler
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param animation
	 */
	public Particle(Handler handler, int x, int y, int width, int height,
			Animation animation) {

		this.handler = handler;
		this.camera = handler.getGameCamera();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.animation = animation;

		this.setCentered(false);

		this.alive = true;

	}

	public void tick() {

		// ParticleManager will remove this object
		if (!alive)
			return;

		animation.tick();

		alive = animation.isAlive();

	}

	public void render(Graphics gfx) {

		BufferedImage image = animation.getCurrentFrame();
		float camX = camera.getXOffset();
		float camY = camera.getYOffset();

		if (isCentered) {

			gfx.drawImage(image, Utils.quickFloor(x - camX - (width / 2)),
					Utils.quickFloor(y - camY - (height / 2)), width, height, null);

		} else {

			gfx.drawImage(image, Utils.quickFloor(x - camX),
					Utils.quickFloor(y - camY), width, height, null);

		}
	}

	/**
	 * Get a copy this
	 */
	@Override
	public Particle clone() {

		Particle newParticle = new Particle(handler, x, y, width, height, null);

		Animation anim = new Animation(animation.getDuration(),
				animation.getFrames(), true);
		newParticle.setAnimation(anim);
		newParticle.setCentered(isCentered);

		return newParticle;

	}

	/**
	 * @return the x
	 */
	public int getX() {

		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {

		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {

		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {

		this.y = y;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {

		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {

		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {

		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {

		this.height = height;
	}

	/**
	 * @return the alive
	 */
	public boolean isAlive() {

		return alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public void setAlive(boolean alive) {

		this.alive = alive;
	}

	/**
	 * true if the particle should be rendered with its x,y as its middle, not
	 * left-top.
	 *
	 * @return the isCentered
	 */
	public boolean isCentered() {

		return isCentered;
	}

	/**
	 * @param isCentered the isCentered to set
	 */
	public void setCentered(boolean isCentered) {

		this.isCentered = isCentered;
	}

	/**
	 * @return the animation
	 */
	public Animation getAnimation() {

		return animation;
	}

	/**
	 * @param animation the animation to set
	 */
	public void setAnimation(Animation animation) {

		this.animation = animation;
	}

}
