
package dev.game.particle;

import dev.game.Handler;
import dev.game.entities.Entity;
import dev.game.gfx.Assets;

/**
 * PlayerAttackParticle.java - The particle for when player attack
 *
 * @author Juhyung Kim
 */
public class PlayerAttackParticle extends Particle {

	public PlayerAttackParticle(Handler handler, int x, int y, int direction) {

		super(handler, x, y, 500, Assets.pandaAttackParticle_down);

		switch (direction) {

		case Entity.UP:
			animation.setFrames(Assets.pandaAttackParticle_up);
			break;

		case Entity.LEFT:
			animation.setFrames(Assets.pandaAttackParticle_left);
			break;

		case Entity.RIGHT:
			animation.setFrames(Assets.pandaAttackParticle_right);
			break;

		}

	}

}
