
package dev.game.entities.statics.placeables;

import java.awt.Graphics;
import java.awt.Rectangle;

import dev.game.Game;
import dev.game.Handler;
import dev.game.entities.EntityId;
import dev.game.entities.EntityManager;
import dev.game.entities.EntityName;
import dev.game.entities.creatures.Creature;
import dev.game.entities.statics.EntityFire;
import dev.game.gfx.Assets;
import dev.game.tiles.Tile;
import dev.game.utils.Utils;

/**
 * EntityCampfire.java - A pile of logs
 *
 * @author Juhyung Kim
 */
public class EntityCampfire extends PlaceableEntity {

	public EntityCampfire(Handler handler, float x, float y) {

		super(handler, EntityId.CAMPFIRE, x, y, Tile.TILE_SIZE, Tile.TILE_SIZE, 8,
				EntityName.CAMPFIRE);

		texture = Assets.campfire;

	}

	@Override
	public void place() {

		isCarried = false;
		texture = Utils.setAlpha(texture, 255);

		// lit for 5 minutes
		EntityFire fire = new EntityFire(handler, x, y - 32, Game.FPS * 300);
		EntityManager.addEntity(fire);

		// the actual y pos and rendered y pos is different due to render order with
		// fire
		y -= Tile.TILE_SIZE;

	}

	@Override
	public void followPlayer() {

		if (!isCarried)
			return;

		int dir = player.getDirection();

		if (dir == Creature.UP) {

			setX(player.getX());
			setY(player.getY() - 64);

		} else if (dir == Creature.LEFT) {

			setX(player.getX() - 64);
			setY(player.getY());

		} else if (dir == Creature.DOWN) {

			setX(player.getX());
			setY(player.getY() + 64);

		} else if (dir == Creature.RIGHT) {

			setX(player.getX() + 64);
			setY(player.getY());

		}
	}

	@Override
	public void updateSize() {

		// no size update needed
	}

	@Override
	public void tick() {

		if (player == null)
			this.player = handler.getWorld().getEntityManager().getPlayer();

		followPlayer();

	}

	@Override
	public void render(Graphics gfx) {

		if (isCarried && updateTexture) {

			updateTexture = false;
			texture = Utils.setAlpha(texture, 150);

		}

		if (isCarried) {

			gfx.drawImage(texture, (int) (x - camera.getXOffset()),
					(int) (y - camera.getYOffset()), width, height, null);

		} else {

			// the actual y pos and rendered y pos is different due to render order
			// with fire
			gfx.drawImage(texture, (int) (x - camera.getXOffset()),
					(int) (y - camera.getYOffset() + Tile.TILE_SIZE), width, height,
					null);

		}
	}

	@Override
	protected Rectangle getNewBounds() {

		return new Rectangle(4, 4, 56, 56);

	}

	@Override
	public void die() {

		// you can't really retreive burnt campfire
	}

}
