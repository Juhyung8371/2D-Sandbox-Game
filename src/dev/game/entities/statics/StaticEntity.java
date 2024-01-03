
package dev.game.entities.statics;

import dev.game.Handler;
import dev.game.entities.Entity;

/**
 * StaticEntity.java - Not moving entity
 *
 * @author j.kim3
 */
public abstract class StaticEntity extends Entity {

	public StaticEntity(Handler handler, int id, float x, float y, int width,
			int height, int health, String name) {

		super(handler, id, x, y, width, height, name);

		this.health = health;
		this.maxHealth = health;

	}

}
