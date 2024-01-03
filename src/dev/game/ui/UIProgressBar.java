
package dev.game.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import dev.game.gfx.Assets;

/**
 * UIProgressBar.java - A bar to show the progress of a value
 *
 * @author j.kim3
 */
public class UIProgressBar extends UIObject {

	private int max;
	private int min = 0;
	private int progress;
	private BufferedImage emptyProgress = Assets.progressBar[1];

	public UIProgressBar(int x, int y, int width, int height, int progress,
			int max) {

		super(x, y, width, height);

		this.max = max;

		this.progress = progress;

	}

	@Override
	public void tick() {

		// nothing really
	}

	@Override
	public void render(Graphics gfx) {

		gfx.drawImage(emptyProgress, (int) getX(), (int) getY(), getWidth(),
				getHeight(), null);

		// if have progress, render it.
		if (progress > 0) {

			double percent = getPercent();

			if ((int) (emptyProgress.getWidth() * percent) <= 0) {

				return;

			}

			BufferedImage currentProgress = Assets.progressBar[0].getSubimage(0, 0,
					(int) (emptyProgress.getWidth() * percent),
					emptyProgress.getHeight());

			gfx.drawImage(currentProgress, (int) getX(), (int) getY(),
					(int) (getWidth() * percent), getHeight(), null);

		}

	}

	@Override
	public void onClick() {

		// not receiving touch events
	}

	// getter setter
	/**
	 * @return the max
	 */
	public int getMax() {

		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(int max) {

		this.max = max;
	}

	/**
	 * @return the min
	 */
	public int getMin() {

		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(int min) {

		this.min = min;
	}

	/**
	 * Get the percent of the progress in decimal
	 *
	 * @return
	 */
	public double getPercent() {

		return (double) ((double) progress / (double) max);
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {

		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) {

		if (progress <= min) {
			this.progress = min;
			return;
		}

		this.progress = progress;
	}

}
