
package dev.game.gfx;

import java.awt.image.BufferedImage;

/**
 * Animation.java - Animation for entity
 *
 * @author j.kim3
 */
public class Animation {

	private int duration; // duration to show all animation frames in milliseconds
	private int index; // index of frame array
	private BufferedImage[] frames;
	public long lastTime, timer;
	private boolean isDisposable; // when only used once
	private boolean isAlive; // this does not necessarily remove the object

	/**
	 * An animation that make image look alive.
	 *
	 * @param duration Speed to show all animation frames in milliseconds
	 * @param frames   frames of images
	 */
	public Animation(int duration, BufferedImage[] frames) {

		this.duration = duration;
		this.frames = frames;

		this.index = 0;
		this.timer = 0;
		this.lastTime = System.currentTimeMillis();
		this.isAlive = true;
		this.isDisposable = false;

	}

	/**
	 * An animation that make image look alive.
	 *
	 * @param duration     Speed to show all animation frames in milliseconds
	 * @param frames       frames of images
	 * @param isDisposable true if this animation is non-repeatable
	 */
	public Animation(int duration, BufferedImage[] frames, boolean isDisposable) {

		this(duration, frames);
		this.isDisposable = isDisposable;

	}

	public void tick() {

		if (!isAlive)
			return;

		// adding amount of time passed
		timer += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();

		// the animation was in rest for too long, reset
		if (timer > (duration / frames.length) + 100) {
			index = 0;
			timer = 0;
		}

		if (timer > duration / frames.length) {

			index++;
			timer = 0;

			// coming back to first frame
			if (index >= frames.length) {

				if (isDisposable)
					isAlive = false;

				index = 0;
			}
		}
	}

	/**
	 * Get the current frame image to show.
	 *
	 * @return
	 */
	public BufferedImage getCurrentFrame() {

		return frames[index];
	}

	/**
	 * If not alive, do not tick
	 *
	 * @return
	 */
	public boolean isAlive() {
		return this.isAlive;
	}

	/**
	 * Set the animation frames
	 *
	 * @param frames
	 */
	public void setFrames(BufferedImage[] frames) {
		this.frames = frames;
	}

	/**
	 * Reset the frame to first one.
	 */
	public void resetFrameIndex() {
		this.index = 0;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {

		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {

		this.duration = duration;
	}

	/**
	 * @return the isDisposable
	 */
	public boolean isDisposable() {

		return isDisposable;
	}

	/**
	 * @param isDisposable the isDisposable to set
	 */
	public void setDisposable(boolean isDisposable) {

		this.isDisposable = isDisposable;
	}

	/**
	 * @return the frames
	 */
	public BufferedImage[] getFrames() {

		return frames;
	}

}
