
package dev.game.states;

import java.awt.Color;
import java.awt.Graphics;

import dev.game.Game;
import dev.game.Handler;
import dev.game.gfx.Assets;
import dev.game.gfx.Text;
import dev.game.ui.ClickListener;
import dev.game.ui.UIImageButton;
import dev.game.ui.UIManager;
import dev.game.utils.Utils;

/**
 * DeathState.java - State for dead
 *
 * @author j.kim3
 */
public class DeathState extends State {

	public DeathState(Handler handler) {

		super(handler);

		if (uiManager == null) {

			uiManager = new UIManager(handler);

			uiManager.addList();

		}

		addQuitBtn();

	}

	@Override
	public void tick() {

		if (handler.getMouseManager().getUIManager() == null) {

			handler.getMouseManager().setUIManager(uiManager);

		}

	}

	@Override
	public void render(Graphics gfx) {

		gfx.drawImage(Assets.background, 0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT,
				null);

		gfx.drawImage(Utils.setAlpha(Assets.panda_ghost, 150), 100, 150, 64 * 4,
				76 * 4, null);

		Text.drawString(gfx, "You DIED!", 450, 100, Color.RED, Assets.titleFont,
				true);

		uiManager.render(gfx);

	}

	// util methods
	/**
	 * Add quit button to screen
	 */
	private void addQuitBtn() {

		UIImageButton quitBtn = new UIImageButton(400, 350, 400, 200,
				Assets.btn_start, new ClickListener() {

					@Override
					public void onClick() {

						System.exit(0);

					}

				});

		quitBtn.setText("Quit Game");
		quitBtn.bigFont = true;

		uiManager.getSubList(UIManager.DEATHSTATE_ALL).add(quitBtn);

	}

}
