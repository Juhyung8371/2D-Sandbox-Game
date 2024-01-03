
package dev.game.ai;

import dev.game.Handler;
import dev.game.entities.Entity;

/**
 * EntityAI.java - Base for entity AI
 *
 * @author j.kim3
 */
public abstract class EntityAIBase {

	protected Handler handler;
	private Entity entity;
	private int priority; // smaller the number, bigger the priority

	public EntityAIBase(int priority, Handler handler, Entity ent) {

		this.handler = handler;
		this.entity = ent;
		this.priority = priority;

	}

	/**
	 * Check if the AI should be executed
	 *
	 * @return True if the AI should be executed
	 */
	public abstract boolean shouldExecute();

	/**
	 * The content of AI that gets executed
	 */
	public abstract void continueExecute();

	// getter
	/**
	 * @return the handler
	 */
	public Handler getHandler() {
		return handler;
	}

	/**
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

}
