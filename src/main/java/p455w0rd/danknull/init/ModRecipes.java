package p455w0rd.danknull.init;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class ModRecipes {

	//private static final ModRecipes INSTANCE = new ModRecipes();

	public static IRecipe upgradeDankNullToLapis = DankNullUtils.addDankNullUpgradeRecipe("redstoneToLapis", " a ", "aba", " a ", 'a', new ItemStack(ModItems.LAPIS_PANEL), 'b', new ItemStack(ModItems.REDSTONE_DANKNULL));
	public static IRecipe upgradeDankNullToIron = DankNullUtils.addDankNullUpgradeRecipe("lapisToIron", " a ", "aba", " a ", 'a', new ItemStack(ModItems.IRON_PANEL), 'b', new ItemStack(ModItems.LAPIS_DANKNULL));
	public static IRecipe upgradeDankNullToGold = DankNullUtils.addDankNullUpgradeRecipe("ironToGold", " a ", "aba", " a ", 'a', new ItemStack(ModItems.GOLD_PANEL), 'b', new ItemStack(ModItems.IRON_DANKNULL));
	public static IRecipe upgradeDankNullToDiamond = DankNullUtils.addDankNullUpgradeRecipe("goldToDiamond", " a ", "aba", " a ", 'a', new ItemStack(ModItems.DIAMOND_PANEL), 'b', new ItemStack(ModItems.GOLD_DANKNULL));
	public static IRecipe upgradeDankNullToEmerald = DankNullUtils.addDankNullUpgradeRecipe("diamondToEmerald", " a ", "aba", " a ", 'a', new ItemStack(ModItems.EMERALD_PANEL), 'b', new ItemStack(ModItems.DIAMOND_DANKNULL));

	public static final IRecipe[] UPGRADE_RECIPES = { //@formatter:off
			upgradeDankNullToLapis,
			upgradeDankNullToIron,
			upgradeDankNullToGold,
			upgradeDankNullToDiamond,
			upgradeDankNullToEmerald//@formatter:on
	};

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