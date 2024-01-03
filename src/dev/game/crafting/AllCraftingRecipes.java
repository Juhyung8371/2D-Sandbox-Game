
package dev.game.crafting;

import dev.game.items.ItemId;

/**
 * AllCraftingRecipes.java - All the recipes.
 *
 * @author Juhyung Kim
 */
public class AllCraftingRecipes {

	private static CraftingRecipe woodenFenceRecipe = new CraftingRecipe(
			CraftingRecipe.HAND_LEVEL, CraftingRecipe.CATEGORY_PLACE,
			new int[] { ItemId.WOOD }, new int[] { 3 }, ItemId.WOODEN_FENCE, 3);

	private static CraftingRecipe stoneFenceRecipe = new CraftingRecipe(
			CraftingRecipe.TABLE_LEVEL, CraftingRecipe.CATEGORY_PLACE,
			new int[] { ItemId.STONE }, new int[] { 5 }, ItemId.STONE_FENCE, 3);

	private static CraftingRecipe torchRecipe = new CraftingRecipe(
			CraftingRecipe.HAND_LEVEL, CraftingRecipe.CATEGORY_PLACE,
			new int[] { ItemId.WOOD }, new int[] { 1 }, ItemId.TORCH, 10);

	private static CraftingRecipe craftingTableRecipe = new CraftingRecipe(
			CraftingRecipe.HAND_LEVEL, CraftingRecipe.CATEGORY_PLACE,
			new int[] { ItemId.WOOD }, new int[] { 6 }, ItemId.CRAFTING_TABLE, 1);

	private static CraftingRecipe advancedCraftingTableRecipe = new CraftingRecipe(
			CraftingRecipe.TABLE_LEVEL, CraftingRecipe.CATEGORY_PLACE,
			new int[] { ItemId.STONE }, new int[] { 6 },
			ItemId.ADVANCED_CRAFTING_TABLE, 1);

	private static CraftingRecipe aaa = new CraftingRecipe(
			CraftingRecipe.TABLE_LEVEL, CraftingRecipe.CATEGORY_TOOL,
			new int[] { ItemId.WOODEN_FENCE }, new int[] { 1 }, ItemId.WOOD, 3);

	/**
	 * Get all the recipes in an array.
	 *
	 * @return
	 */
	public static CraftingRecipe[] getAllRecipes() {

		return new CraftingRecipe[] { woodenFenceRecipe, stoneFenceRecipe,
				torchRecipe, craftingTableRecipe, advancedCraftingTableRecipe, aaa };

	}

}
