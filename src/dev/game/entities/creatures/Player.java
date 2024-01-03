
package dev.game.entities.creatures;

import static dev.game.sounds.Sound.PLAYER_ATTACK;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import dev.game.Game;
import dev.game.Handler;
import dev.game.crafting.CraftingManager;
import dev.game.entities.Entity;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.gfx.Animation;
import dev.game.gfx.Assets;
import dev.game.input.KeyManager;
import dev.game.inventory.Inventory;
import dev.game.particle.ParticleManager;
import dev.game.particle.PlayerAttackParticle;
import dev.game.sounds.Sound;
import dev.game.states.State;
import dev.game.tiles.Tile;
import dev.game.ui.UIManager;
import dev.game.ui.UIProgressBar;
import dev.game.worlds.World;

/**
 * Player.java - The player entity. Even though <code>Player</code> is not
 * classified as <code>HostileCreature</code>, it can attack.
 *
 * @author j.kim3
 */
public class Player extends Creature {

	public final static int PLAYER_ATTACK_DAMAGE = 4;

	public final static int PLAYER_DEAULT_HEALTH = 20;

	public final static float PLAYER_RUN_SPEED = 8;
	public final static float PLAYER_WALK_SPEED = 5;

	private final int ATTACK_COOL = Game.FPS / 2; // in tick
	private final int ATTACK_DUR_TICK = Game.FPS / 2; // in tick

	private int attackTimer = ATTACK_COOL; // start with ready to attack

	private Animation anim_idle_down, anim_idle_up, anim_idle_right, anim_idle_left,
			anim_run_right, anim_run_left, anim_run_up, anim_run_down,
			anim_attack_up, anim_attack_down, anim_attack_left, anim_attack_right;

	private int attackAnimationDuration = 500; // milliseconds

	// inventory
	private Inventory inventory;

	// crafting
	private CraftingManager craftingManager;

	// health bar
	private UIProgressBar bar = null;

	/**
	 * The player entity. Even though <code>Player</code> is not classified as
	 * <code>HostileCreature</code>, it can attack.
	 *
	 * @param handler handler
	 * @param x       pos in pixel
	 * @param y       pos in pixel
	 * @param width   in pixel
	 * @param height  in pixel
	 * @param health  health (also the maximum health)
	 */
	public Player(Handler handler, float x, float y, int width, int height,
			int health) {

		super(handler, EntityId.PLAYER, x, y, width, height, health,
				EntityName.PLAYER);

		setBounds(12, 18, 40, 40);

		this.maxHealth = Player.PLAYER_DEAULT_HEALTH;

		this.inventory = new Inventory(handler);
		this.craftingManager = new CraftingManager(handler);

		// player deserve to run longer than monsters
		maxStamina *= 2;

		attackDamage = PLAYER_ATTACK_DAMAGE;

		// animations
		initAnimation();

	}

	/**
	 * The player entity. Even though <code>Player</code> is not classified as
	 * <code>HostileCreature</code>, it can attack.
	 *
	 * @param handler handler
	 * @param x       pos in pixel
	 * @param y       pos in pixel
	 */
	public Player(Handler handler, float x, float y) {

		this(handler, x, y, Creature.DEFAULT_WIDTH, Creature.DEFAULT_HEIGHT,
				PLAYER_DEAULT_HEALTH);

	}

	/**
	 * Initialize player data
	 */
	public void init() {

		// in case, revive!
		if (health <= 0) {
			health = maxHealth;
		}

	}

	@Override
	public void initAnimation() {

		anim_idle_down = new Animation(1000, Assets.panda_idle_down);
		anim_walk_down = new Animation(1000, Assets.panda_walk_down);
		anim_idle_up = new Animation(1000, Assets.panda_idle_up);
		anim_walk_up = new Animation(1000, Assets.panda_walk_up);
		anim_idle_right = new Animation(1000, Assets.panda_idle_right);
		anim_walk_right = new Animation(1000, Assets.panda_walk_right);
		anim_idle_left = new Animation(1000, Assets.panda_idle_left);
		anim_walk_left = new Animation(1000, Assets.panda_walk_left);

		anim_run_right = new Animation(300, Assets.panda_walk_right);
		anim_run_left = new Animation(300, Assets.panda_walk_left);
		anim_run_up = new Animation(300, Assets.panda_walk_up);
		anim_run_down = new Animation(300, Assets.panda_walk_down);

		anim_attack_up = new Animation(attackAnimationDuration,
				Assets.panda_attack_up);
		anim_attack_down = new Animation(attackAnimationDuration,
				Assets.panda_attack_down);
		anim_attack_left = new Animation(attackAnimationDuration,
				Assets.panda_attack_left);
		anim_attack_right = new Animation(attackAnimationDuration,
				Assets.panda_attack_right);

		// death animation is unnecessary since death of player changes the screen
		// immediately.
	}

	@Override
	public void die() {

		handler.getMouseManager().setUIManager(null); // to delete buttons

		State.setState(handler.getGame().deathState);

		Sound.stopAll();
		Sound.GAME_OVER.loop();

	}

	/**
	 * it ticks
	 */
	@Override
	public void tick() {

		if (!isAlive())
			return;

		if (bar == null) {
			bar = (UIProgressBar) handler.getGame().gameState.getUiManager()
					.getSubList(UIManager.GAMESTATE_HEALTH_BAR).get(0);
		}

		// burning
		checkBurning();

		// running
		updateStamina();

		// inventory
		inventory.tick();

		// crafting
		craftingManager.tick();

		// animation
		if (attacking) {

			switch (this.getDirection()) {

			case Creature.UP:

				anim_attack_up.tick();
				break;
			case Creature.DOWN:
				anim_attack_down.tick();
				break;

			case Creature.LEFT:
				anim_attack_left.tick();
				break;

			case Creature.RIGHT:

				anim_attack_right.tick();

			}

		} else {

			switch (this.getDirection()) {

			case Creature.DOWN:

				if (isMoving()) {

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

				if (isMoving()) {

					if (running)
						anim_run_right.tick();
					else
						anim_walk_right.tick();

				} else {
					anim_idle_right.tick();
				}
				break;

			case Creature.LEFT:

				if (isMoving()) {

					if (running)
						anim_run_left.tick();
					else
						anim_walk_left.tick();

				} else {
					anim_idle_left.tick();
				}

				break;

			default:
				break;
			}

			// movement
			getInput();

			setSpeed((running) ? PLAYER_RUN_SPEED : PLAYER_WALK_SPEED);

			move();

			handler.getGameCamera().centerOnEntity(this);
		}

		// Attack
		checkAttack();

		// update health bar
		bar.setProgress(getHealth());

		checkDeath();

	}

	/**
	 * Check if any entities are in the attack range and hurt them
	 */
	private void checkAttack() {

		if (attacking) {

			attackTimer++;

			if (attackTimer > ATTACK_DUR_TICK) {

				attacking = false;
				attackTimer = 0;

			}

			return;

		} // if attack cooldown is not completed
		else if (attackTimer < ATTACK_COOL) {

			attackTimer++;

			return;

		}

		// if attack key is not pressed
		if (!handler.getKeyManager().a)
			return;

		// no attack while inventory or crafting are opened
		if (inventory.isOpened() || craftingManager.isOpened())
			return;

		PLAYER_ATTACK.play();

		Rectangle theBounds = getBounds(0, 0);

		Rectangle attackRange = new Rectangle();

		int rangeSize = Tile.TILE_SIZE;

		attackRange.width = rangeSize;
		attackRange.height = rangeSize;

		int dir = getDirection();

		// creating the collision box to the direction of the attack
		// and check if any entities are colliding (within the attack range)
		int rsHalf = rangeSize / 2;

		switch (dir) {

		case Creature.UP:
			attackRange.x = (int) (theBounds.getCenterX() - rsHalf);
			attackRange.y = (int) (theBounds.y - rangeSize);
			ParticleManager.addParticle(new PlayerAttackParticle(handler, (int) x,
					(int) y - Tile.TILE_SIZE, Entity.UP));

			break;

		case Creature.DOWN:
			attackRange.x = (int) (theBounds.getCenterX() - rsHalf);
			attackRange.y = (int) (theBounds.y + theBounds.height);
			ParticleManager.addParticle(new PlayerAttackParticle(handler, (int) x,
					(int) y + Tile.TILE_SIZE, Entity.DOWN));
			break;

		case Creature.LEFT:
			attackRange.x = (int) (theBounds.x - rangeSize);
			attackRange.y = (int) (theBounds.getCenterY() - rsHalf);
			ParticleManager.addParticle(new PlayerAttackParticle(handler,
					(int) x - Tile.TILE_SIZE, (int) y, Entity.LEFT));
			break;

		case Creature.RIGHT:
			attackRange.x = (int) (theBounds.x + theBounds.width);
			attackRange.y = (int) (theBounds.getCenterY() - rsHalf);
			ParticleManager.addParticle(new PlayerAttackParticle(handler,
					(int) x + Tile.TILE_SIZE, (int) y, Entity.RIGHT));
			break;

		default:
			return;

		}

		// if player have attacked, reset the timer
		attackTimer = 0;

		attacking = true;

		for (int yy = 0; yy < World.map.length; yy++) {
			for (int xx = 0; xx < World.map[0].length; xx++) {

				if (World.map[yy][xx] == null)
					continue;

				for (Entity ent : World.map[yy][xx].entities) {

					if (ent.equals(this))
						continue;

					if (attackRange.intersects(ent.getBounds(0, 0))) {
						// TODO DamageSource class?
						if (ent.getEntityID() == EntityId.STONE) {
							ent.hurt(attackDamage * 2);
						} else {
							ent.hurt(attackDamage);
						}
					}
				}
			}
		}
	}

	/**
	 * Get movement input
	 */
	private void getInput() {

		setXMove(0);
		setYMove(0);

		if (inventory.isOpened() || craftingManager.isOpened())
			return;

		KeyManager key = handler.getKeyManager();

		if (key.shift && getStamina() > 0) {

			useStamina();
			running = true;

		} else {

			running = false;

		}

		if (key.up)
			setYMove(-getSpeed());

		if (key.down)
			setYMove(getSpeed());

		if (key.left)
			setXMove(-getSpeed());

		if (key.right)
			setXMove(getSpeed());

	}

	@Override
	public void render(Graphics gfx) {

		float camX = camera.getXOffset();
		float camY = camera.getYOffset();
		BufferedImage image = getCurrentAnimationFrame();

		if (isDying) {

			shouldDie = true;

			return;

		}

		if (attacking) {

			int dir = getDirection();

			if (dir == Creature.UP || dir == Creature.DOWN)
				setWidth(76);
			else
				setWidth(64);

		} else {

			setWidth(64);

		}

		// subtract the camera's offset to render what is
		// on top and left of the centered entity
		if (attacking && this.getDirection() == Creature.DOWN) {

			gfx.drawImage(image, (int) (x - camX) - 12, (int) (y - camY), width,
					height, null);

		} else {

			gfx.drawImage(image, (int) (x - camX), (int) (y - camY), width, height,
					null);

		}

		showFireEffect(gfx);

	}

	/**
	 * Call render after all the entities
	 *
	 * @param gfx
	 */
	public void postRender(Graphics gfx) {

		// inventory
		inventory.render(gfx);

		// crafting
		craftingManager.render(gfx);

	}

	/**
	 * To update the animation
	 *
	 * @return
	 */
	private BufferedImage getCurrentAnimationFrame() {

		boolean isMoving = this.isMoving();

		if (attacking) {

			switch (this.getDirection()) {

			case Creature.UP:
				return anim_attack_up.getCurrentFrame();

			case Creature.DOWN:
				return anim_attack_down.getCurrentFrame();

			case Creature.LEFT:
				return anim_attack_left.getCurrentFrame();

			case Creature.RIGHT:
				return anim_attack_right.getCurrentFrame();

			}

		} else {

			switch (this.getDirection()) {

			case Creature.DOWN:

				if (isMoving) {

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
				if (isMoving) {

					if (running) {
						return anim_run_right.getCurrentFrame();
					} else {
						return anim_walk_right.getCurrentFrame();
					}

				} else {
					return anim_idle_right.getCurrentFrame();
				}

			case Creature.LEFT:

				if (isMoving) {

					if (running) {
						return anim_run_left.getCurrentFrame();
					} else {
						return anim_walk_left.getCurrentFrame();
					}

				} else {
					return anim_idle_left.getCurrentFrame();
				}

			}
		}

		// default one
		return anim_idle_down.getCurrentFrame();
	}

	@Override
	public void setUpAI() {

		// no AI needed for player
	}

	////////////////// getter/////////////////////
	public Inventory getInventory() {

		return inventory;
	}

	public CraftingManager getCraftingManager() {

		return craftingManager;
	}

	@Override
	protected void checkShouldDie() {

		// do nothing
	}

}
