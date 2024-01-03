
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
import dev.game.items.Item;
import dev.game.items.ItemId;
import dev.game.items.ItemManager;
import dev.game.sounds.Sound;
import dev.game.tiles.Tile;

/**
 * EntityWolf.java - The wolf entity
 *
 * @author j.kim3
 */
public class EntityWolf extends HostileCreature {

	private Animation anim_idle_down, anim_idle_up, anim_idle_right, anim_idle_left,
			anim_run_right, anim_run_left, anim_run_up, anim_run_down;

	public EntityWolf(Handler handler, float x, float y) {

		super(handler, EntityId.WOLF, x, y, Tile.TILE_SIZE, Tile.TILE_SIZE,
				Player.PLAYER_ATTACK_DAMAGE * 3, 3, 2000, 1000,
				HostileCreature.DEFAULT_ATTACK_DELAY, EntityName.WOLF);

		setBounds(12, 18, 40, 40);

		setSpeed(DEFAULT_SPEED);

		initAnimation();

		setUpAI();

	}

	@Override
	public void die() {

		// TODO proper loot
		Item loot = Item.getItemEntity(ItemId.STONE, (int) (x + width / 2),
				(int) (y + height / 2));

		ItemManager.addItem(loot);

		Sound.ENTITY_WOLF_DIE.play();

	}

	@Override
	public void setUpAI() {

		addTask(new AIWander(1, handler, this));

		addActiveTask(new AIAttack(1, handler, this, attackCoolTime, Tile.TILE_SIZE,
				attackDelay, Sound.ENTITY_WOLF_ATTACK));
		addActiveTask(new AIFindTarget(2, handler, this, EntityId.GOLEM, 6));
		addActiveTask(new AIFindPathToTarget(3, handler, this));
		addActiveTask(new AIChase(4, handler, this));

	}

	@Override
	public void initAnimation() {

		anim_idle_down = new Animation(2000, Assets.wolf_idle_down);
		anim_walk_down = new Animation(800, Assets.wolf_walk_down);
		anim_idle_up = new Animation(2000, Assets.wolf_idle_up);
		anim_walk_up = new Animation(800, Assets.wolf_walk_up);
		anim_idle_right = new Animation(2000, Assets.wolf_idle_right);
		anim_walk_right = new Animation(1000, Assets.wolf_walk_right);
		anim_idle_left = new Animation(2000, Assets.wolf_idle_left);
		anim_walk_left = new Animation(1000, Assets.wolf_walk_left);
		anim_run_right = new Animation(800, Assets.wolf_run_right);
		anim_run_left = new Animation(800, Assets.wolf_run_left);
		anim_run_up = new Animation(600, Assets.wolf_walk_up);
		anim_run_down = new Animation(600, Assets.wolf_walk_down);
		anim_attack_right = new Animation(attackDuration, Assets.wolf_attack_right);
		anim_attack_left = new Animation(attackDuration, Assets.wolf_attack_left);
		anim_attack_down = new Animation(attackDuration, Assets.wolf_attack_down);
		anim_die = new Animation(800, Assets.wolf_die, true);

	}

	@Override
	public void tick() {

		if (isDying)
			return;

		checkBurning();
		updateStamina();
		executeTasks();
		updateAttack();

		switch (this.getDirection()) {

		case Creature.RIGHT:
		case Creature.LEFT:
			setWidth(96);
			break;

		default:
			setWidth(64);

		}

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

			setWidth(64);
			anim_die.tick();

			gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width, height,
					null);

			checkShouldDie();

			return;

		}

		renderAnimation(gfx, image, camX, camY);

		// When moveTo() is called, its own moving variable become TRUE to tell
		// it is moving
		// And this is the right spot to make the move variable to FALSE,
		// since this is the point were the wolf's one cycle is done.
		setIsMoving(false);

		showFireEffect(gfx);

		renderHealthBar(gfx, camX, camY);

	}

	/**
	 * Update the animation tick
	 */
	private void updateAnimationTick() {

		// animation
		switch (this.getDirection()) {

		case Creature.DOWN:

			if (attacking) {

				anim_attack_down.tick();

			} else if (isMoving()) {

				if (running)
					anim_run_down.tick();
				else
					anim_walk_down.tick();

			} else {

				anim_idle_down.tick();

			}

			break;

		case Creature.UP:

			if (isMoving()) {

				if (running)
					anim_run_up.tick();
				else
					anim_walk_up.tick();

			} else {
				anim_idle_up.tick();
			}

			break;

		case Creature.RIGHT:

			if (attacking) {

				anim_attack_right.tick();

			} else if (isMoving()) {

				if (running)
					anim_run_right.tick();
				else
					anim_walk_right.tick();

			} else {
				anim_idle_right.tick();
			}
			break;

		case Creature.LEFT:

			if (attacking) {

				anim_attack_left.tick();

			} else if (isMoving()) {

				if (running)
					anim_run_left.tick();
				else
					anim_walk_left.tick();

			} else {
				anim_idle_left.tick();
			}

			break;

		}

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

		case Creature.RIGHT:
			gfx.drawImage(image, (int) (x - 12 - camX), (int) (y - camY), width,
					height, null);
			break;

		case Creature.LEFT:
			gfx.drawImage(image, (int) (x - 24 - camX), (int) (y - camY), width,
					height, null);
			break;

		default:
			gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width, height,
					null);

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

		boolean isMoving = this.isMoving();

		switch (this.getDirection()) {

		case Creature.DOWN:

			if (attacking) {

				return anim_attack_down.getCurrentFrame();

			} else if (isMoving) {

				if (running) {
					return anim_run_down.getCurrentFrame();
				} else {
					return anim_walk_down.getCurrentFrame();
				}

			} else {
				return anim_idle_down.getCurrentFrame();
			}

		case Creature.UP:

			if (isMoving) {

				if (running) {
					return anim_run_up.getCurrentFrame();
				} else {
					return anim_walk_up.getCurrentFrame();
				}

			} else {
				return anim_idle_up.getCurrentFrame();
			}

		case Creature.RIGHT:

			if (attacking) {

				return anim_attack_right.getCurrentFrame();

			} else if (isMoving) {

				if (running) {
					return anim_run_right.getCurrentFrame();
				} else {
					return anim_walk_right.getCurrentFrame();
				}

			} else {
				return anim_idle_right.getCurrentFrame();
			}

		case Creature.LEFT:

			if (attacking) {

				return anim_attack_left.getCurrentFrame();

			} else if (isMoving) {

				if (running) {
					return anim_run_left.getCurrentFrame();
				} else {
					return anim_walk_left.getCurrentFrame();
				}

			} else {
				return anim_idle_left.getCurrentFrame();
			}

		default:
			return anim_idle_down.getCurrentFrame();

		}

	}

}
