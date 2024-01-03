
package dev.game.entities.creatures.hostile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import dev.game.Handler;
import dev.game.ai.AIAttack;
import dev.game.ai.AIChase;
import dev.game.ai.AIFindTarget;
import dev.game.ai.AIWander;
import dev.game.ai.pathfinding.AIFindPathToTarget;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.entities.creatures.Creature;
import dev.game.entities.creatures.Player;
import dev.game.gfx.Animation;
import dev.game.gfx.Assets;
import dev.game.particle.CrackParticle;
import dev.game.particle.Particle;
import dev.game.sounds.Sound;
import dev.game.tiles.Tile;

/**
 * EntityGolem.java - The golem entity.
 *
 * @author Juhyung Kim
 */
public class EntityGolem extends HostileCreature {

	public EntityGolem(Handler handler, float x, float y) {

		super(handler, EntityId.GOLEM, x, y, 64 * 2, 64 * 2,
				Player.PLAYER_ATTACK_DAMAGE * 6, 6, 3000, 1000,
				HostileCreature.DEFAULT_ATTACK_DELAY, EntityName.GOLEM);

		setBounds(21 * 2, 36 * 2, 25 * 2, 20 * 2);

		setSpeed(DEFAULT_SPEED - 2);

		runnableCreature = false;

		initAnimation();
		setUpAI();

	}

	@Override
	public void setUpAI() {

		addTask(new AIWander(1, handler, this));

		AIAttack attackAI = new AIAttack(1, handler, this, attackCoolTime,
				(int) (Tile.TILE_SIZE * 1.5), attackDelay,
				Sound.ENTITY_GOLEM_ATTACK);

		Particle par = new CrackParticle(handler, 0, 0);
		par.setCentered(true);

		attackAI.setAttackParticle(par);

		addActiveTask(attackAI);
		addActiveTask(new AIFindTarget(2, handler, this, EntityId.WOLF, 6));
		addActiveTask(new AIFindPathToTarget(3, handler, this));
		addActiveTask(new AIChase(4, handler, this));

	}

	@Override
	public void initAnimation() {

		anim_walk_down = new Animation(1000, Assets.golem_walk_down);
		anim_walk_up = new Animation(1000, Assets.golem_walk_up);
		anim_walk_right = new Animation(1000, Assets.golem_walk_right);
		anim_walk_left = new Animation(1000, Assets.golem_walk_left);
		anim_attack_up = new Animation(attackDuration, Assets.golem_attack_up);
		anim_attack_down = new Animation(attackDuration, Assets.golem_attack_down);
		anim_attack_right = new Animation(attackDuration, Assets.golem_attack_right);
		anim_attack_left = new Animation(attackDuration, Assets.golem_attack_left);
		anim_die = new Animation(1500, Assets.golem_die, true);

	}

	@Override
	public void tick() {

		if (isDying)
			return;

		checkBurning();
		executeTasks();
		updateAttack();
		updateAnimationTick();
		move();
		checkDeath();

	}

	@Override
	public void render(Graphics gfx) {

		float camX = camera.getXOffset();
		float camY = camera.getYOffset();
		BufferedImage image = getCurrentAnimationFrame();

		if (isDying) {

			setWidth(64 * 2);
			anim_die.tick();

			gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width, height,
					null);

			checkShouldDie();

			return;

		}

		renderAnimation(gfx, image, camX, camY);

		setIsMoving(false);

		showFireEffect(gfx);

		renderHealthBar(gfx, camX, camY);

	}

	/**
	 * Render the animation
	 *
	 * @param gfx
	 * @param image
	 * @param camX
	 * @param camY
	 */
	private void renderAnimation(Graphics gfx, BufferedImage image, float camX,
			float camY) {

		int normalWidth = 64 * 2;
		int normalHeight = 64 * 2;
		int attackHeight = 96 * 2;
		int attackOffsetY = 32 * 2;
		int attackOffsetX = 14;

		if (attacking) {

			switch (getDirection()) {

			case UP:
			case DOWN:
				gfx.drawImage(image, (int) (x - camX),
						(int) (y - camY - attackOffsetY), normalWidth, attackHeight,
						null);
				break;

			case LEFT:
				gfx.drawImage(image, (int) (x - camX - attackOffsetX),
						(int) (y - camY - attackOffsetY), normalWidth, attackHeight,
						null);
				break;

			case RIGHT:
				gfx.drawImage(image, (int) (x - camX + attackOffsetX),
						(int) (y - camY - attackOffsetY), normalWidth, attackHeight,
						null);
				break;

			}

		} else {

			gfx.drawImage(image, (int) (x - camX), (int) (y - camY), normalWidth,
					normalHeight, null);

		}

	}

	@Override
	public void die() {

		Sound.ENTITY_GOLEM_DIE.play();

	}

	/**
	 * Update the animation tick
	 */
	private void updateAnimationTick() {

		// animation
		switch (currentDirection) {

		case Creature.DOWN:

			if (attacking)
				anim_attack_down.tick();
			else
				anim_walk_down.tick();
			break;

		case Creature.UP:

			if (attacking)
				anim_attack_up.tick();
			else
				anim_walk_up.tick();
			break;

		case Creature.RIGHT:

			if (attacking)
				anim_attack_right.tick();
			else
				anim_walk_right.tick();
			break;

		case Creature.LEFT:

			if (attacking)
				anim_attack_left.tick();
			else
				anim_walk_left.tick();
			break;

		}
	}

	@Override
	protected void checkShouldDie() {

		if (!anim_die.isAlive())
			shouldDie = true;

	}

	/**
	 * To update the animation
	 *
	 * @return
	 */
	private BufferedImage getCurrentAnimationFrame() {

		if (isDying)
			return anim_die.getCurrentFrame();

		switch (this.getDirection()) {

		case Creature.DOWN:

			if (attacking)
				return anim_attack_down.getCurrentFrame();
			else
				return anim_walk_down.getCurrentFrame();

		case Creature.UP:

			if (attacking)
				return anim_attack_up.getCurrentFrame();
			else
				return anim_walk_up.getCurrentFrame();

		case Creature.RIGHT:

			if (attacking)
				return anim_attack_right.getCurrentFrame();
			else
				return anim_walk_right.getCurrentFrame();

		case Creature.LEFT:

			if (attacking)
				return anim_attack_left.getCurrentFrame();
			else
				return anim_walk_left.getCurrentFrame();

		default:
			return anim_walk_down.getCurrentFrame();

		}

	}

}
