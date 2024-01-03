
package dev.game.states;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import dev.game.Game;
import dev.game.Handler;
import dev.game.crafting.CraftingManager;
import dev.game.entities.EntityManager;
import dev.game.entities.creatures.Player;
import dev.game.entities.statics.placeables.PlaceableEntity;
import dev.game.gfx.Assets;
import dev.game.gfx.Text;
import dev.game.gfx.TextManager;
import dev.game.input.KeyManager;
import dev.game.inventory.Inventory;
import dev.game.items.Item;
import dev.game.items.useable.UseableItem;
import dev.game.items.useable.placeable.PlaceableItem;
import dev.game.sounds.Sound;
import dev.game.ui.ClickListener;
import dev.game.ui.UIImageButton;
import dev.game.ui.UIInvSlot;
import dev.game.ui.UIManager;
import dev.game.ui.UIObject;
import dev.game.ui.UIProgressBar;
import dev.game.utils.Utils;
import dev.game.worlds.World;

/**
 * GameState.java - Everything in world is done here
 *
 * @author Juhyung Kim
 */
public class GameState extends State {

	/**
	 * Name of this world.
	 */
	private String worldName;
	/**
	 * Path of the world file. Includes '/' at the end.
	 */
	private String worldPath;

	// the world object
	private World world;

	// quick slot start position
	private final int quickSlotX = 282, quickSlotY = 520;

	// location for highlight of slot when selected
	private int quickSlotSelectX = 0, quickSlotSelectY = 0;
	private boolean quickSlotSelected = false;
	private int quickSelectSlotIndex = 0;

	// index that corresponds with inventory slots
	private final int quickSlotIndex = Inventory.QUICK_SLOT_INDEX;

	private Player player = null;

	private Inventory inventory = null;

	private CraftingManager craftingManager = null;

	private KeyManager keyManager = null;

	private PlaceableEntity carriedItemEntity = null;

	// TODO find use
	private int carriedItemId = 0;

	// true if carried item is still available after spending once
	private boolean shouldRefill = false;

	private boolean isQuitDialogOpened = false;

	// index to check if different slot have been selected
	private int lastSelectedSlotIndex = -1;
	private boolean selectedSlotChanged = false;

	private final int QUIT_YES_BTN_INDEX = 0;
	private final int QUIT_NO_BTN_INDEX = 1;

	private boolean oneTickFromQuit = false;

	private int dimension;

	/**
	 * GameState
	 *
	 * @param handler
	 * @param world
	 */
	public GameState(Handler handler, World world) {

		super(handler);

		this.world = world;

		setWorldNameAndPath();

		if (uiManager == null) {

			uiManager = new UIManager(handler);

			uiManager.addList(); // health bar
			uiManager.addList(); // quick slots
			uiManager.addList(); // inventory
			uiManager.addList(); // crafting
			uiManager.addList(); // quit dialog

		}

		init();

	}

	/**
	 * initialize GameState.
	 */
	private void init() {

		handler.setWorld(world);

		this.dimension = world.getDimension();

		keyManager = handler.getKeyManager();

		// health bar
		UIProgressBar healthBar = new UIProgressBar(0, 5, 324, 60, 20,
				Player.PLAYER_DEAULT_HEALTH);

		uiManager.getSubList(UIManager.GAMESTATE_HEALTH_BAR).add(healthBar);

		// quick-slot
		addQuickSlotBtns();

		// quit dialog part
		createQuitDialog();

		handler.getMouseManager().setUIManager(uiManager);

		player = world.getEntityManager().getPlayer();

		((UIProgressBar) uiManager.getSubList(UIManager.GAMESTATE_HEALTH_BAR).get(0))
				.setProgress(player.getHealth());

		player.getInventory().init();
		player.getCraftingManager().init();

		inventory = player.getInventory();
		craftingManager = player.getCraftingManager();

		player.init();

		// load save file (if have one)
		applySavedWorldData();

	}

	/**
	 * Update the game Game.FPS times every second
	 */
	@Override
	public void tick() {

		// TODO this is needed somehow,
		// when it's already set manually in init()
		// why mouse manager's UIManager is null?
		if (!handler.getMouseManager().hasUIManager())
			handler.getMouseManager().setUIManager(uiManager);

		// freeze world when pressed escape key
		if (isQuitDialogOpened)
			return;

		world.tick();

		updateDimension();

		selectQuickSlotWithKey();
		addSelectedItemEntity();
		checkUseItem();
		toggleQuitDialog();

		// refresh so it can only be checked once in the tick
		// that this become true;
		selectedSlotChanged = false;

		saveAndQuit();

	}

	/**
	 * Update the dimension data and
	 */
	private void updateDimension() {

		if (dimension != world.getDimension()) {

			dimension = world.getDimension();

			if (carriedItemEntity != null) {

				carriedItemEntity.setHealth(0);
				carriedItemEntity = null;
				quickSlotSelected = false;
				lastSelectedSlotIndex = -1;

			}
		}
	}

	@Override
	public void render(Graphics gfx) {

		world.render(gfx);

		player.postRender(gfx);

		gfx.drawImage(Assets.quickSlot, quickSlotX, quickSlotY, null);

		renderQuitDialog(gfx);

		uiManager.render(gfx);

		Text.drawString(gfx, "Health", 50, 10);

		if (quickSlotSelected) {
			gfx.drawImage(Assets.inventory_selected, quickSlotSelectX,
					quickSlotSelectY, null);
		}

	}

	// util methods
	/**
	 * Item use interaction. Whenever press space bar
	 */
	private void checkUseItem() {

		if (inventory.isOpened() || craftingManager.isOpened())
			return;

		if (!quickSlotSelected)
			return;

		Item theItem = inventory.getItems()[quickSelectSlotIndex];

		UIInvSlot slot = getQuickSlot(quickSelectSlotIndex - quickSlotIndex);

		// Syncing the item selected item and quickSlot
		if (slot.getItem() != null) {

			if (theItem == null || theItem.isEmpty()) {

				emptySelectedQuickSlot();

				shouldRefill = true;

				return;

			}
		}

		// gotta press space to use item
		if (!handler.getKeyManager().keyJustPressed(KeyEvent.VK_SPACE))
			return;

		if (theItem == null)
			return;

		carriedItemId = theItem.getID();

		boolean used;

		// TODO this could need fix when ThrowableEntity is added
		if (theItem.isUseable()) {

			UseableItem useableItem = (UseableItem) theItem;

			used = useableItem.useItem(quickSelectSlotIndex,
					(PlaceableEntity) carriedItemEntity);

		} else {

			return;

		}

		if (used) {
			// to prevent removing placed entity by accident
			carriedItemEntity = null;

			// item should be left to refill
			shouldRefill = true;

		}

	}

	/**
	 * Add the selected item into the world so it render as if the player is
	 * carrying it
	 * <p>
	 * If it is placeable item (ex. fence)
	 */
	private void addSelectedItemEntity() {

		if (!quickSlotSelected)
			return;

		Item theItem = inventory.getItems()[quickSelectSlotIndex];

		// if selected empty slot
		if (theItem == null) {
			removeCarriedEntity();
			return;
		}

		// if new item should be rendered
		if (isSelectedSlotChanged() || shouldRefill) {

			shouldRefill = false;

			// reset entity first, in case of bug
			removeCarriedEntity();

			if (theItem.isPlaceable()) {

				carriedItemId = theItem.getID();

				carriedItemEntity = (PlaceableEntity) EntityManager.getEntityById(
						((PlaceableItem) theItem).getCorrespondingEntityId(),
						(int) player.getX(), (int) player.getY());

				EntityManager.addEntity(carriedItemEntity);

			} else {
				carriedItemId = theItem.getID();
				carriedItemEntity = null;
			}
		}
	}

	/**
	 * add quick-slot buttons in the GameState UIManager
	 */
	private void addQuickSlotBtns() {

		final Item[] items = world.getEntityManager().getPlayer().getInventory()
				.getItems();

		for (int i = 0; i < 5; i++) {

			ClickListener quickSlotClicker = null;

			final UIInvSlot quickSlot = new UIInvSlot(quickSlotX + 12 + i * 66,
					quickSlotY + 11, items[quickSlotIndex + i], quickSlotClicker,
					false);

			quickSlot.setIndex(i + quickSlotIndex);

			quickSlotClicker = new ClickListener() {

				@Override
				public void onClick() {

					selectQuickSlot(quickSlot.getIndex() - quickSlotIndex);

				}

			};

			quickSlot.setClickListener(quickSlotClicker);

			uiManager.getSubList(UIManager.GAMESTATE_QUICK_SLOT).add(quickSlot);

		}

	}

	/**
	 * Select the outside quick slot with the index 0 ~ 4
	 *
	 * @param index
	 */
	private void selectQuickSlot(int index) {

		if (inventory.isOpened() || craftingManager.isOpened()) {
			return;
		}

		// in case of out of bound
		if (index < 0 || index > 4) {
			throw new IndexOutOfBoundsException("\n Out of bounds");
		}

		UIInvSlot quickSlot = (UIInvSlot) uiManager
				.getSubList(UIManager.GAMESTATE_QUICK_SLOT).get(index);

		quickSlotSelected = true;

		quickSlotSelectX = (int) quickSlot.getX() - 11;
		quickSlotSelectY = (int) quickSlot.getY() - 11;

		quickSelectSlotIndex = quickSlot.getIndex();

		if (lastSelectedSlotIndex != quickSelectSlotIndex) {

			lastSelectedSlotIndex = quickSelectSlotIndex;

			selectedSlotChanged = true;

		}

		// little text on top of quick slot
		if (quickSlot.getItem() != null) {

			Text text = new Text(quickSlot.getItem().getName(), 450, 500,
					Game.FPS / 2, false, true, Assets.itemCountFont, Color.WHITE);

			TextManager.addText(text);

		}

	}

	/**
	 * Method to allow player to select the quick-slot by pressing 1 ~ 5
	 */
	private void selectQuickSlotWithKey() {

		if (inventory.isOpened()) {
			return;
		}

		boolean slotOne = keyManager.keyJustPressed(KeyEvent.VK_1);
		boolean slotTwo = keyManager.keyJustPressed(KeyEvent.VK_2);
		boolean slotThree = keyManager.keyJustPressed(KeyEvent.VK_3);
		boolean slotFour = keyManager.keyJustPressed(KeyEvent.VK_4);
		boolean slotFive = keyManager.keyJustPressed(KeyEvent.VK_5);
		boolean[] keys = { slotOne, slotTwo, slotThree, slotFour, slotFive };

		for (int i = 0; i < keys.length; i++) {

			if (keys[i]) {

				selectQuickSlot(i);
				break;

			}

		}

	}

	/**
	 * Empty the selected quick slot
	 */
	private void emptySelectedQuickSlot() {

		if (!quickSlotSelected) {
			return;
		}

		((UIInvSlot) uiManager.getSubList(UIManager.GAMESTATE_QUICK_SLOT)
				.get(quickSelectSlotIndex - quickSlotIndex)).setItem(null);

		inventory.getItems()[quickSelectSlotIndex] = null;

		carriedItemId = 0;

		removeCarriedEntity();

		shouldRefill = false;

		quickSlotSelected = false;

	}

	/**
	 * toggle quit dialog when pressed ESC key
	 */
	private void toggleQuitDialog() {

		boolean esc = keyManager.keyJustPressed(KeyEvent.VK_ESCAPE);

		// toggle
		if (esc && !inventory.isOpened() && !craftingManager.isOpened()) {

			isQuitDialogOpened = (!isQuitDialogOpened);

		}

		List<UIObject> objs = uiManager.getSubList(UIManager.GAMESTATE_QUIT_DIALOG);

		UIObject yesBtn = objs.get(QUIT_YES_BTN_INDEX);
		UIObject noBtn = objs.get(QUIT_NO_BTN_INDEX);

		if (isQuitDialogOpened) {

			yesBtn.setHidden(false);
			noBtn.setHidden(false);

		} else {

			yesBtn.setHidden(true);
			noBtn.setHidden(true);

		}

	}

	/**
	 * Add quit dialog buttons to UI manager
	 */
	private void createQuitDialog() {

		ClickListener quitYesBtnClicker = new ClickListener() {

			@Override
			public void onClick() {

				removeCarriedEntity();

				oneTickFromQuit = true;

				// to run tick() again and quit
				isQuitDialogOpened = false;

				Sound.stopAll();

			}

		};

		UIImageButton quitYesBtn = new UIImageButton(275, 300, 150, 50,
				Assets.btn_quitDialog, quitYesBtnClicker);

		quitYesBtn.setText("YES");

		uiManager.getSubList(UIManager.GAMESTATE_QUIT_DIALOG).add(quitYesBtn);

		ClickListener quitNoBtnClicker = new ClickListener() {

			@Override
			public void onClick() {

				isQuitDialogOpened = false;
			}

		};

		UIImageButton quitNoBtn = new UIImageButton(475, 300, 150, 50,
				Assets.btn_quitDialog, quitNoBtnClicker);

		quitNoBtn.setText("NO");

		uiManager.getSubList(UIManager.GAMESTATE_QUIT_DIALOG).add(quitNoBtn);

	}

	/**
	 * render the quit dialog
	 *
	 * @param gfx
	 */
	private void renderQuitDialog(Graphics gfx) {

		if (!isQuitDialogOpened) {
			return;
		}

		gfx.drawImage(Assets.quitDialog, 250, 225, 400, 150, null);

		Text.drawString(gfx, "Do you want to quit?", 450, 260);

	}

	////////////////// world save and load /////////////////////
	/**
	 * If <code>oneTickFromQuit</code> is True, save world file and quit the game.
	 * <p>
	 * The variable exist to prevent the potential bug from abrupt quitting
	 */
	private void saveAndQuit() {

		if (oneTickFromQuit) {

			saveWorldFile();

			handler.getMouseManager().setUIManager(null);

			handler.getGame().menuState = new MenuState(handler);

			handler.getGame().gameState = null;

			State.setState(handler.getGame().menuState);

		}

	}

	/**
	 * Save the world data
	 *
	 * @throws IOException
	 */
	private void saveWorldFile() {

		try {
			createSavePath();
			saveInventoryFile();

			for (int y = 0; y < World.map.length; y++) {
				for (int x = 0; x < World.map[0].length; x++) {
					World.map[y][x].saveInFile();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the parent path for savefile
	 */
	private void createSavePath() {

		File path = new File(SAVEFILE_DIR);

		if (!path.exists())
			path.mkdirs();

	}

	/**
	 * Save inventory file
	 *
	 * @throws IOException
	 */
	private void saveInventoryFile() throws IOException {

		File fout = new File(worldPath + INV_FILE_NAME + State.EXTENSION);

		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		Item[] items = inventory.getItems();

		for (int i = 0; i < items.length; i++) {

			Item item = items[i];

			if (item == null) {

				bw.write(0 + " " + 0);

			} else {

				int itemId = item.getID();
				int itemCount = item.getItemCount();

				bw.write(itemId + " " + itemCount);

			}

			bw.newLine();
		}

		bw.close();
	}

	/**
	 * True if it does exist.
	 * <p>
	 * If Inventory file exists, the world exists.
	 *
	 * @return
	 */
	private boolean isSavedWorldExist() {

		File invFile = new File(worldPath + INV_FILE_NAME + EXTENSION);

		return invFile.exists();

	}

	/**
	 * Check if save data exist and load it
	 * <p>
	 * Also, give player some starter kit if necessary
	 */
	private void applySavedWorldData() {

		if (isSavedWorldExist())
			loadInventoryFile();

	}

	/**
	 * Load inventory data from the file
	 * <p>
	 * Format: itemId itemMateral itemCount
	 */
	private void loadInventoryFile() {

		ArrayList<String> file = Utils
				.loadFileAsArrays(worldPath + INV_FILE_NAME + EXTENSION);

		for (int i = 0; i < file.size(); i++) {

			String line = file.get(i);

			String[] tokens = line.split("\\s+");

			int itemId = Utils.parseInt(tokens[0]);
			int itemCount = Utils.parseInt(tokens[1]);
			/*
			 * // if empty spot, skip. if (itemId == 0) { continue; }
			 */
			Item item = Item.getItemForInv(itemId, itemCount, player);

			// if the item is at somewhere other than inventory slot,
			// set the item to right slot explicitly
			if (i < 2 || i > Inventory.INV_SIZE + 1) {

				inventory.setInvItem(item, i);

			} else {

				inventory.setInvItem(item, i);

			}

		}

	}

	////////////// getter setter ///////////
	private boolean isSelectedSlotChanged() {

		return selectedSlotChanged;

	}

	/**
	 * Get the UIInvSlot of the index (0 ~ 4)
	 *
	 * @param index
	 * @return
	 */
	private UIInvSlot getQuickSlot(int index) {

		// in case of out of bound
		if (index < 0 || index > 4) {
			throw new IndexOutOfBoundsException();
		}

		return (UIInvSlot) uiManager.getSubList(UIManager.GAMESTATE_QUICK_SLOT)
				.get(index);

	}

	/**
	 * Remove the item carried by player
	 */
	private void removeCarriedEntity() {

		carriedItemId = 0;

		if (carriedItemEntity != null) {

			carriedItemEntity.kill();
			carriedItemEntity = null;

		}

	}

	/**
	 * Get carried item id.
	 *
	 * @return
	 */
	public int getCarriedItemId() {

		return carriedItemId;
	}

	/**
	 * @return the WORLD_NAME
	 */
	public String getWorldName() {

		return worldName;
	}

	/**
	 * set the name and path variables
	 * <p>
	 */
	private void setWorldNameAndPath() {

		this.worldName = world.getWorldName();
		this.worldPath = world.getWorldPath();

	}

}
