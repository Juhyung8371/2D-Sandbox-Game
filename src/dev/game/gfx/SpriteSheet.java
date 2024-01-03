
package dev.game.gfx;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * SpriteSheet.java - Spritesheet to make the image cutting easier
 *
 * @author j.kim3
 */
public class SpriteSheet {

	private BufferedImage sheet;

	private static final int WIDTH = 32, HEIGHT = 32;

	private static final int ITEM_WIDTH = 32, ITEM_HEIGHT = 32;

	public SpriteSheet(BufferedImage sheet) {

		this.sheet = sheet;

	}

	/**
	 * Crop an image from the sprite
	 *
	 * @param x      Position along x axis in factor of 32
	 * @param y      Position along y axis in factor of 32
	 * @param width  Image width in factor of 32
	 * @param height Image height in factor of 32
	 * @return Cropped image
	 */
	public BufferedImage crop(int x, int y, int width, int height) {

		return sheet.getSubimage(x * WIDTH, y * HEIGHT, width * WIDTH,
				height * HEIGHT);

	}

	/**
	 * Crop an image from the sprite Width and height is 32 pixel by 32 pixel
	 *
	 * @param x Position along x axis in factor of 32
	 * @param y Position along y axis in factor of 32
	 * @return Cropped image
	 */
	public BufferedImage crop(int x, int y) {

		return this.crop(x, y, 1, 1);

	}

	/**
	 * Crop an image from a sprite, with the specific width and height
	 *
	 * @param x      Position along x axis in factor of width
	 * @param y      Position along x axis in factor of height
	 * @param width  Width of the image
	 * @param height Height of the image
	 * @return Cropped image
	 */
	public BufferedImage cropInScale(int x, int y, int width, int height) {

		return sheet.getSubimage(x * width, y * height, width, height);

	}

	/**
	 * Crop an image from a sprite, with the specific width and height
	 *
	 * @param x      Position along x axis in factor of width
	 * @param y      Position along x axis in factor of height
	 * @param width  Width of the image
	 * @param height Height of the image
	 * @param xStart
	 * @param yStart
	 *
	 * @return Cropped image
	 */
	public BufferedImage cropInScale(int x, int y, int width, int height, int xStart,
			int yStart) {

		return sheet.getSubimage(xStart + x * width, yStart + y * height, width,
				height);

	}

	/**
	 * 100% hard code the position and size for the cropping
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public BufferedImage cropInPixel(int x, int y, int width, int height) {

		return sheet.getSubimage(x, y, width, height);

	}

	/**
	 * Crop item
	 *
	 * @param x in factor of item width
	 * @param y in factor of item height
	 * @return
	 */
	public BufferedImage cropItem(int x, int y) {

		return sheet.getSubimage(x * ITEM_WIDTH, y * ITEM_HEIGHT, ITEM_WIDTH,
				ITEM_HEIGHT);

	}

	/**
	 * Flip the image horizontally
	 *
	 * @param image
	 * @return
	 */
	public static BufferedImage horizontalFlip(BufferedImage image) {

		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-image.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);

		return image;

	}

	/**
	 * Flip the image vertically
	 *
	 * @param image
	 * @return
	 */
	public static BufferedImage verticalFlip(BufferedImage image) {

		// Flip the image vertically
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -image.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);

		return image;

	}

}
