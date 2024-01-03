
package dev.game.inventory;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;

import dev.game.Handler;
import dev.game.crafting.CraftingManager;
import dev.game.gfx.Assets;
import dev.game.gfx.Text;
import dev.game.input.KeyManager;
import dev.game.items.Item;
import dev.game.items.ItemManager;
import dev.game.ui.ClickListener;
import dev.game.ui.UIImageButton;
import dev.game.ui.UIInvSlot;
import dev.game.ui.UIManager;
import dev.game.ui.UIObject;

/**
 * Inventory.java - Inventory system
 *
 * @author j.kim3
 */
public class Inventory {

	private Handler handler;

	private CraftingManager craftingManager;

	// whether if inventory screen is opened
	private boolean opened = false;

	// total 31 slots
	private Item[] inventoryItems = new Item[31];

	private final int INV_ROW = 4, INV_COL = 6;

	/**
	 * Amounts of inventory slots
	 */
	public static final int INV_SIZE = 24;
	/**
	 * Amounts of quick-slots
	 */
	public static final int QUICK_SIZE = 5;

	// graphics locations
	private int invWid = 600, invHei = 400;
	private int invX = 150, invY = 100;
	private int invTextX = 480, invTextY = 120;
	private int equipTextX = 220, equipTextY = 120;
	private int armorTextX = 220, armorTextY = 250;
	private int slotTextX = 480, slotTextY = 410;
	private int deleteBtnX = 170, deleteBtnY = 420;
	private int quitBtnX = 695, quitBtnY = 107;

	// Variables about selected items
	private boolean selected = false;
	private int selectX, selectY;
	private int selectIndex;

	// ui manager for slots
	private UIManager uiManager;

	// index of items in inventoryItem array
	private int equipSlotIndex = 0;
	private int armorSlotIndex = 1;

	/**
	 * First index of inventory slot in inventory items
	 */
	public static final int INV_SLOT_INDEX = 2;
	/**
	 * First index of quick slot in inventory items
	 */
	public static final int QUICK_SLOT_INDEX = 26;

	private int deleteIndex = QUICK_SLOT_INDEX + 5;
	private int quitIndex = deleteIndex + 1;

	// all the objects
	private List<UIObject> objects;

	public Inventory(Handler handler) {

		this.handler = handler;

	}

	/**
	 * Add all the buttons to the UIManager
	 */
	public void init() {

		this.craftingManager = handler.getWorld().getEntityManager().getPlayer()
				.getCraftingManager();

		this.uiManager = handler.getMouseManager().getUIManager();

		objects = uiManager.getSubList(UIManager.GAMESTATE_INVENTORY);

		addSlotBtns();
		addDeleteBtn();
		addQuitBtn();

	}

	/**
	 * updates states
	 */
	public void tick() {

		// toggle the open and close
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_I)
				&& !craftingManager.isOpened()) {

			opened = (!opened);

		}

		showInvBtns();

		moveItemWithQuickSlot();
		dragAndDrop();

		if (!opened)
			return;

		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_DELETE))
			deleteSelectedItem();

	}

	/**
	 * Render inventory screen
	 *
	 * @param gfx
	 */
	public void render(Graphics gfx) {

		if (!opened)
			return;

		gfx.drawImage(Assets.inventory, invX, invY, invWid, invHei, null);

		renderInvTexts(gfx);

		if (selected)
			gfx.drawImage(Assets.inventory_selected, selectX, selectY, null);

	}

	//////////////// inventory //////////////
	/**
	 * Add Item to the inventory slot or quick-slot. If there's no room for it,
	 * throw it out back to the world
	 *
	 * @param newItem
	 */
	public void addItem(Item newItem) {

		// true if it's turn to find empty slot and put item in
		boolean findEmptySlot = false;

		// first 2 is equip and armor
		for (int i = 2; i < INV_SIZE + QUICK_SIZE + 2; i++) {

			Item invItem = inventoryItems[i];

			// no point of checking full slot
			if (invItem != null && invItem.getItemCount() == Item.MAX_STACK)
				continue;

			// if it's checking the empty slot when it's not yet needed to,
			// continue
			if (invItem == null && !findEmptySlot) {

				// if reached final slot, now look for empty slot
				if (i == INV_SIZE + QUICK_SIZE + 1 && !newItem.isEmpty()) {
					findEmptySlot = true;
					i = 1;
				}
				continue;

			}

			// when finally there's is not choice but to put it at new slot
			if (invItem == null && findEmptySlot) {

				setInvItem(newItem, i);

				return;
			}

			// if there is already a same item in the inventory,
			// try add them together
			if (invItem.getID() == newItem.getID()) {

				int totalCount = invItem.getItemCount() + newItem.getItemCount();
				int oldInvItemCount = invItem.getItemCount();

				// true if there is leftover item even after merging into
				// inventory item
				boolean isOverflowing = false;

				if (totalCount >= Item.MAX_STACK) {

					totalCount = Item.MAX_STACK;
					isOverflowing = true;

				}

				invItem.setItemCount(totalCount);

				if (!isOverflowing)
					return;

				newItem.setItemCount(
						(oldInvItemCount + newItem.getItemCount()) - Item.MAX_STACK);

			}
		}

		// if there's literally not spot to add new item,
		// dump it out to world again
		if (!newItem.isEmpty()) {

			Item item = Item.getItemEntity(newItem.getID(), newItem.getX(),
					newItem.getY());

			ItemManager.addItem(item);

		}
	}

	/**
	 * Update the image of the slot
	 *
	 * @param index
	 */
	private void updateSlot(int index) {

		Item item = getInvItem(index);
		UIInvSlot slot = getSlotButton(index);

		if (item == null) {

			if (!slot.isEmpty())
				slot.setItem(null);

		} else if (item.isEmpty()) {

			setInvItem(null, index);

		}

	}

	/**
	 * Add quit button to inventory screen
	 */
	private void addQuitBtn() {

		ClickListener quitClicker = new ClickListener() {

			@Override
			public void onClick() {

				opened = false;

			}

		};

		UIImageButton quitBtn = new UIImageButton(quitBtnX, quitBtnY, 48, 48,
				Assets.inv_quit_btn, quitClicker);

		objects.add(quitBtn);

	}

	/**
	 * Add item delete button to inventory screen
	 */
	private void addDeleteBtn() {

		ClickListener clicker = new ClickListener() {

			@Override
			public void onClick() {

				deleteSelectedItem();

			}

		};

		UIImageButton deleteBtn = new UIImageButton(deleteBtnX, deleteBtnY, 100, 50,
				Assets.inv_del_btn, clicker);

		deleteBtn.setText("Delete");

		objects.add(deleteBtn);
	}

	/**
	 * Add slots to inventory screen
	 */
	private void addSlotBtns() {

		// equip slot
		ClickListener equipSlotClicker = null;

		final UIInvSlot equipSlot = new UIInvSlot(invX + 44, invY + 42,
				getInvItem(equipSlotIndex), equipSlotClicker, true);

		equipSlot.setIndex(equipSlotIndex);

		equipSlotClicker = new ClickListener() {

			@Override
			public void onClick() {

				selected = true;

				selectX = (int) equipSlot.getX() - 11;
				selectY = (int) equipSlot.getY() - 11;

				selectIndex = equipSlot.getIndex();

			}

		};

		equipSlot.setClickListener(equipSlotClicker);

		objects.add(equipSlot);

		// armor slot
		ClickListener armorSlotClicker = null;

		final UIInvSlot armorSlot = new UIInvSlot(invX + 44, invY + 174,
				getInvItem(armorSlotIndex), armorSlotClicker, true);

		armorSlot.setIndex(armorSlotIndex);

		armorSlotClicker = new ClickListener() {

			@Override
			public void onClick() {

				selected = true;

				selectX = (int) armorSlot.getX() - 11;
				selectY = (int) armorSlot.getY() - 11;

				selectIndex = armorSlot.getIndex();

			}

		};

		armorSlot.setClickListener(armorSlotClicker);

		objects.add(armorSlot);

		// inventory slots
		for (int y = 0; y < INV_ROW; y++) {

			for (int x = 0; x < INV_COL; x++) {

				Item item = getInvItem(x, y);

				ClickListener clicker = null;

				final UIInvSlot slot = new UIInvSlot((x * (66)) + 286 + 8,
						(y * (66)) + 134 + 8, item, clicker, true);

				slot.setIndex(getInvIndex(x, y));

				clicker = new ClickListener() {

					@Override
					public void onClick() {

						selected = true;

						selectX = (int) slot.getX() - 11;
						selectY = (int) slot.getY() - 11;

						selectIndex = slot.getIndex();

					}

				};

				slot.setClickListener(clicker);

				objects.add(slot);

			}

		}

		// quick-slots
		for (int i = 0; i < 5; i++) {

			ClickListener quickSlotClicker = null;

			final UIInvSlot quickSlot = new UIInvSlot(invX + 176 + i * 66,
					invY + 332, getInvItem(QUICK_SLOT_INDEX + i), quickSlotClicker,
					true);

			quickSlot.setIndex(i + QUICK_SLOT_INDEX);

			quickSlotClicker = new ClickListener() {

				@Override
				public void onClick() {

					selected = true;

					selectX = (int) quickSlot.getX() - 11;
					selectY = (int) quickSlot.getY() - 11;

					selectIndex = quickSlot.getIndex();

				}

			};

			quickSlot.setClickListener(quickSlotClicker);

			objects.add(quickSlot);

		}

	}

	/**
	 * Drag and drop feature
	 */
	private void dragAndDrop() {

		// gotta be open, and select something to move items
		if (!opened || !selected) {
			return;
		}

		UIInvSlot selectedSlot = getSlotButton(selectIndex);
		Item selectedItem = selectedSlot.getItem();

		// if selected slot has item and
		// if item is released
		if (selectedItem != null && selectedSlot.isReleased()) {

			// check all slots to move selected item to
			for (int i = equipSlotIndex; i < deleteIndex; i++) {

				if (i == selectIndex)
					continue;

				UIInvSlot slot = getSlotButton(i);

				// if slots intersects
				if (slot.bounds.intersects(selectedSlot.bounds)) {

					// swap items
					setInvItem(slot.getItem(), selectIndex);
					setInvItem(selectedItem, slot.getIndex());

					selectX = (int) slot.getX() - 11;
					selectY = (int) slot.getY() - 11;
					selectIndex = slot.getIndex();

					selectedSlot.setX(selectedSlot.getOriX());
					selectedSlot.setY(selectedSlot.getOriY());

					return;
				}
			}

			// reset the dragged slot back to its original position
			selectedSlot.setX(selectedSlot.getOriX());
			selectedSlot.setY(selectedSlot.getOriY());
			selectX = (int) selectedSlot.getX() - 11;
			selectY = (int) selectedSlot.getY() - 11;

		}
	}

	/**
	 * Allow use 1~5 keys to move items between inventory slots and quick-slots
	 */
	private void moveItemWithQuickSlot() {

		// gotta be open, and select something to move items
		if (!opened || !selected) {
			return;
		}

		// shortcut only allowed between inventory slots and quick slots
		if (selectIndex <= armorSlotIndex || selectIndex >= QUICK_SLOT_INDEX) {
			return;
		}

		KeyManager keyManager = handler.getKeyManager();

		boolean slotOne = keyManager.keyJustPressed(KeyEvent.VK_1);
		boolean slotTwo = keyManager.keyJustPressed(KeyEvent.VK_2);
		boolean slotThree = keyManager.keyJustPressed(KeyEvent.VK_3);
		boolean slotFour = keyManager.keyJustPressed(KeyEvent.VK_4);
		boolean slotFive = keyManager.keyJustPressed(KeyEvent.VK_5);
		boolean[] keys = { slotOne, slotTwo, slotThree, slotFour, slotFive };

		Item selectedItem = getInvItem(selectIndex);

		for (int i = 0; i < keys.length; i++) {

			// if not pressed
			if (!keys[i])
				continue;

			// if selected a empty inventory slot,
			// check if it can move item from quick-slot to inventory
			if (selectedItem == null) {

				if (getInvItem(QUICK_SLOT_INDEX + i) != null) {

					setInvItem(getInvItem(QUICK_SLOT_INDEX + i), selectIndex);
					setInvItem(null, QUICK_SLOT_INDEX + i);
					return;

				}

			} // else, if selected a inventory slot with item,
				// check if it can move item to quickslot
			else if (getInvItem(QUICK_SLOT_INDEX + i) == null) {

				setInvItem(selectedItem, QUICK_SLOT_INDEX + i);
				setInvItem(null, selectIndex);
				return;

			}

		}

	}

	/**
	 * Add many buttons in inventory screen
	 * <p>
	 * (hide and show the buttons)
	 */
	private void showInvBtns() {

		UIInvSlot equipSlot = (UIInvSlot) objects.get(equipSlotIndex);
		UIInvSlot armorSlot = (UIInvSlot) objects.get(armorSlotIndex);

		if (!opened) {

			equipSlot.setHidden(true);
			armorSlot.setHidden(true);

			for (int y = 0; y < INV_ROW * INV_COL; y++) {

				getSlotButton(y + INV_SLOT_INDEX).setHidden(true);

			}

			for (int i = 0; i < 5; i++) {

				objects.get(QUICK_SLOT_INDEX + i).setHidden(true);

			}

			objects.get(deleteIndex).setHidden(true);
			objects.get(quitIndex).setHidden(true);

			return;

		} // if opened
		else if (opened) {

			equipSlot.setHidden(false);
			updateSlot(equipSlotIndex);

			armorSlot.setHidden(false);
			updateSlot(armorSlotIndex);

			// inventory slots
			for (int i = 0; i < INV_ROW * INV_COL; i++) {

				getSlotButton(INV_SLOT_INDEX + i).setHidden(false);

				updateSlot(INV_SLOT_INDEX + i);

			}

			// quickSlots
			for (int i = 0; i < 5; i++) {

				getSlotButton(QUICK_SLOT_INDEX + i).setHidden(false);

				updateSlot(QUICK_SLOT_INDEX + i);

			}

			objects.get(deleteIndex).setHidden(false);
			objects.get(quitIndex).setHidden(false);

		}

	}

	/**
	 * Render the texts in inventory screen
	 *
	 * @param gfx
	 */
	private void renderInvTexts(Graphics gfx) {

		Text.drawString(gfx, "Inventory", invTextX, invTextY);
		Text.drawString(gfx, "Equip", equipTextX, equipTextY);
		Text.drawString(gfx, "Armor", armorTextX, armorTextY);
		Text.drawString(gfx, "Quick Slot", slotTextX, slotTextY);

	}

	/**
	 * Delete selected item
	 */
	private void deleteSelectedItem() {

		if (!selected) {
			return;
		}

		setInvItem(null, selectIndex);

	}

	////// getter setter//////
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public boolean isOpened() {
		return opened;
	}

	/**
	 * Get an array of all items in inventory
	 *
	 * @return
	 */
	public Item[] getItems() {
		return inventoryItems;
	}

	/**
	 * Get the slot for the slot
	 * <p>
	 * Index:
	 * <li>equipSlot = 0
	 * <li>armorSlot = 1
	 * <li>invSlot = 2 ~ 25
	 * <li>quickSlot = 26 ~ 30
	 *
	 * @param index
	 * @return
	 */
	public UIInvSlot getSlotButton(int index) {

		return (UIInvSlot) objects.get(index);

	}

	/**
	 * Only used for double for-loops that loops through the inventory slots
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private int getInvIndex(int x, int y) {

		return (x + (INV_COL * y) + INV_SLOT_INDEX);
	}

	/**
	 * Only used for double for-loops that loops through the inventory slots
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private Item getInvItem(int x, int y) {

		int index = getInvIndex(x, y);

		if (index > INV_SIZE + INV_SLOT_INDEX) { // first 2 indexs are equip and
			// armor
			return null;
		}

		return inventoryItems[index];

	}

	/**
	 * Get item in certain index
	 * <p>
	 * Index:
	 * <li>equipSlot = 0
	 * <li>armorSlot = 1
	 * <li>invSlot = 2 ~ 25
	 * <li>quickSlot = 26 ~ 30
	 *
	 * @param index
	 * @return
	 */
	private Item getInvItem(int index) {

		return inventoryItems[index];

	}

	/**
	 * Set item in certain index
	 * <p>
	 * Index:
	 * <li>equipSlot = 0
	 * <li>armorSlot = 1
	 * <li>invSlot = 2 ~ 25
	 * <li>quickSlot = 26 ~ 30
	 *
	 * @param item
	 * @param index
	 */
	public void setInvItem(Item item, int index) {

		if (item != null && item.getItemCount() <= 0) {

			item = null;

		}

		inventoryItems[index] = item;

		// update the slot image
		((UIInvSlot) objects.get(index)).setItem(item);

		// if setting item in quickslot, update the outer quickslot too
		if (index >= QUICK_SLOT_INDEX) {

			((UIInvSlot) uiManager.getSubList(UIManager.GAMESTATE_QUICK_SLOT)
					.get(index - QUICK_SLOT_INDEX)).setItem(item);

		}

	}

}
