
package dev.game.entities.statics;

import java.awt.Graphics;

import dev.game.Handler;
import dev.game.entities.EntityId;
import dev.game.entities.EntityName;
import dev.game.entities.creatures.Player;
import dev.game.gfx.Assets;
import dev.game.tiles.Tile;
import dev.game.worlds.CaveWorld;
import dev.game.worlds.World;

/**
 * Allow player to travel underground.
 *
 * @author Juhyung Kim
 */
public class EntityCave extends StaticEntity {

	public static final int DEFAULT_WIDTH = Tile.TILE_SIZE,
			DEFAULT_HEIGHT = Tile.TILE_SIZE;

	public static final int CLOSED = 0, OPENED = 1;

	private int isOpen;
	private World world = null;
	private Player player = null;

	public EntityCave(Handler handler, float x, float y) {

		super(handler, EntityId.CAVE, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT,
				DEFAULT_HEALTH * 100, EntityName.CAVE);

		this.noCollision = true;
		this.isOpen = CLOSED;

		setBounds(32, 32, 1, 1);

	}

	@Override
	public void tick() {

		if (world == null && handler.getWorld() != null) {
			this.world = handler.getWorld();
			this.player = world.getEntityManager().getPlayer();
		}

		if (world == null)
			return;

		updateAvailability();
		checkEntering();

	}

	@Override
	public void render(Graphics gfx) {

		gfx.drawImage(Assets.cave, (int) (x - camera.getXOffset()),
				(int) (y - camera.getYOffset()), width, height, null);

	}

	@Override
	public void die() {
		// do nothing
	}

	/**
	 * Update the availability, so the player do not get stuck in infinite loop of
	 * cave traveling.
	 */
	private void updateAvailability() {

		if (isOpen == CLOSED && !isPlayerEntered())
			isOpen = OPENED;

	}

	/**
	 * Check if player have entered
	 */
	private void checkEntering() {

		boolean isOverlapped = isPlayerEntered();

		if (isOverlapped && isOpen == OPENED) {

			isOpen = CLOSED;
			sendPlayer();

		}

	}

	/**
	 * Send player to cave or world
	 */
	private void sendPlayer() {

		int dimension = world.getDimension();

		if (dimension == World.DIMENSION_WORLD) {

			CaveWorld.loadCaveWorld(world);

		} else {

			World.loadWorld(world);

		}

	}

	/////// getter setter//////////
	/**
	 * Check if player have entered.
	 *
	 * @return
	 */
	public boolean isPlayerEntered() {

		if (player == null)
			return false;

		return getBounds(0, 0).intersects(player.getBounds(0, 0));
	}

	/**
	 * @return the isOpen
	 */
	public int isOpen() {

		return isOpen;
	}

	/**
	 * @param isOpen the isOpen to set
	 */
	public void setOpen(int isOpen) {

		this.isOpen = isOpen;
	}

}
