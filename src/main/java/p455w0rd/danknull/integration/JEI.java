package p455w0rd.danknull.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.init.ModIntegration.Mods;
import p455w0rd.danknull.init.ModItems;

/**
 * @author p455w0rd
 *
 */
@JEIPlugin
public class JEI implements IModPlugin {

	public static IIngredientBlacklist blacklist;

	@Override
	public void register(@Nonnull IModRegistry registry) {
		blacklist = registry.getJeiHelpers().getIngredientBlacklist();

		//blacklistItem(new ItemStack(ModItems.DANK_NULL_HOLDER, 1, OreDictionary.WILDCARD_VALUE));

		List<ItemStack> dankNulls = new ArrayList<ItemStack>();
		dankNulls.addAll(Arrays.asList(new ItemStack(ModItems.DANK_NULL, 1, 0), new ItemStack(ModItems.DANK_NULL, 1, 1), new ItemStack(ModItems.DANK_NULL, 1, 2), new ItemStack(ModItems.DANK_NULL, 1, 3), new ItemStack(ModItems.DANK_NULL, 1, 4), new ItemStack(ModItems.DANK_NULL, 1, 5)));
		registry.addIngredientInfo(dankNulls, ItemStack.class, "jei.danknull.desc");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 0), ItemStack.class, "jei.danknull.desc0");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 1), ItemStack.class, "jei.danknull.desc1");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 2), ItemStack.class, "jei.danknull.desc2");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 3), ItemStack.class, "jei.danknull.desc3");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 4), ItemStack.class, "jei.danknull.desc4");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 5), ItemStack.class, "jei.danknull.desc5");

		//registry.addIngredientInfo(new ItemStack(ModBlocks.DANKNULL_DOCK), ItemStack.class, "jei.danknull_dock.desc");

	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime runtime) {
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry registry) {
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
	}

	public static void blacklistItem(ItemStack stack) {
		if (Mods.JEI.isLoaded() && blacklist != null && !isItemBlacklisted(stack)) {
			blacklist.addIngredientToBlacklist(stack);
		}
	}

	public static boolean isItemBlacklisted(ItemStack stack) {
		if (Mods.JEI.isLoaded()) {
			return blacklist.isIngredientBlacklisted(stack);
		}
		return false;
	}

	public static void whitelistItem(ItemStack stack) {
		if (Mods.JEI.isLoaded() && isItemBlacklisted(stack)) {
			blacklist.removeIngredientFromBlacklist(stack);
		}
	}

	public static void handleItemBlacklisting(ItemStack stack, boolean shouldBlacklist) {
		if (shouldBlacklist) {
			if (!isItemBlacklisted(stack)) {
				blacklistItem(stack);
			}
			return;
		}
		if (isItemBlacklisted(stack)) {
			whitelistItem(stack);
		}
	}

}