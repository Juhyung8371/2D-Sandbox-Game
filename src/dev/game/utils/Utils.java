
package dev.game.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import dev.game.entities.Entity;
import dev.game.gfx.Assets;
import dev.game.gfx.ImageLoader;
import dev.game.tiles.Tile;
import dev.game.tiles.TileId;

/**
 * Utils.java - the library of utility methods
 *
 * @author Juhyung Kim
 */
public class Utils {

	/**
	 * Load the file as a ArrayList of Strings (each line)
	 *
	 * @param path
	 * @return null if an error occur
	 */
	public static ArrayList<String> loadFileAsArrays(String path) {

		ArrayList<String> data = new ArrayList<>();

		BufferedReader br = null;

		try {

			br = new BufferedReader(new FileReader(path));

			String line; // current line reading

			while ((line = br.readLine()) != null) {

				data.add(line);

			}

		} catch (IOException e) {

			return null;

		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException ex) {
					Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null,
							ex);
				}
			}

		}

		return data;

	}

	/**
	 * Load a file as a String from the path
	 *
	 * @param path
	 * @return
	 */
	public static String loadFileAsString(String path) {

		StringBuilder builder = new StringBuilder();
		BufferedReader br = null;

		try {

			br = new BufferedReader(new FileReader(path));

			String line; // current line reading

			while ((line = br.readLine()) != null) {

				builder.append(line).append("\n"); // adding new lines

			}

			// remove new line chracter for last line
			builder.delete(builder.length() - 1, builder.length());

		} catch (IOException e) {

			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);

		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException ex) {
					Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null,
							ex);
				}
			}

		}

		return builder.toString();

	}

	/**
	 * Parse integer from a string. If an error occur, returns -1
	 *
	 * @param number String to parse
	 * @return parsed integer (-1 if <code>number</code> cannot be parsed)
	 */
	public static int parseInt(String number) {

		try {

			return Integer.parseInt(number);

		} catch (NumberFormatException e) {

			return -1;

		}

	}

	/**
	 * Check if the string is parsable number.
	 *
	 * @param number
	 * @return
	 */
	public static boolean isNumber(String number) {

		try {

			Integer.parseInt(number);

			return true;

		} catch (NumberFormatException e) {

			return false;

		}

	}

	/**
	 * Set Alpha value of BufferedImage
	 *
	 * @param img
	 * @param opacity
	 * @return new image
	 */
	public static BufferedImage setAlpha(BufferedImage img, int opacity) {

		BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		for (int xx = 0; xx < img.getWidth(); xx++) {

			for (int yy = 0; yy < img.getHeight(); yy++) {

				int rbg = img.getRGB(xx, yy);

				int transparency = (rbg >> 24) & 0xff;
				int r = (rbg >> 16) & 0xff;
				int g = (rbg >> 8) & 0xff;
				int b = (rbg) & 0xff;

				int alpha = opacity;

				// if the pixel is already suppose to be transparent, alpha = 0
				if (transparency == 0) {

					alpha = 0;

				} else if (alpha < 0) {

					alpha = 0;

				} else if (alpha > 255) {

					alpha = 255;

				}

				Color color = new Color(r, g, b, alpha);

				image.setRGB(xx, yy, color.getRGB());

			}

		}
		return image;
	}

	/**
	 *
	 * Generate a image of a map tile data
	 *
	 * @param data     map data
	 * @param fileName without .png
	 */
	public static void generateImageFromMap(Tile[][] data, String fileName) {

		BufferedImage image = new BufferedImage(data[0].length, data.length,
				BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < data.length; y++) {

			for (int x = 0; x < data[0].length; x++) {

				int r = 0, g = 0, b = 0;

				switch (data[y][x].getId()) {

				case TileId.GRASS:
					r = 40;
					g = 140;
					b = 10;
					break;

				case TileId.DIRT:
					r = 100;
					g = 50;
					b = 0;
					break;

				case TileId.GRASSY_DIRT:
					r = 80;
					g = 110;
					b = 10;
					break;

				case TileId.TOPSOIL:
					r = 60;
					g = 30;
					b = 0;
					break;

				case TileId.GRAVEL:
					r = 140;
					g = 140;
					b = 140;
					break;

				case TileId.GRASSY_GRAVEL:
					r = 140;
					g = 180;
					b = 140;
					break;

				case TileId.WATER:
					r = 0;
					g = 0;
					b = 255;
					break;

				case TileId.SAND:
					r = 240;
					g = 240;
					b = 0;
					break;

				case TileId.OCHER:
					r = 250;
					g = 140;
					b = 20;
					break;

				case TileId.GRASSY_MUD:
					r = 120;
					g = 120;
					b = 0;
					break;

				case TileId.SNOW:
					r = 250;
					g = 250;
					b = 250;
					break;

				case TileId.MUD:
					r = 20;
					g = 20;
					b = 20;
					break;
				}

				Color color = new Color(r, g, b);

				int rgb = color.getRGB();

				image.setRGB(x, y, rgb);

			}

		}

		try {
			// retrieve image
			File outputfile = new File(fileName + ".png");
			outputfile.createNewFile();

			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			throw new RuntimeException("I didn't handle this very well");
		}

	}

	/**
	 * Generate a world map file by converting the image file
	 * <p>
	 * TODO: Not used in game yet (for a minimap)
	 * <p>
	 * Image Path: "/res/map.png" Map file Path: "generate.txt"
	 *
	 * @return true if file is made
	 * @throws IOException
	 */
	public static boolean generateMapFromImage() throws IOException {

		File fout = new File("generate.txt");

		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		if (!new File("/res/map.png").exists()) {
			bw.close();
			return false;
		}

		BufferedImage image = ImageLoader.load("/res/map.png");

		// map width and height
		bw.write(image.getWidth() + " " + image.getHeight());
		bw.newLine();

		// player spawn location
		bw.write("64 64");
		bw.newLine();

		for (int yy = 0; yy < image.getHeight(); yy++) {

			for (int xx = 0; xx < image.getWidth(); xx++) {

				int rbg = image.getRGB(xx, yy);

				int r = (rbg >> 16) & 0xff;
				int g = (rbg >> 8) & 0xff;
				int b = (rbg) & 0xff;

				// yellow sand
				if (r == 255 && g == 255 && b == 0) {
					bw.write(4 + "");
				} // green grass
				else if (r == 0 && g == 255 && b == 0) {
					bw.write(2 + "");
				} // gray stone
				else if (r == 150 && g == 150 && b == 150) {
					bw.write(1 + "");
				} // blue water
				else if (r == 0 && g == 0 && b == 255) {
					bw.write(3 + "");
				}

				if (xx != image.getWidth() - 1) {
					bw.write(" ");
				}

			}
			bw.newLine();
		}
		bw.close();

		return true;
	}

	/**
	 * Get the distance between two point by Manhattan method. Manhattan method is
	 * the way to get distance by counting the blocks between the points only by
	 * 4-way (NSEW)
	 *
	 * @param startX
	 * @param startY
	 * @param targetX
	 * @param targetY
	 * @return distance
	 */
	public static int getDistanceManhattan(int startX, int startY, int targetX,
			int targetY) {

		int xDis = Math.abs(startX - targetX);
		int yDis = Math.abs(startY - targetY);

		return (xDis + yDis);

	}

	/**
	 * Get shortest distance including diagonal path. Both straight and diagonal
	 * path counts as 1 block each.
	 *
	 * @param startX
	 * @param startY
	 * @param targetX
	 * @param targetY
	 * @return
	 */
	public static int getDistance(int startX, int startY, int targetX, int targetY) {

		int xDis = Math.abs(startX - targetX);
		int yDis = Math.abs(startY - targetY);

		int diagonal = Math.min(xDis, yDis); // amount of diagonal paths

		int straight = Math.max(xDis, yDis) - diagonal; // amount of straight
		// paths

		return (diagonal + straight);

	}

	/**
	 * Quick-sort a list in ascending order.
	 *
	 * @param toSort    The list to sort
	 * @param lowIndex  First index (0)
	 * @param highIndex Last index (list.size() - 1)
	 */
	public static void quickSort(List<Entity> toSort, int lowIndex, int highIndex) {

		if (toSort.isEmpty())
			return;

		if (highIndex == lowIndex) {
			return;
		}

		// two elements left for this partition
		if (highIndex - lowIndex == 1) {

			Entity left = toSort.get(lowIndex);
			Entity right = toSort.get(highIndex);

			// if left one is bigger than right one
			if (left.getY() + left.getHeight() > right.getY() + right.getHeight()) {

				// swap them
				toSort.set(lowIndex, right);
				toSort.set(highIndex, left);

			}

			return;

		}

		// first index
		int i = lowIndex;
		// last index
		int j = highIndex;

		Entity pivotEnt = toSort.get(lowIndex + (highIndex - lowIndex) / 2);

		// pick a pivot value to divide the list with
		float pivot = pivotEnt.getY() + pivotEnt.getHeight();

		while (i <= j) {

			// from left, move toward right until
			// it finds a value bigger than pivot
			while (toSort.get(i).getY() + toSort.get(i).getHeight() < pivot) {
				i++;
			}

			// from right, move toward left until
			// it finds a value smaller than pivot
			while (toSort.get(j).getY() + toSort.get(j).getHeight() > pivot) {
				j--;
			}

			// swap the i and j indexes
			if (i <= j) {

				if (i < j) {
					Entity temp = toSort.get(i);
					toSort.set(i, toSort.get(j));
					toSort.set(j, temp);
				}

				// move the indexes
				i++;
				j--;

			}

		}

		// if there are more sorting to do on smaller values side
		if (lowIndex < j)
			quickSort(toSort, lowIndex, j);

		// if there are more sorting to do on bigger values side
		if (i < highIndex)
			quickSort(toSort, i, highIndex);

	}

	/**
	 * Quick floor method. Used when flooring possible negative number. From
	 * SimplexNoise class author
	 *
	 * @param num
	 * @return
	 */
	public static int quickFloor(float num) {

		int num2 = (int) num;
		return (num < num2) ? num2 - 1 : num2;
	}

	/**
	 * Delete a file (folder), and all files in the folder.
	 *
	 * @param file
	 * @param isRoot always input true, true if this file is the topmost file
	 */
	public static void deleteFile(File file, boolean isRoot) {

		if (isRoot) {

			int option = JOptionPane.showConfirmDialog(null,
					"Will you delete this world?", "Confirm",
					JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
					new ImageIcon(Assets.panda_idle_down[0]));

			if (option != JOptionPane.YES_OPTION)
				return;

		}

		for (File f : file.listFiles()) {

			if (f.isDirectory()) {
				deleteFile(f, false);
				f.delete();
			} else {
				f.delete();
			}
		}
		file.delete();
	}

}
