
package dev.game.crafting;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dev.game.Game;
import dev.game.Handler;
import dev.game.entities.creatures.Player;
import dev.game.gfx.Assets;
import dev.game.gfx.Text;
import dev.game.gfx.TextManager;
import dev.game.inventory.Inventory;
import dev.game.items.Item;
import dev.game.ui.ClickListener;
import dev.game.ui.UIImageButton;
import dev.game.ui.UIInvSlot;
import dev.game.ui.UILabel;
import dev.game.ui.UIManager;
import dev.game.ui.UIObject;
import dev.game.ui.UIScrollBar;
import dev.game.ui.UIToggleButton;

/**
 * CraftingManager.java - Manager for all crafting recipes, and also for the
 * crafting screen.
 *
 * @author Juhyung Kim
 */
public class CraftingManager {

	// All recipes, will get sorted
	private List<List<CraftingRecipe>> allRecipes = new ArrayList<>();

	// All recipes, unsorted
	private CraftingRecipe[] tempAllRecipe = AllCraftingRecipes.getAllRecipes();

	// all the constants
	/**
	 * Number of slots in the crafting screen
	 */
	public static final int SLOTS_COUNT = 24, SLOTS_ROW = 4, SLOTS_COL = 6;

	private final int SLOT_INDEX = 0;
	private final int CATEGORY_PLACE_INDEX = 24;
	private final int CATEGORY_FOOD_INDEX = 25;
	private final int CATEGORY_TOOL_INDEX = 26;
	private final int SCROLL_INDEX = 27;
	private final int PRODUCT_ICON_INDEX = 28;
	private final int CRAFT_BTN_INDEX = 29;
	private final int QUIT_BTN_INDEX = 30;
	private final int ING_SCREEN_INDEX = 31;
	private final int ING_TITLE_INDEX = ING_SCREEN_INDEX + 1;
	private final int ING_SLOT_INDEX = ING_TITLE_INDEX + 1;
	private final int ING_SLOT_ROW = 4;
	private final int ING_SLOT_TEXT_INDEX = ING_SLOT_INDEX + ING_SLOT_ROW;
	private final int ING_SCROLL_INDEX = ING_SLOT_TEXT_INDEX + ING_SLOT_ROW;
	private final int ING_QUIT_INDEX = ING_SCROLL_INDEX + 1;

	//// class stuff////
	private Handler handler;

	private Player player;

	// UI manager for slots
	private UIManager uiManager;

	// all the objects
	private List<UIObject> objects;

	private Inventory inventory;

	private boolean shouldUpdateCraftingScreen = false;

	// should open or close the crafting screen
	private boolean openCraftingScreen, closeCraftingScreen;

	// whether if crafting screen is opened
	private boolean opened;

	// true if the screen is opened by interacting with crafting table
	private boolean openedWithTable;

	// should open or close the ingredient screen
	private boolean openIngScreen, closeIngScreen;

	// whether ingredient screen is opened or not
	private boolean isIngScreenOpened = false;

	// crafting level
	// if crafting level is lower than required level, you cannot craft it
	private int currentCraftLevel;

	// Variables about selected slot
	private boolean selected = false;
	private int selectX, selectY;

	// to only update product icon whenever the selected index change
	private int oldSelectedRecipeIndex = -1;
	private int selectedRecipeIndex;

	// current progress of the scroll in crafting screen
	private int currentRow = 0;

	private int oldCraftingScrollProgress = -1;
	private int oldSelectedCategory = -1, selectedCategory;

	private int ingScrollOldProgress = -1;

	/**
	 * TODO. When player open table from lower y-coordinate than table, an item is
	 * crafted immediately (i'm suspicious about entity order in EntityManager and
	 * their tick order (maybe table is ticking earlier than the player so it's
	 * ignoring the keyJustTyped? ))
	 */
	private boolean justOpened;

	// graphics locations
	private int craftingWid = 600, craftingHei = 400;
	private int craftingX = 150, craftingY = 100;
	private int titleX = craftingX + craftingWid / 2, titleY = craftingY + 20; // title
	private int productIconX = craftingX + 475, productIconY = craftingY + 97;
	private int productTextX = craftingX + 516, productTextY = craftingY + 214;
	private int scrollX = craftingX + 412, scrollY = craftingY + 110;

	private int category_placeX = 158, category_placeY = craftingY + 38;
	private int category_foodX = 158 + 144, category_foodY = craftingY + 38;
	private int category_toolX = 158 + 288, category_toolY = craftingY + 38;
	private int craftBtnX = craftingX + 455, craftBtnY = craftingY + 300;
	private int quitBtnX = craftingX + 540, quitBtnY = craftingY + 9;

	// sort the recipe by their crafting level in ascending order
	private Comparator<CraftingRecipe> recipeSorter = new Comparator<CraftingRecipe>() {

		public int compare(CraftingRecipe a, CraftingRecipe b) {

			// if -1, than a is smaller than b (ascending order)
			return (a.getCraftingLevel() < b.getCraftingLevel()) ? -1 : 1;

		}

	};

	/**
	 * Constructor
	 *
	 * @param handler
	 */
	public CraftingManager(Handler handler) {

		this.handler = handler;

	}

	/**
	 * Add all the buttons to the UIManager
	 */
	public void init() {

		this.player = handler.getWorld().getEntityManager().getPlayer();

		this.inventory = player.getInventory();

		this.uiManager = handler.getMouseManager().getUIManager();

		objects = uiManager.getSubList(UIManager.GAMESTATE_CRAFTING);

		recipeInit();

		// the order is important
		addSlotBtns();
		addCategoryBtns();
		addScroll();
		addCraftBtn();
		addQuitBtn();

		addIngredientScreen(); // this must be last
		// (should be on top of the every other screen)

	}

	/**
	 * Initialize all recipes
	 */
	private void recipeInit() {

		// three categories
		allRecipes.add(new ArrayList<CraftingRecipe>());
		allRecipes.add(new ArrayList<CraftingRecipe>());
		allRecipes.add(new ArrayList<CraftingRecipe>());

		// put the
		for (int i = 0; i < tempAllRecipe.length; i++) {

			CraftingRecipe recipe = tempAllRecipe[i];

			allRecipes.get(recipe.getCategory()).add(recipe);

		}

		// sort the recipes according to their crafting level.
		// ascending order
		Collections.sort(allRecipes.get(CraftingRecipe.CATEGORY_PLACE),
				recipeSorter);
		Collections.sort(allRecipes.get(CraftingRecipe.CATEGORY_FOOD), recipeSorter);
		Collections.sort(allRecipes.get(CraftingRecipe.CATEGORY_TOOL), recipeSorter);

		// temporary recipe array not needed anymore.
		tempAllRecipe = null;

	}

	/**
	 * updates states
	 */
	public void tick() {

		if (justOpened)
			justOpened = false;

		// opening screen by table
		if (openedWithTable) {

			openedWithTable = false;
			opened = true;
			openCraftingScreen = true;
			shouldUpdateCraftingScreen = true;
			justOpened = true;

		} // toggle the open and close
		else if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_C)
				&& !isIngScreenOpened && !inventory.isOpened()) {

			if (!opened) {

				opened = true;
				openCraftingScreen = true;

				// when opened by key, it is bare hand
				currentCraftLevel = CraftingRecipe.HAND_LEVEL;

				shouldUpdateCraftingScreen = true;

			} else if (opened) {

				opened = false;
				closeCraftingScreen = true;

			}

		}

		showCraftingScreen();

		if (!opened) {
			return;
		}

		updateCraftingScreen();
		updateProductIcon();

		showIngredientScreen();
		updateIngredientScreen();

		// press space for crafting
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_SPACE) && !justOpened)
			craftItem();

	}

	/**
	 * Render screen
	 *
	 * @param gfx
	 */
	public void render(Graphics gfx) {

		if (!opened) {
			return;
		}

		gfx.drawImage(Assets.crafting, craftingX, craftingY, craftingWid,
				craftingHei, null);

		Text.drawString(gfx, "Crafting", titleX, titleY);

		renderProductName(gfx);

		if (selected) {
			gfx.drawImage(Assets.inventory_selected, selectX, selectY, null);
		}

	}

	// utility methods //////////////
	/**
	 * Render the name of product under the product icon.
	 *
	 * @param gfx
	 */
	private void renderProductName(Graphics gfx) {

		Item product = ((UIInvSlot) objects.get(PRODUCT_ICON_INDEX)).getItem();

		if (product != null) {

			Text.drawMultiLineString(gfx, product.getName(), productTextX,
					productTextY, 142, Color.WHITE, Assets.font, true);

		}

	}

	/**
	 * Update the product icon depend on the selected recipe.
	 */
	private void updateProductIcon() {

		CraftingRecipe recipe = getSelectedRecipe();

		UIInvSlot icon = (UIInvSlot) objects.get(PRODUCT_ICON_INDEX);

		if (oldSelectedRecipeIndex != selectedRecipeIndex) {

			oldSelectedRecipeIndex = selectedRecipeIndex;

			if (recipe != null) {

				int id = recipe.getProductId();
				int count = recipe.getProductCount();

				Item item = Item.getItem(id, count);

				icon.setItem(item);

			} else {

				icon.setItem(null);

			}
		}
	}

	/**
	 * Update the UIObjects in the crafting screen. Slots, selected index, and
	 * scroll.
	 */
	private void updateCraftingScreen() {

		// while screen is opened, update...
		if (!opened || isIngScreenOpened)
			return;

		UIScrollBar scroll = (UIScrollBar) objects.get(SCROLL_INDEX);

		currentRow = scroll.getProgress();

		// if different category is selected
		if (oldSelectedCategory != selectedCategory) {

			// if it is being updated now, no point of updating it again in the next
			// tick
			shouldUpdateCraftingScreen = false;

			// reset scroll bar
			scroll.setProgress(0);
			oldCraftingScrollProgress = 0;

			// reset index
			selectedRecipeIndex = 0;
			oldSelectedRecipeIndex = -1;

			// reset selected slot
			selected = true;

			UIInvSlot firstSlot = (UIInvSlot) objects.get(SLOT_INDEX);

			selectX = (int) firstSlot.getX() - 11;
			selectY = (int) firstSlot.getY() - 11;

			// update new max progress
			scroll.setMaxProgress(getMaxProgressByCategory());

			// update the category change
			oldSelectedCategory = selectedCategory;

			// update the slots
			for (int y = 0; y < SLOTS_ROW; y++) {

				for (int x = 0; x < SLOTS_COL; x++) {

					// currentRow is 0 anyway, so no need to add it in
					int slotIndex = x + (y * SLOTS_COL);

					UIInvSlot slot = (UIInvSlot) objects.get(SLOT_INDEX + slotIndex);

					// if there are no more recipes to show, leave the slot
					// blank
					if (slotIndex >= allRecipes.get(selectedCategory).size()) {

						slot.setItem(null);

					} else {

						CraftingRecipe recipe = allRecipes.get(selectedCategory)
								.get(slotIndex);

						int id = recipe.getProductId();
						int count = recipe.getProductCount();

						Item item = Item.getItem(id, count);

						slot.setItem(item);

						if (!recipe.isCraftable(player)) {

							slot.fadeImage(true);

						}

					}

				}

			}

		} // else if the scroll have been scrolled
		else if (oldCraftingScrollProgress != currentRow
				|| shouldUpdateCraftingScreen) {

			shouldUpdateCraftingScreen = false;

			int changeInProgress = currentRow - oldCraftingScrollProgress;

			selectedRecipeIndex += changeInProgress * SLOTS_COL;

			oldCraftingScrollProgress = currentRow;

			// update the slots
			for (int y = 0; y < SLOTS_ROW; y++) {

				for (int x = 0; x < SLOTS_COL; x++) {

					int slotIndex = x + (y * SLOTS_COL);
					int recipeIndex = x + (y * SLOTS_COL) + (currentRow * SLOTS_COL);

					UIInvSlot slot = (UIInvSlot) objects.get(SLOT_INDEX + slotIndex);

					// if there are no more recipes to show, leave the slot
					// blank
					if (recipeIndex >= allRecipes.get(selectedCategory).size()) {

						slot.setItem(null);

					} else {

						CraftingRecipe recipe = allRecipes.get(selectedCategory)
								.get(recipeIndex);

						int id = recipe.getProductId();
						int count = recipe.getProductCount();

						Item item = Item.getItem(id, count);

						slot.setItem(item);

						if (!recipe.isCraftable(player)) {

							slot.fadeImage(true);

						}

					}

				}

			}

		}

	}

	/**
	 * Hide or show the UIObjects in the crafting screen.
	 */
	private void showCraftingScreen() {

		if (openCraftingScreen && opened) {

			openCraftingScreen = false;
			closeCraftingScreen = true;

			// 1st row by default
			currentRow = 0;

			// default category
			((UIToggleButton) objects.get(CATEGORY_PLACE_INDEX)).setPressed(true);
			((UIToggleButton) objects.get(CATEGORY_FOOD_INDEX)).setPressed(false);
			((UIToggleButton) objects.get(CATEGORY_TOOL_INDEX)).setPressed(false);

			// default category
			selectedCategory = CraftingRecipe.CATEGORY_PLACE;

			// select first slot by default
			selected = true;

			UIInvSlot slot = (UIInvSlot) objects.get(SLOT_INDEX);

			selectX = (int) slot.getX() - 11;
			selectY = (int) slot.getY() - 11;

			selectedRecipeIndex = 0;

			// show screen now
			hideCraftingScreen(false);

		} else if (closeCraftingScreen && !opened) {

			closeCraftingScreen = false;
			openCraftingScreen = true;

			hideCraftingScreen(true);

		}

	}

	/**
	 * Update ingredient screen when scroll moves.
	 */
	private void updateIngredientScreen() {

		if (isIngScreenOpened) {

			UIScrollBar scroll = (UIScrollBar) objects.get(ING_SCROLL_INDEX);

			int newProgress = scroll.getProgress();

			if (newProgress != ingScrollOldProgress || ingScrollOldProgress == -1) {

				CraftingRecipe recipe = getSelectedRecipe();

				for (int i = 0; i < ING_SLOT_ROW; i++) {

					int index = newProgress + i;

					UIInvSlot slot = (UIInvSlot) objects.get(ING_SLOT_INDEX + i);

					UILabel text = (UILabel) objects.get(ING_SLOT_TEXT_INDEX + i);

					if (index >= recipe.ingredients.length) {

						slot.setItem(null);
						text.setText("");

					} else {

						Item item = Item.getItem(recipe.ingredients[index],
								recipe.ingredientCount[index]);

						slot.setItem(item);
						text.setText(item.getName());

					}

				}

				ingScrollOldProgress = newProgress;

			}

		}

	}

	/**
	 * Set hidden true or false the ingredient screen. Set the max progress of
	 * scroll in ingredient screen too.
	 */
	private void showIngredientScreen() {

		if (openIngScreen && isIngScreenOpened) {

			openIngScreen = false;
			closeIngScreen = true;

			// reset the scroll of ingredient screen
			ingScrollOldProgress = -1;

			CraftingRecipe recipe = getSelectedRecipe();

			int maxProgress = recipe.ingredients.length - ING_SLOT_ROW + 1;

			if (maxProgress < 0)
				maxProgress = 1;

			// screen
			objects.get(ING_SCREEN_INDEX).setHidden(false);

			// title
			objects.get(ING_TITLE_INDEX).setHidden(false);

			// bar
			UIScrollBar scroll = (UIScrollBar) objects.get(ING_SCROLL_INDEX);
			// update max progress depend on the selected recipe
			scroll.setMaxProgress(maxProgress);
			scroll.setHidden(false);

			// quit button
			objects.get(ING_QUIT_INDEX).setHidden(false);

			// slots
			for (int i = 0; i < ING_SLOT_ROW; i++) {

				objects.get(ING_SLOT_INDEX + i).setHidden(false);
				objects.get(ING_SLOT_TEXT_INDEX + i).setHidden(false);

			}

			lockCraftingScreen(true);

		} else if (closeIngScreen && !isIngScreenOpened) {

			closeIngScreen = false;
			openIngScreen = true;

			objects.get(ING_SCREEN_INDEX).setHidden(true);
			objects.get(ING_TITLE_INDEX).setHidden(true);
			objects.get(ING_SCROLL_INDEX).setHidden(true);
			objects.get(ING_QUIT_INDEX).setHidden(true);

			for (int i = 0; i < ING_SLOT_ROW; i++) {

				objects.get(ING_SLOT_INDEX + i).setHidden(true);
				objects.get(ING_SLOT_TEXT_INDEX + i).setHidden(true);

			}

			lockCraftingScreen(false);

		}

	}

	private void addIngredientScreen() {

		// screen
		UILabel screen = new UILabel(310, 300 - 132 - 32, 280, 264 + 32,
				Assets.background);

		screen.setHidden(true);

		objects.add(screen);

		// Title
		UILabel title = new UILabel(450, 300 - 132 - 32 + 16, 1, 1, "Ingredients");

		title.setHidden(true);

		objects.add(title);

		// slots
		for (int i = 0; i < ING_SLOT_ROW; i++) {

			UIInvSlot slot = new UIInvSlot(screen.getX() + 6,
					screen.getY() + 38 + i * 66, null, null, false);

			slot.setHidden(true);

			objects.add(slot);

		}

		// slot item names
		for (int i = 0; i < ING_SLOT_ROW; i++) {

			UILabel text = new UILabel(screen.getX() + 72,
					screen.getY() + 38 + i * 66, 172, 50, "");

			text.setMaxTextWidth(UILabel.FIT_OBJECT_WIDTH);

			text.setHidden(true);

			objects.add(text);

		}

		// bar
		UIScrollBar scroll = new UIScrollBar(screen.getX() + 252, screen.getY() + 48,
				24, 242, 1);

		scroll.setHidden(true);

		objects.add(scroll);

		// quit button
		ClickListener quitClicker = null;

		final UIImageButton quitBtn = new UIImageButton(screen.getX() + 235,
				screen.getY() + 4, 42, 42, Assets.inv_quit_btn, quitClicker);

		quitBtn.setHidden(true);

		quitClicker = new ClickListener() {

			@Override
			public void onClick() {

				quitBtn.setHovering(false);

				// TODO i dont know why this stay as true
				// after click and not be false...
				isIngScreenOpened = false;

			}

		};

		quitBtn.setClickLisenter(quitClicker);

		objects.add(quitBtn);

	}

	/**
	 * Add the slot buttons to the crafting screen.
	 * <p>
	 * #1 UI adding method
	 */
	private void addSlotBtns() {

		for (int y = 0; y < SLOTS_ROW; y++) {

			for (int x = 0; x < SLOTS_COL; x++) {

				ClickListener clicker = null;

				final UIInvSlot slot = new UIInvSlot(craftingX + 24 + x * 66,
						craftingY + 119 + y * 66, null, clicker, false);

				slot.setHidden(true);

				slot.setIndex(x + y * SLOTS_COL);

				clicker = new ClickListener() {

					@Override
					public void onClick() {

						selected = true;

						selectX = (int) slot.getX() - 11;
						selectY = (int) slot.getY() - 11;

						selectedRecipeIndex = slot.getIndex()
								+ (currentRow * SLOTS_COL);

					}

				};

				slot.setClickListener(clicker);

				ClickListener longClicker = new ClickListener() {

					@Override
					public void onClick() {

						selected = true;

						selectX = (int) slot.getX() - 11;
						selectY = (int) slot.getY() - 11;

						selectedRecipeIndex = slot.getIndex()
								+ (currentRow * SLOTS_COL);

						if (getSelectedRecipe() != null && !isIngScreenOpened) {

							isIngScreenOpened = true;
							openIngScreen = true;

						}

					}

				};

				slot.setLongClickListener(longClicker);

				objects.add(slot);
			}
		}

	}

	/**
	 * Add the slot buttons to the crafting screen.
	 * <p>
	 * #2 UI adding method
	 */
	private void addCategoryBtns() {

		ClickListener placeClicker = null;
		ClickListener foodClicker = null;
		ClickListener toolClicker = null;

		final UIToggleButton place = new UIToggleButton(category_placeX,
				category_placeY, 140, 64, Assets.crafting_category, placeClicker);

		final UIToggleButton food = new UIToggleButton(category_foodX,
				category_foodY, 140, 64, Assets.crafting_category, foodClicker);

		final UIToggleButton tool = new UIToggleButton(category_toolX,
				category_toolY, 140, 64, Assets.crafting_category, toolClicker);

		// category place-able items
		place.setImage(Assets.itemFence_wood);

		place.setHidden(true);

		placeClicker = new ClickListener() {

			@Override
			public void onClick() {

				// if it have been un-pressed
				if (!place.isPressed()) {

					// toggle it back to pressed
					// (not giving option for un-selecting the category)
					place.toggle();

				}

				selectedCategory = CraftingRecipe.CATEGORY_PLACE;

				food.setPressed(false);
				tool.setPressed(false);

			}

		};

		place.setClickLisenter(placeClicker);

		objects.add(place);

		// category consumable items
		food.setImage(Assets.itemApple);

		food.setHidden(true);

		foodClicker = new ClickListener() {

			@Override
			public void onClick() {

				// if it have been un-pressed
				if (!food.isPressed()) {

					// toggle it back to pressed
					// (not giving option for un-selecting the category)
					food.toggle();

				}

				selectedCategory = CraftingRecipe.CATEGORY_FOOD;

				place.setPressed(false);
				tool.setPressed(false);

			}

		};

		food.setClickLisenter(foodClicker);

		objects.add(food);

		// category place-able items
		// TODO tool image
		tool.setImage(Assets.itemHammer);

		tool.setHidden(true);

		toolClicker = new ClickListener() {

			@Override
			public void onClick() {

				// if it have been un-pressed
				if (!tool.isPressed()) {

					// toggle it back to pressed
					// (not giving option for un-selecting the category)
					tool.toggle();

				}

				selectedCategory = CraftingRecipe.CATEGORY_TOOL;

				place.setPressed(false);
				food.setPressed(false);

			}

		};

		tool.setClickLisenter(toolClicker);

		objects.add(tool);

	}

	/**
	 * Add a vertical scroll-bar to the crafting screen.
	 * <p>
	 * #3 UI adding method
	 */
	private void addScroll() {

		UIScrollBar scroll = new UIScrollBar(scrollX, scrollY, 20, 272,
				getMaxProgressByCategory());

		scroll.setHidden(true);

		objects.add(scroll);

	}

	/**
	 * Add the product icon slot and crafting buttons to the crafting screen.
	 * <p>
	 * #4 UI adding method
	 */
	private void addCraftBtn() {

		// no click needed for an icon
		ClickListener productClicker = null;

		final UIInvSlot product = new UIInvSlot(productIconX, productIconY, null,
				productClicker, false);

		product.setSize(80);

		product.setItem(null);

		product.setHidden(true);

		objects.add(product);

		// craftBtn
		ClickListener craftClicker = null;

		final UIImageButton craftBtn = new UIImageButton(craftBtnX, craftBtnY, 120,
				60, Assets.btn_start, craftClicker);

		craftBtn.setHidden(true);

		craftBtn.setText("Craft");

		craftClicker = new ClickListener() {

			@Override
			public void onClick() {

				craftItem();

			}

		};

		craftBtn.setClickLisenter(craftClicker);

		objects.add(craftBtn);

	}

	/**
	 * Add the quit button to the crafting screen.
	 * <p>
	 * #5 UI adding method
	 */
	private void addQuitBtn() {

		ClickListener clicker = null;

		final UIImageButton btn = new UIImageButton(quitBtnX, quitBtnY, 48, 48,
				Assets.inv_quit_btn, clicker);

		btn.setHidden(true);

		clicker = new ClickListener() {

			@Override
			public void onClick() {

				opened = false;
				closeCraftingScreen = true;

			}

		};

		btn.setClickLisenter(clicker);

		objects.add(btn);

	}

	/**
	 * Lock the all UIObjects in the crafting screen. To prevent user to touch them
	 * while opening ingredient screen.
	 *
	 * @param isLocked
	 */
	private void lockCraftingScreen(boolean isLocked) {

		for (int y = 0; y < SLOTS_ROW; y++) {

			for (int x = 0; x < SLOTS_COL; x++) {

				objects.get(SLOT_INDEX + x + y * SLOTS_COL).setLocked(isLocked);

			}

		}

		objects.get(CATEGORY_PLACE_INDEX).setLocked(isLocked);
		objects.get(CATEGORY_FOOD_INDEX).setLocked(isLocked);
		objects.get(CATEGORY_TOOL_INDEX).setLocked(isLocked);
		objects.get(SCROLL_INDEX).setLocked(isLocked);
		objects.get(CRAFT_BTN_INDEX).setLocked(isLocked);
		objects.get(QUIT_BTN_INDEX).setLocked(isLocked);

	}

	/**
	 * Hide or show the all UIObjects in the crafting screen. To close or open
	 * crafting screen.
	 *
	 * @param isHidden
	 */
	private void hideCraftingScreen(boolean isHidden) {

		for (int y = 0; y < SLOTS_ROW; y++) {

			for (int x = 0; x < SLOTS_COL; x++) {

				objects.get(SLOT_INDEX + x + y * SLOTS_COL).setHidden(isHidden);

			}

		}

		objects.get(CATEGORY_PLACE_INDEX).setHidden(isHidden);
		objects.get(CATEGORY_FOOD_INDEX).setHidden(isHidden);
		objects.get(CATEGORY_TOOL_INDEX).setHidden(isHidden);
		objects.get(SCROLL_INDEX).setHidden(isHidden);
		objects.get(PRODUCT_ICON_INDEX).setHidden(isHidden);
		objects.get(CRAFT_BTN_INDEX).setHidden(isHidden);
		objects.get(QUIT_BTN_INDEX).setHidden(isHidden);

	}

	/**
	 * Get the max progress for Crafting screen scroll bar. Used when updating it.
	 *
	 * @return
	 */
	private int getMaxProgressByCategory() {

		int maxProgress = (int) Math
				.ceil((double) allRecipes.get(selectedCategory).size()
						/ (double) SLOTS_COL)
				- SLOTS_ROW + 1;

		// if not enough recipes to fill the 24 slots
		if (maxProgress < 0)
			maxProgress = 1;

		return maxProgress;

	}

	/**
	 * Get selected recipe.
	 *
	 * @return selected slot's CraftingRecipe, or it could be null
	 */
	private CraftingRecipe getSelectedRecipe() {

		if (selectedRecipeIndex < allRecipes.get(selectedCategory).size()) {

			return allRecipes.get(selectedCategory).get(selectedRecipeIndex);

		} else {

			return null;

		}

	}

	/**
	 * Try craft item, and show the outcome at screen.
	 */
	private void craftItem() {

		CraftingRecipe recipe = getSelectedRecipe();

		if (recipe == null)
			return;

		String outcome = recipe.craftItem(player);

		Text text = new Text(outcome, 450, 300, Game.FPS, true, true, Assets.font,
				Color.YELLOW);

		TextManager.addText(text);

		shouldUpdateCraftingScreen = true;

		updateCraftingScreen();

	}

	////////////////// getter setter////////////////////////
	/**
	 * Whether the crafting screen is opened or not.
	 *
	 * @return true if opened
	 */
	public boolean isOpened() {

		return opened;
	}

	/**
	 * Open or close the screen;
	 *
	 * @param opened
	 */
	public void setOpened(boolean opened) {

		this.opened = opened;

	}

	/**
	 * Get the current category in the crafting screen.
	 *
	 * @return the selectedCategory
	 */
	public int getSelectedCategory() {

		return selectedCategory;
	}

	/**
	 * Get the current crafting level.
	 * <p>
	 * CrafitngRecipe...
	 * <li>HAND_LEVEL
	 * <li>TABLE_LEVEL
	 * <li>ADVANCED_TABLE_LEVEL
	 *
	 * @return level
	 */
	public int getCurrentCraftLevel() {

		return currentCraftLevel;
	}

	/**
	 * Set the current crafting level.
	 * <p>
	 * CrafitngRecipe...
	 * <li>HAND_LEVEL
	 * <li>TABLE_LEVEL
	 * <li>ADVANCED_TABLE_LEVEL
	 */
	public void setCurrentCraftingLevel(int level) {

		currentCraftLevel = level;

	}

	/**
	 * Open the crafting screen by table.
	 *
	 * @param craftingLevel CraftingRecipe.HAND_LEVEL, TABLE_LEVEL, or
	 *                      ADVANCED_TABLE_LEVEL
	 */
	public void openWithTable(int craftingLevel) {

		openedWithTable = true;
		this.currentCraftLevel = craftingLevel;

	}

}
