
package dev.game.states;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import dev.game.Game;
import dev.game.Handler;
import dev.game.gfx.Assets;
import dev.game.gfx.Text;
import dev.game.gfx.TextManager;
import dev.game.map.MapGenerator;
import dev.game.ui.ClickListener;
import dev.game.ui.UIEditText;
import dev.game.ui.UIImageButton;
import dev.game.ui.UILabel;
import dev.game.ui.UIManager;
import dev.game.ui.UIObject;
import dev.game.ui.UIScrollBar;
import dev.game.ui.UIToggleButton;
import dev.game.utils.Utils;
import dev.game.worlds.World;

/**
 * MenuState.java - The menu screen of the game
 *
 * @author Juhyung Kim
 */
public class MenuState extends State {

	private final int MAP_BTN1_INDEX = 0;
	private final int MAP_BTN2_INDEX = MAP_BTN1_INDEX + 1;
	private final int MAP_SCROLL_INDEX = MAP_BTN2_INDEX + 1;
	private final int START_BTN_INDEX = MAP_SCROLL_INDEX + 1;
	private final int CREATE_BTN_INDEX = START_BTN_INDEX + 1;
	private final int DELETE_BTN_INDEX = CREATE_BTN_INDEX + 1;
	private final int BACK_BTN_INDEX = DELETE_BTN_INDEX + 1;

	private final int NAME_TEXT_INDEX = 0;
	private final int NAME_EDITTEXT_INDEX = NAME_TEXT_INDEX + 1;
	private final int SEED1_TEXT_INDEX = NAME_EDITTEXT_INDEX + 1;
	private final int SEED1_EDITTEXT_INDEX = SEED1_TEXT_INDEX + 1;
	private final int SEED2_TEXT_INDEX = SEED1_EDITTEXT_INDEX + 1;
	private final int SEED2_EDITTEXT_INDEX = SEED2_TEXT_INDEX + 1;

	public final int MAP_SCREEN = 0;
	public final int NEW_WOLRD_SCREEN = 1;

	/**
	 * Value returned by {@linkplain #checkEditTexts()}
	 */
	private final int VALID_ALL = 0, INVALID_NAME = 1, OVERLAPPING_NAME = 2,
			INVALID_SEED = 3;

	/**
	 * Current screen in the state. Either MAP_SCREEN or NEW_WOLRD_SCREEN
	 */
	private int currentScreen = MAP_SCREEN; // default

	/**
	 * Index of the world selected. -1 for default
	 */
	private int selectedWorldIndex = -1;

	// all the objects
	private List<UIObject> mapsObjs;
	private List<UIObject> newWorldObjs;

	private MapGenerator gen;

	public MenuState(Handler handler) {

		super(handler);

		if (uiManager == null) {

			uiManager = new UIManager(handler);

			uiManager.addList(); // map choosing screen
			uiManager.addList(); // new world screen

			mapsObjs = uiManager.getSubList(UIManager.MENUSTATE_MAPS);
			newWorldObjs = uiManager.getSubList(UIManager.MENUSTATE_NEWWORLD);

			init();

		}

	}

	@Override
	public void tick() {

		if (handler.getMouseManager().getUIManager() == null) {

			handler.getMouseManager().setUIManager(uiManager);

		}

		if (getScroll().isProgressUpdated()) {

			updateMapObjects();

			getScroll().setProgressUpdated(false);

		}

	}

	@Override
	public void render(Graphics gfx) {

		// background
		gfx.drawImage(Assets.menuState_bg, 0, 0, Game.SCREEN_WIDTH,
				Game.SCREEN_HEIGHT, null);

		// title
		if (currentScreen == MAP_SCREEN) {
			Text.drawString(gfx, "Worlds", 450, 50, Color.WHITE, Assets.mediumFont,
					true);
		} else {
			Text.drawString(gfx, "Create New World", 450, 50, Color.WHITE,
					Assets.mediumFont, true);
		}

		// objects
		uiManager.render(gfx);

	}

	/**
	 * Initialize objects
	 */
	private void init() {

		addMapBtns();
		addMapScroll();
		addBtns();

		addNameEdit();
		addSeedEdits();

		updateMapObjects();
	}

	/**
	 * Hide and show the objects in the screen. Use this method whenever the current
	 * screen have changed.
	 */
	private void showObjects() {

		// start button and back button are always visible
		if (currentScreen == MAP_SCREEN) {

			// map screen
			mapsObjs.get(MAP_BTN1_INDEX).setHidden(false);
			mapsObjs.get(MAP_BTN2_INDEX).setHidden(false);
			mapsObjs.get(MAP_SCROLL_INDEX).setHidden(false);
			mapsObjs.get(CREATE_BTN_INDEX).setHidden(false);
			mapsObjs.get(DELETE_BTN_INDEX).setHidden(false);

			// new world screen
			newWorldObjs.get(NAME_TEXT_INDEX).setHidden(true);
			newWorldObjs.get(NAME_EDITTEXT_INDEX).setHidden(true);
			newWorldObjs.get(SEED1_TEXT_INDEX).setHidden(true);
			newWorldObjs.get(SEED1_EDITTEXT_INDEX).setHidden(true);
			newWorldObjs.get(SEED2_TEXT_INDEX).setHidden(true);
			newWorldObjs.get(SEED2_EDITTEXT_INDEX).setHidden(true);

		} // if new world screen
		else {

			// map screen
			mapsObjs.get(MAP_BTN1_INDEX).setHidden(true);
			mapsObjs.get(MAP_BTN2_INDEX).setHidden(true);
			mapsObjs.get(MAP_SCROLL_INDEX).setHidden(true);
			mapsObjs.get(CREATE_BTN_INDEX).setHidden(true);
			mapsObjs.get(DELETE_BTN_INDEX).setHidden(true);

			// new world screen
			newWorldObjs.get(NAME_TEXT_INDEX).setHidden(false);
			newWorldObjs.get(NAME_EDITTEXT_INDEX).setHidden(false);
			newWorldObjs.get(SEED1_TEXT_INDEX).setHidden(false);
			newWorldObjs.get(SEED1_EDITTEXT_INDEX).setHidden(false);
			newWorldObjs.get(SEED2_TEXT_INDEX).setHidden(false);
			newWorldObjs.get(SEED2_EDITTEXT_INDEX).setHidden(false);

		}

	}

	/**
	 * Update scroll and then map buttons.
	 */
	private void updateMapObjects() {

		File[] files = getWorldFiles();

		int leng = files.length;

		updateScrollMaxProgress(leng);

		int max = getScroll().getMaxProgress();
		int progress = getScroll().getProgress();

		// in case the deleted world was the most bottom one
		if (selectedWorldIndex > max) {
			selectedWorldIndex = max;
		}

		getMapButton(0).setPressed(false);
		getMapButton(1).setPressed(false);

		// set the world buttons text
		for (int i = 0; i < 2; i++) {

			if (i < leng) {

				File file = files[progress + i];

				String text = "Name: " + file.getName();

				BasicFileAttributes attr;

				try {

					attr = Files.readAttributes(file.toPath(),
							BasicFileAttributes.class);

					text += "\n\nSince: "
							+ attr.creationTime().toString().substring(0, 10);

				} catch (IOException e) {

					e.printStackTrace();

				}

				getMapButton(i).setText(text);

			} else {

				getMapButton(i).setText("");

			}

		}

	}

	/**
	 * Update the <code>maxProgress</code> of the scroll in the map screen.
	 *
	 * @param fileLength Number of world file. If the files have not been called
	 *                   yet, this could be -1 to call it within the method.
	 */
	private void updateScrollMaxProgress(int fileLength) {

		int count;

		count = (fileLength < 0) ? getWorldFiles().length : fileLength;

		// if there are < 3 maps, no scroll needed
		count = (count < 3) ? 1 : count - 1;

		getScroll().setMaxProgress(count);

		if (getScroll().getProgress() >= getScroll().getMaxProgress()) {
			getScroll().setProgress(count - 1);
		}

	}

	/**
	 * Add buttons for selecting maps.
	 */
	private void addMapBtns() {

		ClickListener clicker = null, clicker2 = null;

		final UIToggleButton btn = new UIToggleButton(32, 116, 564, 220,
				Assets.menuState_mapBtn, clicker);

		final UIToggleButton btn2 = new UIToggleButton(32, 352, 564, 220,
				Assets.menuState_mapBtn, clicker2);

		// top button
		clicker = new ClickListener() {

			@Override
			public void onClick() {

				btn2.setPressed(false);

				selectedWorldIndex = getScroll().getProgress();

			}

		};

		btn.setClickLisenter(clicker);

		mapsObjs.add(btn);

		// bottom button
		clicker2 = new ClickListener() {

			@Override
			public void onClick() {

				btn.setPressed(false);

				selectedWorldIndex = getScroll().getProgress() + 1;

			}

		};

		btn2.setClickLisenter(clicker2);

		mapsObjs.add(btn2);

	}

	/**
	 * Add scroll for list of maps.
	 */
	private void addMapScroll() {

		UIScrollBar scroll = new UIScrollBar(610, 108, 30, 472, 1);

		mapsObjs.add(scroll);

	}

	/**
	 * Add start game, create new map, delete map, and back buttons.
	 */
	private void addBtns() {

		ClickListener startClicker = new ClickListener() {

			@Override
			public void onClick() {

				if (currentScreen == NEW_WOLRD_SCREEN) {

					int result = checkEditTexts();

					switch (result) {

					case VALID_ALL:

						UIEditText name = (UIEditText) newWorldObjs
								.get(NAME_EDITTEXT_INDEX);
						UIEditText seed1 = (UIEditText) newWorldObjs
								.get(SEED1_EDITTEXT_INDEX);
						UIEditText seed2 = (UIEditText) newWorldObjs
								.get(SEED2_EDITTEXT_INDEX);

						// return -1 if it cannot be parsed
						// (which makes the seed a random value)
						int seed1I = Utils.parseInt(seed1.getText());
						int seed2I = Utils.parseInt(seed2.getText());

						generateWorldAndStart(name.getText(), seed1I, seed2I);

						break;

					case INVALID_SEED:

						Text t1 = new Text("Invalid Seed!!", true, Assets.mediumFont,
								Color.RED);

						TextManager.addText(t1);

						break;

					case INVALID_NAME:

						Text t2 = new Text("Invalid World Name!!", true,
								Assets.mediumFont, Color.RED);

						TextManager.addText(t2);

						break;

					case OVERLAPPING_NAME:

						Text t3 = new Text("Overlapping World Name!!", true,
								Assets.mediumFont, Color.RED);

						TextManager.addText(t3);

					}

				} else { // else, world choose screen

					File[] worlds = getWorldFiles();

					// return if selected empty button
					if (selectedWorldIndex == -1
							|| worlds.length <= selectedWorldIndex) {

						Text t = new Text("Choose the World First!", true,
								Assets.font, Color.YELLOW);

						TextManager.addText(t);

						return;

					}

					String theWorldName = worlds[selectedWorldIndex].getName();
					gen = new MapGenerator(handler, theWorldName);
					World world = new World(handler, gen);
					handler.getGame().gameState = new GameState(handler, world);
					handler.getMouseManager().setUIManager(null);
					State.setState(handler.getGame().gameState);

				}

			}

		};

		ClickListener createClicker = new ClickListener() {

			@Override
			public void onClick() {

				currentScreen = NEW_WOLRD_SCREEN;
				showObjects();

			}

		};

		ClickListener deleteClicker = new ClickListener() {

			@Override
			public void onClick() {

				File[] files = getWorldFiles();

				if (selectedWorldIndex == -1 || files == null
						|| files.length <= selectedWorldIndex) {

					Text t = new Text("Choose the World First!", true, Assets.font,
							Color.YELLOW);

					TextManager.addText(t);

					return;

				}

				File file = files[selectedWorldIndex];

				Utils.deleteFile(file, true);

				updateMapObjects();

				selectedWorldIndex = -1;

			}

		};

		UIImageButton startBtn = new UIImageButton(660, 100, 220, 90,
				Assets.menuState_btn, startClicker);

		startBtn.setText("Start Game");

		UIImageButton createBtn = new UIImageButton(660, 300, 220, 90,
				Assets.menuState_btn, createClicker);

		createBtn.setText("New World");

		UIImageButton deleteBtn = new UIImageButton(660, 500, 220, 90,
				Assets.menuState_btn, deleteClicker);

		deleteBtn.setText("Delete World");

		mapsObjs.add(startBtn);
		mapsObjs.add(createBtn);
		mapsObjs.add(deleteBtn);

		// back button
		ClickListener backClicker = new ClickListener() {

			@Override
			public void onClick() {

				if (currentScreen == MAP_SCREEN) {

					handler.getMouseManager().setUIManager(null);

					State.setState(handler.getGame().lobbyState);

				} else {

					currentScreen = MAP_SCREEN;

					// reset
					((UIEditText) newWorldObjs.get(NAME_EDITTEXT_INDEX)).setText("");
					((UIEditText) newWorldObjs.get(SEED1_EDITTEXT_INDEX))
							.setText("");
					((UIEditText) newWorldObjs.get(SEED2_EDITTEXT_INDEX))
							.setText("");

					showObjects();

				}

			}

		};

		UIImageButton backBtn = new UIImageButton(20, 30, 120, 42,
				Assets.menuState_backBtn, backClicker);

		mapsObjs.add(backBtn);

	}

	/**
	 * Add text and editable text for new world name.
	 */
	private void addNameEdit() {

		UILabel text = new UILabel(32, 130, 100, 20, "Name");
		text.setHidden(true);
		newWorldObjs.add(text);

		UIEditText edit = new UIEditText(160, 120, 400, 40, "",
				handler.getKeyManager());
		edit.setHidden(true);
		edit.setHint("No special characaters");
		edit.setIsFileName(true);
		newWorldObjs.add(edit);

	}

	/**
	 * Add texts and editable texts for new world seeds.
	 */
	private void addSeedEdits() {

		UILabel text1 = new UILabel(32, 230, 100, 20, "Seed 1");
		text1.setHidden(true);
		newWorldObjs.add(text1);

		UIEditText edit1 = new UIEditText(160, 220, 400, 40, "",
				handler.getKeyManager());
		edit1.setHidden(true);
		edit1.setHint("Optional (number)");
		newWorldObjs.add(edit1);

		UILabel text2 = new UILabel(32, 330, 100, 20, "Seed 2");
		text2.setHidden(true);
		newWorldObjs.add(text2);

		UIEditText edit2 = new UIEditText(160, 320, 400, 40, "",
				handler.getKeyManager());
		edit2.setHidden(true);
		edit2.setHint("Optional (number)");
		newWorldObjs.add(edit2);

	}

	/**
	 * Generate the world and change the state to game state with the world.
	 *
	 * @param worldName folder name
	 * @param seed1     Seed to generate world. -1 for random.
	 * @param seed2     Seed to generate world. -1 for random.
	 */
	private void generateWorldAndStart(String worldName, int seed1, int seed2) {

		System.out.println("Menustate: generateWorldandStart started");

		// hide everything for map generating screen
		mapsObjs.get(BACK_BTN_INDEX).setHidden(true);
		newWorldObjs.get(START_BTN_INDEX).setHidden(true);
		newWorldObjs.get(NAME_EDITTEXT_INDEX).setHidden(true);
		newWorldObjs.get(SEED1_EDITTEXT_INDEX).setHidden(true);
		newWorldObjs.get(SEED2_EDITTEXT_INDEX).setHidden(true);
		newWorldObjs.get(NAME_TEXT_INDEX).setHidden(true);
		newWorldObjs.get(SEED1_TEXT_INDEX).setHidden(true);
		newWorldObjs.get(SEED2_TEXT_INDEX).setHidden(true);
		mapsObjs.get(START_BTN_INDEX).setHidden(true);

		gen = new MapGenerator(handler, worldName);

		World world = new World(handler, gen);

		handler.getGame().gameState = new GameState(handler, world);

		handler.getMouseManager().setUIManager(null);

		State.setState(handler.getGame().gameState);

	}

	/**
	 * Check the EditTexts in the new world screen to check if all the contents are
	 * valid for map generation.
	 */
	private int checkEditTexts() {

		UIEditText name = (UIEditText) newWorldObjs.get(NAME_EDITTEXT_INDEX);
		UIEditText seed1 = (UIEditText) newWorldObjs.get(SEED1_EDITTEXT_INDEX);
		UIEditText seed2 = (UIEditText) newWorldObjs.get(SEED2_EDITTEXT_INDEX);

		File file;

		String nameText = name.getText();

		// too short name!
		if (nameText.length() == 0) {
			return INVALID_NAME;
		}

		// check path name
		try {

			Path path = Paths.get(State.SAVEFILE_DIR, nameText);

			// check overlapping name
			file = path.toFile();

			if (file.exists()) {
				return OVERLAPPING_NAME;
			}

		} catch (InvalidPathException ex) {

			return INVALID_NAME;

		}

		// check seeds
		if ((seed1.getText().length() > 0 && !Utils.isNumber(seed1.getText()))
				|| (seed2.getText().length() > 0
						&& !Utils.isNumber(seed2.getText()))) {
			return INVALID_SEED;
		}

		// all tests completed, this is a valid name
		return VALID_ALL;

	}

	/**
	 * Get the files of the saved worlds.
	 *
	 * @return Files (could be empty one).
	 */
	private File[] getWorldFiles() {

		File file = new File(State.SAVEFILE_DIR);

		if (!file.exists()) {

			file.mkdirs();

			File[] empty = {};

			return empty;

		}

		// getting the world directories
		File[] files = file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File name) {

				return (name.isDirectory());

			}

		});

		return files;

	}

	/**
	 * Get the scroll in the map screen.
	 *
	 * @return
	 */
	private UIScrollBar getScroll() {

		return ((UIScrollBar) mapsObjs.get(MAP_SCROLL_INDEX));

	}

	/**
	 * Get the map button, either 0 or 1 index.
	 *
	 * @param index 0 for top button, 1 for bottom button.
	 * @return
	 */
	private UIToggleButton getMapButton(int index) {

		return (UIToggleButton) mapsObjs.get(MAP_BTN1_INDEX + index);

	}

}
