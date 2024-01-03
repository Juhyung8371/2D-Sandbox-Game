
package dev.game.ai;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import dev.game.Handler;
import dev.game.entities.Entity;
import dev.game.entities.creatures.Creature;
import dev.game.entities.creatures.hostile.HostileCreature;
import dev.game.gfx.Assets;
import dev.game.gfx.GameCamera;
import dev.game.gfx.Text;
import dev.game.particle.Particle;
import dev.game.particle.ParticleManager;
import dev.game.sounds.Sound;
import dev.game.tiles.Tile;

/**
 * AIAttack.java - Attack entity AI
 *
 * @author j.kim3
 */
public class AIAttack extends EntityAIBase {

	private HostileCreature creature;
	/**
	 * The counter for <code>period</code>.
	 */
	private int coolTime = 0;
	/**
	 * Time to wait for next attack in milliseconds.
	 */
	private int period;
	/**
	 * Range of the attack in pixel (attack box collision size).
	 */
	private int attackRange;
	/**
	 * The last time the creature attacked.
	 */
	private long lastAttackTime;
	/**
	 * Delay between the start of the attack motion and the actual attack in
	 * millisec.
	 */
	private int attackDelay;
	/**
	 * Counter for <code>attackDelay</code>.
	 */
	private long lastAttackDelay = 0;
	/**
	 * True if it have attacked (to prevent attacking multiple times in a given
	 * attack duration)
	 */
	private boolean haveAttacked;
	private boolean startAttacking;
	private Particle attackParticle = null;
	private Rectangle attackRangeBounds;
	private Sound attackSound;

	/**
	 * Allow this creature to attack the target if it has one.
	 *
	 * @param priority    priority of this task
	 * @param handler     handler
	 * @param creature    to execute attack
	 * @param period      time to wait for next attack in milliseconds
	 * @param attackRange range of the attack in pixel (attack box collision size)
	 * @param attackDelay Delay between the start of the attack motion and the
	 *                    actual attack in millisec.
	 * @param attackSound
	 */
	public AIAttack(int priority, Handler handler, HostileCreature creature,
			int period, int attackRange, int attackDelay, Sound attackSound) {

		super(priority, handler, creature);

		this.creature = creature;
		this.period = period;
		this.attackRange = attackRange;
		this.attackSound = attackSound;

		this.attackDelay = attackDelay;
		this.startAttacking = false;

		this.attackRangeBounds = new Rectangle();

		attackRangeBounds.width = this.attackRange;
		attackRangeBounds.height = this.attackRange;

		lastAttackTime = System.currentTimeMillis();
		lastAttackDelay = System.currentTimeMillis();

	}

	/**
	 * Check if the target is within the range Also check the attack coolTime
	 */
	@Override
	public boolean shouldExecute() {

		if (creature.isAttacking())
			return true;

		// reseting everything
		if (haveAttacked) {
			haveAttacked = false;
			lastAttackTime = System.currentTimeMillis();
			startAttacking = false;
		}

		if (coolTime < period) {
			coolTime += System.currentTimeMillis() - lastAttackTime;
			lastAttackTime = System.currentTimeMillis();
		}

		Entity target = creature.getTarget();

		if (target == null)
			return false;

		boolean inRange = isWithinRange();

		lastAttackDelay = System.currentTimeMillis();

		return (coolTime >= period && inRange);

	}

	/**
	 * Attack
	 */
	@Override
	public void continueExecute() {

		Entity target = creature.getTarget();

		if (!startAttacking) {

			creature.setAttackStartTime(System.currentTimeMillis());
			startAttacking = true;

		}

		creature.setAttacking(true);
		coolTime = 0;

		// damaging part is done, but attack animation would still continue in entity
		// class
		if (haveAttacked)
			return;

		if (System.currentTimeMillis() - lastAttackDelay > attackDelay) {

			// target might have ran away from the range
			if (isWithinRange())
				target.hurt(creature.getAttackDamage());

			showAttackParticle();
			attackSound.play();
			haveAttacked = true;

		}

	}

	/**
	 * Testing purpose code, it shows some information
	 *
	 * @param gfx
	 */
	public void render(Graphics gfx) {

		float x = creature.getX();
		float y = creature.getY();
		GameCamera cam = handler.getGameCamera();
		float camX = cam.getXOffset();
		float camY = cam.getYOffset();
		int renderX = (int) (x - camX) + Tile.TILE_SIZE / 2;
		int renderY = (int) (y - camY) + Tile.TILE_SIZE / 2;

		Text.drawString(gfx, "targetNull? " + (creature.getTarget() == null),
				renderX, renderY - 80, Color.WHITE, Assets.cordFont, true);

		Color c2 = (creature.isAttacking()) ? Color.RED : Color.WHITE;

		Text.drawString(gfx, "isAttacking? " + (creature.isAttacking()), renderX,
				renderY - 70, c2, Assets.cordFont, true);

		Color c3 = (haveAttacked) ? Color.RED : Color.WHITE;

		Text.drawString(gfx, "haveAttacked? " + (haveAttacked), renderX,
				renderY - 60, c3, Assets.cordFont, true);

		Text.drawString(gfx, "cool? " + (coolTime), renderX, renderY - 50,
				Color.WHITE, Assets.cordFont, true);

		Text.drawString(gfx,
				"delay? " + ((System.currentTimeMillis() - lastAttackDelay) + " > "
						+ attackDelay),
				renderX, renderY - 40, Color.WHITE, Assets.cordFont, true);

	}

	/**
	 * Show the attack Particle, if has one
	 */
	private void showAttackParticle() {

		if (attackParticle == null)
			return;

		Particle newParticle = attackParticle.clone();

		newParticle.setX((int) attackRangeBounds.getCenterX());
		newParticle.setY((int) attackRangeBounds.getCenterY());

		ParticleManager.addParticle(newParticle);

	}

	/**
	 * Check if the attckRange intersects with target collision bounds.
	 *
	 * @return
	 */
	private boolean isWithinRange() {

		Rectangle creatureBounds = creature.getBounds(0, 0);

		int rangeSize = this.attackRange;

		int rangeHalf = rangeSize / 2;

		switch (creature.getDirection()) {

		case Creature.UP:
			attackRangeBounds.x = (int) (creatureBounds.getCenterX() - rangeHalf);
			attackRangeBounds.y = (int) (creatureBounds.y - rangeSize);
			break;

		case Creature.DOWN:
			attackRangeBounds.x = (int) (creatureBounds.getCenterX() - rangeHalf);
			attackRangeBounds.y = (int) (creatureBounds.y + creatureBounds.height);
			break;

		case Creature.LEFT:
			attackRangeBounds.x = (int) (creatureBounds.x - rangeSize);
			attackRangeBounds.y = (int) (creatureBounds.getCenterY() - rangeHalf);
			break;

		case Creature.RIGHT:
			attackRangeBounds.x = (int) (creatureBounds.x + creatureBounds.width);
			attackRangeBounds.y = (int) (creatureBounds.getCenterY() - rangeHalf);
			break;

		}

		Entity target = creature.getTarget();

		return attackRangeBounds.intersects(target.getBounds(0, 0));

	}

	/**
	 * Set the particle that appears when attack. X and Y do not matter, put 0,0
	 * might as well
	 *
	 * @param particle
	 */
	public void setAttackParticle(Particle particle) {

		this.attackParticle = particle;
	}

}
