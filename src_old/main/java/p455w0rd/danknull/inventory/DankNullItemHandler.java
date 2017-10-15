package p455w0rd.danknull.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock.ExtractionMode;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.ItemUtils;

/**
 * @author p455w0rd
 *
 */
public class DankNullItemHandler implements IItemHandlerModifiable, ICapabilitySerializable<NBTBase> {

	int maxSize = 0;
	ItemStack dankNull;
	TileDankNullDock dankDock;
	protected NonNullList<ItemStack> stacks;

	public DankNullItemHandler(@Nonnull ItemStack stack) {
		if (!stack.isEmpty()) {
			dankNull = stack;
		}
		stacks = NonNullList.withSize(54, ItemStack.EMPTY);
		//maxSize = stack.getItemDamage() + 1 == 6 ? Integer.MAX_VALUE : ((stack.getItemDamage() + 1) * 128) * (stack.getItemDamage() + 1);
	}

	public void setTile(TileDankNullDock dock) {
		dankDock = dock;
	}

	public TileDankNullDock getTile() {
		return dankDock;
	}

	public void setSize(int size) {
		stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		validateSlotIndex(slot);
		if (ItemStack.areItemStacksEqual(stacks.get(slot), stack)) {
			return;
		}
		stacks.set(slot, stack);
		onContentsChanged(slot);
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= stacks.size()) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
		}
	}

	protected void onLoad() {

	}

	protected void onContentsChanged(int slot) {

	}

	@Override
	public int getSlots() {
		return stacks.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return stacks.get(slot);
	}

	@Override
	public int getSlotLimit(int slot) {
		return Integer.MAX_VALUE;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty() || stack.getCount() == 0) {
			return ItemStack.EMPTY;
		}

		validateSlotIndex(slot);

		ItemStack existing = stacks.get(slot);

		if (!existing.isEmpty() && !ItemUtils.areItemsEqual(stack, stacks.get(slot))) {
			return ItemStack.EMPTY;
		}

		if (!simulate) {
			if (existing.isEmpty()) {
				stacks.set(slot, stack);
			}
			else {
				existing.setCount(stack.getCount());
			}
			//onContentsChanged(slot);
			serializeNBT();
		}

		return stacks.get(slot);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack ret = ItemStack.EMPTY;
		if (getTile() != null) {
			if (getTile().getExtractionMode() == ExtractionMode.NORMAL) {
				ret = doExtract(slot, amount, simulate);
			}
			else if (getTile().getExtractionMode() == ExtractionMode.SELECTED) {
				if (ItemUtils.areItemsEqual(getStackInSlot(slot), getTile().getSelectedStack())) {
					ret = doExtract(slot, amount, simulate);
				}
			}
		}
		else {
			ret = doExtract(slot, amount, simulate);
		}
		//DankNullUtils.reArrangeStacks(dankNull);
		return ret;
	}

	private ItemStack doExtract(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}

		validateSlotIndex(slot);

		ItemStack existing = stacks.get(slot);

		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				stacks.set(slot, ItemStack.EMPTY);
				onContentsChanged(slot);
			}
			return existing;
		}
		else {
			if (!simulate) {
				stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
				onContentsChanged(slot);
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	/*
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack == null || stack.stackSize == 0) {
			return null;
		}
		validateSlotIndex(slot);
		ItemStack existing = stacks[slot];
		int limit = getStackLimit(slot, stack);
		if (existing != null) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
				return stack;
			}
			limit -= existing.stackSize;
		}
		if (limit <= 0) {
			return stack;
		}
		boolean reachedLimit = stack.stackSize > limit;
		if (!simulate) {
			if (existing == null) {
				stacks[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
			}
			else {
				existing.stackSize += reachedLimit ? limit : stack.stackSize;
			}
			onContentsChanged(slot);
		}
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
	}
	*/

	@Override
	public NBTBase serializeNBT() {
		NBTTagList nbtTagList = new NBTTagList();
		for (int i = 0; i < stacks.size(); i++) {
			if (!stacks.get(i).isEmpty()) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				int size = stacks.get(i).getCount();
				int max = DankNullUtils.getDankNullMaxStackSize(dankNull);
				if (size > max) {
					stacks.get(i).setCount(max);
				}
				itemTag.setInteger("RealCount", stacks.get(i).getCount());
				stacks.get(i).writeToNBT(itemTag);
				nbtTagList.appendTag(itemTag);
			}
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag(getName(), nbtTagList);
		nbt.setInteger("Size", stacks.size());
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(this, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbtIn) {
		if (nbtIn instanceof NBTTagCompound) {
			NBTTagCompound nbt = (NBTTagCompound) nbtIn;
			setSize(nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.size());
			NBTTagList tagList = nbt.getTagList(getName(), Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
				int slot = itemTags.getInteger("Slot");

				if (slot >= 0 && slot < stacks.size()) {
					stacks.set(slot, new ItemStack(itemTags));
					stacks.get(slot).setCount(itemTags.getInteger("RealCount"));
				}
			}
			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(this, null, tagList);
			onLoad();
		}
	}

	private String getName() {
		return "danknull-inventory";
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return hasCapability(capability, facing) ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this) : null;
	}

}
