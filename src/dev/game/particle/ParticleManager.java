
package dev.game.particle;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import dev.game.Game;
import dev.game.Handler;
import dev.game.gfx.GameCamera;
import dev.game.tiles.Tile;

/**
 * ParticleManager.java - Manages particles
 *
 * @author Juhyung Kim
 */
public class ParticleManager {

	private Handler handler;

	private GameCamera camera;

	private static ArrayList<Particle> particles;
	// items in queue to be spawned
	private static ArrayList<Particle> addParticleList;

	public ParticleManager(Handler handler) {

		this.handler = handler;
		this.camera = handler.getGameCamera();

		particles = new ArrayList<Particle>();
		addParticleList = new ArrayList<Particle>();

	}

	public void tick() {

		addParticles();

		Iterator<Particle> iter = particles.iterator();

		while (iter.hasNext()) {

			Particle particle = iter.next();

			if (!isOutOfSight(particle)) {

				particle.tick();

				if (!particle.isAlive())
					iter.remove();

			}
		}
	}

	/**
	 * Render items in the world
	 *
	 * @param gfx
	 */
	public void render(Graphics gfx) {

		Iterator<Particle> iter = particles.iterator();

		while (iter.hasNext()) {

			Particle particle = iter.next();

			if (!isOutOfSight(particle))
				particle.render(gfx);

		}

	}

	/**
	 * Add Particle to queue so it could be spawned in world in order safely
	 *
	 * @param particle
	 */
	public static void addParticle(Particle particle) {

		addParticleList.add(particle);

	}

	/**
	 * Add all the particles in queue to world
	 */
	private void addParticles() {

		for (Particle p : addParticleList) {

			particles.add(p);

		}

		addParticleList.clear();

	}

	/**
	 * Check if item is within screen
	 *
	 * @param particle
	 * @return
	 */
	public boolean isOutOfSight(Particle particle) {

		float xOffset = camera.getXOffset();
		float yOffset = camera.getYOffset();
		float xEnd = xOffset + Game.SCREEN_WIDTH;
		float yEnd = yOffset + Game.SCREEN_HEIGHT;

		int boundX = (int) particle.getX() - Tile.TILE_SIZE;
		int boundY = (int) particle.getY() - Tile.TILE_SIZE;
		int boundMaxX = (int) particle.getX() + Tile.TILE_SIZE * 2;
		int boundMaxY = (int) particle.getY() + Tile.TILE_SIZE * 2;

		boolean result = false;

		if (boundMaxY < yOffset) { // up side
			result = true;
		} else if (boundY > yEnd) { // down side
			result = true;
		} else if (boundMaxX < xOffset) { // left side
			result = true;
		} else if (boundX > xEnd) { // right side
			result = true;
		}

		return result;

	}

}
