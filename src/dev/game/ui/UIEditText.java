
package dev.game.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import dev.game.gfx.Assets;
import dev.game.gfx.Text;
import dev.game.input.KeyManager;

/**
 * UIEditText.java - Editable text object.
 *
 * @author Juhyung Kim
 */
public class UIEditText extends UIObject {

	// the text
	private String text;
	// the text that shows when the text EditText is empty
	private String hint = "";
	// if selected, this allows typing
	private boolean isSelected = false;
	// max number of chars allowed to show at once
	private int maxChars;
	// the width allowed for the text to show at once
	private int contentWidth;
	// true if this edit text is receiving a text for file name
	private boolean isFileName = false;

	// class stuff
	// true if maxChars should be updated
	private boolean doUpdateMaxChars = false;
	// the visible part of the text that gets showed
	private String visibleText = "";
	// index of the cursor (always the same as text.length() - 1)
	private int cursorIndex = 0;
	// for keys
	private KeyManager keyManager;

	public UIEditText(int x, int y, int width, int height, String text,
			KeyManager keyManager) {

		super(x, y, width, height);

		this.text = text;

		if (text.length() > 1) {
			cursorIndex = text.length() - 1;
		}

		this.contentWidth = width - 8;

		doUpdateMaxChars = true;

		this.keyManager = keyManager;

	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics gfx) {

		keyTyped();

		if (doUpdateMaxChars) {

			doUpdateMaxChars = false;

			updateMaxChars(gfx);

			updateVisibleText();

		}

		if (isSelected) {

			gfx.drawImage(Assets.edit_text[1], x, y, null);

		} else {

			gfx.drawImage(Assets.edit_text[0], x, y, null);

		}

		FontMetrics matrics = gfx.getFontMetrics(Assets.font);

		int yy = y + (height / 2) - (matrics.getHeight() / 2) + matrics.getAscent();

		if (text.length() == 0) {

			Text.drawString(gfx, hint, x + 4, yy, new Color(255, 255, 255, 100),
					Assets.font, false);

		} else {

			Text.drawString(gfx, visibleText, x + 4, yy, Color.WHITE, Assets.font,
					false);

		}

	}

	@Override
	protected void onClick() {

	}

	/**
	 * On mouse released.
	 *
	 * @param e
	 */
	@Override
	protected void onMouseRelease(MouseEvent e) {

		if (isLocked) {
			isSelected = false;
		} else {
			// to unselect object when outside of the object is pressed
			isSelected = hovering;
		}

	}

	/**
	 * On typing event
	 */
	private void keyTyped() {

		if (!isSelected) {
			return;
		}

		if (keyManager.keyJustPressed(KeyEvent.VK_DELETE)) {

			text = "";
			cursorIndex = 0;

		} else if (keyManager.keyJustPressed(KeyEvent.VK_BACK_SPACE)
				&& text.length() > 0) {

			text = text.substring(0, text.length() - 1);

			cursorIndex--;

			updateVisibleText();

		} else if (keyManager.keyTyped) {

			keyManager.keyTyped = false;

			char key = keyManager.typedChar;

			if (isFileName) {

				switch (key) {

				case '\\':
				case '/':
				case ':':
				case '?':
				case '<':
				case '>':
				case '|':
				case '*':
				case '"':
					return;

				}

			}

			text += key;

			cursorIndex++;

			updateVisibleText();

			if (visibleText.length() == maxChars) {

				doUpdateMaxChars = true;

			}

		}

	}

	/**
	 * Update the max amount of characters that can be shown in the object.
	 *
	 * @param gfx
	 */
	private void updateMaxChars(Graphics gfx) {

		FontMetrics matrics = gfx.getFontMetrics(Assets.font);

		// if the visible text reached the end
		if (visibleText.length() == maxChars) {

			if (matrics.stringWidth(visibleText + 'a') <= contentWidth) {

				maxChars++;

			} else {

				// resize
				while (matrics.stringWidth(visibleText) > contentWidth) {

					maxChars--;

					updateVisibleText();

				}

			}

		} else {

			maxChars = contentWidth / matrics.charWidth('a');

		}

	}

	/**
	 * Update the visible part of the texts that are typed.
	 */
	private void updateVisibleText() {

		int index = cursorIndex - maxChars;

		if (index < 0) {
			visibleText = text;
		} else {
			visibleText = text.substring(index, text.length());
		}

	}

	// getter setter//////////
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

		if (text.length() == 0) {

			cursorIndex = 0;

		}

	}

	/**
	 * Get the hint text that shows when the content is empty.
	 *
	 * @return the hint
	 */
	public String getHint() {

		return hint;
	}

	/**
	 * Set the hint text that shows when the content is empty.
	 *
	 * @param hint the hint to set
	 */
	public void setHint(String hint) {

		this.hint = hint;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {

		return isSelected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {

		this.isSelected = isSelected;
	}

	/**
	 * Allowed width for the text content.
	 *
	 * @return in pixel
	 */
	public int getContentWidth() {

		return contentWidth;
	}

	/**
	 * Set allowed width for the text content.
	 *
	 * @param contentWidth the contentWidth to set
	 */
	public void setContentWidth(int contentWidth) {

		this.contentWidth = contentWidth;
	}

	/**
	 * Get if this EditText is receiving text for a file name.
	 *
	 * @return the isFileName
	 */
	public boolean isFileName() {

		return isFileName;
	}

	/**
	 * True if this EditText is receiving text for a file name.
	 *
	 * @param isFileName the isFileName to set
	 */
	public void setIsFileName(boolean isFileName) {

		this.isFileName = isFileName;
	}

}
