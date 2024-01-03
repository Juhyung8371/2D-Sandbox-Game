
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
import dev.game.sounds.Sound;
import dev.game.tiles.Tile;

/**
 * EntityTreant.java - Nature will rise against you!
 *
 * @author Juhyung Kim
 */
public class EntityTreant extends HostileCreature {

	/**
	 * Nature will rise against you!
	 *
	 * @param handler
	 * @param x       in pixel
	 * @param y       in pixel
	 */
	public EntityTreant(Handler handler, float x, float y) {

		super(handler, EntityId.TREANT, x, y, 30 * 4, 48 * 4,
				Player.PLAYER_ATTACK_DAMAGE * 4, 4, 3000, 1000,
				HostileCreature.DEFAULT_ATTACK_DELAY, EntityName.TREANT);

		setBounds(11 * 4, 26 * 4, 9 * 4, 17 * 4);

		setSpeed(DEFAULT_SPEED - 2);

		initAnimation();

		runnableCreature = false;

		setUpAI();

	}

	@Override
	public void setUpAI() {

		addTask(new AIWander(1, handler, this));

		addActiveTask(new AIAttack(1, handler, this, attackCoolTime,
				Tile.TILE_SIZE * 2, attackDelay, Sound.ENTITY_TREANT_ATTACK));
		addActiveTask(new AIFindTarget(2, handler, this, EntityId.PLAYER, 6));
		addActiveTask(new AIFindPathToTarget(3, handler, this));
		addActiveTask(new AIChase(4, handler, this));

	}

	@Override
	public void initAnimation() {

		anim_walk_down = new Animation(800, Assets.treant_walk_down);
		anim_walk_up = new Animation(800, Assets.treant_walk_up);
		anim_walk_right = new Animation(1000, Assets.treant_walk_right);
		anim_walk_left = new Animation(1000, Assets.treant_walk_left);
		anim_attack_up = new Animation(attackDuration, Assets.treant_attack_up);
		anim_attack_down = new Animation(attackDuration, Assets.treant_attack_down);
		anim_attack_right = new Animation(attackDuration,
				Assets.treant_attack_right);
		anim_attack_left = new Animation(attackDuration, Assets.treant_attack_left);
		anim_die = new Animation(1200, Assets.treant_die, true);

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

			setWidth(30 * 4);

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

		switch (currentDirection) {

		case Creature.UP:

			if (attacking) {

				width = 38 * 4;
				height = 66 * 4;

				gfx.drawImage(image, (int) (x - 16 - camX), (int) (y - 72 - camY),
						width, height, null);

			} else {

				width = 30 * 4;
				height = 48 * 4;

				gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width,
						height, null);

			}

			break;

		case Creature.DOWN:

			if (attacking) {

				width = 36 * 4;
				height = 64 * 4;

				gfx.drawImage(image, (int) (x - 12 - camX), (int) (y - camY), width,
						height, null);

			} else {

				width = 30 * 4;
				height = 48 * 4;

				gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width,
						height, null);

			}

			break;

		case Creature.LEFT:

			if (attacking) {

				width = 50 * 4;
				height = 48 * 4;

				gfx.drawImage(image, (int) (x - 80 - camX), (int) (y - camY), width,
						height, null);

			} else {

				width = 30 * 4;
				height = 48 * 4;

				gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width,
						height, null);

			}

			break;

		case Creature.RIGHT:

			if (attacking) {

				width = 50 * 4;
				height = 48 * 4;

				gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width,
						height, null);

			} else {

				width = 30 * 4;
				height = 48 * 4;

				gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width,
						height, null);

			}

			break;

		default:
			gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width, height,
					null);

		}
	}

	@Override
	public void die() {

		Sound.ENTITY_TREANT_DIE.play();

	}

	@Override
	protected void checkShouldDie() {

		if (!anim_die.isAlive())
			shouldDie = true;

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
