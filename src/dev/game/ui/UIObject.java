
package dev.game.ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * UIObject.java - UIObject base
 *
 * @author j.kim3
 */
public abstract class UIObject {

	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected boolean hovering;

	protected boolean hidden;

	public Rectangle bounds;

	// if locked, does not respond to mouse
	protected boolean isLocked = false;

	public UIObject(int x, int y, int width, int height) {

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.hidden = false;

		bounds = new Rectangle((int) x, (int) y, width, height);

	}

	public abstract void tick();

	public abstract void render(Graphics gfx);

	/**
	 * On click.
	 */
	protected abstract void onClick();

	/**
	 * On press. Override if needed.
	 *
	 * @param e
	 */
	protected void onMousePress(MouseEvent e) {
	}

	/**
	 * On long press (0.3 second). Override if needed.
	 *
	 * @param e
	 */
	protected void onMouseLongPress(MouseEvent e) {
	}

	/**
	 * On mouse move. Only get called when the mouse is hovering over the object.
	 *
	 * @param e
	 */
	protected void onMouseMove(MouseEvent e) {

		if (!isLocked) {

			hovering = bounds.contains(e.getX(), e.getY());

		} else {

			hovering = false;

		}

	}

	/**
	 * On mouse released.
	 *
	 * @param e
	 */
	protected void onMouseRelease(MouseEvent e) {

		if (hovering)
			onClick();

	}

	/**
	 * On drag. Get called as long as the mouse button is not released. Override if
	 * needed.
	 *
	 * @param e
	 */
	protected void onMouseDrag(MouseEvent e) {

	}

	// getter setter
	/**
	 * @return the x
	 */
	public int getX() {
		return bounds.x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.bounds.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return bounds.y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.bounds.y = y;
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
	 * @return the hovering
	 */
	public boolean isHovering() {
		return hovering;
	}

	/**
	 * @param hovering the hovering to set
	 */
	public void setHovering(boolean hovering) {
		this.hovering = hovering;
	}

	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * @param hidden the hidden to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * Get if the object is not receiving mouse event.
	 *
	 * @return the isLocked
	 */
	public boolean isLocked() {
		return isLocked;
	}

	/**
	 * Set if the object should receive mouse event or not.
	 *
	 * @param isLocked the isLocked to set
	 */
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

}
