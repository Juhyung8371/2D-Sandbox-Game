
package dev.game.ai;

import dev.game.Handler;
import dev.game.entities.Entity;
import dev.game.entities.creatures.Creature;
import dev.game.utils.Utils;
import dev.game.worlds.World;

/**
 * AIFindTarget.java - Find the target
 *
 * @author j.kim3
 */
public class AIFindTarget extends EntityAIBase {

	private int range;
	private Creature attacker;
	private int targetId;
	private Entity target = null;

	public AIFindTarget(int priority, Handler handler, Creature attacker,
			int targetId, int range) {

		super(priority, handler, attacker);

		this.attacker = attacker;
		this.range = range;
		this.targetId = targetId;

	}

	/**
	 * Find the target depend on the distance between them
	 */
	@Override
	public boolean shouldExecute() {

		if (attacker.getTarget() != null) {

			int dis = Utils.getDistance(target.getCenterX(), target.getCenterY(),
					attacker.getCenterX(), attacker.getCenterY());

			// lost the target
			if (dis > range || !target.isAlive()) {

				target = null;
				attacker.setTarget(null);
				attacker.setPathToTarget(null);

			} else {

				// already has valid target
				return false;

			}

		}

		if (attacker.getTarget() == null) {

			for (int yy = 0; yy < World.map.length; yy++) {

				for (int xx = 0; xx < World.map[0].length; xx++) {

					if (World.map[yy][xx] == null)
						continue;

					for (Entity ent : World.map[yy][xx].entities) {

						// skip the non-targeting entities
						if (ent.getEntityID() != targetId) {
							continue;
						}

						// just in case entity isn't alive
						if (!ent.isAlive()) {
							continue;
						}

						// skip itself
						if (ent.equals(attacker)) {
							continue;
						}

						int dis = Utils.getDistance(ent.getCenterX(),
								ent.getCenterY(), attacker.getCenterX(),
								attacker.getCenterY());

						if (dis <= range) {

							target = ent;

							return true;

						}
					}
				}
			}
		}

		return false;

	}

	/**
	 * Set the target
	 */
	@Override
	public void continueExecute() {

		attacker.setTarget(target);

	}

}
