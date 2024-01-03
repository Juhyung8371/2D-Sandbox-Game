
package dev.game.ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import dev.game.gfx.Assets;

/**
 * UIScrollBar.java - A vertical scroll-bar that allows scrolling the thumb or
 * pressing the up/down buttons to dynamically set the progress.
 *
 * @author Juhyung Kim
 */
public class UIScrollBar extends UIObject {

	private int oldProgress = 0;
	private int progress = 0;
	private int minProgress = 0;
	private int maxProgress = 0;

	private int thumbOriY, thumbOldY, thumbNewY;

	// number of pixels to move for a progress
	private float progressToPixel;

	private boolean upHovering, downHovering, thumbHovering;

	private Rectangle upBtnBounds, downBtnBounds, thumbBounds;

	private boolean thumbPressed;

	private boolean isProgressUpdated = false;

	private int bodyHeight;

	/**
	 * A vertical scroll-able bar that allows scrolling the thumb or pressing the
	 * up/down buttons to dynamically set the progress. Specified max progress value
	 * is excluded (actual max = <code>maxProgress</code> - 1). If the
	 * <code>maxProgress</code> is only 1, thumb to not render.
	 *
	 * @param x           X position.
	 * @param y           Y position.
	 * @param width       Width of the whole bar.
	 * @param height      Height of the whole bar.
	 * @param maxProgress Maximum progress that is > 0 and it's exclusive. Minimum
	 *                    is always 0.
	 */
	public UIScrollBar(int x, int y, int width, int height, int maxProgress) {

		super(x, y, width, height);

		this.maxProgress = maxProgress;

		bodyHeight = (height - width * 2);

		progressToPixel = (float) bodyHeight / (float) maxProgress;

		upBtnBounds = new Rectangle((int) x, (int) y, width, width);
		downBtnBounds = new Rectangle((int) x, (int) y + height - width, width,
				width);

		thumbBounds = new Rectangle((int) x, (int) y + width, width,
				(int) progressToPixel);

		thumbOriY = thumbBounds.y;
		thumbOldY = thumbBounds.y;

	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics gfx) {

		if (oldProgress != progress && !isProgressUpdated) {

			isProgressUpdated = true;
			oldProgress = progress;

		}

		if (upHovering) {

			gfx.drawImage(Assets.scroll_up_pressed, upBtnBounds.x, upBtnBounds.y,
					upBtnBounds.width, upBtnBounds.height, null);

		} else {

			gfx.drawImage(Assets.scroll_up_unpressed, upBtnBounds.x, upBtnBounds.y,
					upBtnBounds.width, upBtnBounds.height, null);

		}

		if (downHovering) {

			gfx.drawImage(Assets.scroll_down_pressed, downBtnBounds.x,
					downBtnBounds.y, downBtnBounds.width, downBtnBounds.height,
					null);

		} else {

			gfx.drawImage(Assets.scroll_down_unpressed, downBtnBounds.x,
					downBtnBounds.y, downBtnBounds.width, downBtnBounds.height,
					null);

		}

		gfx.drawImage(Assets.scroll_body, upBtnBounds.x,
				upBtnBounds.y + upBtnBounds.height, upBtnBounds.width,
				getHeight() - upBtnBounds.height * 2, null);

		// thumb at the top over the body part
		// and do not render when the max progress is 1 (looks weird)
		if (maxProgress > 1)
			gfx.drawImage(Assets.scroll_thumb, thumbBounds.x, thumbBounds.y,
					thumbBounds.width, thumbBounds.height, null);

	}

	@Override
	public void onClick() {
		// no click event for this
		// at least not as a whole bar
	}

	@Override
	public void onMousePress(MouseEvent e) {

		if (thumbHovering)
			thumbPressed = true;

	}

	@Override
	public void onMouseLongPress(MouseEvent e) {

		if (upHovering) {
			decreaseProgress();
		} else if (downHovering) {
			increaseProgress();
		}

	}

	@Override
	public void onMouseMove(MouseEvent e) {

		if (isLocked) {

			upHovering = false;
			downHovering = false;
			thumbHovering = false;

		} else {

			upHovering = upBtnBounds.contains(e.getX(), e.getY());
			downHovering = downBtnBounds.contains(e.getX(), e.getY());
			thumbHovering = thumbBounds.contains(e.getX(), e.getY());

		}
	}

	@Override
	public void onMouseDrag(MouseEvent e) {

		if (thumbPressed) {

			thumbNewY = e.getY();

			int move = thumbNewY - thumbOldY;

			thumbBounds.y += move;

			// limiting the thumb moving
			if (thumbBounds.y < upBtnBounds.y + upBtnBounds.height) {

				thumbBounds.y = upBtnBounds.y + upBtnBounds.height;

			} else if (thumbBounds.y + thumbBounds.height > downBtnBounds.y) {

				thumbBounds.y = downBtnBounds.y - thumbBounds.height;

			}

			updateProgress();

			thumbOldY = thumbNewY;

		}

	}

	@Override
	public void onMouseRelease(MouseEvent e) {

		if (upHovering) {

			decreaseProgress();

		} else if (downHovering) {

			increaseProgress();

		}

		// putting the thumbOldY back to within the bound
		// or later the thumb could fly off to the one side
		if (thumbOldY < y + upBtnBounds.height)
			thumbOldY = y + upBtnBounds.height;
		else if (thumbOldY > downBtnBounds.y)
			thumbOldY = downBtnBounds.y;

		thumbPressed = false;

	}

	//////// methods ////////////
	/**
	 * Increase the progress by 1.
	 */
	public void increaseProgress() {

		// actual max excluded, so -1
		if (progress < maxProgress - 1) {

			progress++;
			updateThumbPos();

		}

	}

	/**
	 * Decrease the progress by 1.
	 */
	public void decreaseProgress() {

		if (progress > minProgress) {

			progress--;
			updateThumbPos();

		}

	}

	/**
	 * Update the position of the thumb by the progress.
	 */
	public void updateThumbPos() {

		thumbBounds.y = (int) (thumbOriY + progress * progressToPixel);
		thumbOldY = thumbBounds.y + thumbBounds.height / 2;

	}

	/**
	 * Update the progress by the position of the thumb.
	 */
	public void updateProgress() {

		progress = (int) ((thumbBounds.y - thumbOriY) / progressToPixel);

	}

	// getter setter //////////////
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

		this.progress = progress;
	}

	/**
	 * @return the maxProgress
	 */
	public int getMaxProgress() {

		return maxProgress;
	}

	/**
	 * @param max
	 */
	public void setMaxProgress(int max) {

		this.maxProgress = max;

		progressToPixel = (float) bodyHeight / (float) maxProgress;

		thumbBounds = new Rectangle((int) x, thumbBounds.y, width,
				(int) progressToPixel);

		// if thumb escape the body
		if (thumbBounds.y + thumbBounds.height > downBtnBounds.y) {

			thumbBounds.y = downBtnBounds.y - thumbBounds.height;

		}

	}

	/**
	 * Get if the progress have been updated.
	 * <p>
	 * <i><b>[IMPORTANT] If this method returns true, set it back to false to update
	 * this again.</b></i>
	 *
	 * @return the isProgressUpdated
	 */
	public boolean isProgressUpdated() {

		return isProgressUpdated;
	}

	/**
	 * <i><b>[IMPORTANT] If <code>isProgressUpdated()</code> returns true, set it
	 * back to false to update this again.</b></i>
	 *
	 * @param isProgressUpdated the isProgressUpdated to set
	 */
	public void setProgressUpdated(boolean isProgressUpdated) {

		this.isProgressUpdated = isProgressUpdated;
	}

}
