package p455w0rd.danknull.util;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Created by covers1624 on 6/30/2016.
 */
public class ItemNBTUtils {

	/**
	 * Checks if an ItemStack has an NBTTag.
	 *
	 * @param stack Stack to check.
	 * @return If tag is in existence.
	 */
	public static boolean hasTag(@Nonnull final ItemStack stack) {
		return stack.hasTagCompound();
	}

	/**
	 * Sets the tag on an ItemStack.
	 *
	 * @param stack       Stack to set tag on.
	 * @param tagCompound Tag to set on item.
	 */
	public static void setTag(@Nonnull final ItemStack stack, final NBTTagCompound tagCompound) {
		stack.setTagCompound(tagCompound);
	}

	/**
	 * Gets the NBTTag associated with an ItemStack.
	 *
	 * @param stack Stack to get tag from.
	 * @return Tag from the ItemStack.
	 */
	public static NBTTagCompound getTag(@Nonnull final ItemStack stack) {
		return stack.getTagCompound();
	}

	/**
	 * Checks if an NBTTag exists on an item and if not it will create a new one.
	 *
	 * @param stack Stack to check.
	 * @return The Tag on the item.
	 */
	public static NBTTagCompound validateTagExists(@Nonnull final ItemStack stack) {
		if (!hasTag(stack)) {
			setTag(stack, new NBTTagCompound());
		}
		return getTag(stack);
	}

	/**
	 * Checks if an ItemStack has an NBTTag on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to check.
	 * @param key   Key to check for.
	 * @return False if key does not exist or if the tag does not exist.
	 */
	public static boolean hasKey(@Nonnull final ItemStack stack, final String key) {
		return hasTag(stack) && getTag(stack).hasKey(key);
	}

	/**
	 * Checks if an ItemStack has an NBTTag of a specific type and name.
	 *
	 * @param stack   Stack to check.
	 * @param key     Key to check for.
	 * @param nbtType Primitive NBT Type.
	 * @return False if key does not exist or if the tag does not exist.
	 */
	public static boolean hasKey(@Nonnull final ItemStack stack, final String key, final int nbtType) {
		return hasTag(stack) && getTag(stack).hasKey(key, nbtType);
	}

	/**
	 * Removes a key from the ItemStacks NBTTag.
	 *
	 * @param stack Stack to edit.
	 * @param key   Key to remove.
	 */
	public static void removeTag(@Nonnull final ItemStack stack, final String key) {
		if (hasTag(stack)) {
			getTag(stack).removeTag(key);
		}
	}

	//region Setters

	/**
	 * Sets a byte on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to set.
	 * @param key   Key.
	 * @param b     Value.
	 */
	public static void setByte(@Nonnull final ItemStack stack, final String key, final byte b) {
		validateTagExists(stack);
		getTag(stack).setByte(key, b);
	}

	/**
	 * Sets a short on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to set.
	 * @param key   Key.
	 * @param s     Value.
	 */
	public static void setShort(@Nonnull final ItemStack stack, final String key, final short s) {
		validateTagExists(stack);
		getTag(stack).setShort(key, s);
	}

	/**
	 * Sets a int on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to set.
	 * @param key   Key.
	 * @param i     Value.
	 */
	public static void setInteger(@Nonnull final ItemStack stack, final String key, final int i) {
		validateTagExists(stack);
		getTag(stack).setInteger(key, i);
	}

	/**
	 * Sets a long on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to set.
	 * @param key   Key.
	 * @param l     Value.
	 */
	public static void setLong(@Nonnull final ItemStack stack, final String key, final long l) {
		validateTagExists(stack);
		getTag(stack).setLong(key, l);
	}

	/**
	 * Sets a UUID on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to set.
	 * @param key   Key.
	 * @param uuid  Value.
	 */
	public static void setUUID(@Nonnull final ItemStack stack, final String key, final UUID uuid) {
		validateTagExists(stack);
		getTag(stack).setUniqueId(key, uuid);
	}

	/**
	 * Sets a float on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to set.
	 * @param key   Key.
	 * @param f     Value.
	 */
	public static void setFloat(@Nonnull final ItemStack stack, final String key, final float f) {
		validateTagExists(stack);
		getTag(stack).setFloat(key, f);
	}

	/**
	 * Sets a double on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to set.
	 * @param key   Key.
	 * @param d     Value.
	 */
	public static void setDouble(@Nonnull final ItemStack stack, final String key, final double d) {
		validateTagExists(stack);
		getTag(stack).setDouble(key, d);
	}

	/**
	 * Sets a String on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to set.
	 * @param key   Key.
	 * @param s     Value.
	 */
	public static void setString(@Nonnull final ItemStack stack, final String key, final String s) {
		validateTagExists(stack);
		getTag(stack).setString(key, s);
	}

	/**
	 * Sets a byte array on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to set.
	 * @param key   Key.
	 * @param b     Value.
	 */
	public static void setByteArray(@Nonnull final ItemStack stack, final String key, final byte[] b) {
		validateTagExists(stack);
		getTag(stack).setByteArray(key, b);
	}

	/**
	 * Sets a int array on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to set.
	 * @param key   Key.
	 * @param i     Value.
	 */
	public static void setIntArray(@Nonnull final ItemStack stack, final String key, final int[] i) {
		validateTagExists(stack);
		getTag(stack).setIntArray(key, i);
	}

	/**
	 * Sets a boolean on an ItemStack with the specified Key and Value.
	 *
	 * @param stack Stack to set.
	 * @param key   Key.
	 * @param b     Value.
	 */
	public static void setBoolean(@Nonnull final ItemStack stack, final String key, final boolean b) {
		validateTagExists(stack);
		getTag(stack).setBoolean(key, b);
	}

	//endregion

	//region Getters

	/**
	 * Gets a byte from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static byte getByte(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getByte(key);
	}

	/**
	 * Gets a short from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static short getShort(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getShort(key);
	}

	/**
	 * Gets a int from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static int getInteger(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getInteger(key);
	}

	/**
	 * Gets a long from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static long getLong(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getLong(key);
	}

	/**
	 * Gets a UUID from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static UUID getUUID(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getUniqueId(key);
	}

	/**
	 * Gets a float from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static float getFloat(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getFloat(key);
	}

	/**
	 * Gets a double from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static double getDouble(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getDouble(key);
	}

	/**
	 * Gets a String from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static String getString(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getString(key);
	}

	/**
	 * Gets a byte array from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static byte[] getByteArray(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getByteArray(key);
	}

	/**
	 * Gets a int array from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static int[] getIntArray(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getIntArray(key);
	}

	/**
	 * Gets a boolean from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static boolean getBoolean(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getBoolean(key);
	}

	/**
	 * Gets a NBTTagCompound from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @return Value.
	 */
	public static NBTTagCompound getCompoundTag(@Nonnull final ItemStack stack, final String key) {
		validateTagExists(stack);
		return getTag(stack).getCompoundTag(key);
	}

	/**
	 * Gets a NBTTagList from an ItemStacks NBTTag.
	 *
	 * @param stack Stack key exists on.
	 * @param key   Key for the value.
	 * @param type  Primitive NBT Type the List should be made up of.
	 * @return Value.
	 */
	public static NBTTagList getTagList(@Nonnull final ItemStack stack, final String key, final int type) {
		validateTagExists(stack);
		return getTag(stack).getTagList(key, type);
	}

	//endregion

}
