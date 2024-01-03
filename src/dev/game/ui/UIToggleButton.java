
package dev.game.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import dev.game.gfx.Assets;
import dev.game.gfx.Text;

/**
 * UIToggleButton.java - a button that can stay pressed
 *
 * @author Juhyung Kim
 */
public class UIToggleButton extends UIImageButton {

	private boolean pressed;

	public UIToggleButton(int x, int y, int width, int height,
			BufferedImage[] images, ClickListener clicker) {

		super(x, y, width, height, images, clicker);

	}

	@Override
	public void render(Graphics gfx) {

		int x = (int) getX();
		int y = (int) getY();
		int wid = getWidth();
		int hei = getHeight();

		int xPos = x + wid / 2;
		int yPos = y + hei / 2;

		// not pressed, not hovering, so default image
		if (!pressed && !isHovering()) {

			gfx.drawImage(images[0], x, y, wid, hei, null);

			if (image != null) {

				int size = (int) (Math.min(wid, hei) / 1.4);

				gfx.drawImage(image, xPos - size / 2, yPos - size / 2, size, size,
						null);

			}

			if (text != "") {

				if (bigFont) {

					Text.drawString(gfx, text, xPos, yPos, Color.WHITE,
							Assets.bigFont, true);

				} else {

					Text.drawString(gfx, text, xPos, yPos);

				}
			}

		} // else, it's pressed
		else {

			gfx.drawImage(images[1], x, y, wid, hei, null);

			if (image != null) {

				int size = (int) (Math.min(wid, hei) / 1.2);

				gfx.drawImage(image, xPos - size / 2, yPos - size / 2, size, size,
						null);

			}

			if (text != "") {

				if (bigFont) {

					Text.drawString(gfx, text, xPos, yPos, Color.YELLOW,
							Assets.bigFontPressed, true);

				} else {

					Text.drawString(gfx, text, xPos, yPos, Color.YELLOW,
							Assets.fontPressed, true);

				}

			}
		}
	}

	/**
	 * On toggle button and call click event
	 */
	@Override
	protected void onClick() {

		toggle();

		clicker.onClick();

	}

	/**
	 * @return the pressed
	 */
	public boolean isPressed() {

		return pressed;
	}

	/**
	 * @param pressed the pressed to set
	 */
	public void setPressed(boolean pressed) {

		this.pressed = pressed;
	}

	/**
	 * Toggle the button
	 */
	public void toggle() {

		this.pressed = (!this.pressed);
	}

}
