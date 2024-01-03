
package dev.game.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * ImageLoader.java - load the image from path
 *
 * @author j.kim3
 */
public class ImageLoader {

	public static BufferedImage load(String path) {

		try {

			return ImageIO.read(ImageLoader.class.getResource(path));

		} catch (IOException ex) {

			Logger.getLogger(ImageLoader.class.getName()).log(Level.SEVERE, null,
					ex);

			System.exit(1);
		}

		return null;

	}

}
