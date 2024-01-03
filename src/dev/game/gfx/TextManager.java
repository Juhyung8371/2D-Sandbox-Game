
package dev.game.gfx;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * TextManager.java - Manager for texts, mainly for client message.
 *
 * @author Juhyung Kim
 */
public class TextManager {

	private static List<Text> texts = new ArrayList<>();

	/**
	 * Manager for texts, mainly for client message.
	 */
	public TextManager() {

	}

	public void tick() {

		for (int i = 0; i < texts.size(); i++) {

			Text text = texts.get(i);

			text.tick();

		}

	}

	public void render(Graphics gfx) {

		for (int i = 0; i < texts.size(); i++) {

			Text text = texts.get(i);

			text.render(gfx);

			if (!text.isAlive())
				texts.remove(text);

		}

	}

	public static void addText(Text text) {

		texts.add(text);

	}

}
