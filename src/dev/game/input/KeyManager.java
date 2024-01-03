
package dev.game.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * KeyManager.java - manages key input
 *
 * @author j.kim3
 */
public class KeyManager implements KeyListener {

	// if pressed, true
	// index for the key is its KeyCode
	// parallel arrays
	private boolean[] keys, justPressed;

	// all keys are available to pressed at first,
	// default boolean array is all false, so i used "can't" instead of "can"
	private boolean[] cantPress;

	// various kinds of keys used
	// TODO: find use of w, s, d keys
	public boolean w, a, s, d, up, down, left, right, shift;

	/**
	 * True if key have been typed.
	 */
	public boolean keyTyped = false;
	/**
	 * The character that have been typed.
	 */
	public char typedChar;

	/**
	 * Constructor.
	 */
	public KeyManager() {

		keys = new boolean[256];
		justPressed = new boolean[256];
		cantPress = new boolean[256];

	}

	public void tick() {

		checkJustPressed();

		w = keys[KeyEvent.VK_W];
		s = keys[KeyEvent.VK_S];
		a = keys[KeyEvent.VK_A];
		d = keys[KeyEvent.VK_D];

		up = keys[KeyEvent.VK_UP];
		down = keys[KeyEvent.VK_DOWN];
		left = keys[KeyEvent.VK_LEFT];
		right = keys[KeyEvent.VK_RIGHT];

		shift = keys[KeyEvent.VK_SHIFT];

	}

	/**
	 * Check if the key is being pressed
	 */
	public void keyPressed(KeyEvent e) {

		int code = e.getKeyCode();

		if (isKeyOutOfBound(code))
			return;

		keys[code] = true;

	}

	/**
	 * Check if key is being pressed
	 *
	 * @param code
	 * @return
	 */
	public boolean keyPressed(int code) {

		if (isKeyOutOfBound(code))
			return false;

		return keys[code];

	}

	/**
	 * Only allow the key to be pressed once
	 *
	 * @param code
	 * @return
	 */
	public boolean keyJustPressed(int code) {

		if (isKeyOutOfBound(code))
			return false;

		return justPressed[code];

	}

	@Override
	public void keyReleased(KeyEvent e) {

		int code = e.getKeyCode();

		if (isKeyOutOfBound(code))
			return;

		keys[code] = false;

		char character = e.getKeyChar();

		if (keyTyped)
			return;

		if (character == KeyEvent.CHAR_UNDEFINED) {
			keyTyped = false;
			return;
		}

		// for EditText
		switch (e.getKeyCode()) {

		case KeyEvent.VK_BACK_SPACE:
		case KeyEvent.VK_DELETE:
		case KeyEvent.VK_ENTER:
			return;

		}

		typedChar = character;
		keyTyped = true;

	}

	@Override
	public void keyTyped(KeyEvent e) {

		// nothing
	}

	private boolean isKeyOutOfBound(int keyCode) {

		return (keyCode < 0 || keyCode > keys.length);

	}

	/**
	 * Allow keys to get pressed only once, To ignore long-press
	 */
	private void checkJustPressed() {

		for (int i = 0; i < keys.length; i++) {

			boolean key = keys[i];

			if (!key) {

				justPressed[i] = false;
				cantPress[i] = false;

			} else if (justPressed[i]) {

				justPressed[i] = false;

			} else if (!cantPress[i]) {

				justPressed[i] = true;
				cantPress[i] = true;

			}

		}

	}

}
