
package dev.game.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import dev.game.Game;

/**
 * Text.java - Draw string on screen
 *
 * @author j.kim3
 */
public class Text {

	public static final int INFINITE_DURATION = -1;

	private String text;
	private int duration;
	private int x, y;
	private boolean isCentered;
	private boolean doFall;
	private boolean isAlive;
	private Font font;
	private Color color;

	/**
	 * A text that shows on the most top of the screen.
	 * <p>
	 * This constructor is used mainly to show client message on top of the screen.
	 * Creating a Text object is recommended when the text is temporary.
	 *
	 * @param text       to show
	 * @param x          position in pixel
	 * @param y          position in pixel
	 * @param duration   length of time to show the text in tick (Game.FPS)
	 * @param doFall     true if the text should gradually go downward from its
	 *                   original spot
	 * @param isCentered true if text should be centered in the position
	 * @param font       font of text
	 * @param color      color of text
	 */
	public Text(String text, int x, int y, int duration, boolean doFall,
			boolean isCentered, Font font, Color color) {

		this.text = text;
		this.x = x;
		this.y = y;
		this.duration = duration;
		this.doFall = doFall;
		this.isCentered = isCentered;
		this.font = font;
		this.color = color;

		this.isAlive = true;

	}

	/**
	 * A text that shows on the most top of the screen.
	 * <p>
	 * Duration is Game.FPS (1 second). And text is Centered at the middle of the
	 * screen.
	 *
	 * @param text   to show
	 * @param doFall true if the text should gradually go downward from its original
	 *               spot
	 * @param font   font of text
	 * @param color  color of text
	 */
	public Text(String text, boolean doFall, Font font, Color color) {

		this(text, Game.SCREEN_WIDTH / 2, Game.SCREEN_HEIGHT / 2, Game.FPS, doFall,
				true, font, color);

	}

	public void tick() {

		if (duration != INFINITE_DURATION) {

			duration--;

			if (duration <= 0)
				isAlive = false;

		}

		if (doFall)
			y++;

	}

	public void render(Graphics gfx) {

		drawString(gfx, text, x, y, color, font, isCentered);

	}

	// static methods/////////////
	/**
	 * Draw string on screen. New line character accepted (\n).
	 *
	 * @param gfx        graphics
	 * @param text       to write
	 * @param xPos       x pos
	 * @param yPos       y pos
	 * @param c          color
	 * @param font       font in assets
	 * @param isCentered true if x, y is the value for center
	 */
	public static void drawString(Graphics gfx, String text, int xPos, int yPos,
			Color c, Font font, boolean isCentered) {

		gfx.setColor(c);
		gfx.setFont(font);
		FontMetrics matrics = gfx.getFontMetrics(font);

		String[] strings = text.split("\n");

		int x = xPos;
		int y = yPos;

		int length = strings.length;

		int maxWidth = 0;

		// find the longest line
		for (int i = 0; i < length; i++) {

			if (matrics.stringWidth(strings[i]) > maxWidth) {
				maxWidth = matrics.stringWidth(strings[i]);
			}

		}

		if (isCentered) {

			x = xPos - maxWidth / 2;
			y = (yPos - matrics.getHeight() / 2) + matrics.getAscent();

		}

		// centering y
		y -= matrics.getHeight() * (strings.length / 2);

		for (int i = 0; i < strings.length; i++) {

			gfx.drawString(strings[i], x, y);

			y += matrics.getHeight();

		}

	}

	/**
	 * Draw string on screen. Text Centered. New line character accepted (\n).
	 *
	 * @param gfx  graphics
	 * @param text to write
	 * @param xPos x pos
	 * @param yPos y pos
	 */
	public static void drawString(Graphics gfx, String text, int xPos, int yPos) {

		drawString(gfx, text, xPos, yPos, Color.WHITE, Assets.font, true);

	}

	/**
	 * Draw string on screen. Inventory title text size. New line character accepted
	 * (\n).
	 *
	 * @param gfx        graphics
	 * @param text       to write
	 * @param xPos       x pos
	 * @param yPos       y pos
	 * @param isCentered True if x, y is the value for center
	 */
	public static void drawString(Graphics gfx, String text, int xPos, int yPos,
			boolean isCentered) {

		drawString(gfx, text, xPos, yPos, Color.WHITE, Assets.font, isCentered);

	}

	/**
	 * Draw the multi-line text. The text will continue in new line if the string
	 * cross over the maximum width. New line character ignored (\n).
	 *
	 * @param gfx
	 * @param text
	 * @param xPos
	 * @param yPos
	 * @param maxWidth
	 * @param c
	 * @param font
	 * @param isCentered
	 */
	public static void drawMultiLineString(Graphics gfx, String text, int xPos,
			int yPos, int maxWidth, Color c, Font font, boolean isCentered) {

		FontMetrics matrics = gfx.getFontMetrics(font);

		gfx.setColor(c);

		gfx.setFont(font);

		int x = xPos;
		int y = yPos;

		if (isCentered) {

			y = (yPos - matrics.getHeight() / 2) + matrics.getAscent();

		}

		if (matrics.stringWidth(text) < maxWidth) {

			if (isCentered) {

				x = xPos - matrics.stringWidth(text) / 2;

			}

			gfx.drawString(text, x, y);

		} else {

			String[] words = text.split(" ");

			String currentLine = words[0];

			for (int i = 1; i < words.length; i++) {

				if (matrics.stringWidth(currentLine + " " + words[i]) < maxWidth) {

					currentLine += " " + words[i];

				} else {

					if (isCentered) {

						x = xPos - matrics.stringWidth(currentLine) / 2;

					}

					gfx.drawString(currentLine, x, y);

					y += matrics.getHeight();

					currentLine = words[i];

				}

			}

			if (currentLine.length() > 0) {

				if (isCentered) {

					x = xPos - matrics.stringWidth(currentLine) / 2;

				}

				gfx.drawString(currentLine, x, y);

			}

		}

	}

	/// getter setter/////////////
	/**
	 * @return the isAlive
	 */
	public boolean isAlive() {

		return isAlive;
	}

	/**
	 * @param isAlive the isAlive to set
	 */
	public void setAlive(boolean isAlive) {

		this.isAlive = isAlive;
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
	 * @return the duration
	 */
	public int getDuration() {

		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {

		this.duration = duration;
	}

	/**
	 * @return the x in pixel
	 */
	public int getX() {

		return x;
	}

	/**
	 * @param x the x to set in pixel
	 */
	public void setX(int x) {

		this.x = x;
	}

	/**
	 * @return the y in pixel
	 */
	public int getY() {

		return y;
	}

	/**
	 * @param y the y to set in pixel
	 */
	public void setY(int y) {

		this.y = y;
	}

	/**
	 * @return the isCentered
	 */
	public boolean isCentered() {

		return isCentered;
	}

	/**
	 * @param isCentered true if centered
	 */
	public void setCentered(boolean isCentered) {

		this.isCentered = isCentered;
	}

	/**
	 * True if the text should gradually go downward from its original spot
	 *
	 * @return the doFall
	 */
	public boolean isdoFall() {

		return doFall;
	}

	/**
	 * @param doFall true if the text should gradually go downward from its original
	 *               spot
	 */
	public void setdoFall(boolean doFall) {

		this.doFall = doFall;
	}

	/**
	 * @return the font
	 */
	public Font getFont() {

		return font;
	}

	/**
	 * @param font the font to set
	 */
	public void setFont(Font font) {

		this.font = font;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {

		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {

		this.color = color;
	}

}
