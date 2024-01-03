
package dev.game.gfx;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FontLoader.java - load font
 *
 * @author j.kim3
 */
public class FontLoader {

	public static Font loadFont(String path, float size) {

		try {

			Font font = Font.createFont(Font.TRUETYPE_FONT,
					FontLoader.class.getResourceAsStream(path));

			return font.deriveFont(Font.PLAIN, size);

		} catch (FontFormatException | IOException ex) {

			Logger.getLogger(FontLoader.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(1);
		}
		return null;

	}

}
