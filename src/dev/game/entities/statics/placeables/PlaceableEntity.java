
package dev.game.entities.statics.placeables;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import dev.game.Handler;
import dev.game.entities.Entity;
import dev.game.entities.EntityId;
import dev.game.entities.creatures.Player;
import dev.game.entities.statics.StaticEntity;
import dev.game.worlds.World;

/**
 * The entity that can be placed in the world (ex. fence).
 * <p>
 * The <code>isCarried</code> is True by default. The <code>player</code>
 * variable is null, it should be set at tick() method
 */
public abstract class PlaceableEntity extends StaticEntity {

	// if being carried, make it invulnerable
	protected boolean isCarried = true;

	protected Player player = null;

	/**
	 * True if the texture should be updated.
	 */
	protected boolean updateTexture = true;

	/**
	 * Texture of the entity when it is being carried.
	 */
	protected BufferedImage texture;

	public PlaceableEntity(Handler handler, int id, float x, float y, int width,
			int height, int health, String name) {

		super(handler, id, x, y, width, height, health, name);

		setBounds(getNewBounds());

		// ready to place
		noCollision = true;

	}

	/**
	 * Place the entity to world. Set the <code>isCarried = false</code>. Set the
	 * <code> noCollision = false;
	 * </code>. Texture should be set to fully opaque one, use
	 * {@linkplain dev.juhyung.game.utils.Utils#setAlpha(BufferedImage, int)}.
	 */
	public abstract void place();

	/**
	 * Make the entity to follow player when carried
	 * <p>
	 * <code>if(!isCarried) return;</code> is necessary
	 */
	public abstract void followPlayer();

	/**
	 * Update the size of the entity when carried if necessary This method is not
	 * required if the entity does not change size depend on the alignment.
	 * <p>
	 * <code>if(!isCarried) return;</code> is necessary
	 */
	public abstract void updateSize();

	/**
	 * Put followPlayer() and then updateSize() in here. Also, assign a value to the
	 * player variable.
	 */
	@Override
	public abstract void tick();

	/**
	 * Return the appropriate collision bounds value for this entity. Since Entity
	 * like fence needs update in collision box.
	 *
	 * @return bounds
	 */
	protected abstract Rectangle getNewBounds();

	/**
	 * true if there are no entity colliding with this entity if placed.
	 *
	 * @return
	 */
	public boolean canPlace() {

		for (int yy = 0; yy < World.map.length; yy++) {
			for (int xx = 0; xx < World.map[0].length; xx++) {

				if (World.map[yy][xx] == null)
					continue;

				for (Entity ent : World.map[yy][xx].entities) {

					if (ent == this || ent.getEntityID() == EntityId.PLAYER)
						continue;

					if (getBounds(0, 0).intersects(ent.getBounds(0, 0)))
						return false;

				}
			}
		}

		return true;

	}

	// getter setter ////////
	/**
	 * true is texture should get updated.
	 *
	 * @return the updateTexture
	 */
	public boolean getUpdateTexture() {

		return updateTexture;
	}

	/**
	 * @param shouldUpdate the updateTexture to set
	 */
	public void setUpdateTexture(boolean shouldUpdate) {

		this.updateTexture = shouldUpdate;
	}

	/**
	 * Check if this entity is carried by player.
	 *
	 * @return
	 */
	public boolean isCarried() {
		return this.isCarried;
	}

}
