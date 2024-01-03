
package dev.game.ai;

import java.util.ArrayList;

import dev.game.Handler;
import dev.game.ai.pathfinding.Node;
import dev.game.entities.creatures.Creature;

/**
 * AIChase.java - Chase the target
 *
 * @author j.kim3
 */
public class AIChase extends EntityAIBase {

	private Creature creature;

	// index of the target path that's currently traveling to
	int pathIndex;

	public AIChase(int priority, Handler handler, Creature creature) {

		super(priority, handler, creature);

		this.creature = creature;

		this.pathIndex = -1;

	}

	/**
	 * Check if path and target is valid
	 */
	@Override
	public boolean shouldExecute() {

		return ((creature.getTarget() != null)
				&& (creature.getPathToTarget() != null));

	}

	/**
	 * Chase!!!!!!!!!!!!!!
	 */
	@Override
	public void continueExecute() {

		if (creature.isAttacking()) {

			return;

		}

		ArrayList<Node> paths = creature.getPathToTarget();

		if (paths.isEmpty()) {

			creature.setPathToTarget(null);

			return;
		}

		// if the path is updated, assign new pathIndex
		if (pathIndex == -1 || creature.getPathUpdated()) {

			pathIndex = (paths.size() - 1);

			creature.setPathUpdated(false);
		}

		// in case of out of bound
		if (pathIndex >= paths.size()) {

			pathIndex = paths.size() - 1;
		}

		Node nextPath = paths.get(pathIndex);

		// the moving part
		boolean reached = creature.moveTo(nextPath.getX(), nextPath.getY());

		if (reached && pathIndex > 0) {

			pathIndex--;

			return;
		}

		// if the creature reached the destination, set the path to null
		if (pathIndex == 0) {

			pathIndex = -1; // reset back to -1

			creature.setPathToTarget(null);

		}

	}

}
