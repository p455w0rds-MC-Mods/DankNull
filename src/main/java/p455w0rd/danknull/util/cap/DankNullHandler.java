package p455w0rd.danknull.util.cap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.ItemUtils;

/**
 * @author BrockWS
 */
public class DankNullHandler implements IDankNullHandler {

	private ModGlobals.DankNullTier tier;
	private NonNullList<ItemStack> stacks;
	private Map<ItemStack, Boolean> oreStacks;
	private Map<ItemStack, DankNullUtils.ItemExtractionMode> extractionStacks;
	private Map<ItemStack, DankNullUtils.ItemPlacementMode> placementStacks;
	private int selected;
	private boolean isLocked;
	private String uuid;

	public DankNullHandler(ModGlobals.DankNullTier tier) {
		this.tier = tier;
		this.stacks = NonNullList.withSize(this.tier.getNumRows() * 9, ItemStack.EMPTY);
		this.oreStacks = new HashMap<>();
		this.extractionStacks = new HashMap<>();
		this.placementStacks = new HashMap<>();
		this.selected = -1;
		this.isLocked = false;
		this.uuid = UUID.randomUUID().toString();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		this.validateSlot(slot);
		return this.stacks.get(slot);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		this.validateSlot(slot);
		this.stacks.set(slot, stack);
		this.onContentsChanged(slot);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		// TODO Implement OreDict stuff

		slot = -1;
		// Check for existing stacks
		for (int i = 0; i < this.stacks.size(); i++) {
			if (this.stacks.get(i).isEmpty() || !ItemHandlerHelper.canItemStacksStack(stack, this.stacks.get(i)))
				continue;
			slot = i;
			break;
		}
		if (slot == -1) { // Before checking for an empty slot
			for (int i = 0; i < this.stacks.size(); i++) {
				if (!this.stacks.get(i).isEmpty())
					continue;
				slot = i;
				break;
			}
		}
		if (slot < 0 || slot >= this.getSlots())
			return stack;

		ItemStack existing = this.stacks.get(slot);
		int limit = this.getSlotLimit(slot);

		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;

			limit -= existing.getCount();
		}

		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate) {
			if (existing.isEmpty()) {
				this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
			onContentsChanged(slot);
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount < 1)
			return ItemStack.EMPTY;
		this.validateSlot(slot);

		// TODO Implement ExtractionMode stuff

		ItemStack existing = this.stacks.get(slot);
		if (existing.isEmpty())
			return ItemStack.EMPTY;

		int existingCount = existing.getCount();
		int extract = Math.min(amount, existing.getMaxStackSize());
		if (existingCount <= extract) {
			if (!simulate) {
				this.stacks.set(slot, ItemStack.EMPTY);
				this.onContentsChanged(slot);
			}
			return existing;
		} else {
			if (!simulate) {
				this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existingCount - extract));
				this.onContentsChanged(slot);
			}
			return ItemHandlerHelper.copyStackWithSize(existing, extract);
		}
	}

	@Override
	public boolean containsItemStack(@Nonnull ItemStack stack) {
		return this.findItemStack(stack) > -1;
	}

	@Override
	public int findItemStack(@Nonnull ItemStack stack) {
		for (int i = 0; i < this.stacks.size(); i++)
			if (ItemUtils.areItemStacksEqualIgnoreSize(this.stacks.get(i), stack))
				return i;
		return -1;
	}

	@Override
	public int stackCount() {
		int count = 0;
		for (ItemStack stack : this.stacks)
			if (!stack.isEmpty())
				count++;
		return count;
	}

	@Nonnull
	@Override
	public ModGlobals.DankNullTier getTier() {
		return this.tier;
	}

	@Override
	public void setSelected(int slot) {
		this.selected = slot;
		this.onSettingsChanged();
	}

	@Override
	public int getSelected() {
		return this.selected;
	}

	@Override
	public void cycleSelected(boolean forward) {
		int current = this.getSelected();
		int stackCount = this.stackCount();
		int newIndex = 0;
		if (stackCount > 1) {
			if (forward) {
				if (current != stackCount - 1) {
					newIndex++;
				}
			} else {
				if (current == 0) {
					newIndex = stackCount - 1;
				} else {
					newIndex = current - 1;
				}
			}
		}
		if (current != newIndex)
			this.setSelected(newIndex);
	}

	@Override
	public void setLocked(boolean lock) {
		this.isLocked = lock;
		this.onSettingsChanged();
	}

	@Override
	public boolean isLocked() {
		return this.isLocked;
	}

	@Override
	public boolean isLockingSupported() {
		return this.tier.isCreative();
	}

	@Override
	public void setUUID(@Nonnull String uuid) {
		this.uuid = uuid;
	}

	@Nonnull
	@Override
	public String getUUID() {
		return this.uuid;
	}

	@Override
	public void setOre(@Nonnull ItemStack stack, boolean ore) {
		if (stack.isEmpty())
			return;
		for (ItemStack currentStack : this.oreStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				this.oreStacks.put(currentStack, ore);
				this.onSettingsChanged();
				return;
			}
		}
		stack = stack.copy();
		stack.setCount(1);
		this.oreStacks.put(stack, ore);
		this.onSettingsChanged();
	}

	@Override
	public boolean isOre(@Nonnull ItemStack stack) {
		for (ItemStack currentStack : this.oreStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				return this.oreStacks.get(currentStack);
			}
		}
		return false;
	}

	@Override
	public boolean isOreSupported(@Nonnull ItemStack stack) {
		return OreDictionary.getOreIDs(stack).length > 0;
	}

	@Nonnull
	@Override
	public Map<ItemStack, Boolean> getOres() {
		return this.oreStacks;
	}

	@Override
	public void setExtractionMode(@Nonnull ItemStack stack, @Nonnull DankNullUtils.ItemExtractionMode mode) {
		if (stack.isEmpty())
			return;
		for (ItemStack currentStack : this.extractionStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				this.extractionStacks.put(currentStack, mode);
				this.onSettingsChanged();
				return;
			}
		}
		stack = stack.copy();
		stack.setCount(1);
		this.extractionStacks.put(stack, mode);
		this.onSettingsChanged();
	}

	@Override
	public void cycleExtractionMode(@Nonnull ItemStack stack, boolean forward) {
		DankNullUtils.ItemExtractionMode current = this.getExtractionMode(stack);
		DankNullUtils.ItemExtractionMode[] values = DankNullUtils.ItemExtractionMode.values();
		if (forward) {
			if (current.ordinal() == values.length - 1) {
				current = values[0];
			} else {
				current = values[current.ordinal() + 1];
			}
		} else {
			if (current.ordinal() == 0) {
				current = values[values.length - 1];
			} else {
				current = values[current.ordinal() - 1];
			}
		}
		this.setExtractionMode(stack, current);
	}

	@Nonnull
	@Override
	public DankNullUtils.ItemExtractionMode getExtractionMode(@Nonnull ItemStack stack) {
		for (ItemStack currentStack : this.extractionStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				return this.extractionStacks.get(currentStack);
			}
		}
		return DankNullUtils.ItemExtractionMode.KEEP_ALL;
	}

	@Nonnull
	@Override
	public Map<ItemStack, DankNullUtils.ItemExtractionMode> getExtractionModes() {
		return this.extractionStacks;
	}

	@Override
	public void setPlacementMode(@Nonnull ItemStack stack, @Nonnull DankNullUtils.ItemPlacementMode mode) {
		if (stack.isEmpty())
			return;
		for (ItemStack currentStack : this.placementStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				this.placementStacks.put(currentStack, mode);
				this.onSettingsChanged();
				return;
			}
		}
		stack = stack.copy();
		stack.setCount(1);
		this.placementStacks.put(stack, mode);
		this.onSettingsChanged();
	}

	@Override
	public void cyclePlacementMode(@Nonnull ItemStack stack, boolean forward) {
		DankNullUtils.ItemPlacementMode current = this.getPlacementMode(stack);
		DankNullUtils.ItemPlacementMode[] values = DankNullUtils.ItemPlacementMode.values();
		if (forward) {
			if (current.ordinal() == values.length - 1) {
				current = values[0];
			} else {
				current = values[current.ordinal() + 1];
			}
		} else {
			if (current.ordinal() == 0) {
				current = values[values.length - 1];
			} else {
				current = values[current.ordinal() - 1];
			}
		}
		this.setPlacementMode(stack, current);
	}

	@Nonnull
	@Override
	public DankNullUtils.ItemPlacementMode getPlacementMode(@Nonnull ItemStack stack) {
		for (ItemStack currentStack : this.placementStacks.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
				return this.placementStacks.get(currentStack);
			}
		}
		return DankNullUtils.ItemPlacementMode.KEEP_1;
	}

	@Override
	public Map<ItemStack, DankNullUtils.ItemPlacementMode> getPlacementMode() {
		return this.placementStacks;
	}

	@Override
	public int getSlots() {
		return this.stacks.size();
	}

	@Override
	public int getSlotLimit(int slot) {
		return this.tier.getMaxStackSize();
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return !(stack.getItem() instanceof ItemDankNull);
	}

	public void sort() {
		this.stacks.sort((o1, o2) -> {
			if (o1.equals(o2))
				return 0;
			if (o1.isEmpty())
				return 1;
			if (o2.isEmpty())
				return -1;
			return 0;
		});
	}

	public void updateSelectedSlot() {
		int selected = this.getSelected();
		if (selected >= 0 && !this.getStackInSlot(selected).isEmpty())
			return;
		int newSelected = -1;
		if (selected > 0) {
			for (int i = selected; i > -1; i--) {
				if (this.getStackInSlot(i).isEmpty())
					continue;
				newSelected = i;
				break;
			}
		} else if (!this.getStackInSlot(0).isEmpty()) {
			newSelected = 0;
		}

		this.setSelected(newSelected);
	}

	protected void onContentsChanged(int slot) {
		this.sort();
		this.updateSelectedSlot();
	}

	protected void onSettingsChanged() {
	}

	private void validateSlot(int slot) {
		if (slot < 0 || slot >= this.getSlots())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
	}
}
