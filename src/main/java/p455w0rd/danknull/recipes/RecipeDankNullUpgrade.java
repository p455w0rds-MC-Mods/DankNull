package p455w0rd.danknull.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.items.ItemDankNull;

/**
 * @author p455w0rd
 */
public class RecipeDankNullUpgrade extends IForgeRegistryEntry.Impl<IRecipe> implements IShapedRecipe {

    public final NonNullList<Ingredient> recipeItems;

    public RecipeDankNullUpgrade(final NonNullList<Ingredient> ingredients) {
        recipeItems = ingredients;
    }

    @Override
    public String getGroup() {
        return ModGlobals.MODID + ":danknullupgrade";
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemDankNull.getTier(getInputDankNull()).getUpgradedVersion(getInputDankNull());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(final InventoryCrafting inv) {
        final NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            final ItemStack itemstack = inv.getStackInSlot(i);

            nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }

        return nonnulllist;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    public ItemStack getInputDankNull() {
        for (final Ingredient item : getIngredients()) {
            if (item.getMatchingStacks().length > 0 && ItemDankNull.isDankNull(item.getMatchingStacks()[0])) {
                return item.getMatchingStacks()[0];
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canFit(final int width, final int height) {
        return width >= 3 && height >= 3;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(final InventoryCrafting inv, final World worldIn) {
        for (int widthIndex = 0; widthIndex <= inv.getWidth() - this.getRecipeWidth(); ++widthIndex) {
            for (int heightIndex = 0; heightIndex <= inv.getHeight() - this.getRecipeHeight(); ++heightIndex) {
                if (checkMatch(inv, widthIndex, heightIndex)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(final InventoryCrafting inventory, final int widthIndexStart, final int heightIndexStart) {
        for (int column = 0; column < 3; ++column) {
            for (int row = 0; row < 3; ++row) {
                final int recipeColumn = column - widthIndexStart;
                final int recipeRow = row - heightIndexStart;
                Ingredient ingredient = Ingredient.EMPTY;

                if (recipeColumn >= 0 && recipeRow >= 0 && recipeColumn < 3 && recipeRow < 3) {
                    ingredient = recipeItems.get(recipeColumn + recipeRow * 3);
                }

                if (!ingredient.apply(inventory.getStackInRowAndColumn(column, row))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(final InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (ItemDankNull.isDankNull(inv.getStackInSlot(i))) {
                NBTTagCompound oldNBT = inv.getStackInSlot(i).getTagCompound();
                final ItemStack newStack = getNewNextTierStack(inv.getStackInSlot(i));
                newStack.setTagCompound(oldNBT);
                return newStack;
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack getNewNextTierStack(final ItemStack dankNull) {
        final int tier = ItemDankNull.getTier(dankNull).ordinal();
        if (tier < 5) {
            ItemDankNull item = null;
            switch (tier) {
                case 0:
                    item = ModItems.LAPIS_DANKNULL;
                    break;
                case 1:
                    item = ModItems.IRON_DANKNULL;
                    break;
                case 2:
                    item = ModItems.GOLD_DANKNULL;
                    break;
                case 3:
                    item = ModItems.DIAMOND_DANKNULL;
                    break;
                case 4:
                    item = ModItems.EMERALD_DANKNULL;
                    break;
            }
            return new ItemStack(item);
        }
        return ItemStack.EMPTY;
    }

    public int getWidth() {
        return 3;
    }

    public int getHeight() {
        return 3;
    }

    //================================================ FORGE START ================================================
    @Override
    public int getRecipeWidth() {
        return getWidth();
    }

    @Override
    public int getRecipeHeight() {
        return getHeight();
    }
}