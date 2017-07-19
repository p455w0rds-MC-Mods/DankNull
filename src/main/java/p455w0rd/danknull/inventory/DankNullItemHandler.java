package p455w0rd.danknull.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
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
	protected ItemStack[] stacks;

	public DankNullItemHandler(ItemStack stack) {
		if (stack != null) {
			dankNull = stack;
		}
		stacks = new ItemStack[54];
		//maxSize = stack.getItemDamage() + 1 == 6 ? Integer.MAX_VALUE : ((stack.getItemDamage() + 1) * 128) * (stack.getItemDamage() + 1);
	}

	public void setTile(TileDankNullDock dock) {
		dankDock = dock;
	}

	public TileDankNullDock getTile() {
		return dankDock;
	}

	public void setSize(int size) {
		stacks = new ItemStack[size];
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		if (ItemStack.areItemStacksEqual(stacks[slot], stack)) {
			return;
		}
		stacks[slot] = stack;
		onContentsChanged(slot);
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= stacks.length) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.length + ")");
		}
	}

	protected void onLoad() {

	}

	protected void onContentsChanged(int slot) {

	}

	@Override
	public int getSlots() {
		return stacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return stacks[slot];
	}

	@Override
	public int getSlotLimit(int slot) {
		return Integer.MAX_VALUE;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack == null || stack.getCount() == 0) {
			return null;
		}

		validateSlotIndex(slot);

		ItemStack existing = stacks[slot];

		if (existing != null && !ItemUtils.areItemsEqual(stack, stacks[slot])) {
			return null;
		}

		if (!simulate) {
			if (existing == null) {
				stacks[slot] = stack;
			}
			else {
				existing.setCount(stack.getCount());
			}
			//onContentsChanged(slot);
			serializeNBT();
		}

		return stacks[slot];
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack ret = null;
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
			return null;
		}

		validateSlotIndex(slot);

		ItemStack existing = stacks[slot];

		if (existing == null) {
			return null;
		}

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				stacks[slot] = null;
				onContentsChanged(slot);
			}
			return existing;
		}
		else {
			if (!simulate) {
				stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract);
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
		for (int i = 0; i < stacks.length; i++) {
			if (stacks[i] != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				int size = stacks[i].getCount();
				int max = DankNullUtils.getDankNullMaxStackSize(dankNull);
				if (size > max) {
					stacks[i].setCount(max);
				}
				itemTag.setInteger("RealCount", stacks[i].getCount());
				stacks[i].writeToNBT(itemTag);
				nbtTagList.appendTag(itemTag);
			}
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag(getName(), nbtTagList);
		nbt.setInteger("Size", stacks.length);
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(this, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbtIn) {
		if (nbtIn instanceof NBTTagCompound) {
			NBTTagCompound nbt = (NBTTagCompound) nbtIn;
			setSize(nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.length);
			NBTTagList tagList = nbt.getTagList(getName(), Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
				int slot = itemTags.getInteger("Slot");

				if (slot >= 0 && slot < stacks.length) {
					stacks[slot] = new ItemStack(itemTags);
					stacks[slot].setCount(itemTags.getInteger("RealCount"));
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
