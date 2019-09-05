package p455w0rd.danknull.recipes;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.items.ItemDankNull;

/**
 * @author p455w0rd
 *
 */
public class RecipeDankNullUpgrade extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements net.minecraftforge.common.crafting.IShapedRecipe {

	public final NonNullList<Ingredient> recipeItems;
	private NBTTagCompound oldNBT = new NBTTagCompound();

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
		final NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);

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
		for (int i = 0; i <= 3 - 3; ++i) {
			for (int j = 0; j <= 3 - 3; ++j) {
				if (checkMatch(inv, i, j, true)) {
					return true;
				}

				if (checkMatch(inv, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the region of a crafting inventory is match for the recipe.
	 */
	private boolean checkMatch(final InventoryCrafting p_77573_1_, final int p_77573_2_, final int p_77573_3_, final boolean p_77573_4_) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				final int k = i - p_77573_2_;
				final int l = j - p_77573_3_;
				Ingredient ingredient = Ingredient.EMPTY;

				if (k >= 0 && l >= 0 && k < 3 && l < 3) {
					if (p_77573_4_) {
						ingredient = recipeItems.get(3 - k - 1 + l * 3);
					}
					else {
						ingredient = recipeItems.get(k + l * 3);
					}
				}

				if (!ingredient.apply(p_77573_1_.getStackInRowAndColumn(i, j))) {
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
				oldNBT = inv.getStackInSlot(i).getTagCompound();
				final ItemStack newStack = getNewNextTierStack(inv.getStackInSlot(i));
				newStack.setTagCompound(oldNBT);
				//newStack.setItemDamage(inv.getStackInSlot(i).getItemDamage() + 1);
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
			if (item != null) {
				return new ItemStack(item);
			}
		}
		return ItemStack.EMPTY;
	}

	public int getWidth() {
		return 3;
	}

	public int getHeight() {
		return 3;
	}

	public static ShapedRecipes deserialize(final JsonObject p_193362_0_) {
		final String s = JsonUtils.getString(p_193362_0_, "group", "");
		final Map<String, Ingredient> map = deserializeKey(JsonUtils.getJsonObject(p_193362_0_, "key"));
		final String[] astring = shrink(patternFromJson(JsonUtils.getJsonArray(p_193362_0_, "pattern")));
		final int i = astring[0].length();
		final int j = astring.length;
		final NonNullList<Ingredient> nonnulllist = deserializeIngredients(astring, map, i, j);
		final ItemStack itemstack = deserializeItem(JsonUtils.getJsonObject(p_193362_0_, "result"), true);
		return new ShapedRecipes(s, i, j, nonnulllist, itemstack);
	}

	private static NonNullList<Ingredient> deserializeIngredients(final String[] p_192402_0_, final Map<String, Ingredient> p_192402_1_, final int p_192402_2_, final int p_192402_3_) {
		final NonNullList<Ingredient> nonnulllist = NonNullList.<Ingredient>withSize(p_192402_2_ * p_192402_3_, Ingredient.EMPTY);
		final Set<String> set = Sets.newHashSet(p_192402_1_.keySet());
		set.remove(" ");

		for (int i = 0; i < p_192402_0_.length; ++i) {
			for (int j = 0; j < p_192402_0_[i].length(); ++j) {
				final String s = p_192402_0_[i].substring(j, j + 1);
				final Ingredient ingredient = p_192402_1_.get(s);

				if (ingredient == null) {
					throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
				}

				set.remove(s);
				nonnulllist.set(j + p_192402_2_ * i, ingredient);
			}
		}

		if (!set.isEmpty()) {
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
		}
		else {
			return nonnulllist;
		}
	}

	@VisibleForTesting
	static String[] shrink(final String... p_194134_0_) {
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;

		for (int i1 = 0; i1 < p_194134_0_.length; ++i1) {
			final String s = p_194134_0_[i1];
			i = Math.min(i, firstNonSpace(s));
			final int j1 = lastNonSpace(s);
			j = Math.max(j, j1);

			if (j1 < 0) {
				if (k == i1) {
					++k;
				}

				++l;
			}
			else {
				l = 0;
			}
		}

		if (p_194134_0_.length == l) {
			return new String[0];
		}
		else {
			final String[] astring = new String[p_194134_0_.length - l - k];

			for (int k1 = 0; k1 < astring.length; ++k1) {
				astring[k1] = p_194134_0_[k1 + k].substring(i, j + 1);
			}

			return astring;
		}
	}

	private static int firstNonSpace(final String str) {
		int i;

		for (i = 0; i < str.length() && str.charAt(i) == ' '; ++i) {
			;
		}

		return i;
	}

	private static int lastNonSpace(final String str) {
		int i;

		for (i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) {
			;
		}

		return i;
	}

	private static String[] patternFromJson(final JsonArray p_192407_0_) {
		final String[] astring = new String[p_192407_0_.size()];

		if (astring.length > 3) {
			throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
		}
		else if (astring.length == 0) {
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		}
		else {
			for (int i = 0; i < astring.length; ++i) {
				final String s = JsonUtils.getString(p_192407_0_.get(i), "pattern[" + i + "]");

				if (s.length() > 3) {
					throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
				}

				if (i > 0 && astring[0].length() != s.length()) {
					throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
				}

				astring[i] = s;
			}

			return astring;
		}
	}

	private static Map<String, Ingredient> deserializeKey(final JsonObject p_192408_0_) {
		final Map<String, Ingredient> map = Maps.<String, Ingredient>newHashMap();

		for (final Entry<String, JsonElement> entry : p_192408_0_.entrySet()) {
			if (entry.getKey().length() != 1) {
				throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}

			if (" ".equals(entry.getKey())) {
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}

			map.put(entry.getKey(), deserializeIngredient(entry.getValue()));
		}

		map.put(" ", Ingredient.EMPTY);
		return map;
	}

	public static Ingredient deserializeIngredient(@Nullable final JsonElement p_193361_0_) {
		if (p_193361_0_ != null && !p_193361_0_.isJsonNull()) {
			if (p_193361_0_.isJsonObject()) {
				return Ingredient.fromStacks(deserializeItem(p_193361_0_.getAsJsonObject(), false));
			}
			else if (!p_193361_0_.isJsonArray()) {
				throw new JsonSyntaxException("Expected item to be object or array of objects");
			}
			else {
				final JsonArray jsonarray = p_193361_0_.getAsJsonArray();

				if (jsonarray.size() == 0) {
					throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
				}
				else {
					final ItemStack[] aitemstack = new ItemStack[jsonarray.size()];

					for (int i = 0; i < jsonarray.size(); ++i) {
						aitemstack[i] = deserializeItem(JsonUtils.getJsonObject(jsonarray.get(i), "item"), false);
					}

					return Ingredient.fromStacks(aitemstack);
				}
			}
		}
		else {
			throw new JsonSyntaxException("Item cannot be null");
		}
	}

	public static ItemStack deserializeItem(final JsonObject p_192405_0_, final boolean useCount) {
		final String s = JsonUtils.getString(p_192405_0_, "item");
		final Item item = Item.REGISTRY.getObject(new ResourceLocation(s));

		if (item == null) {
			throw new JsonSyntaxException("Unknown item '" + s + "'");
		}
		else if (item.getHasSubtypes() && !p_192405_0_.has("data")) {
			throw new JsonParseException("Missing data for item '" + s + "'");
		}
		else {
			final int i = JsonUtils.getInt(p_192405_0_, "data", 0);
			final int j = useCount ? JsonUtils.getInt(p_192405_0_, "count", 1) : 1;
			return new ItemStack(item, j, i);
		}
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