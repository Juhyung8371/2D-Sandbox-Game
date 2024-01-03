
package dev.game.ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import dev.game.gfx.Text;
import dev.game.items.Item;
import dev.game.utils.Utils;

/**
 * UIInvSlot.java - inventory slot button
 *
 * @author j.kim3
 */
public class UIInvSlot extends UIObject {

	private int index = 0;
	private BufferedImage image;
	private Item item;
	private int size;
	private ClickListener clicker = null;
	private ClickListener longClicker = null;
	private static BufferedImage emptyImage = null;
	private boolean canDragItem;
	private int oriX;
	private int oriY;

	private boolean released = false;

	/**
	 * A slot.
	 *
	 * @param x       In pixel
	 * @param y       In pixel
	 * @param item    The Item to display
	 * @param size    In pixel
	 * @param clicker Can be null
	 * @param canDrag
	 */
	public UIInvSlot(int x, int y, Item item, int size, ClickListener clicker,
			boolean canDrag) {

		super(x, y, size, size);
		this.canDragItem = canDrag;
		this.oriX = x;
		this.oriY = y;

		if (emptyImage == null)
			emptyImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

		this.setSize(size);
		this.item = item;

		if (item == null) {
			emptyImage();
		} else {
			this.image = item.getTexture();
		}

		this.clicker = clicker;

	}

	/**
	 * A slot with the size of 48 pixels.
	 *
	 * @param x       pos
	 * @param y       pos
	 * @param item    The Item to display
	 * @param clicker Can be null
	 * @param canDrag
	 */
	public UIInvSlot(int x, int y, Item item, ClickListener clicker,
			boolean canDrag) {

		this(x, y, item, 48, clicker, canDrag);

	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics gfx) {

		gfx.drawImage(image, (int) getX(), (int) getY(), size, size, null);

		if (item != null) {

			String count = Integer.toString(item.getItemCount());

			Text.drawString(gfx, count, (int) getX() + size - 4,
					(int) getY() + size - 2);

		}

	}

	@Override
	public void onClick() {

		if (clicker != null)
			clicker.onClick();

	}

	@Override
	public void onMouseLongPress(MouseEvent e) {

		if (longClicker != null)
			longClicker.onClick();

	}

	@Override
	public void onMouseDrag(MouseEvent e) {

		if (canDragItem && isHovering() && item != null) {

			if (clicker != null)
				clicker.onClick();

			released = false;

			setX(e.getX() - size / 2);
			setY(e.getY() - size / 2);

		}

	}

	/**
	 * On mouse released.
	 *
	 * @param e
	 */
	@Override
	protected void onMouseRelease(MouseEvent e) {

		// reset position
		released = true;

		// clicked
		if (hovering) {
			onClick();
		}

	}

	// getter setter
	public void setLongClickListener(ClickListener longClicker) {

		this.longClicker = longClicker;
	}

	public void setClickListener(ClickListener clicker) {

		this.clicker = clicker;

	}

	public int getIndex() {

		return index;
	}

	public void setIndex(int index) {

		this.index = index;
	}

	public void setImage(BufferedImage image) {

		this.image = image;
	}

	public void emptyImage() {

		this.image = emptyImage;

	}

	/**
	 * Is "image" empty
	 *
	 * @return
	 */
	public boolean isEmpty() {

		return (this.image == emptyImage);
	}

	public Item getItem() {

		return item;
	}

	/**
	 * Set the item for the slot. If item is null, the image and text disappear.
	 *
	 * @param item
	 */
	public void setItem(Item item) {

		this.item = item;

		if (item != null)
			image = item.getTexture();
		else
			emptyImage();
	}

	/**
	 * Make the image 50% translucent or full opaque.
	 *
	 * @param isFaded
	 */
	public void fadeImage(boolean isFaded) {

		if (isFaded)
			image = Utils.setAlpha(image, 128);
		else
			image = Utils.setAlpha(image, 255);

	}

	/**
	 * @return the size
	 */
	public int getSize() {

		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {

		this.size = size;
	}

	/**
	 * @return the canDragItem
	 */
	public boolean isCanDragItem() {
		return canDragItem;
	}

	/**
	 * @param canDragItem the canDragItem to set
	 */
	public void setCanDragItem(boolean canDragItem) {
		this.canDragItem = canDragItem;
	}

	/**
	 * @return the released
	 */
	public boolean isReleased() {
		return released;
	}

	/**
	 * @return the oriX
	 */
	public int getOriX() {
		return oriX;
	}

	/**
	 * @return the oriY
	 */
	public int getOriY() {
		return oriY;
	}

}
