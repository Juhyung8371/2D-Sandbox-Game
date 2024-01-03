
package dev.game.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import dev.game.gfx.Assets;
import dev.game.gfx.Text;

/**
 * UILabel.java - A label with text and image.
 *
 * @author j.kim3
 */
public class UILabel extends UIObject {

	/**
	 * When there's no auto line change needed
	 */
	public static final int NO_MAX_TEXT_WIDTH = -1;

	/**
	 * Fit the max width of the text as the width of the object
	 */
	public static final int FIT_OBJECT_WIDTH = -2;

	private BufferedImage image = null;
	private String text = "";
	private int maxTextWidth = NO_MAX_TEXT_WIDTH;

	/**
	 * A label with text and image. Text is centered.
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param image
	 */
	public UILabel(int x, int y, int width, int height, BufferedImage image) {

		super(x, y, width, height);
		this.image = image;

	}

	/**
	 * A label with text and image. Text is centered.
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param text
	 */
	public UILabel(int x, int y, int width, int height, String text) {

		super(x, y, width, height);
		this.text = text;

	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(Graphics gfx) {

		if (image != null)
			gfx.drawImage(image, (int) getX(), (int) getY(), getWidth(), getHeight(),
					null);

		if (!text.equals("")) {

			if (maxTextWidth == NO_MAX_TEXT_WIDTH) {

				Text.drawString(gfx, text, (int) getX() + getWidth() / 2,
						(int) getY() + getHeight() / 2);

			} else {

				Text.drawMultiLineString(gfx, text, (int) getX() + getWidth() / 2,
						(int) getY() + getHeight() / 2, maxTextWidth, Color.WHITE,
						Assets.font, true);

			}

		}

	}

	@Override
	public void onClick() {

		// no click event
	}

	/**
	 * @return the image
	 */
	public BufferedImage getImage() {

		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(BufferedImage image) {

		this.image = image;
	}

	/**
	 * @return the text
	 */
	public String getText() {

		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {

		this.text = text;
	}

	/**
	 * @return the maxWidth
	 */
	public int getMaxTextWidth() {

		return maxTextWidth;
	}

	/**
	 * @param max the maxWidth to set
	 */
	public void setMaxTextWidth(int max) {

		if (max == FIT_OBJECT_WIDTH) {

			this.maxTextWidth = width;

		} else {

			this.maxTextWidth = max;

		}

	}

}
