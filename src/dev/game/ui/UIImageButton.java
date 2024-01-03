
package dev.game.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import dev.game.gfx.Assets;
import dev.game.gfx.Text;

/**
 * UIImageButton.java - image button with text available
 *
 * @author j.kim3
 */
public class UIImageButton extends UIObject {

	protected BufferedImage[] images; // index 0 = not clicked, 1 = clicked
	protected ClickListener clicker;
	protected String text;
	protected BufferedImage image;

	public boolean bigFont;

	/**
	 * A button with image, text, background image etc.
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param images
	 * @param clicker can be null
	 */
	public UIImageButton(int x, int y, int width, int height, BufferedImage[] images,
			ClickListener clicker) {

		super(x, y, width, height);

		this.images = images;
		this.clicker = clicker;
		this.text = "";
		this.bigFont = false;
		this.image = null;

	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics gfx) {

		int x = (int) getX();
		int y = (int) getY();
		int wid = getWidth();
		int hei = getHeight();

		int xPos = x + wid / 2;
		int yPos = y + hei / 2;

		if (isHovering()) {

			// background
			gfx.drawImage(images[1], x, y, wid, hei, null);

			if (image != null) {

				int size = Math.min(wid, hei) / 2;

				gfx.drawImage(image, xPos - size / 2, yPos - size / 2, size, size,
						null);

			}

			if (!text.equals("")) {

				if (bigFont) {

					Text.drawString(gfx, text, xPos, yPos, Color.YELLOW,
							Assets.bigFontPressed, true);

				} else {

					Text.drawString(gfx, text, xPos, yPos, Color.YELLOW,
							Assets.fontPressed, true);

				}

			}

		} else {

			// background
			gfx.drawImage(images[0], x, y, wid, hei, null);

			if (image != null) {

				int size = (int) (Math.min(wid, hei) / 2.4);

				gfx.drawImage(image, xPos - size / 2, yPos - size / 2, size, size,
						null);

			}

			if (!text.equals("")) {

				if (bigFont) {

					Text.drawString(gfx, text, xPos, yPos, Color.WHITE,
							Assets.bigFont, true);

				} else {

					Text.drawString(gfx, text, xPos, yPos);

				}

			}

		}

	}

	@Override
	protected void onClick() {

		if (clicker != null)
			clicker.onClick();

	}

	//// getter setter /////
	public void setClickLisenter(ClickListener clicker) {

		this.clicker = clicker;

	}

	public String getText() {

		return text;
	}

	public void setText(String text) {

		this.text = text;
	}

	public void setImages(BufferedImage[] images) {

		this.images = images;
	}

	public void setImage(BufferedImage image) {

		this.image = image;
	}

}
