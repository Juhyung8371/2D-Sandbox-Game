
package dev.game.display;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * Display.java - Create the frame for the game
 *
 * @author j.kim3
 */
public class Display {

	private JFrame frame; // frame to display canvas

	private Canvas canvas; // canvas to draw

	private String title;
	private int width, height;

	public Display(String title, int width, int height) {

		this.title = title;
		this.width = width;
		this.height = height;

		createDisplay();
	}

	private void createDisplay() {

		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);

		Dimension dimen = new Dimension(width, height);

		canvas = new Canvas();
		canvas.setPreferredSize(dimen);
		canvas.setMaximumSize(dimen);
		canvas.setMinimumSize(dimen);
		canvas.setFocusable(false); // let JFrame the one to be focused

		frame.add(canvas);
		frame.pack();

	}

	public Canvas getCanvas() {
		return canvas;
	}

	public JFrame getFrame() {
		return frame;
	}

}
