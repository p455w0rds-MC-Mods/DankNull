package p455w0rd.danknull.util.cap;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandlerModifiable;

import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * Capability that is attached to every DankNull
 *
 * @author BrockWS
 */
public interface IDankNullHandler extends IItemHandlerModifiable {

	/**
	 * Checks if the given ItemStack is contained in the inventory
	 *
	 * @param stack Stack to check
	 * @return true if the stack is found within the inventory
	 */
	boolean containsItemStack(@Nonnull ItemStack stack);

	/**
	 * Searches the inventory for the given stack
	 * Ignores stack size
	 *
	 * @param stack Stack to search for
	 * @return Slot the Stack is in
	 */
	int findItemStack(@Nonnull ItemStack stack);

	/**
	 * Calculates the amount of stacks in the inventory
	 *
	 * @return Amount of stacks in the inventory
	 */
	int stackCount();

	/**
	 * Gets the Tier of DankNull
	 *
	 * @return Dank Null Tier
	 */
	@Nonnull
	ModGlobals.DankNullTier getTier();

	/**
	 * Sets the selected stack
	 *
	 * @param slot Selected index
	 */
	void setSelected(int slot);

	/**
	 * Returns the selected index
	 *
	 * @return Selected Index
	 */
	int getSelected();

	/**
	 * Cycles to the next (or previous) index
	 *
	 * @param forward Cycle forwards if true
	 */
	void cycleSelected(boolean forward);

	/**
	 * Sets if the DankNull should be locked
	 * Used for Creative DankNull
	 *
	 * @param lock True if DankNull should be locked
	 */
	void setLocked(boolean lock);

	/**
	 * Returns if the DankNull is locked
	 * Used for Creative DankNull
	 *
	 * @return True if DankNull is Locked
	 */
	boolean isLocked();

	/**
	 * Returns if the DankNull can be locked
	 * Used for Creative DankNull
	 *
	 * @return True if the DankNull can be locked
	 */
	boolean isLockingSupported();

	/**
	 * Sets the UUID for syncing
	 *
	 * @param uuid UUID
	 */
	void setUUID(@Nonnull String uuid);

	/**
	 * Gets the UUID for syncing
	 *
	 * @return UUID
	 */
	@Nonnull
	String getUUID();

	/**
	 * Sets the ore mode for the given stack
	 *
	 * @param stack Stack to set the mode for
	 * @param ore   Mode
	 */
	void setOre(@Nonnull ItemStack stack, boolean ore);

	/**
	 * Gets the ore mode for the stack
	 *
	 * @param stack Stack to check
	 * @return True if oredict is enabled
	 */
	boolean isOre(@Nonnull ItemStack stack);

	/**
	 * Checks if the oredict is supported and enabled for the given stack
	 *
	 * @param stack Stack
	 * @return True if oredict is supported and enabled
	 */
	boolean isOreSupported(@Nonnull ItemStack stack);

	/**
	 * Get the ItemStack Ore Map
	 * Shouldn't be modified
	 *
	 * @return ItemStack Ore Map
	 */
	@Nonnull
	Map<ItemStack, Boolean> getOres();

	/**
	 * Sets the extraction mode for the given ItemStack
	 *
	 * @param stack Stack
	 * @param mode  Mode
	 */
	void setExtractionMode(@Nonnull ItemStack stack, @Nonnull DankNullUtils.ItemExtractionMode mode);

	/**
	 * Cycles the Extraction Mode for the given Stack
	 *
	 * @param stack   Stack
	 * @param forward Cycle forwards if true
	 */
	void cycleExtractionMode(@Nonnull ItemStack stack, boolean forward);

	/**
	 * Gets the Extraction Mode for the given Stack
	 *
	 * @param stack Stack
	 * @return Extraction Mode
	 */
	@Nonnull
	DankNullUtils.ItemExtractionMode getExtractionMode(@Nonnull ItemStack stack);

	/**
	 * Get the ItemStack ExtractionMode Map
	 * Shouldn't be modified
	 *
	 * @return ItemStack ExtractionMode Map
	 */
	@Nonnull
	Map<ItemStack, DankNullUtils.ItemExtractionMode> getExtractionModes();

	/**
	 * Sets the Placement Mode for the given Stack
	 *
	 * @param stack Stack
	 * @param mode  Placement Mode
	 */
	void setPlacementMode(@Nonnull ItemStack stack, @Nonnull DankNullUtils.ItemPlacementMode mode);

	/**
	 * Cycles the Placement Mode for the given stack
	 *
	 * @param stack   Stack
	 * @param forward Cycle forwards if true
	 */
	void cyclePlacementMode(@Nonnull ItemStack stack, boolean forward);

	/**
	 * Gets the Placement Mode for the given Stack
	 *
	 * @param stack Stack
	 * @return Placement Mode
	 */
	@Nonnull
	DankNullUtils.ItemPlacementMode getPlacementMode(@Nonnull ItemStack stack);

	/**
	 * Gets the ItemStack PlacementMode Map
	 * Shouldn't be modified
	 *
	 * @return ItemStack PlacementMode Map
	 */
	Map<ItemStack, DankNullUtils.ItemPlacementMode> getPlacementMode();
}
