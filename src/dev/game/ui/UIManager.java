
package dev.game.ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import dev.game.Handler;

/**
 * UIManager.java - Manages all the UIobjectList in each State. For better
 * organization, there is an main list of List<UIObject>. The UIObject that are
 * related together could be tied into a single List<UIObject> (ex. a List of
 * objects just for inventory screen objects).
 *
 * @author j.kim3
 */
public class UIManager {

	// indexes of all UIObjects in each states
	public static final int DEATHSTATE_ALL = 0;

	public static final int LOBBYSTATE_ALL = 0;

	public static final int MENUSTATE_MAPS = 0;
	public static final int MENUSTATE_NEWWORLD = 1;

	public static final int GAMESTATE_HEALTH_BAR = 0;
	public static final int GAMESTATE_QUICK_SLOT = 1;
	public static final int GAMESTATE_INVENTORY = 2;
	public static final int GAMESTATE_CRAFTING = 3;
	public static final int GAMESTATE_QUIT_DIALOG = 4;

	// class stuff
	private Handler handler;

	// The main list that contains the List<UIObject>'s
	private List<List<UIObject>> objectList;

	/**
	 * Each State have their own UIManager
	 *
	 * @param handler
	 */
	public UIManager(Handler handler) {

		this.handler = handler;
		objectList = new ArrayList<List<UIObject>>();

	}

	/**
	 * Unused
	 */
	public void tick() {
		// well.. everything could be done in render()
	}

	public void render(Graphics gfx) {

		for (List<UIObject> list : objectList) {

			for (UIObject obj : list) {

				if (!obj.isHidden())
					obj.render(gfx);
			}

		}

	}

	public void onMouseMove(MouseEvent e) {

		for (List<UIObject> list : objectList) {

			for (UIObject obj : list) {

				if (!obj.isHidden())
					obj.onMouseMove(e);

			}

		}

	}

	public void onMouseRelease(MouseEvent e) {

		for (List<UIObject> list : objectList) {

			for (UIObject obj : list) {

				if (!obj.isHidden())
					obj.onMouseRelease(e);

			}

		}

	}

	public void onMousePress(MouseEvent e) {

		for (List<UIObject> list : objectList) {

			for (UIObject obj : list) {

				if (!obj.isHidden())
					obj.onMousePress(e);

			}

		}
	}

	public void onMouseLongPress(MouseEvent e) {

		for (List<UIObject> list : objectList) {

			for (UIObject obj : list) {

				if (!obj.isHidden() && obj.hovering)
					obj.onMouseLongPress(e);

			}

		}

	}

	public void onMouseDrag(MouseEvent e) {

		for (List<UIObject> list : objectList) {

			for (UIObject obj : list) {

				if (!obj.isHidden())
					obj.onMouseDrag(e);

			}

		}

	}

	// getter setter
	/**
	 * @return the handler
	 */
	public Handler getHandler() {
		return handler;
	}

	/**
	 * @param handler the handler to set
	 */
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * The main list that contains the List<UIObject>'s
	 *
	 * @return the list of objectList
	 */
	public List<List<UIObject>> getObjectList() {
		return objectList;
	}

	/**
	 * The main list that contains the List<UIObject>'s
	 *
	 * @param objectList the objectList to set
	 */
	public void setObjectList(List<List<UIObject>> objectList) {
		this.objectList = objectList;
	}

	/**
	 * Get the sub-list from the main list
	 *
	 * @param index
	 * @return
	 */
	public List<UIObject> getSubList(int index) {
		return objectList.get(index);
	}

	/**
	 * Add a new empty list to the main list
	 */
	public void addList() {

		objectList.add(new ArrayList<>());

	}

}
