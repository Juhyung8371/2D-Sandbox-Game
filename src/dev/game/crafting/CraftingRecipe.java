
package dev.game.crafting;

import dev.game.entities.creatures.Player;
import dev.game.inventory.Inventory;
import dev.game.items.Item;

/**
 * CraftingRecipe.java - Crafting recipe of an item
 *
 * @author Juhyung Kim
 */
public class CraftingRecipe {

	// texts about the outcome of the crafting
	private static final String NOT_ENOUGH_INGREDIENT = "Not enough ingredients!"
			+ "\nPress recipe to see ingredients!";
	private static final String LOW_CRAFTING_LEVEL = "Crafting level is too low!";
	private static final String CRAFTING_SUCCESSFUL = " is crafted.";

	/**
	 * Level of crafting: available by hand or higher
	 */
	public static final int HAND_LEVEL = 1;
	/**
	 * Level of crafting: available by crafting table or higher
	 */
	public static final int TABLE_LEVEL = 2;
	/**
	 * Level of crafting: available by advanced crafting table
	 */
	public static final int ADVANCED_TABLE_LEVEL = 3;

	/**
	 * Category of crafting: place-able items
	 */
	public static final int CATEGORY_PLACE = 0;
	/**
	 * Category of crafting: consumable items
	 */
	public static final int CATEGORY_FOOD = 1;
	/**
	 * Category of crafting: tool items
	 */
	public static final int CATEGORY_TOOL = 2;

	// level of crafting required to craft this item
	protected int craftingLevel;

	protected int category;

	protected int[] ingredients;
	protected int[] ingredientCount;
	protected int productId;
	protected int productCount;

	/**
	 * A crafting recipe of an item
	 *
	 * @param craftingLevel   Level of crafting required to craft the item
	 *                        (HAND_LEVEL, TABLE_LEVEL, ADVANCED_TABLE_LEVEL)
	 * @param category        Category of product
	 * @param ingredients     Id's of items
	 * @param ingredientCount Count of items required to craft (parallel array with
	 *                        <code>ingredients</code>)
	 * @param productId       Id of the product item
	 * @param productCount    Count of the product item
	 */
	public CraftingRecipe(int craftingLevel, int category, int[] ingredients,
			int[] ingredientCount, int productId, int productCount) {

		this.craftingLevel = craftingLevel;
		this.category = category;

		this.ingredients = ingredients;
		this.ingredientCount = ingredientCount;

		this.productId = productId;
		this.productCount = productCount;

		findRecipeError();

	}

	/**
	 * Get the array of indexes of inventory items that reference to the valid
	 * ingredients.
	 *
	 * @param inventory
	 * @return indexes of valid ingredients if exists, else return null
	 */
	private int[] getIngredientIndexes(Inventory inventory) {

		Item[] items = inventory.getItems();

		// Index of the ingredients in the inventory
		int[] indexes = new int[ingredients.length];

		// default value of "not found" is -1
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = -1;
		}

		int foundCount = 0;

		// go through the inventory
		for (int i = Inventory.INV_SLOT_INDEX; i < items.length; i++) {

			// skip empty item
			if (items[i] == null)
				continue;

			int invId = items[i].getID();
			int invCount = items[i].getItemCount();

			// comparing the inventory item with all ingredients
			for (int a = 0; a < ingredients.length; a++) {

				// if the ingredient in the index is already found
				if (indexes[a] != -1)
					continue;

				// if found valid ingredient
				if (invId == ingredients[a] && invCount >= ingredientCount[a]) {

					indexes[a] = i;
					foundCount++;

					// found all ingredients
					if ((foundCount == ingredients.length)) {

						return indexes;

					}

				}

			}

		}

		// some ingredients are missing
		return null;

	}

	/**
	 * Craft the item and give the product to the player. Ingredients will be
	 * consumed in the inventory. If there's no room in inventory, the item will be
	 * thrown outside.
	 *
	 * @param player
	 * @return The text about outcome of the attempt of crafting item (successful,
	 *         failed)
	 */
	public String craftItem(Player player) {

		CraftingManager craftingManager = player.getCraftingManager();

		// not high enough level to craft
		if (craftingManager.getCurrentCraftLevel() < craftingLevel)
			return LOW_CRAFTING_LEVEL;

		Inventory inventory = player.getInventory();

		int[] indexes = getIngredientIndexes(inventory);

		if (indexes == null)
			return NOT_ENOUGH_INGREDIENT;

		Item[] items = inventory.getItems();

		// Consume the ingredients
		for (int x = 0; x < indexes.length; x++) {

			int ind = indexes[x];
			Item invItem = items[ind];

			invItem.setItemCount(invItem.getItemCount() - ingredientCount[x]);
			inventory.setInvItem(invItem, ind);

		}

		Item product = Item.getItemForInv(productId, productCount, player);

		// now give the product
		inventory.addItem(product);

		return (product.getName() + CRAFTING_SUCCESSFUL);

	}

	/**
	 * Find the possible errors in the variables passed in the constructor
	 */
	private void findRecipeError() {

		// errors in crafting level
		if (craftingLevel < HAND_LEVEL || craftingLevel > ADVANCED_TABLE_LEVEL) {

			throw new RuntimeException("\n Invalid crafting level.");

		}

		// errors in ingredients
		if (ingredients.length != ingredientCount.length) {

			throw new RuntimeException(
					"\n Size of ingredient array and amount array should be same!");

		}

		for (int i = 0; i < ingredients.length; i++) {

			int ing = ingredients[i];

			if (ing < 1 || ing > Item.MAX_ITEMID) {

				throw new RuntimeException(
						"\n Invalid ingredient in the recipe: Item ID " + ing + ".");

			}

			int amt = ingredientCount[i];

			if (amt < 1) {

				throw new RuntimeException(
						"\n The amount of ingredient must be at least 1.");

			}

		}

		// errors in product
		if (productId < 1 || productId > Item.MAX_ITEMID) {

			throw new RuntimeException("\n Invalid product item ID.");

		}

		if (productCount < 1) {

			throw new RuntimeException("\n Amount of product must be at least 1.");

		}

	}

	/////// getter setter /////////
	/**
	 * The Level of crafting skill required to craft.
	 * <p>
	 * <li>HAND_LEVEL
	 * <li>TABLE_LEVEL
	 * <li>ADVANCED_TABLE_LEVEL
	 *
	 * @return the craftingLevel
	 */
	public int getCraftingLevel() {
		return craftingLevel;
	}

	/**
	 * @return the ingredients
	 */
	public int[] getIngredients() {
		return ingredients;
	}

	/**
	 * @return the ingredientCount
	 */
	public int[] getIngredientCount() {
		return ingredientCount;
	}

	/**
	 * @return the productId
	 */
	public int getProductId() {
		return productId;
	}

	/**
	 * @return the productCount
	 */
	public int getProductCount() {
		return productCount;
	}

	/**
	 * Get the category of the product.
	 * <li>CATEGORY_PLACE
	 * <li>CATEGORY_FOOD
	 * <li>CATEGORY_TOOL
	 *
	 * @return the category
	 */
	public int getCategory() {
		return category;
	}

	/**
	 * Check if the recipe is craft-able depend on the current crafting level and
	 * inventory items.
	 *
	 * @param player
	 * @return
	 */
	public boolean isCraftable(Player player) {

		// if low crafting level
		if (player.getCraftingManager().getCurrentCraftLevel() < craftingLevel) {

			return false;

		}

		Inventory inventory = player.getInventory();

		Item[] items = inventory.getItems();

		boolean[] foundArr = new boolean[ingredients.length];

		int foundCount = 0;

		// go through the inventory
		for (int i = Inventory.INV_SLOT_INDEX; i < items.length; i++) {

			if (items[i] == null)
				continue;

			int invId = items[i].getID();
			int invCount = items[i].getItemCount();

			// comparing the inventory item with all ingredients
			for (int a = 0; a < foundArr.length; a++) {

				// if the ingredient in the index is already found
				if (foundArr[a])
					continue;

				// if found valid ingredient
				if (invId == ingredients[a] && invCount >= ingredientCount[a]) {

					foundArr[a] = true;
					foundCount++;

					// found all ingredients
					if ((foundCount == ingredients.length)) {

						return true;

					}

				}

			}

		}

		return false;

	}

}
