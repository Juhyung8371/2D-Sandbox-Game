
package dev.game.ai.pathfinding;

import java.util.ArrayList;

import dev.game.Handler;
import dev.game.ai.EntityAIBase;
import dev.game.entities.Entity;
import dev.game.entities.creatures.Creature;

/**
 * AIFindPathToTarget.java - Find path to target
 *
 * @author j.kim3
 */
public class AIFindPathToTarget extends EntityAIBase {

	private Creature creature;

	private int targetOldX;
	private int targetOldY;

	public AIFindPathToTarget(int priority, Handler handler, Creature creature) {

		super(priority, handler, creature);

		this.creature = creature;

		// default
		this.targetOldX = -1;
		this.targetOldY = -1;

	}

	/**
	 * Check the distance to target
	 */
	@Override
	public boolean shouldExecute() {

		if (creature.getTarget() != null) {

			Entity target = creature.getTarget();

			int x = target.getCenterX();
			int y = target.getCenterY();

			if (targetOldX == -1) {

				targetOldX = x;
				targetOldY = y;

			}

			boolean xDiff = (targetOldX == x);
			boolean yDiff = (targetOldY == y);

			// if the target changed its position, find new path
			if (!xDiff || !yDiff) {

				creature.setPathToTarget(null);

			}

		}

		return (creature.getTarget() != null && creature.getPathToTarget() == null);

	}

	/**
	 * Get the path to target
	 * <p>
	 */
	@Override
	public void continueExecute() {

		Entity target = creature.getTarget();

		PathFinding path = new PathFinding(getHandler(), creature, target);

		ArrayList<Node> paths = path.getPath();

		creature.setPathToTarget(paths);

		targetOldX = target.getCenterX();
		targetOldY = target.getCenterY();

		creature.setPathUpdated(true);

	}

}
