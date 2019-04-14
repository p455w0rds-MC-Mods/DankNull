package p455w0rd.danknull.inventory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rd.danknull.util.DankNullUtils.SlotExtractionMode;
import p455w0rd.danknull.util.DankNullUtils.SlotPlacementMode;

/**
 * @author p455w0rd
 *
 */
public class InventoryDankNull implements IInventory, INBTSerializable<NBTTagCompound> {

	public static final String INVENTORY_NAME = "danknull-inventory";

	private final NonNullList<ItemStack> inventory;
	private final ItemStack dankNull;
	int selectedIndex = -1;
	boolean isLocked = false;

	// For the following, we reference ItemStacks so that the actual stack can be fully removed, but the /dank/null will retain its setting

	// Stack, Use Ore Dictionary
	private Map<ItemStack, Boolean> oreDictModes = new HashMap<>();
	// Stack, Mode
	private Map<ItemStack, SlotExtractionMode> extractionModes = new HashMap<>();
	// Stack, Mode
	private Map<ItemStack, SlotPlacementMode> placementModes = new HashMap<>();

	public InventoryDankNull(final ItemStack dankNull) {
		this.dankNull = dankNull;
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
		deserializeNBT(dankNull.getTagCompound());
	}

	@Override
	public String getName() {
		return INVENTORY_NAME;
	}

	public NonNullList<ItemStack> getStacks() {
		return inventory;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	public void setSelectedIndex(final int index) {
		selectedIndex = index;
		markDirty();
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	// Extraction Mode
	public Map<ItemStack, SlotExtractionMode> getExtractionModes() {
		return extractionModes;
	}

	public SlotExtractionMode getExtractionMode(@Nonnull final ItemStack stack) {
		for (final Map.Entry<ItemStack, SlotExtractionMode> entry : getExtractionModes().entrySet()) {
			if (DankNullUtils.areStacksEqual(entry.getKey(), stack)) {
				return entry.getValue();
			}
		}
		setExtractionMode(stack, SlotExtractionMode.KEEP_1);
		return SlotExtractionMode.KEEP_1;
	}

	public void setExtractionModes(final Map<ItemStack, SlotExtractionMode> modes) {
		extractionModes = modes;
		markDirty();
	}

	public void setExtractionMode(final ItemStack stack, final SlotExtractionMode mode) {
		ItemStack storedStack = stack.copy();
		for (final Map.Entry<ItemStack, SlotExtractionMode> entry : extractionModes.entrySet()) {
			if (DankNullUtils.areStacksEqual(stack, entry.getKey())) {
				storedStack = entry.getKey();
			}
		}
		storedStack.setCount(1);
		extractionModes.put(storedStack, mode);
		markDirty();
	}

	// Placement Mode
	public Map<ItemStack, SlotPlacementMode> getPlacementModes() {
		return placementModes;
	}

	public SlotPlacementMode getPlacementMode(@Nonnull final ItemStack stack) {
		for (final Map.Entry<ItemStack, SlotPlacementMode> entry : getPlacementModes().entrySet()) {
			if (DankNullUtils.areStacksEqual(entry.getKey(), stack)) {
				return entry.getValue();
			}
		}
		setPlacementMode(stack, SlotPlacementMode.KEEP_1);
		return SlotPlacementMode.KEEP_1;
	}

	public void setPlacementModes(final Map<ItemStack, SlotPlacementMode> modes) {
		placementModes = modes;
		markDirty();
	}

	public void setPlacementMode(final ItemStack stack, final SlotPlacementMode mode) {
		ItemStack storedStack = stack.copy();
		for (final Map.Entry<ItemStack, SlotPlacementMode> entry : placementModes.entrySet()) {
			if (DankNullUtils.areStacksEqual(stack, entry.getKey())) {
				storedStack = entry.getKey();
			}
		}
		storedStack.setCount(1);
		placementModes.put(storedStack, mode);
	}

	// OreDict Mode
	public Map<ItemStack, Boolean> getOreDictModes() {
		return oreDictModes;
	}

	public boolean getOreDictMode(@Nonnull final ItemStack stack) {
		for (final Map.Entry<ItemStack, Boolean> entry : getOreDictModes().entrySet()) {
			if (DankNullUtils.areStacksEqual(entry.getKey(), stack)) {
				return entry.getValue();
			}
		}
		return false;
	}

	public void setOreDictModes(final Map<ItemStack, Boolean> modes) {
		oreDictModes = modes;
		markDirty();
	}

	public void setOreDictMode(final ItemStack stack, final boolean mode) {
		ItemStack storedStack = stack.copy();
		for (final Map.Entry<ItemStack, Boolean> entry : oreDictModes.entrySet()) {
			if (DankNullUtils.areStacksEqual(stack, entry.getKey())) {
				storedStack = entry.getKey();
			}
		}
		storedStack.setCount(1);
		oreDictModes.put(storedStack, mode);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		final NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inventory.size(); i++) {
			final ItemStack currentStack = inventory.get(i);
			if (!currentStack.isEmpty()) {
				final NBTTagCompound slotTag = new NBTTagCompound();
				slotTag.setInteger(NBT.SLOT_KEY, i);
				currentStack.writeToNBT(slotTag);
				itemList.appendTag(slotTag);
			}
		}

		// OreDict Mode
		final NBTTagList oreDictList = new NBTTagList();
		for (final ItemStack stack : oreDictModes.keySet()) {
			final NBTTagCompound tempNBT = new NBTTagCompound();
			if (stack.getCount() != 1) {
				stack.setCount(1);
			}
			tempNBT.setTag(NBT.STACK_KEY, stack.serializeNBT());
			tempNBT.setBoolean(NBT.MODE_KEY, oreDictModes.get(stack));
			oreDictList.appendTag(tempNBT);
		}

		// Extraction Mode
		final NBTTagList extractionList = new NBTTagList();
		for (final ItemStack stack : extractionModes.keySet()) {
			final NBTTagCompound tempNBT = new NBTTagCompound();
			if (stack.getCount() != 1) {
				stack.setCount(1);
			}
			tempNBT.setTag(NBT.STACK_KEY, stack.serializeNBT());
			tempNBT.setInteger(NBT.MODE_KEY, extractionModes.get(stack).ordinal());
			extractionList.appendTag(tempNBT);
		}

		// Placement Mode
		final NBTTagList placementList = new NBTTagList();
		for (final ItemStack stack : placementModes.keySet()) {
			final NBTTagCompound tempNBT = new NBTTagCompound();
			if (stack.getCount() != 1) {
				stack.setCount(1);
			}
			tempNBT.setTag(NBT.STACK_KEY, stack.serializeNBT());
			tempNBT.setInteger(NBT.MODE_KEY, placementModes.get(stack).ordinal());
			extractionList.appendTag(tempNBT);
		}

		final NBTTagCompound invNBT = new NBTTagCompound();
		invNBT.setTag(NBT.ITEMS_KEY, itemList);
		invNBT.setInteger(NBT.SELECTEDINDEX_KEY, selectedIndex);
		invNBT.setBoolean(NBT.LOCKED_KEY, isLocked);
		invNBT.setTag(NBT.OREDICT_MODES_KEY, oreDictList);
		invNBT.setTag(NBT.EXTRACTION_MODES_KEY, extractionList);
		invNBT.setTag(NBT.PLACEMENT_MODES_KEY, placementList);
		final NBTTagCompound itemHandlerNBT = new NBTTagCompound();
		itemHandlerNBT.setTag(getName(), invNBT);
		return itemHandlerNBT;
	}

	@Override
	public void deserializeNBT(final NBTTagCompound compound) {
		// we generally package everything under the getName() subTag, but allow for sending of individual parts (like ext mode) for syncing
		final NBTTagCompound nbt = compound.hasKey(getName(), Constants.NBT.TAG_COMPOUND) ? compound.getCompoundTag(getName()) : compound;
		if (nbt != null) {
			final NBTTagList nbtTL = nbt.hasKey(NBT.ITEMS_KEY, Constants.NBT.TAG_LIST) ? nbt.getTagList(NBT.ITEMS_KEY, Constants.NBT.TAG_COMPOUND) : new NBTTagList();
			for (int i = 0; i < nbtTL.tagCount(); i++) {
				final NBTTagCompound nbtTC = nbtTL.getCompoundTagAt(i);
				if (nbtTC != null) {
					setInventorySlotContents(nbtTC.getInteger(NBT.SLOT_KEY), new ItemStack(nbtTC));
				}
			}

			if (nbt.hasKey(NBT.SELECTEDINDEX_KEY, Constants.NBT.TAG_INT)) {
				selectedIndex = nbt.getInteger(NBT.SELECTEDINDEX_KEY);
			}

			if (nbt.hasKey(NBT.LOCKED_KEY, Constants.NBT.TAG_BYTE)) {
				isLocked = nbt.getBoolean(NBT.LOCKED_KEY);
			}

			// OreDict Mode
			if (nbt.hasKey(NBT.OREDICT_MODES_KEY)) {
				final NBTTagList oreDictList = nbt.getTagList(NBT.OREDICT_MODES_KEY, Constants.NBT.TAG_COMPOUND);
				for (int i = 0; i < oreDictList.tagCount(); i++) {
					final NBTTagCompound tmpNBT = oreDictList.getCompoundTagAt(i);
					setOreDictMode(new ItemStack(tmpNBT.getCompoundTag(NBT.STACK_KEY)), tmpNBT.getBoolean(NBT.MODE_KEY));
				}
			}

			// Extraction Mode
			if (nbt.hasKey(NBT.EXTRACTION_MODES_KEY)) {
				final NBTTagList extractionList = nbt.getTagList(NBT.EXTRACTION_MODES_KEY, Constants.NBT.TAG_COMPOUND);
				for (int i = 0; i < extractionList.tagCount(); i++) {
					final NBTTagCompound tempNBT = extractionList.getCompoundTagAt(i);
					setExtractionMode(new ItemStack(tempNBT.getCompoundTag(NBT.STACK_KEY)), SlotExtractionMode.values()[tempNBT.getInteger(NBT.MODE_KEY)]);
				}
			}

			// Placement Mode
			if (nbt.hasKey(NBT.PLACEMENT_MODES_KEY)) {
				final NBTTagList placementList = nbt.getTagList(NBT.PLACEMENT_MODES_KEY, Constants.NBT.TAG_COMPOUND);
				for (int i = 0; i < placementList.tagCount(); i++) {
					final NBTTagCompound tempNBT = placementList.getCompoundTagAt(i);
					setPlacementMode(new ItemStack(tempNBT.getCompoundTag(NBT.STACK_KEY)), SlotPlacementMode.values()[tempNBT.getInteger(NBT.MODE_KEY)]);
				}
			}
		}
	}

	@Override
	public int getSizeInventory() {
		int numRows = getDankNull().getItemDamage() + 1;
		if (DankNullUtils.isCreativeDankNull(getDankNull())) {
			numRows--;
		}
		return numRows * 9;
	}

	@Override
	public boolean isEmpty() {
		for (int x = 0; x < getSizeInventory(); x++) {
			if (!getStackInSlot(x).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(final int index) {
		return index >= 0 && index < inventory.size() ? (ItemStack) inventory.get(index) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(final int index, final int count) {
		final ItemStack itemstack = ItemStackHelper.getAndSplit(inventory, index, count);
		if (!itemstack.isEmpty()) {
			markDirty();
		}
		return itemstack;
	}

	@Override
	public ItemStack removeStackFromSlot(final int index) {
		final ItemStack itemstack = inventory.get(index);
		if (itemstack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		else {
			inventory.set(index, ItemStack.EMPTY);
			return itemstack;
		}
	}

	@Override
	public void setInventorySlotContents(final int index, final ItemStack stack) {
		inventory.set(index, stack);
		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
			stack.setCount(getInventoryStackLimit());
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return DankNullUtils.getDankNullMaxStackSize(getDankNull());
	}

	public ItemStack getDankNull() {
		return dankNull;
	}

	@Override
	public void markDirty() {
		if (getDankNull().isEmpty()) {
			return;
		}
		if (!getDankNull().hasTagCompound()) {
			getDankNull().setTagCompound(new NBTTagCompound());
		}
		getDankNull().setTagCompound(serializeNBT());
	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(final EntityPlayer player) {
	}

	@Override
	public void closeInventory(final EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(final int index, final ItemStack stack) {
		return true;
	}

	@Override
	public int getField(final int id) {
		return 0;
	}

	@Override
	public void setField(final int id, final int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inventory.clear();
	}

}