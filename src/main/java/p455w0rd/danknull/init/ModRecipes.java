package p455w0rd.danknull.init;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.event.RegistryEvent;
import p455w0rd.danknull.recipes.RecipeDankNullUpgrade;

/**
 * @author p455w0rd
 *
 */
public class ModRecipes {

	//private static final ModRecipes INSTANCE = new ModRecipes();

	public static IRecipe upgradeDankNullToLapis = addDankNullUpgradeRecipe("redstoneToLapis", " a ", "aba", " a ", 'a', new ItemStack(ModItems.LAPIS_PANEL), 'b', new ItemStack(ModItems.REDSTONE_DANKNULL));
	public static IRecipe upgradeDankNullToIron = addDankNullUpgradeRecipe("lapisToIron", " a ", "aba", " a ", 'a', new ItemStack(ModItems.IRON_PANEL), 'b', new ItemStack(ModItems.LAPIS_DANKNULL));
	public static IRecipe upgradeDankNullToGold = addDankNullUpgradeRecipe("ironToGold", " a ", "aba", " a ", 'a', new ItemStack(ModItems.GOLD_PANEL), 'b', new ItemStack(ModItems.IRON_DANKNULL));
	public static IRecipe upgradeDankNullToDiamond = addDankNullUpgradeRecipe("goldToDiamond", " a ", "aba", " a ", 'a', new ItemStack(ModItems.DIAMOND_PANEL), 'b', new ItemStack(ModItems.GOLD_DANKNULL));
	public static IRecipe upgradeDankNullToEmerald = addDankNullUpgradeRecipe("diamondToEmerald", " a ", "aba", " a ", 'a', new ItemStack(ModItems.EMERALD_PANEL), 'b', new ItemStack(ModItems.DIAMOND_DANKNULL));

	public static final IRecipe[] UPGRADE_RECIPES = { //@formatter:off
			upgradeDankNullToLapis,
			upgradeDankNullToIron,
			upgradeDankNullToGold,
			upgradeDankNullToDiamond,
			upgradeDankNullToEmerald//@formatter:on
	};

	public static IRecipe addDankNullUpgradeRecipe(final String recipeName, final Object... params) {
		final ShapedPrimer primer = CraftingHelper.parseShaped(params);
		final IRecipe recipe = new RecipeDankNullUpgrade(primer.input).setRegistryName(new ResourceLocation(ModGlobals.MODID, recipeName));
		return recipe;
	}

	//public static ModRecipes getInstance() {
	//	return INSTANCE;
	//}

	public static void register(final RegistryEvent.Register<IRecipe> event) {
		event.getRegistry().registerAll(UPGRADE_RECIPES);
	}

	/*public IRecipe[] getArray() {
		final IRecipe[] recipesArray = new IRecipe[getRecipes().size()];
		for (int i = 0; i < getRecipes().size(); i++) {
			recipesArray[i] = getRecipes().get(i);
		}
		return recipesArray;
	}*/

}