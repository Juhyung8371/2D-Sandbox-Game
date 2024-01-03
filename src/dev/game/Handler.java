
package dev.game;

import dev.game.gfx.GameCamera;
import dev.game.input.KeyManager;
import dev.game.input.MouseManager;
import dev.game.worlds.World;

/**
 * Handler.java - Handler to give developer easy access to many objects
 *
 * @author j.kim3
 */
public class Handler {

	private Game game;
	private World world;

	public Handler(Game game) {

		this.game = game;

	}

	public GameCamera getGameCamera() {

		return game.getGameCamera();
	}

	public KeyManager getKeyManager() {

		return game.getKeyManager();
	}

	public MouseManager getMouseManager() {

		return game.getMouseManager();
	}

	public Game getGame() {

		return game;
	}

	public void setGame(Game game) {

		this.game = game;
	}

	public World getWorld() {

		return world;
	}

	public void setWorld(World world) {

		this.world = world;
	}

}
