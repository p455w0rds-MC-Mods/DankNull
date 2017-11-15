package p455w0rd.danknull.init;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import p455w0rdslib.util.RecipeUtils;

/**
 * @author p455w0rd
 *
 */
public class ModRecipes {

	private static RecipeUtils recipeUtils = new RecipeUtils(ModGlobals.MODID, ModGlobals.NAME);

	private static final ModRecipes INSTANCE = new ModRecipes();
	private static boolean init = true;

	public static final List<IRecipe> CRAFTING_RECIPES = Lists.<IRecipe>newLinkedList();

	public IRecipe panelRedstone;
	public IRecipe panelLapis;
	public IRecipe panelIron;
	public IRecipe panelGold;
	public IRecipe panelDiamond;
	public IRecipe panelEmerald;

	public IRecipe dankNullRedstone;
	public IRecipe dankNullLapis;
	public IRecipe dankNullIron;
	public IRecipe dankNullGold;
	public IRecipe dankNullDiamond;
	public IRecipe dankNullEmerald;

	public IRecipe upgradeDankNullToLapis;
	public IRecipe upgradeDankNullToIron;
	public IRecipe upgradeDankNullToGold;
	public IRecipe upgradeDankNullToDiamond;
	public IRecipe upgradeDankNullToEmerald;

	public IRecipe dankNullDock;

	public static ModRecipes getInstance() {
		return INSTANCE;
	}

	public static void init() {
		if (init) {
			getInstance().addRecipes();
			init = false;
		}
	}

	@SuppressWarnings("deprecation")
	private void addRecipes() {

		ItemStack coalBlock = new ItemStack(Blocks.COAL_BLOCK);

		ItemStack redPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 14);
		ItemStack bluePane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 3);
		ItemStack whitePane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 0);
		ItemStack yellowPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 4);
		ItemStack cyanPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 9);
		ItemStack limePane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 5);

		ItemStack panel0 = new ItemStack(ModItems.DANK_NULL_PANEL);
		ItemStack panel1 = new ItemStack(ModItems.DANK_NULL_PANEL, 1, 1);
		ItemStack panel2 = new ItemStack(ModItems.DANK_NULL_PANEL, 1, 2);
		ItemStack panel3 = new ItemStack(ModItems.DANK_NULL_PANEL, 1, 3);
		ItemStack panel4 = new ItemStack(ModItems.DANK_NULL_PANEL, 1, 4);
		ItemStack panel5 = new ItemStack(ModItems.DANK_NULL_PANEL, 1, 5);

		ItemStack dankNull0 = new ItemStack(ModItems.DANK_NULL, 1, 0);
		ItemStack dankNull1 = new ItemStack(ModItems.DANK_NULL, 1, 1);
		ItemStack dankNull2 = new ItemStack(ModItems.DANK_NULL, 1, 2);
		ItemStack dankNull3 = new ItemStack(ModItems.DANK_NULL, 1, 3);
		ItemStack dankNull4 = new ItemStack(ModItems.DANK_NULL, 1, 4);
		ItemStack dankNull5 = new ItemStack(ModItems.DANK_NULL, 1, 5);

		ItemStack dankNullDockItem = new ItemStack(Item.getItemFromBlock(ModBlocks.DANKNULL_DOCK));

		CRAFTING_RECIPES.add(panelRedstone = recipeUtils.addOldShaped(panel0, "aca", "cbc", "aca", 'a', new ItemStack(Items.REDSTONE), 'b', redPane, 'c', coalBlock));
		CRAFTING_RECIPES.add(panelLapis = recipeUtils.addOldShaped(panel1, "aca", "cbc", "aca", 'a', new ItemStack(Items.DYE, 1, 4), 'b', bluePane, 'c', coalBlock));
		CRAFTING_RECIPES.add(panelIron = recipeUtils.addOldShaped(panel2, "aca", "cbc", "aca", 'a', new ItemStack(Items.IRON_INGOT), 'b', whitePane, 'c', coalBlock));
		CRAFTING_RECIPES.add(panelGold = recipeUtils.addOldShaped(panel3, "aca", "cbc", "aca", 'a', new ItemStack(Items.GOLD_INGOT), 'b', yellowPane, 'c', coalBlock));
		CRAFTING_RECIPES.add(panelDiamond = recipeUtils.addOldShaped(panel4, "aca", "cbc", "aca", 'a', new ItemStack(Items.DIAMOND), 'b', cyanPane, 'c', coalBlock));
		CRAFTING_RECIPES.add(panelEmerald = recipeUtils.addOldShaped(panel5, "aca", "cbc", "aca", 'a', new ItemStack(Blocks.EMERALD_BLOCK), 'b', limePane, 'c', coalBlock));

		CRAFTING_RECIPES.add(dankNullRedstone = recipeUtils.addOldShaped(dankNull0, " a ", "aaa", " a ", 'a', panel0));
		CRAFTING_RECIPES.add(dankNullLapis = recipeUtils.addOldShaped(dankNull1, " a ", "aaa", " a ", 'a', panel1));
		CRAFTING_RECIPES.add(dankNullIron = recipeUtils.addOldShaped(dankNull2, " a ", "aaa", " a ", 'a', panel2));
		CRAFTING_RECIPES.add(dankNullGold = recipeUtils.addOldShaped(dankNull3, " a ", "aaa", " a ", 'a', panel3));
		CRAFTING_RECIPES.add(dankNullDiamond = recipeUtils.addOldShaped(dankNull4, " a ", "aaa", " a ", 'a', panel4));
		CRAFTING_RECIPES.add(dankNullEmerald = recipeUtils.addOldShaped(dankNull5, " a ", "aaa", " a ", 'a', panel5));

		CRAFTING_RECIPES.add(upgradeDankNullToLapis = recipeUtils.addOldShaped(dankNull1, " a ", "aba", " a ", 'a', panel1, 'b', dankNull0));
		CRAFTING_RECIPES.add(upgradeDankNullToIron = recipeUtils.addOldShaped(dankNull2, " a ", "aba", " a ", 'a', panel2, 'b', dankNull1));
		CRAFTING_RECIPES.add(upgradeDankNullToGold = recipeUtils.addOldShaped(dankNull3, " a ", "aba", " a ", 'a', panel3, 'b', dankNull2));
		CRAFTING_RECIPES.add(upgradeDankNullToDiamond = recipeUtils.addOldShaped(dankNull4, " a ", "aba", " a ", 'a', panel4, 'b', dankNull3));
		CRAFTING_RECIPES.add(upgradeDankNullToEmerald = recipeUtils.addOldShaped(dankNull5, " a ", "aba", " a ", 'a', panel5, 'b', dankNull4));

		CRAFTING_RECIPES.add(dankNullDock = recipeUtils.addOldShaped(dankNullDockItem, "aba", "bcb", "aba", 'a', new ItemStack(Items.EMERALD), 'b', new ItemStack(Items.REDSTONE), 'c', new ItemStack(Blocks.OBSIDIAN)));

		for (IRecipe recipe : CRAFTING_RECIPES) {
			ForgeRegistries.RECIPES.register(recipe);
		}
	}

}