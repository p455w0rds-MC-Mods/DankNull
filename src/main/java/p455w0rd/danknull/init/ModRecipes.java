package p455w0rd.danknull.init;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.RecipeUtils;

/**
 * @author p455w0rd
 *
 */
public class ModRecipes {

	private static RecipeUtils recipeUtils = new RecipeUtils(ModGlobals.MODID, ModGlobals.NAME);

	private static final ModRecipes INSTANCE = new ModRecipes();

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

	@SuppressWarnings("deprecation")
	public List<IRecipe> getRecipes() {
		if (CRAFTING_RECIPES.isEmpty()) {
			final ItemStack coalBlock = new ItemStack(Blocks.COAL_BLOCK);

			final ItemStack redPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 14);
			final ItemStack bluePane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 3);
			final ItemStack whitePane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 0);
			final ItemStack yellowPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 4);
			final ItemStack cyanPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 9);
			final ItemStack limePane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 5);

			final ItemStack panel0 = new ItemStack(ModItems.REDSTONE_PANEL);
			final ItemStack panel1 = new ItemStack(ModItems.LAPIS_PANEL);
			final ItemStack panel2 = new ItemStack(ModItems.IRON_PANEL);
			final ItemStack panel3 = new ItemStack(ModItems.GOLD_PANEL);
			final ItemStack panel4 = new ItemStack(ModItems.DIAMOND_PANEL);
			final ItemStack panel5 = new ItemStack(ModItems.EMERALD_PANEL);

			final ItemStack dankNull0 = new ItemStack(ModItems.REDSTONE_DANKNULL);
			final ItemStack dankNull1 = new ItemStack(ModItems.LAPIS_DANKNULL);
			final ItemStack dankNull2 = new ItemStack(ModItems.IRON_DANKNULL);
			final ItemStack dankNull3 = new ItemStack(ModItems.GOLD_DANKNULL);
			final ItemStack dankNull4 = new ItemStack(ModItems.DIAMOND_DANKNULL);
			final ItemStack dankNull5 = new ItemStack(ModItems.EMERALD_DANKNULL);

			final ItemStack dankNullDockItem = new ItemStack(Item.getItemFromBlock(ModBlocks.DANKNULL_DOCK));

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

			CRAFTING_RECIPES.add(upgradeDankNullToLapis = DankNullUtils.addDankNullUpgradeRecipe("redstoneToLapis", " a ", "aba", " a ", 'a', panel1, 'b', dankNull0));
			CRAFTING_RECIPES.add(upgradeDankNullToIron = DankNullUtils.addDankNullUpgradeRecipe("lapisToIron", " a ", "aba", " a ", 'a', panel2, 'b', dankNull1));
			CRAFTING_RECIPES.add(upgradeDankNullToGold = DankNullUtils.addDankNullUpgradeRecipe("ironToGold", " a ", "aba", " a ", 'a', panel3, 'b', dankNull2));
			CRAFTING_RECIPES.add(upgradeDankNullToDiamond = DankNullUtils.addDankNullUpgradeRecipe("goldToDiamond", " a ", "aba", " a ", 'a', panel4, 'b', dankNull3));
			CRAFTING_RECIPES.add(upgradeDankNullToEmerald = DankNullUtils.addDankNullUpgradeRecipe("diamondToEmerald", " a ", "aba", " a ", 'a', panel5, 'b', dankNull4));

			CRAFTING_RECIPES.add(dankNullDock = recipeUtils.addOldShaped(dankNullDockItem, "aba", "bcb", "aba", 'a', new ItemStack(Items.EMERALD), 'b', new ItemStack(Items.REDSTONE), 'c', new ItemStack(Blocks.OBSIDIAN)));
		}
		return CRAFTING_RECIPES;
	}

	public IRecipe[] getArray() {
		final IRecipe[] recipesArray = new IRecipe[getRecipes().size()];
		for (int i = 0; i < getRecipes().size(); i++) {
			recipesArray[i] = getRecipes().get(i);
		}
		return recipesArray;
	}

}