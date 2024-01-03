
package dev.game.entities.statics.placeables;

import java.awt.Graphics;
import java.awt.Rectangle;

import dev.game.Handler;

/**
 * Planning to add this entity that grows into tree
 *
 * @author Juhyung Kim
 */
public class EntitySapling extends PlaceableEntity {

	public EntitySapling(Handler handler, int id, float x, float y, int width,
			int height, int health) {
		super(handler, id, x, y, width, height, health, "Sapling");
	}

	@Override
	public void tick() {
		return; // TODO do something
	}

	@Override
	public void render(Graphics gfx) {
		return; // TODO do something
	}

	@Override
	public void place() {
		return; // TODO do something
	}

	@Override
	public void followPlayer() {
		return; // TODO do something
	}

	@Override
	public void updateSize() {
		return; // TODO do something
	}

	@Override
	protected Rectangle getNewBounds() {
		return null; // TODO do something
	}

	@Override
	public void die() {
		return; // TODO do something
	}

}
