
package dev.game.states;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import dev.game.Game;
import dev.game.Handler;
import dev.game.gfx.Assets;
import dev.game.gfx.Text;
import dev.game.ui.ClickListener;
import dev.game.ui.UIImageButton;
import dev.game.ui.UILabel;
import dev.game.ui.UIManager;
import dev.game.ui.UIObject;

/**
 * MenuState.java - the start menu screen
 *
 * @author j.kim3
 */
public class LobbyState extends State {

	private boolean isHelpOpened = false;

	// all the objects
	private List<UIObject> objects;

	public LobbyState(Handler handler) {

		super(handler);

		if (uiManager == null) {

			uiManager = new UIManager(handler);

			uiManager.addList();

			objects = uiManager.getSubList(UIManager.LOBBYSTATE_ALL);

		}

		objects.add(new UILabel(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT,
				Assets.background));

		addStartBtn();
		addHelpBtn();

	}

	public void tick() {

		if (handler.getMouseManager().getUIManager() == null) {

			handler.getMouseManager().setUIManager(uiManager);

		}

	}

	public void render(Graphics gfx) {

		uiManager.render(gfx);

		Text.drawString(gfx, "The Panda Game", 450, 100, Color.WHITE,
				Assets.titleFont, true);

		gfx.drawImage(Assets.panda_idle_down[0], 100, 150, 196, 196, null);
		gfx.drawImage(Assets.panda_walk_left[0], 320, 330, 128, 128, null);
		gfx.drawImage(Assets.panda_walk_left[1], 500, 200, 128, 128, null);
		gfx.drawImage(Assets.wolf_run_left[2], 700, 150, 288, 196, null);

		showHelpScreen(gfx);

	}

	// util methods
	/**
	 * Show help screen with all the info
	 *
	 * @param gfx
	 */
	private void showHelpScreen(Graphics gfx) {

		if (isHelpOpened) {

			gfx.drawImage(Assets.background, 50, 50, 800, 500, null);

			Text.drawString(gfx, "The Panda Game", 450, 100, Color.WHITE,
					Assets.bigFont, true);
			Text.drawString(gfx, "Made by Juhyung Kim", 450, 150);
			Text.drawString(gfx, "Since Jun 19, 2017", 450, 180);

			int keyX = 350, keyY = 240;

			String[] keyHelps = { "Attack:        A", "Inventory:   I",
					"Crafting:     C", "Quick Slot:   1 ~ 5", "Use Item:      Space",
					"Move:          Arrow Keys", "Sprint:        Shift",
					"Quit(save):   ESC" };

			for (int i = 0; i < keyHelps.length; i++) {

				Text.drawString(gfx, keyHelps[i], keyX, keyY + (i * 25), false);
			}

		}
	}

	/**
	 * Add start button to screen
	 */
	private void addStartBtn() {

		UIImageButton startBtn = new UIImageButton((Game.SCREEN_WIDTH / 2) + 50,
				(Game.SCREEN_HEIGHT / 2) + 50, 300, 200, Assets.btn_start,
				new ClickListener() {

					@Override
					public void onClick() {

						if (isHelpOpened)
							return;

						handler.getMouseManager().setUIManager(null); // to delete
																		// buttons

						State.setState(handler.getGame().menuState);

					}

				});

		startBtn.setText("Start");
		startBtn.bigFont = true;

		objects.add(startBtn);

	}

	/**
	 * Add help button to screen
	 */
	private void addHelpBtn() {

		ClickListener clicker = new ClickListener() {

			@Override
			public void onClick() {

				isHelpOpened = (!isHelpOpened);

			}

		};

		UIImageButton helpBtn = new UIImageButton(830, 530, 60, 60,
				Assets.btn_quitDialog, clicker);

		helpBtn.setText("?");

		objects.add(helpBtn);

	}

}
