
package dev.game.entities.statics.placeables;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

import dev.game.Handler;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.entities.creatures.Creature;
import dev.game.gfx.Animation;
import dev.game.gfx.Assets;
import dev.game.items.Item;
import dev.game.items.ItemId;
import dev.game.items.ItemManager;
import dev.game.lights.Light;
import dev.game.lights.LightManager;
import dev.game.tiles.Tile;
import dev.game.utils.Utils;

/**
 * EntityTorch.java - Torch to light
 *
 * @author Juhyung Kim
 */
public class EntityTorch extends PlaceableEntity {

	public static final int LIGHT_RANGE = 6;

	private Animation animation;

	public EntityTorch(Handler handler, float x, float y) {

		super(handler, EntityId.TORCH, x, y, 16, 64, 1, EntityName.TORCH);

		animation = new Animation(250, Assets.torch);

		// to prevent all the torches flickering at uniform interval (which is
		// unnatural)
		int ran = new Random().nextInt(20) * 5;
		animation.timer += ran;

		texture = Assets.torch[0];

	}

	@Override
	public void place() {

		isCarried = false;

		texture = Utils.setAlpha(texture, 255);

		// centering it
		setX(getCenterX() * Tile.TILE_SIZE + (Tile.TILE_SIZE / 2 - width / 2));
		setY(getCenterY() * Tile.TILE_SIZE);

		LightManager.add(new Light(this, getCenterX(), getCenterY(), LIGHT_RANGE,
				Tile.DEFAULT_BRIGHTNESS));

	}

	@Override
	public void followPlayer() {

		if (!isCarried) {
			return;
		}

		int dir = player.getDirection();

		if (dir == Creature.UP) {

			setX(player.getX() + 26);
			setY(player.getY() - 32);

		} else if (dir == Creature.LEFT) {

			setX(player.getX() - 10);
			setY(player.getY());

		} else if (dir == Creature.DOWN) {

			setX(player.getX() + 26);
			setY(player.getY() + 32);

		} else if (dir == Creature.RIGHT) {

			setX(player.getX() + 58);
			setY(player.getY());

		}

	}

	@Override
	public void updateSize() {

		// unnecessary
	}

	@Override
	public void tick() {

		if (player == null)
			this.player = handler.getWorld().getEntityManager().getPlayer();

		followPlayer();

		updateSize();

		animation.tick();
	}

	/**
	 * Torch is an light emitter, so the brightness of the texture is always opaque
	 */
	@Override
	public void render(Graphics gfx) {

		if (isCarried && updateTexture) {

			updateTexture = false;
			texture = Utils.setAlpha(texture, 150);

		} else if (!isCarried) {

			texture = animation.getCurrentFrame();

		}

		gfx.drawImage(texture, (int) (x - camera.getXOffset()),
				(int) (y - camera.getYOffset()), width, height, null);

	}

	@Override
	public void die() {

		Item loot = Item.getItemEntity(ItemId.TORCH, 0, 0);

		loot.setPositionInBlock(getCenterX(), getCenterY());

		ItemManager.addItem(loot);
	}

	@Override
	protected Rectangle getNewBounds() {

		return new Rectangle(24, 2, 16, 60);

	}

}
