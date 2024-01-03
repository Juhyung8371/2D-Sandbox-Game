
package dev.game.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import dev.game.ui.UIManager;

/**
 * MouseManager.java - manage mouse events
 *
 * @author j.kim3
 */
public class MouseManager implements MouseListener, MouseMotionListener {

	private boolean leftPressed, rightPressed, leftLongPressed;

	private int mouseX, mouseY;

	// timer for left button long press detection
	private long leftLastTime, leftTimer = 0;

	private UIManager uiManager;

	private MouseEvent mouseEvent = null;

	/**
	 * Constructor.
	 */
	public MouseManager() {
		// empty...
	}

	public void tick() {

		if (uiManager == null)
			return;

		if (leftPressed && mouseEvent != null) {

			leftTimer += System.currentTimeMillis() - leftLastTime;

			leftLastTime = System.currentTimeMillis();

			// if pressed long enough (0.3 second)
			if (leftTimer > 300) {

				leftLongPressed = true;

				mouseLongPressed(mouseEvent);

			}

		} else {

			leftTimer = 0;

			leftLongPressed = false;

		}

	}

	public void mouseLongPressed(MouseEvent e) {

		if (uiManager != null)
			uiManager.onMouseLongPress(e);

	}

	//////////// getter setter //////////////
	public boolean hasUIManager() {
		return (uiManager != null);
	}

	public UIManager getUIManager() {
		return uiManager;
	}

	/**
	 * This need to be set to null when changing the state
	 *
	 * @param uiManager
	 */
	public void setUIManager(UIManager uiManager) {
		this.uiManager = uiManager;
	}

	public boolean isLeftPressed() {
		return leftPressed;
	}

	public boolean isLeftLongPressed() {
		return leftLongPressed;
	}

	public boolean isRightPressed() {
		return rightPressed;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	////// implements///////////
	@Override
	public void mouseClicked(MouseEvent e) {
		// nothing
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		mouseX = e.getX();
		mouseY = e.getY();

		if (uiManager != null) {
			uiManager.onMouseMove(e);
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {

		// BUTTON2 is middle button, by the way
		int btn = e.getButton();

		if (btn == MouseEvent.BUTTON1) {

			leftPressed = true;

			leftLastTime = System.currentTimeMillis();

			mouseEvent = e;

		}

		if (btn == MouseEvent.BUTTON3)
			rightPressed = true;

		if (uiManager != null) {
			uiManager.onMousePress(e);
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		int btn = e.getButton();

		if (btn == MouseEvent.BUTTON1) {
			leftPressed = false;
			leftLongPressed = false;
		}

		if (btn == MouseEvent.BUTTON3)
			rightPressed = false;

		if (uiManager != null)
			uiManager.onMouseRelease(e);

	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if (uiManager != null) {
			mouseEvent = e;
			uiManager.onMouseDrag(e);
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
