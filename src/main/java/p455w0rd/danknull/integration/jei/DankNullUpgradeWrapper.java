package p455w0rd.danknull.integration.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import p455w0rd.danknull.recipes.RecipeDankNullUpgrade;

/**
 * @author p455w0rd
 *
 */
public class DankNullUpgradeWrapper extends ShapelessRecipeWrapper<RecipeDankNullUpgrade> implements IShapedCraftingRecipeWrapper {

	public DankNullUpgradeWrapper(IJeiHelpers jeiHelpers, RecipeDankNullUpgrade recipe) {
		super(jeiHelpers, recipe);
	}

	@Override
	public int getWidth() {
		return 3;
	}

	@Override
	public int getHeight() {
		return 3;
	}

}
