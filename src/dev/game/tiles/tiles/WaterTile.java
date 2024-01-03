
package dev.game.tiles.tiles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import dev.game.gfx.Animation;
import dev.game.gfx.Assets;
import dev.game.tiles.Tile;
import dev.game.tiles.TileId;
import dev.game.tiles.TileManager;

/**
 * WaterTile.java - water tile
 *
 * @author j.kim3
 */
public class WaterTile extends Tile {

	private Animation anim_water;

	// true if the side of block is water
	private boolean up, down, left, right;

	// to check the sides only once
	private boolean checked = false;

	public WaterTile() {

		super(Assets.water[0], TileId.WATER, "Water", true);

		anim_water = new Animation(3000, Assets.water);

	}

	@Override
	public void tick() {

		if (!checked) {

			int x = getX(), y = getY();

			TileManager man = getHandler().getWorld().getTileManager();

			int idUp = man.getTile(x, y - 1).getId();
			int idDown = man.getTile(x, y + 1).getId();
			int idLeft = man.getTile(x - 1, y).getId();
			int idRight = man.getTile(x + 1, y).getId();

			up = (idUp == TileId.WATER || idUp == TileId.AIR);
			down = (idDown == TileId.WATER || idDown == TileId.AIR);
			left = (idLeft == TileId.WATER || idLeft == TileId.AIR);
			right = (idRight == TileId.WATER || idRight == TileId.AIR);

			checked = true;
		}

		anim_water.tick();

	}

	@Override
	public void render(Graphics gfx) {

		BufferedImage image;

		// various looks of water tile
		if (up && down && left && right) {
			image = anim_water.getCurrentFrame();
		} else if (!up && !down && !left && !right) {
			image = Assets.water_cccc;
		} else if (!up && !down && !left && right) {
			image = Assets.water_ccco;
		} else if (!up && !down && left && right) {
			image = Assets.water_ccoo;
		} else if (!up && !down && left && !right) {
			image = Assets.water_ccoc;
		} else if (!up && down && !left && !right) {
			image = Assets.water_cocc;
		} else if (up && down && !left && !right) {
			image = Assets.water_oocc;
		} else if (up && !down && !left && !right) {
			image = Assets.water_occc;
		} else if (!up && down && !left && right) {
			image = Assets.water_coco;
		} else if (!up && down && left && right) {
			image = Assets.water_cooo;
		} else if (!up && down && left && !right) {
			image = Assets.water_cooc;
		} else if (up && down && !left && right) {
			image = Assets.water_ooco;
		} else if (up && down && left && !right) {
			image = Assets.water_oooc;
		} else if (up && !down && !left && right) {
			image = Assets.water_occo;
		} else if (up && !down && left && right) {
			image = Assets.water_ocoo;
		} else if (up && !down && left && !right) {
			image = Assets.water_ococ;
		} else {
			image = Assets.water_cccc;
		}

		gfx.drawImage(image, (int) (getX() * Tile.TILE_SIZE - camera.getXOffset()),
				(int) (getY() * Tile.TILE_SIZE - camera.getYOffset()), TILE_SIZE,
				TILE_SIZE, null);

	}

}
