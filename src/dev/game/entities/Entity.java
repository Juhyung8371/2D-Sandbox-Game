
package dev.game.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import dev.game.Handler;
import dev.game.entities.creatures.Player;
import dev.game.gfx.GameCamera;
import dev.game.tiles.Tile;
import dev.game.utils.Utils;
import dev.game.worlds.World;

/**
 * Entity.java - An object that can be spawned in world, and also potentially
 * get removed from the world.
 * <p>
 * It dynamically gets updated and interact with environment.
 *
 * @author j.kim3
 */
public abstract class Entity {

	/**
	 * For {@linkplain #isOverlappedWithEntity(int entityId)}, the condition to
	 * check all entities.
	 */
	public static final int CHECK_ALL_ENTITIES = -1;

	public static final int DEFAULT_HEALTH = Player.PLAYER_ATTACK_DAMAGE;

	/**
	 * Direction
	 */
	public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;

	protected int entityID;

	protected Handler handler;

	// position in pixel, left top corner of the entity sprite
	protected float x, y;

	// physical size (not necessarily same as boundbox)
	protected int width, height, defaultWidth, defaultHeight;
	protected int centerX, centerY; // in block

	protected int maxHealth, health;
	private boolean alive = true;

	// bounding box
	protected Rectangle bounds;

	/**
	 * true if this entity does not physically collide with other entity
	 */
	protected boolean noCollision = false;

	protected GameCamera camera;

	/**
	 * When entity is spawned, it's index of chunk to spawn. The value is assigned
	 * in EntityManager.addEntity(Entity)
	 */
	protected String chunkIndex;

	/**
	 * Name of this entity.
	 */
	public final String name;

	/**
	 * An object that can be spawned in world, and also potentially get removed from
	 * the world.
	 * <p>
	 * It dynamically gets updated and interact with environment.
	 *
	 * @param handler handler
	 * @param id      EtityId
	 * @param x       pos in pixel
	 * @param y       pos in pixel
	 * @param width   in pixel
	 * @param height  in pixel
	 * @param name    name
	 */
	public Entity(Handler handler, int id, float x, float y, int width, int height,
			String name) {

		this.handler = handler;
		this.entityID = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.defaultWidth = width;
		this.defaultHeight = height;
		this.name = name;

		this.bounds = new Rectangle(Utils.quickFloor(x), Utils.quickFloor(y), width,
				height);

		this.camera = handler.getGameCamera();

		updateCenterX();
		updateCenterY();

		// in case one forgot to set default health of an entity
		if (maxHealth == 0) {

			maxHealth = DEFAULT_HEALTH;
			health = DEFAULT_HEALTH;

		}

	}

	/// abstract methods
	public abstract void tick();

	public abstract void render(Graphics gfx);

	public abstract void die();

	// methods
	/**
	 * Render the bound of the entity
	 * <p>
	 * For development purpose
	 *
	 * @param gfx
	 */
	protected void renderBounds(Graphics gfx) {

		GameCamera cam = handler.getGameCamera();

		// collision box visualization
		gfx.setColor(Color.RED);
		gfx.fillRect((int) (x + bounds.x - cam.getXOffset()),
				(int) (y + bounds.y - cam.getYOffset()), (int) (bounds.width),
				(int) (bounds.height));

	}

	/**
	 * Check if the entity's bound is going to collide with other entity's bound.
	 * <p>
	 * Any noCollision entities cannot collide.
	 *
	 * @param xOffset of the target entity away from this entity in pixel
	 * @param yOffset of the target entity away from this entity in pixel
	 * @return Entity object if collided else null
	 */
	public Entity isCollidedWithEntity(float xOffset, float yOffset) {

		for (int yy = 0; yy < World.map.length; yy++) {
			for (int xx = 0; xx < World.map[0].length; xx++) {

				if (World.map[yy][xx] == null)
					continue;

				for (Entity ent : World.map[yy][xx].entities) {

					// entity shouldn't collide with itself, or with noCollision
					// entities
					if (ent.equals(this) || ent.noCollision) {
						continue;
					}

					int dis = Utils.getDistance(ent.getCenterX(), ent.getCenterY(),
							getCenterX(), getCenterY());

					// for the sake of performance
					// ignore checking collision if entities are practically too far
					if (dis > 3) {
						continue;
					}

					// create a bound where the entity is going to go,
					// and check if they're going to collide
					if (ent.getBounds(0f, 0f)
							.intersects(getBounds(xOffset, yOffset))) {
						return ent;
					}

				}
			}
		}

		return null;

	}

	/**
	 * Check for overlap for entities.
	 * <p>
	 * Use {@linkplain #CHECK_ALL_ENTITIES} to include all entities.
	 *
	 * @param entityId The target entity ID to check
	 * @return
	 */
	public boolean isOverlappedWithEntity(int entityId) {

		for (int yy = 0; yy < World.map.length; yy++) {
			for (int xx = 0; xx < World.map[0].length; xx++) {

				if (World.map[yy][xx] == null)
					continue;

				for (Entity ent : World.map[yy][xx].entities) {

					// entity shouldn't collide with itself
					if (ent.equals(this))
						continue;

					if (entityId != CHECK_ALL_ENTITIES
							&& ent.getEntityID() != entityId)
						continue;

					int dis = Utils.getDistance(ent.getCenterX(), ent.getCenterY(),
							getCenterX(), getCenterY());

					// for the sake of performance
					// ignore checking collision if entities are too far
					if (dis > 5)
						continue;

					// and check if they're going to collide
					if (ent.getBounds(0f, 0f).intersects(getBounds(0, 0)))
						return true;

				}
			}
		}
		return false;
	}

	/**
	 * Decrease the Entity's health
	 *
	 * @param amount Amount of health to decrease
	 */
	public void hurt(int amount) {

		setHealth(health - amount);

	}

	/**
	 * Heal the entity's health
	 *
	 * @param amount
	 */
	public void heal(int amount) {

		if (amount < 0) {
			return; // no negative healing allowed
		}
		if (health + amount > maxHealth) {

			setHealth(maxHealth);

		} else {

			setHealth(health + amount);
		}

	}

	///// getter setter///////////
	public boolean isAlive() {

		return alive;
	}

	/**
	 * Kill the entity without calling die()
	 */
	public void kill() {

		alive = false;

	}

	/**
	 * Block coordinate made by exactly center of the entity
	 *
	 * @return
	 */
	public int getCenterX() {

		return centerX;
	}

	/**
	 * Block coordinate made by exactly center of the entity
	 *
	 * @return
	 */
	public int getCenterY() {

		return centerY;
	}

	/**
	 * In pixel
	 *
	 * @return
	 */
	public float getX() {

		return x;
	}

	/**
	 * In pixel
	 *
	 * @param x
	 */
	public void setX(float x) {

		this.x = x;
		updateCenterX();

	}

	public void setXInBlock(int x) {

		this.x = (x * Tile.TILE_SIZE);
		updateCenterX();
	}

	/**
	 * In pixel
	 *
	 * @return
	 */
	public float getY() {

		return y;
	}

	/**
	 * In pixel
	 * <p>
	 *
	 * @param y
	 */
	public void setY(float y) {

		this.y = y;
		updateCenterY();
	}

	public void setYInBlock(int y) {

		this.y = (y * Tile.TILE_SIZE);
		updateCenterY();

	}

	/**
	 * In pixel
	 *
	 * @return
	 */
	public int getWidth() {

		return width;
	}

	/**
	 * In pixel
	 * <p>
	 *
	 * @param width
	 */
	public void setWidth(int width) {

		this.width = width;
	}

	/**
	 * In pixel
	 *
	 * @return
	 */
	public int getHeight() {

		return height;
	}

	/**
	 * In pixel
	 *
	 * @param height
	 */
	public void setHeight(int height) {

		this.height = height;
	}

	public int getMaxHealth() {

		return maxHealth;
	}

	public void setMaxHealth(int health) {

		maxHealth = health;
	}

	public int getHealth() {

		return health;
	}

	/**
	 * Set the health of the Entity If it is set to 0, the entity die, and isAlive()
	 * will return FALSE as well
	 *
	 * @param health
	 */
	public void setHealth(int health) {

		if (health <= 0) {

			this.health = 0;
			alive = false;
			die();

		} else if (health > maxHealth) {

			this.health = maxHealth;

		} else {

			this.health = health;

		}

	}

	/**
	 * Get the bound of the entity. This value is NOT the absolute size of the
	 * bounds, but also include the x and y coordinates of the entity.
	 *
	 * @param xOffset start x
	 * @param yOffset start y
	 * @return
	 */
	public Rectangle getBounds(float xOffset, float yOffset) {

		return new Rectangle((int) (x + bounds.x + xOffset),
				(int) (y + bounds.y + yOffset), bounds.width, bounds.height);
	}

	/**
	 * Set the collision bounds.
	 *
	 * @param xStart
	 * @param yStart
	 * @param width
	 * @param height
	 */
	public void setBounds(int xStart, int yStart, int width, int height) {

		this.bounds = new Rectangle(xStart, yStart, width, height);

		centerX = (int) Math.floor(
				(this.x + this.bounds.x + this.bounds.width / 2) / Tile.TILE_SIZE);
		centerY = (int) Math.floor(
				(this.y + this.bounds.y + this.bounds.height / 2) / Tile.TILE_SIZE);
	}

	/**
	 * Set the collision bounds.
	 *
	 * @param bounds
	 */
	public void setBounds(Rectangle bounds) {

		this.bounds = bounds;

		centerX = Utils.quickFloor(
				(this.x + this.bounds.x + this.bounds.width / 2) / Tile.TILE_SIZE);
		centerY = Utils.quickFloor(
				(this.y + this.bounds.y + this.bounds.height / 2) / Tile.TILE_SIZE);

	}

	public int getEntityID() {

		return entityID;
	}

	/**
	 * Get if the entity can collide with other entity
	 *
	 * @return
	 */
	public boolean getNoCollision() {

		return noCollision;

	}

	/**
	 * When entity is spawned, it's index of chunk to spawn. The value is assigned
	 * in EntityManager.addEntity(Entity)
	 *
	 * @return the chunkIndex
	 */
	public String getChunkIndex() {

		return chunkIndex;
	}

	/**
	 * When entity is spawned, it's index of chunk to spawn. The value is assigned
	 * in EntityManager.addEntity(Entity)
	 *
	 * @param chunkIndex the chunkIndex to set
	 */
	public void setChunkIndex(String chunkIndex) {

		this.chunkIndex = chunkIndex;
	}

	// util methods
	/**
	 * Update the centerX;
	 */
	private void updateCenterX() {

		centerX = Utils
				.quickFloor((float) (this.x + this.bounds.x + this.bounds.width / 2)
						/ (float) Tile.TILE_SIZE);

	}

	/**
	 * Update the centerY.
	 */
	private void updateCenterY() {

		centerY = Utils
				.quickFloor((float) (this.y + this.bounds.y + this.bounds.height / 2)
						/ (float) Tile.TILE_SIZE);

	}

}
