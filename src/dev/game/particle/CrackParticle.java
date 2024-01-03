
package dev.game.particle;

import dev.game.Handler;
import dev.game.gfx.Assets;

/**
 * CrackParticle.java - Description
 *
 * @author Juhyung Kim
 */
public class CrackParticle extends Particle {

	public CrackParticle(Handler handler, int x, int y) {

		super(handler, x, y, 2000, Assets.crackParticle);

		width *= 1.5;
		height *= 1.5;

	}

}
