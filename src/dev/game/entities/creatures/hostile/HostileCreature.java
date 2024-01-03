
package dev.game.entities.creatures.hostile;

import dev.game.Handler;
import dev.game.entities.creatures.Creature;
import dev.game.gfx.Animation;

/**
 * HostileCreature.java - A hostile creature that attacks other entities.
 *
 * @author Juhyung Kim
 */
public abstract class HostileCreature extends Creature {

	protected Animation anim_attack_up, anim_attack_down, anim_attack_right,
			anim_attack_left;

	public static final int DEFAULT_ATTACK_DAMAGE = 4;
	public static final int DEFAULT_ATTACK_DURATION = 1000;
	public static final int DEFAULT_COOL_TIME = 1000;
	public static final int DEFAULT_ATTACK_DELAY = 800;

	/**
	 * timer to count the time passed after attack
	 */
	private long attackStartTime, attackDurationTimer = 0;
	/**
	 * duration of attack motion in milliseconds
	 */
	protected int attackDuration;
	/**
	 * cool down time for next attack in milliseconds
	 */
	protected int attackCoolTime;
	/**
	 * Delay between the start of the attack motion and the actual attack in tick.
	 */
	protected int attackDelay;

	/**
	 * A hostile creature that attacks other entities.
	 *
	 * @param handler        handler
	 * @param id             EntityId
	 * @param x              pos in pixel
	 * @param y              pos in pixel
	 * @param width          in pixel
	 * @param height         in pixel
	 * @param health         health (also the maximum health)
	 * @param attackDamage   damage dealth per attack
	 * @param attackCoolTime cool-down time between each attack in milliseconds
	 * @param attackDuration duration of the attack in milliseconds (for animation)
	 * @param attackDelay    Delay between the start of the attack motion and the
	 *                       actual attack in millisec.
	 * @param name           name
	 */
	public HostileCreature(Handler handler, int id, float x, float y, int width,
			int height, int health, int attackDamage, int attackCoolTime,
			int attackDuration, int attackDelay, String name) {

		super(handler, id, x, y, width, height, health, name);

		this.attackDamage = attackDamage;
		this.attackCoolTime = attackCoolTime;
		this.attackDuration = attackDuration;
		this.attackDelay = attackDelay;

		this.attackStartTime = System.currentTimeMillis();

		// total duration = (delay + attack motion time)
		this.attackDuration += attackDelay;

	}

	/**
	 * A hostile creature that attacks other entities.
	 *
	 * @param handler handler
	 * @param id      EntityId
	 * @param x       pos in pixel
	 * @param y       pos in pixel
	 * @param width   in pixel
	 * @param height  in pixel
	 * @param health  health (also the maximum health)
	 * @param name    name
	 */
	public HostileCreature(Handler handler, int id, float x, float y, int width,
			int height, int health, String name) {

		this(handler, id, x, y, width, height, health, DEFAULT_ATTACK_DAMAGE,
				DEFAULT_COOL_TIME, DEFAULT_ATTACK_DURATION, DEFAULT_ATTACK_DELAY,
				name);

	}

	/**
	 * Update the variables associated with attacking.
	 * <p>
	 * Insert this in tick() method.
	 */
	protected void updateAttack() {

		if (attacking) {

			attackDurationTimer += System.currentTimeMillis() - attackStartTime;
			attackStartTime = System.currentTimeMillis();

			if (attackDurationTimer >= attackDuration) {
				attacking = false;
				attackDurationTimer = 0;

			}

		}
	}

	/**
	 * Set the start of attack time
	 *
	 * @param time
	 */
	public void setAttackStartTime(long time) {

		this.attackStartTime = time;
	}

}
