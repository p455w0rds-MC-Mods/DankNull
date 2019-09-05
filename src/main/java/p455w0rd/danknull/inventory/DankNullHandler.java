package p455w0rd.danknull.inventory;

import java.util.*;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.ItemUtils;

/**
 * @author BrockWS
 */
public class DankNullHandler implements IDankNullHandler {

	private final ModGlobals.DankNullTier tier;
	private final NonNullList<ItemStack> stacks;
	private final Map<ItemStack, Boolean> oreStacks;
	private final Map<ItemStack, DankNullUtils.ItemExtractionMode> extractionStacks;
	private final Map<ItemStack, DankNullUtils.ItemPlacementMode> placementStacks;
	private int selected;
	private boolean isLocked;
	private String uuid;

	public DankNullHandler(final ModGlobals.DankNullTier tier) {
		this.tier = tier;
		stacks = NonNullList.withSize(this.tier.getNumRows() * 9, ItemStack.EMPTY);
		oreStacks = new HashMap<>();
		extractionStacks = new HashMap<>();
		placementStacks = new HashMap<>();
		selected = -1;
		isLocked = false;
		uuid = UUID.randomUUID().toString();
	}

	@Nonnull
	@Override
	public ItemStack getExtractableStackInSlot(final int slot) {
		validateSlot(slot);
		ItemStack slotStack = getStackList().get(slot).copy();
		if (!slotStack.isEmpty()) {
			if (getExtractionMode(slotStack) == DankNullUtils.ItemExtractionMode.KEEP_NONE) {
				slotStack = ItemStack.EMPTY;
			}
			else {
				final int amountToBeKept = getExtractionMode(slotStack).getNumberToKeep();
				if (slotStack.getCount() > amountToBeKept) {
					final ItemStack availableStack = slotStack.copy();
					availableStack.setCount(slotStack.getCount() - amountToBeKept);
					return availableStack;
				}
			}
		}
		return slotStack;
	}

	@Override
	public ItemStack getFullStackInSlot(final int slot) {
		validateSlot(slot);
		return getStackList().get(slot);
	}

	@Nonnull
	@Override
	public ItemStack getRenderableStackForSlot(final int slot) {
		final ItemStack visualStack = getStackInSlot(slot).copy();
		if (!visualStack.isEmpty()) {
			visualStack.setCount(1);
		}
		return visualStack;
	}

	@Override
	public void setStackInSlot(final int slot, @Nonnull final ItemStack stack) {
		validateSlot(slot);
		getStackList().set(slot, stack);
		onContentsChanged(slot);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, final boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		stack = convertOreDict(stack);

		slot = -1;
		// Check for existing stacks
		for (int i = 0; i < getStackList().size(); i++) {
			if (getStackList().get(i).isEmpty() || !ItemHandlerHelper.canItemStacksStack(stack, getStackList().get(i))) {
				continue;
			}
			slot = i;
			break;
		}
		if (slot == -1) { // Before checking for an empty slot
			for (int i = 0; i < getStackList().size(); i++) {
				if (!getStackList().get(i).isEmpty()) {
					continue;
				}
				slot = i;
				break;
			}
		}
		if (slot < 0 || slot >= getSlots()) {
			return stack;
		}

		final ItemStack existing = getStackList().get(slot);
		if (existing.isEmpty() || !ItemHandlerHelper.canItemStacksStack(stack, existing)) {
			return stack;
		}

		if (!simulate) {
			if (existing.isEmpty()) {
				getStackList().set(slot, stack.copy());
			}
			else {
				existing.grow(stack.getCount());
			}
			final ItemStack slotStack = getStackList().get(slot);
			if (slotStack.getCount() > getSlotLimit(slot)) {
				slotStack.setCount(getSlotLimit(slot));
			}
			onContentsChanged(slot);
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean isOreDictFiltered(final ItemStack stack) {
		for (final ItemStack storedStack : getStackList()) {
			if (!storedStack.isEmpty() && oreMatches(storedStack, stack)) {
				return true;
			}
		}
		return false;
	}

	private ItemStack convertOreDict(final ItemStack incomingStack) {
		for (final ItemStack storedStack : getStackList()) {
			if (isOre(storedStack) && !isOre(incomingStack) && oreMatches(storedStack, incomingStack)) {
				final ItemStack newStack = storedStack.copy();
				newStack.setCount(incomingStack.getCount());
				return newStack;
			}
		}
		return incomingStack;
	}

	private boolean oreMatches(final ItemStack storedStack, final ItemStack incomingStack) {
		final List<String> oreNamesForStoredStack = getOreNames(storedStack);
		final List<String> oreNamesForIncomingStack = getOreNames(incomingStack);
		for (final String currentStoredName : oreNamesForStoredStack) {
			for (final String currentIncomingName : oreNamesForIncomingStack) {
				if (currentIncomingName.equals(currentStoredName)) {
					return true;
				}
			}
		}
		return false;
	}

	private List<String> getOreNames(final ItemStack stack) {
		final int[] oreIds = OreDictionary.getOreIDs(stack);
		if (oreIds.length > 0) {
			final List<String> nameList = new ArrayList<>();
			for (final int oreId : oreIds) {
				final String name = OreDictionary.getOreName(oreId);
				if (!name.equals("Unknown")) {
					nameList.add(name);
				}
			}
			return nameList;
		}
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
		if (amount < 1) {
			return ItemStack.EMPTY;
		}
		validateSlot(slot);

		final ItemStack existing = getStackList().get(slot);
		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}

		final int existingCount = existing.getCount();
		final int extract = Math.min(amount, existing.getMaxStackSize());
		if (existingCount <= extract) {
			if (!simulate) {
				getStackList().set(slot, ItemStack.EMPTY);
				onContentsChanged(slot);
			}
			return existing;
		}
		else {
			if (!simulate) {
				getStackList().set(slot, ItemHandlerHelper.copyStackWithSize(existing, existingCount - extract));
				onContentsChanged(slot);
			}
			return ItemHandlerHelper.copyStackWithSize(existing, extract);
		}
	}

	@Override
	public boolean containsItemStack(@Nonnull final ItemStack stack) {
		return findItemStack(stack) > -1;
	}

	@Override
	public int findItemStack(@Nonnull final ItemStack stack) {
		for (int i = 0; i < getStackList().size(); i++) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(getStackList().get(i), stack)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int stackCount() {
		int count = 0;
		for (final ItemStack stack : getStackList()) {
			if (!stack.isEmpty()) {
				count++;
			}
		}
		return count;
	}

	@Nonnull
	@Override
	public ModGlobals.DankNullTier getTier() {
		return tier;
	}

	@Override
	public void setSelected(final int slot) {
		selected = slot;
		onSettingsChanged();
	}

	@Override
	public int getSelected() {
		return selected;
	}

	@Override
	public void cycleSelected(final boolean forward) {
		final int current = getSelected();
		final int stackCount = stackCount();
		int newIndex = 0;
		if (stackCount > 1) {
			if (forward) {
				if (current != stackCount - 1) {
					newIndex = current + 1;
				}
			}
			else {
				if (current == 0) {
					newIndex = stackCount - 1;
				}
				else {
					newIndex = current - 1;
				}
			}
		}
		if (current != newIndex) {
			setSelected(newIndex);
		}
	}

	@Override
	public void setLocked(final boolean lock) {
		isLocked = lock;
		onSettingsChanged();
	}

	@Override
	public boolean isLocked() {
		return isLocked;
	}

	@Override
	public boolean isLockingSupported() {
		return tier.isCreative();
	}

	@Override
	public void setUUID(@Nonnull final String uuid) {
		this.uuid = uuid;
	}

	@Nonnull
	@Override
	public String getUUID() {
		return uuid;
	}

	@Override
	public void setOre(@Nonnull ItemStack stack, final boolean ore) {
		if (stack.isEmpty()) {
			return;
		}
		for (final ItemStack currentStack : oreStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				oreStacks.put(currentStack, ore);
				onSettingsChanged();
				return;
			}
		}
		stack = stack.copy();
		stack.setCount(1);
		oreStacks.put(stack, ore);
		onSettingsChanged();
	}

	@Override
	public boolean isOre(@Nonnull final ItemStack stack) {
		for (final ItemStack currentStack : oreStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				return oreStacks.get(currentStack);
			}
		}
		return false;
	}

	@Override
	public boolean isOreSupported(@Nonnull final ItemStack stack) {
		return OreDictionary.getOreIDs(stack).length > 0;
	}

	@Nonnull
	@Override
	public Map<ItemStack, Boolean> getOres() {
		return oreStacks;
	}

	@Override
	public void setExtractionMode(@Nonnull ItemStack stack, @Nonnull final DankNullUtils.ItemExtractionMode mode) {
		if (stack.isEmpty()) {
			return;
		}
		for (final ItemStack currentStack : extractionStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				extractionStacks.put(currentStack, mode);
				onSettingsChanged();
				return;
			}
		}
		stack = stack.copy();
		stack.setCount(1);
		extractionStacks.put(stack, mode);
		onSettingsChanged();
	}

	@Override
	public void cycleExtractionMode(@Nonnull final ItemStack stack, final boolean forward) {
		DankNullUtils.ItemExtractionMode current = getExtractionMode(stack);
		final DankNullUtils.ItemExtractionMode[] values = DankNullUtils.ItemExtractionMode.values();
		if (forward) {
			if (current.ordinal() == values.length - 1) {
				current = values[0];
			}
			else {
				current = values[current.ordinal() + 1];
			}
		}
		else {
			if (current.ordinal() == 0) {
				current = values[values.length - 1];
			}
			else {
				current = values[current.ordinal() - 1];
			}
		}
		setExtractionMode(stack, current);
	}

	@Nonnull
	@Override
	public DankNullUtils.ItemExtractionMode getExtractionMode(@Nonnull final ItemStack stack) {
		for (final ItemStack currentStack : extractionStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				return extractionStacks.get(currentStack);
			}
		}
		return DankNullUtils.ItemExtractionMode.KEEP_ALL;
	}

	@Nonnull
	@Override
	public Map<ItemStack, DankNullUtils.ItemExtractionMode> getExtractionModes() {
		return extractionStacks;
	}

	@Override
	public void setPlacementMode(@Nonnull ItemStack stack, @Nonnull final DankNullUtils.ItemPlacementMode mode) {
		if (stack.isEmpty()) {
			return;
		}
		for (final ItemStack currentStack : placementStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				placementStacks.put(currentStack, mode);
				onSettingsChanged();
				return;
			}
		}
		stack = stack.copy();
		stack.setCount(1);
		placementStacks.put(stack, mode);
		onSettingsChanged();
	}

	@Override
	public void cyclePlacementMode(@Nonnull final ItemStack stack, final boolean forward) {
		DankNullUtils.ItemPlacementMode current = this.getPlacementMode(stack);
		final DankNullUtils.ItemPlacementMode[] values = DankNullUtils.ItemPlacementMode.values();
		if (forward) {
			if (current.ordinal() == values.length - 1) {
				current = values[0];
			}
			else {
				current = values[current.ordinal() + 1];
			}
		}
		else {
			if (current.ordinal() == 0) {
				current = values[values.length - 1];
			}
			else {
				current = values[current.ordinal() - 1];
			}
		}
		setPlacementMode(stack, current);
	}

	@Nonnull
	@Override
	public DankNullUtils.ItemPlacementMode getPlacementMode(@Nonnull final ItemStack stack) {
		for (final ItemStack currentStack : placementStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				return placementStacks.get(currentStack);
			}
		}
		return DankNullUtils.ItemPlacementMode.KEEP_1;
	}

	@Override
	public Map<ItemStack, DankNullUtils.ItemPlacementMode> getPlacementMode() {
		return placementStacks;
	}

	@Override
	public int getSlots() {
		return getStackList().size();
	}

	@Override
	public NonNullList<ItemStack> getStackList() {
		return stacks;
	}

	@Override
	public int getSlotLimit(final int slot) {
		return tier.getMaxStackSize();
	}

	@Override
	public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
		return !(stack.getItem() instanceof ItemDankNull); // Maybe consider and return based on the current inventory
	}

	public void sort() {
		getStackList().sort((o1, o2) -> {
			if (o1.equals(o2)) {
				return 0;
			}
			if (o1.isEmpty()) {
				return 1;
			}
			if (o2.isEmpty()) {
				return -1;
			}
			return 0;
		});
	}

	public void updateSelectedSlot() {
		final int selected = getSelected();
		if (selected >= 0 && !getStackInSlot(selected).isEmpty()) {
			return;
		}
		int newSelected = -1;
		if (selected > 0) {
			for (int i = selected; i > -1; i--) {
				if (getStackInSlot(i).isEmpty()) {
					continue;
				}
				newSelected = i;
				break;
			}
		}
		else if (!getStackInSlot(0).isEmpty()) {
			newSelected = 0;
		}

		setSelected(newSelected);
	}

	protected void onContentsChanged(final int slot) {
		sort();
		updateSelectedSlot();
	}

	protected void onSettingsChanged() {
	}

}
