
package dev.game.entities.statics;

import java.awt.Graphics;

import dev.game.Game;
import dev.game.Handler;
import dev.game.entities.Entity;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.entities.creatures.Creature;
import dev.game.gfx.Animation;
import dev.game.gfx.Assets;
import dev.game.lights.Light;
import dev.game.lights.LightManager;
import dev.game.tiles.Tile;
import dev.game.worlds.World;

/**
 * EntityFire.java - The FIRE!!!
 *
 * @author j.kim3
 */
public class EntityFire extends StaticEntity {

	public static final int LIGHT_RANGE = 6;
	/**
	 * Three seconds (Game.FPS)
	 */
	public static final int DEFAULT_LIFESPAN = 120; // 3 seconds

	private Animation anim_fire;
	private final int BURN_TIME = Game.FPS * 2; // burn creature for 2 seconds

	public EntityFire(Handler handler, float x, float y) {

		this(handler, x, y, DEFAULT_LIFESPAN);

	}

	public EntityFire(Handler handler, float x, float y, int lifeSpan) {

		super(handler, EntityId.FIRE, x, y, Tile.TILE_SIZE, Tile.TILE_SIZE, lifeSpan,
				EntityName.FIRE);

		anim_fire = new Animation(250, Assets.fire);

		setBounds(0, 0, width, height);

		noCollision = true;

		// Where there is Fire, there is Light!
		LightManager
				.add(new Light(this, getCenterX(), getCenterY(), LIGHT_RANGE, 255));

	}

	/**
	 * Burn any creature within range
	 */
	private void checkBurnCreature() {

		// creating the collision box to the range of the attack
		// and check if any entities are colliding (within the attack range)
		// BURN!!!!
		for (int yy = 0; yy < World.map.length; yy++) {

			for (int xx = 0; xx < World.map[0].length; xx++) {

				if (World.map[yy][xx] == null)
					continue;

				for (Entity ent : World.map[yy][xx].entities) {

					// only creature can burn
					if (!(ent instanceof Creature)) {
						continue;
					}

					Creature cre = (Creature) ent;

					if (getBounds(0, 0).intersects(ent.getBounds(0, 0))) {

						// update fire effect pos
						if (cre.getFireTick() == 0) {

							cre.updateFireEffect();

						}

						cre.setFireTick(BURN_TIME); // 2 sec

					}

				}
			}
		}

	}

	@Override
	public void tick() {

		hurt(1); // reducing health so fire can extinguish itself later

		anim_fire.tick();

		checkBurnCreature();

	}

	@Override
	public void render(Graphics gfx) {

		gfx.drawImage(anim_fire.getCurrentFrame(), (int) (x - camera.getXOffset()),
				(int) (y - camera.getYOffset()), width, height, null);

	}

	@Override
	public void die() {
		// extinguished...
	}

}
