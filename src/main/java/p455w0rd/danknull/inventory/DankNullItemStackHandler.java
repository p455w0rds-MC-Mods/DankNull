package p455w0rd.danknull.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rdslib.util.ItemUtils;

/**
 * TODO Implement IItemHandler instead of extending ItemStackHandler
 *
 * @author BrockWS
 */
public class DankNullItemStackHandler extends ItemStackHandler {

	private int maxStackSize;

	public DankNullItemStackHandler(int maxStackSize) {
		super();
		this.maxStackSize = maxStackSize;
	}

	public DankNullItemStackHandler(int size, int maxStackSize) {
		super(size);
		this.maxStackSize = maxStackSize;
	}

	public DankNullItemStackHandler(NonNullList<ItemStack> list, int maxStackSize) {
		super(list);
		this.maxStackSize = maxStackSize;
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		// TODO Rearrange slots
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		if (stack.isEmpty() || stack.getItem() instanceof ItemDankNull)
			return false;
		for (int i = 0; i < this.getSlots(); i++) { // FIXME javadoc says The actual items in the inventory, its fullness, or any other state are not considered by isItemValid.
			if (i == slot)
				continue;
			ItemStack slotStack = this.getStackInSlot(i);
			if (!slotStack.isEmpty() && ItemUtils.areItemStacksEqualIgnoreSize(stack, slotStack))
				return false;
		}
		return true;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagList items = new NBTTagList();
		for (int i = 0; i < stacks.size(); i++) {
			ItemStack stack = stacks.get(i);
			if (stack.isEmpty())
				continue;
			NBTTagCompound stackTag = new NBTTagCompound();
			stack.writeToNBT(stackTag);
			stackTag.setInteger("Count", stack.getCount());
			items.appendTag(stackTag);
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag(ModGlobals.NBT.DANKNULL_INVENTORY, items);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		NBTTagList items = tag.getTagList(ModGlobals.NBT.DANKNULL_INVENTORY, Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < items.tagCount(); i++) {
			final NBTTagCompound itemTag = items.getCompoundTagAt(i);
			final int slot = itemTag.getInteger(ModGlobals.NBT.SLOT);
			final ItemStack stack = new ItemStack(itemTag);
			stack.setCount(itemTag.getInteger("Count"));
			stacks.set(slot, stack);
		}
		onLoad();
	}

	@Override
	public int getSlotLimit(int slot) {
		return this.maxStackSize;
	}

	@Override
	protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
		return this.getSlotLimit(slot);
	}
}
