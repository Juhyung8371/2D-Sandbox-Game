
package dev.game.states;

import java.awt.Graphics;

import dev.game.Handler;
import dev.game.ui.UIManager;

/**
 * State.java - Base of every state in game
 *
 * @author j.kim3
 */
public abstract class State {

	/**
	 * custom save file extension (Panda Game Save-file)
	 */
	public static final String EXTENSION = ".pgsf";
	public static final String SAVEFILE_DIR = "SaveFile";
	public static final String PLAYER_FILE_NAME = "Player";
	public static final String CHUNKS_DIR = "Chunks";
	public static final String SEEDS_NAME = "Seeds";
	public static final String INV_FILE_NAME = "inventory";

	private static State currentState = null;

	protected UIManager uiManager;

	protected Handler handler;

	public State(Handler handler) {

		this.handler = handler;

	}

	public abstract void tick();

	public abstract void render(Graphics gfx);

	// getter setter
	public static void setState(State state) {

		currentState = state;

	}

	public static State getState() {

		return currentState;

	}

	/**
	 * @return the uiManager
	 */
	public UIManager getUiManager() {

		return uiManager;
	}

}
