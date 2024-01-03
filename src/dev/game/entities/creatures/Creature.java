
package dev.game.entities.creatures;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dev.game.Game;
import dev.game.Handler;
import dev.game.ai.EntityAIBase;
import dev.game.ai.pathfinding.Node;
import dev.game.entities.Entity;
import dev.game.gfx.Animation;
import dev.game.gfx.Assets;
import dev.game.gfx.GameCamera;
import dev.game.tiles.Tile;
import dev.game.utils.Utils;
import dev.game.worlds.World;

/**
 * Creature.java - Moving, living entity
 *
 * @author j.kim3
 */
public abstract class Creature extends Entity {

	/**
	 * Default width of creature
	 */
	public static final int DEFAULT_WIDTH = 64;
	/**
	 * Default height of the creature
	 */
	public static final int DEFAULT_HEIGHT = 64;
	/**
	 * Default walking speed
	 */
	public static final float DEFAULT_SPEED = 5;
	/**
	 * Default running speed
	 */
	public static final float DEFAULT_RUN_SPEED = 8;
	/**
	 * Default damage taken by fire every second
	 */
	public static final int FIRE_DAMAGE = 4;

	/**
	 * ' Amount of pixels pushed when overlapped
	 */
	public static final int PUSH_AMOUNT = 4;

	protected boolean running = false;
	protected boolean attacking = false;

	// if not runnable, don't bother with all the stamina stuff
	protected boolean runnableCreature = true;

	protected long staminaLastTime, staminaTimer;
	protected int maxStamina = Game.FPS * 5;
	protected int stamina = maxStamina; // start with max stamina to run
	private boolean isStaminaRefueling = false;

	protected float speed;
	protected float xMove;
	protected float yMove;
	protected boolean collided;

	// Buying some time to show the death animation
	protected boolean isDying = false;
	// if death animation is over, the true moment to vanish
	protected boolean shouldDie = false;

	protected int attackDamage;

	private int fireTick; // tick while burning
	private int fireDamageTick; // ticks to count the time to damage creature
	public List<Integer[]> fireEffectPos;
	private int fireNumber;

	private static final int HEALTHBAR_HEIGHT = 6;

	private Entity target;
	private ArrayList<Node> pathToTarget;

	private boolean pathUpdated;

	private boolean moving;

	protected int currentDirection; // Creature.UP, DOWN, LEFT, RIGHT

	// the list of passive action AI's
	public ArrayList<EntityAIBase> tasks;
	// the list of AI's associated with attacking and targeting
	public ArrayList<EntityAIBase> activeTasks;

	// basic animations
	protected Animation anim_walk_down, anim_walk_up, anim_walk_right,
			anim_walk_left, anim_die;

	/**
	 * Moving, living entity.
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
	public Creature(Handler handler, int id, float x, float y, int width, int height,
			int health, String name) {

		super(handler, id, x, y, width, height, name);

		staminaLastTime = System.currentTimeMillis();

		this.health = health;
		this.maxHealth = health;

		this.xMove = 0;
		this.yMove = 0;
		this.moving = false;

		this.attackDamage = 0; // default

		this.speed = DEFAULT_SPEED;
		this.currentDirection = DOWN;
		this.collided = false;

		this.fireTick = 0;
		this.fireDamageTick = 0;
		this.fireEffectPos = new ArrayList<>();
		this.fireNumber = width / 3;

		updateFireEffect();

		this.target = null;
		this.pathUpdated = false;

		this.tasks = new ArrayList<>();
		this.activeTasks = new ArrayList<>();

		this.pathToTarget = null;

	}

	/**
	 * Setup the AI list for the creature, insert the desired AI.
	 * <p>
	 * Put this method in the last line of the constructor.
	 *
	 * @see #addTask(EntityAI)
	 * @see #addActiveTask(EntityAI)
	 */
	public abstract void setUpAI();

	/**
	 * Assign the animation variables.
	 */
	public abstract void initAnimation();

	/**
	 * Render the health bar of this creature.
	 *
	 * @param gfx
	 * @param camX
	 * @param camY
	 */
	public void renderHealthBar(Graphics gfx, float camX, float camY) {

		// decided not to render when it's full
		if (health == maxHealth)
			return;

		int barX = (int) (x - camX);
		int barY = (int) (y - camY) + height + 4;
		float percent = (float) health / (float) maxHealth;
		int barWidth = (int) (defaultWidth * percent);

		if (percent > 0.5)
			gfx.setColor(Color.GREEN);
		else if (percent > 0.2)
			gfx.setColor(Color.YELLOW);
		else
			gfx.setColor(Color.RED);

		// health bar
		gfx.fillRect(barX, barY, barWidth, HEALTHBAR_HEIGHT);

		// bar frame
		gfx.drawRect(barX - 2, barY - 2, defaultWidth + 3, HEALTHBAR_HEIGHT + 3);

	}

	/**
	 * Update the stamina of the runnable creature.
	 * <p>
	 * Insert this in creature's tick()
	 */
	public void updateStamina() {

		if (!runnableCreature || stamina >= maxStamina || running)
			return;

		// stamina need to be filled up when the creature is not ticking too
		// so, use current millisecond for timing
		staminaTimer += System.currentTimeMillis() - staminaLastTime;
		staminaLastTime = System.currentTimeMillis();

		// half tick(Game.FPS) of stamina per sec
		// 1000 / Game.FPS = 25 milliseconds = 1 tick
		if (staminaTimer > Game.FPS_TO_MILLISEC * 2) {

			// heal stamina
			stamina += staminaTimer / Game.FPS_TO_MILLISEC * 2;

			if (stamina > maxStamina)
				stamina = maxStamina;

			staminaTimer = 0;

		}

	}

	/**
	 * Update the fire effect
	 */
	public void updateFireEffect() {

		fireEffectPos.clear();

		for (int i = 0; i < fireNumber; i++) {

			Integer[] pos = { new Random().nextInt(width),
					new Random().nextInt(height) };

			fireEffectPos.add(pos);

		}
	}

	/**
	 * Show the burning effect (particle) if the creature is burning
	 * <p>
	 * Insert this in the render()
	 *
	 * @param gfx
	 */
	protected void showFireEffect(Graphics gfx) {

		if (fireTick > 0) {

			GameCamera cam = handler.getGameCamera();

			float xOff = cam.getXOffset();
			float yOff = cam.getYOffset();

			int size = 24;

			for (int i = 0; i < fireEffectPos.size(); i++) {

				Integer[] pos = fireEffectPos.get(i);

				gfx.drawImage(Assets.fire[0], (int) (x - xOff + pos[0]) - size / 2,
						(int) (y - yOff + pos[1]) - size / 2, size, size, null);

			}
		}
	}

	/**
	 * Check if the creature is burning
	 * <p>
	 * Insert this in the tick() method
	 */
	protected void checkBurning() {

		if (fireTick <= 0) {
			return;
		}

		fireDamageTick++;

		if (fireDamageTick >= Game.FPS) {

			hurt(FIRE_DAMAGE);

			fireDamageTick = 0;
		}

		fireTick--;

	}

	/**
	 * Add a task(AI) to the creature's task list. Normal task is about the stuff to
	 * do with normal living (walking, idling etc.)
	 *
	 * @param task AI to add
	 */
	public void addTask(EntityAIBase task) {

		if (tasks.isEmpty()) {

			tasks.add(task);
			return;

		}

		int priThis = task.getPriority();

		for (int i = 0; i < tasks.size(); i++) {

			EntityAIBase ai = tasks.get(i);

			int priCurrent = ai.getPriority();

			if (priThis < priCurrent) {

				tasks.add(task);
				return;
			}

		}

		// in this case, this task have the least priority
		tasks.add(task);

	}

	/**
	 * Add a task(AI) to the creature's active task list. Active task is the tasks
	 * about attacking and chasing etc.
	 *
	 * @param task AI to add
	 */
	public void addActiveTask(EntityAIBase task) {

		if (activeTasks.isEmpty()) {

			activeTasks.add(task);
			return;

		}

		int priThis = task.getPriority();

		for (int i = 0; i < activeTasks.size(); i++) {

			EntityAIBase ai = activeTasks.get(i);

			int priCurrent = ai.getPriority();

			if (priThis < priCurrent) {

				activeTasks.add(i, task);
				return;
			}

		}

		// in this case, this task have the least priority
		activeTasks.add(task);

	}

	/**
	 * Check and update if stamina should be refueled.
	 */
	private void checkStaminaRefueling() {

		if (stamina <= 0)
			isStaminaRefueling = true;
		else if (stamina >= maxStamina)
			isStaminaRefueling = false;

	}

	/**
	 * Move to certain position
	 *
	 * @param tx X position in block
	 * @param ty Y position in block
	 * @return true if it is going to reached the destination
	 */
	public boolean moveTo(int tx, int ty) {

		int cx = (int) ((x) + (bounds.x) + (bounds.width / 2));
		int cy = (int) ((y) + (bounds.y) + (bounds.height / 2));

		int targetX = (tx * Tile.TILE_SIZE) + (Tile.TILE_SIZE / 2);
		int targetY = (ty * Tile.TILE_SIZE) + (Tile.TILE_SIZE / 2);

		setXMove(0);
		setYMove(0);

		if (runnableCreature) {

			checkStaminaRefueling();

			if (isStaminaRefueling) {

				running = false;

			} else if (getStamina() > 0) {

				useStamina();

				running = true;

			}

			setSpeed((running) ? DEFAULT_RUN_SPEED : DEFAULT_SPEED);

		}

		if (targetX > cx + speed) {

			if (isCollidedWithEntity(speed, 0f) == null) {
				setXMove(speed);
				moveX();
			}

		}

		if (targetX + speed < cx) {

			if (isCollidedWithEntity(-speed, 0f) == null) {
				setXMove(-speed);
				moveX();
			}

		}

		if (targetY > cy + speed) {

			if (isCollidedWithEntity(0f, speed) == null) {
				setYMove(speed);
				moveY();
			}

		}

		if (targetY + speed < cy) {

			if (isCollidedWithEntity(0f, -speed) == null) {
				setYMove(-speed);
				moveY();
			}

		}

		moving = true;

		updateDirection();

		setXMove(0);
		setYMove(0);

		cx = (int) ((x) + (bounds.x) + (bounds.width / 2));
		cy = (int) ((y) + (bounds.y) + (bounds.height / 2));

		int rangeUncertianty = (int) speed; // pixels

		boolean reached = ((targetX <= cx + rangeUncertianty
				&& targetX >= cx - rangeUncertianty)
				&& (targetY <= cy + rangeUncertianty
						&& targetY >= cy - rangeUncertianty));

		return reached;
	}

	/**
	 * This method move the creature so precisely, so it does not reference to the
	 * creature's own speed to move
	 *
	 * @param tx X position in pixel
	 * @param ty Y position in pixel
	 *
	 * @return true if the creature reached the target position
	 */
	protected boolean moveToInPixel(float tx, float ty) {

		setXMove(0);
		setYMove(0);

		int theSpeed = 4; // pixels

		if (tx > x) {

			if (isCollidedWithEntity(theSpeed, 0f) == null) {
				setXMove(theSpeed);
				moveX();
			}
		}
		if (tx < x) {
			if (isCollidedWithEntity(-theSpeed, 0f) == null) {
				setXMove(-theSpeed);
				moveX();
			}
		}
		if (ty > y) {
			if (isCollidedWithEntity(0f, theSpeed) == null) {
				setYMove(theSpeed);
				moveY();
			}
		}
		if (ty < y) {
			if (isCollidedWithEntity(0f, -theSpeed) == null) {
				setYMove(-theSpeed);
				moveY();
			}
		}

		moving = true;

		setXMove(0);
		setYMove(0);

		boolean reached = (tx == x && ty == y);

		return reached;

	}

	/**
	 * Move the creature by the xMove and yMove
	 */
	public void move() {

		Entity xcol = isCollidedWithEntity(xMove, 0f);
		Entity ycol = isCollidedWithEntity(0f, yMove);

		// pushing each other
		if (ycol != null) {

			if (ycol instanceof Creature) {
				yMove /= 2;
				Creature target = (Creature) ycol;
				target.setY(target.getY() + yMove);
			}

			// if overlapped somehow, separate
			if (ycol.getBounds(0, 0).intersects(getBounds(0, 0))) {
				int xx = (ycol.getX() > x) ? -PUSH_AMOUNT : PUSH_AMOUNT;
				int yy = (ycol.getY() > y) ? -PUSH_AMOUNT : PUSH_AMOUNT;
				x += xx;
				y += yy;

			}

			collided = true;

		} else if (xcol != null) {

			if (xcol instanceof Creature) {
				xMove /= 2;
				Creature target = (Creature) xcol;
				target.setX(target.getX() + xMove);
			}

			// if overlapped somehow, separate
			if (xcol.getBounds(0, 0).intersects(getBounds(0, 0))) {
				int xx = (xcol.getX() > x) ? -PUSH_AMOUNT : PUSH_AMOUNT;
				int yy = (xcol.getY() > y) ? -PUSH_AMOUNT : PUSH_AMOUNT;
				x += xx;
				y += yy;
			}

			collided = true;

		} else {

			collided = false;

		}

		updateDirection();

		if (xcol == null)
			moveX();

		if (ycol == null)
			moveY();

	}

	/**
	 * Update the direction by the direction its moving
	 */
	public void updateDirection() {

		if (xMove > 0)
			currentDirection = RIGHT;
		if (xMove < 0)
			currentDirection = LEFT;
		if (yMove > 0)
			currentDirection = DOWN;
		if (yMove < 0)
			currentDirection = UP;

	}

	/**
	 * Move the creature in X direction
	 */
	public void moveX() {

		int tempYTop = Utils.quickFloor((y + bounds.y) / Tile.TILE_SIZE);
		int tempYBtm = Utils
				.quickFloor((y + bounds.y + bounds.height) / Tile.TILE_SIZE);

		if (xMove > 0) { // if moving right

			float tempXf = (float) (x + xMove + bounds.x + bounds.width)
					/ (float) Tile.TILE_SIZE;

			// (x + xMove) is where we are trying to move to (add bound.width
			// for right side)
			int tempX = Utils.quickFloor(tempXf);

			// checking collision for right top & bottom corner
			if (!isCollidedWithBlock(tempX, tempYTop)
					&& !isCollidedWithBlock(tempX, tempYBtm)) {

				setX(x + xMove);

				collided = false;

			} else {

				// if collided, put entity right beside the tile
				// (because collision code make 1 pixel gap between the block
				// and the entity)
				// but when entity is right beside a block, y side collision
				// happen and entity dont move
				// so, minus 1 pixels to give a gap entity move
				setX(tempX * Tile.TILE_SIZE - bounds.x - bounds.width - 1);

				collided = true;

			}

		} else if (xMove < 0) { // if moving left

			float tempXf = ((float) (x + xMove + bounds.x) / (float) Tile.TILE_SIZE);

			// (x + xMove) is where we are trying to move to
			int tempX = Utils.quickFloor(tempXf);

			// checking collision for right top & bottom corner
			if (!isCollidedWithBlock(tempX, tempYTop)
					&& !isCollidedWithBlock(tempX, tempYBtm)) {

				setX(x + xMove);

				collided = false;

			} else { // if collided, put entity right beside the block

				setX(tempX * Tile.TILE_SIZE + Tile.TILE_SIZE - bounds.x);

				collided = true;
			}

		}

	}

	/**
	 * Move the creature in Y direction
	 */
	public void moveY() {

		int tempXLeft = Utils.quickFloor((x + bounds.x) / Tile.TILE_SIZE);
		int tempXRight = Utils
				.quickFloor((x + bounds.x + bounds.width) / Tile.TILE_SIZE);

		if (yMove < 0) { // negative y is up

			// top side
			float tempYf = (float) (y + yMove + bounds.y) / (float) Tile.TILE_SIZE;

			int tempY = Utils.quickFloor(tempYf);

			if (!isCollidedWithBlock(tempXLeft, tempY)
					&& !isCollidedWithBlock(tempXRight, tempY)) {

				setY(y + yMove);
				collided = false;

			} else { // if collided, put entity right on the bottom side of the
				// block

				setY(tempY * Tile.TILE_SIZE + Tile.TILE_SIZE - bounds.y);
				collided = true;
			}

		} else if (yMove > 0) { // down

			// bottom side
			float tempYf = (float) (y + yMove + bounds.y + bounds.height)
					/ (float) Tile.TILE_SIZE;

			int tempY = Utils.quickFloor(tempYf);

			if (!isCollidedWithBlock(tempXLeft, tempY)
					&& !isCollidedWithBlock(tempXRight, tempY)) {

				setY(y + yMove);
				collided = false;
			} else { // if collided, put entity right on top side of the block

				// minus 1 to prevent invoking x side collision
				setY(tempY * Tile.TILE_SIZE - bounds.y - bounds.height - 1);
				collided = true;
			}

		}

	}

	/**
	 * Execute all the tasks of the creature. Insert this in tick() method.
	 */
	protected void executeTasks() {

		for (int i = 0; i < activeTasks.size(); i++) {

			EntityAIBase ai = activeTasks.get(i);

			if (ai.shouldExecute()) {

				ai.continueExecute();

				break;

			}

		}

		for (int i = 0; i < tasks.size(); i++) {

			EntityAIBase ai = tasks.get(i);

			if (ai.shouldExecute()) {

				ai.continueExecute();

				break;

			}

		}

	}

	/**
	 * Render the path of the creature if exists. For developing purpose.
	 *
	 * @param gfx
	 */
	protected void renderPath(Graphics gfx) {

		if (getPathToTarget() != null) {

			ArrayList<Node> paths = getPathToTarget();

			int size = 20;

			for (Node a : paths) {

				int x = a.getXInPixel();
				int y = a.getYInPixel();

				gfx.setColor(Color.RED);

				gfx.fillRect(
						(int) (x + Tile.TILE_SIZE / 2 - (size / 2)
								- camera.getXOffset()),
						(int) (y + Tile.TILE_SIZE / 2 - (size / 2)
								- camera.getYOffset()),
						size, size);

			}
		}
	}

	/**
	 * Check if this creature is dead or not. Add this method to tick()
	 */
	protected void checkDeath() {

		if (!isAlive() && !isDying)
			isDying = true;

	}

	/**
	 * Check the death animation, and if the death animation is over, set the
	 * shouldDie to true;
	 */
	protected abstract void checkShouldDie();

	// getters setters
	/**
	 * in block
	 */
	public boolean isCollidedWithBlock(int x, int y) {

		return World.getTile(x, y).isSolid();

	}

	public boolean getPathUpdated() {

		return pathUpdated;
	}

	public void setPathUpdated(boolean updated) {

		this.pathUpdated = updated;
	}

	public float getSpeed() {

		return speed;
	}

	public void setSpeed(float speed) {

		this.speed = speed;
	}

	public float getXMove() {

		return xMove;
	}

	public void setXMove(float xMove) {

		this.xMove = xMove;
	}

	public float getYMove() {

		return yMove;
	}

	public void setYMove(float yMove) {

		this.yMove = yMove;
	}

	/**
	 * Set whether the creature is moving or not.
	 * <p>
	 * But even if you set this to FALSE, isMoving() method could return TRUE if the
	 * XMove or YMove is not 0
	 *
	 * @param move If creature is moving or not
	 */
	public void setIsMoving(boolean move) {

		this.moving = move;

	}

	public boolean isMoving() {

		if (moving) {
			return true;
		}

		return (xMove != 0 || yMove != 0);

	}

	public boolean isCollided() {

		return collided;

	}

	public void setTarget(Entity ent) {

		this.target = ent;

	}

	public int getDirection() {

		return this.currentDirection;

	}

	/**
	 * Get the target, null if there's no target
	 *
	 * @return the target Entity
	 */
	public Entity getTarget() {

		return this.target;

	}

	public ArrayList<Node> getPathToTarget() {

		return this.pathToTarget;

	}

	public void setPathToTarget(ArrayList<Node> path) {

		this.pathToTarget = path;

	}

	/**
	 * Set the duration of burning
	 */
	public void setFireTick(int tick) {

		this.fireTick = tick;

	}

	/**
	 * The duration of burning
	 *
	 * @return
	 */
	public int getFireTick() {

		return this.fireTick;

	}

	public int getAttackDamage() {

		return attackDamage;

	}

	public void setAttackDamage(int attackDamage) {

		this.attackDamage = attackDamage;

	}

	/**
	 * @return the stamina to sprint
	 */
	public int getStamina() {

		return stamina;

	}

	public void setStamina(int stamina) {

		this.stamina = stamina;

	}

	/**
	 * Decrease stamina by 1. If it reaches 0 or lower, running get cancelled.
	 */
	public void useStamina() {

		if (stamina <= 0)
			running = false;
		else
			this.stamina--;
	}

	/**
	 * @return true is can run
	 */
	public boolean isRunnableCreature() {

		return runnableCreature;

	}

	/**
	 * @return the attacking
	 */
	public boolean isAttacking() {

		return attacking;
	}

	/**
	 * @param the attacking to set
	 */
	public void setAttacking(boolean attacking) {

		this.attacking = attacking;

	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {

		return running;

	}

	/**
	 * @return should vanish or not
	 */
	public boolean shouldDie() {

		return this.shouldDie;
	}

}
