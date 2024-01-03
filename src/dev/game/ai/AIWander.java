
package dev.game.ai;

import java.util.Random;

import dev.game.Handler;
import dev.game.entities.creatures.Creature;

/**
 * AIWander.java - Wander around when there's nothing to do
 *
 * @author j.kim3
 */
public class AIWander extends EntityAIBase {

	private Creature creature;

	private int counter = 0;

	private int randomDir = -1; // default

	/**
	 * Wander around when there's nothing to do
	 *
	 * @param priority lower value, higher priority
	 * @param handler
	 * @param creature this creature
	 */
	public AIWander(int priority, Handler handler, Creature creature) {

		super(priority, handler, creature);

		this.creature = creature;

	}

	@Override
	public boolean shouldExecute() {

		return (creature.getTarget() == null);

	}

	@Override
	public void continueExecute() {

		counter++;

		// if counter is over, or collided. change direction
		if (((counter / 45) > 1) || creature.isCollided() || randomDir == -1) {

			randomDir = new Random().nextInt(4); // Creature direction is (0, 1, 2,
													// 3)

			counter = 0;

		}

		creature.setXMove(0);
		creature.setYMove(0);

		float speed = creature.getSpeed() / 2;

		if (randomDir == Creature.UP)
			creature.setYMove(-speed);

		else if (randomDir == Creature.DOWN)
			creature.setYMove(speed);

		else if (randomDir == Creature.LEFT)
			creature.setXMove(-speed);

		else if (randomDir == Creature.RIGHT)
			creature.setXMove(speed);

	}

}
