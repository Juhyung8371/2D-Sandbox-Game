
package dev.game;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import dev.game.display.Display;
import dev.game.gfx.Assets;
import dev.game.gfx.GameCamera;
import dev.game.gfx.TextManager;
import dev.game.input.KeyManager;
import dev.game.input.MouseManager;
import dev.game.states.DeathState;
import dev.game.states.LobbyState;
import dev.game.states.MenuState;
import dev.game.states.State;

/**
 * Game.java - Main code for game
 *
 * @author j.kim3
 */
public class Game implements Runnable {

	/**
	 * Size of the game screen in pixel.
	 */
	public static final int SCREEN_WIDTH = 900, SCREEN_HEIGHT = 600;

	/**
	 * Title text showing on the top left corner of the screen.
	 */
	public static final String TITLE = "The Panda Game - by Juhyung Kim";

	/**
	 * Frames per second
	 */
	public static final int FPS = 40;
	/**
	 * Time per Frame
	 */
	public static final int FPS_TO_MILLISEC = 1000 / FPS;

	public String title;

	private Display display;
	private int width, height; // screen size in pixel

	// main thread for running
	private Thread thread;

	// true if the game is running
	private boolean running;

	// tells computer what to render
	// buffer is like a hidden screen to draw
	private BufferStrategy bs;
	private Graphics gfx;

	// States
	public State gameState;
	public State lobbyState;
	public State menuState;
	public State deathState;

	// inputs
	private KeyManager keyManager;
	private MouseManager mouseManager;
	private TextManager textManager;

	// camera
	private GameCamera gameCamera;

	// handler
	private Handler handler;

	public Game() {

		this.width = SCREEN_WIDTH;
		this.height = SCREEN_HEIGHT;
		this.title = TITLE;
		this.keyManager = new KeyManager();
		this.mouseManager = new MouseManager();
		this.textManager = new TextManager();

	}

	/**
	 * Initialize data for game
	 */
	private void init() {

		display = new Display(title, width, height);

		JFrame frame = display.getFrame();
		Canvas canvas = display.getCanvas();

		frame.addKeyListener(keyManager);

		// adding them to both frame and canvas in case one is not focused and create
		// bug
		frame.addMouseListener(mouseManager);
		frame.addMouseMotionListener(mouseManager);
		canvas.addMouseListener(mouseManager);
		canvas.addMouseMotionListener(mouseManager);

		Assets.init();

		handler = new Handler(this);

		gameCamera = new GameCamera(handler, 0, 0);

		lobbyState = new LobbyState(handler);
		menuState = new MenuState(handler);
		deathState = new DeathState(handler);

		// start game with menu screen
		State.setState(lobbyState);

	}

	/**
	 * Method used to update game stuff
	 * <p>
	 * A method that get called every frame
	 */
	private void tick() {

		keyManager.tick(); // important to do this before State

		mouseManager.tick();

		if (State.getState() != null) {
			State.getState().tick();
		}

		textManager.tick();

	}

	/**
	 * Render the stuff on screen
	 * <p>
	 * A method that get called every frame
	 */
	private void render() {

		bs = display.getCanvas().getBufferStrategy();

		if (bs == null) {

			display.getCanvas().createBufferStrategy(3);

			return;

		}

		gfx = bs.getDrawGraphics();

		gfx.clearRect(0, 0, width, height);

		// draw starts
		if (State.getState() != null) {
			State.getState().render(gfx);
		}

		textManager.render(gfx);

		// draw ends
		// show graphics
		bs.show();

		// done drawing, get rid of graphics
		gfx.dispose();

	}

	/**
	 * A very common game loop to control the FPS
	 */
	@Override
	public void run() {

		init();

		// max amount of time we can use to call methods FPS times in a second
		double timePerTick = 1000000000 / FPS;
		double delta = 0; // time left until to call methods
		long now;
		long lastTime = System.nanoTime();

		// time passed
		long timer = 0;
		long ticks = 0;

		// game is running...
		while (running) {

			now = System.nanoTime();

			delta += (now - lastTime) / timePerTick;

			timer += (now - lastTime);

			lastTime = now;

			if (delta >= 1) {

				tick();
				render();
				delta--;
				ticks++;

			}

			if (timer >= 1000000000) {

				System.out.println(
						"Game--- Tick(): " + ticks + "-------------------------");
				ticks = 0;
				timer = 0;
			}

		}

		// in case of error or exiting
		stop();

	}

	/**
	 * Start the thread. synchronized make other threads to wait for this to
	 * execute.
	 */
	public synchronized void start() {

		if (running) {
			return;
		}

		running = true;

		thread = new Thread(this);
		thread.start();

	}

	/**
	 * End the thread. synchronized make other threads to wait for this to execute.
	 */
	public synchronized void stop() {

		if (!running) {
			return;
		}

		try {
			thread.join();
		} catch (InterruptedException ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	// getters setter
	public GameCamera getGameCamera() {

		return gameCamera;
	}

	public KeyManager getKeyManager() {

		return keyManager;
	}

	public MouseManager getMouseManager() {

		return mouseManager;
	}

	public Graphics getGraphics() {

		return gfx;
	}

}
