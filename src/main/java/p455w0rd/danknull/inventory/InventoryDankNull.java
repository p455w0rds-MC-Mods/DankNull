package p455w0rd.danknull.inventory;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class InventoryDankNull implements IInventory, Iterable<ItemStack> {

	public static final String INVENTORY_NAME = "danknull-inventory";
	public static final String TAG_SLOT = "Slot";
	public static final String TAG_COUNT = "RealCount";

	private int size = 54;
	private final NonNullList<ItemStack> STACKLIST;
	private final int[] sizesArray;
	private ItemStack dankNullStack;
	private int numRows = 0;
	private EntityPlayer player;

	public InventoryDankNull(ItemStack dankNull) {
		dankNullStack = dankNull;
		numRows = (dankNullStack.getItemDamage() + 1);
		size = (numRows * 9);
		STACKLIST = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
		sizesArray = new int[size];
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		readFromNBT(dankNull.getTagCompound());
	}

	@Override
	public int getSizeInventory() {
		return size;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index < getSizeInventory() ? STACKLIST.get(index) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int amount) {
		if (!getStackInSlot(index).isEmpty()) {
			if (getStackInSlot(index).getCount() <= amount) {
				ItemStack itemstack = getStackInSlot(index);
				setInventorySlotContents(index, ItemStack.EMPTY);
				setSizeForSlot(index, 0);
				markDirty();
				return itemstack;
			}
			ItemStack itemstack1 = getStackInSlot(index).splitStack(amount);
			setSizeForSlot(index, getSizeForSlot(index) - amount);
			if (getStackInSlot(index).getCount() == 0) {
				setInventorySlotContents(index, ItemStack.EMPTY);
			}
			markDirty();
			return itemstack1;
		}
		else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack itemStack) {
		STACKLIST.set(index, itemStack);
		markDirty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return !(stack.getItem() instanceof ItemDankNull);
	}

	@Override
	public String getName() {
		return INVENTORY_NAME;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = getStackInSlot(index);
		if (!stack.isEmpty()) {
			setInventorySlotContents(index, ItemStack.EMPTY);
		}
		return stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		if (player != null) {
			this.player = player;
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return new InvIterator(this);
	}

	public NonNullList<ItemStack> getStacks() {
		return STACKLIST;
	}

	public int getSizeForSlot(int index) {
		return sizesArray[index];
	}

	public void setSizeForSlot(int index, int size) {
		sizesArray[index] = size < 0 ? 0 : size;
	}

	public long getMaxStackSize() {
		return DankNullUtils.getDankNullMaxStackSize(getDankNull());
	}

	@Override
	public boolean isEmpty() {
		for (int x = 0; x < size; x++) {
			if (!getStackInSlot(x).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public ItemStack getDankNull() {
		return !dankNullStack.isEmpty() ? dankNullStack : ItemStack.EMPTY;
	}

	public static boolean isSameItem(@Nullable ItemStack left, @Nullable ItemStack right) {
		return (!left.isEmpty()) && (!right.isEmpty()) && (left.isItemEqual(right));
	}

	@Override
	public void markDirty() {
		if (getDankNull().isEmpty()) {
			return;
		}
		if (!getDankNull().hasTagCompound()) {
			getDankNull().setTagCompound(new NBTTagCompound());
		}
		writeToNBT(getDankNull().getTagCompound());
	}

	public void writeToNBT(NBTTagCompound itemTC) {
		NBTTagList nbtTL = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) != null) {
				NBTTagCompound nbtTC = new NBTTagCompound();
				nbtTC.setInteger(TAG_SLOT, i);
				nbtTC.setInteger(TAG_COUNT, getStackInSlot(i).getCount() <= DankNullUtils.getDankNullMaxStackSize(this) ? getStackInSlot(i).getCount() : DankNullUtils.getDankNullMaxStackSize(this));
				getStackInSlot(i).writeToNBT(nbtTC);
				nbtTL.appendTag(nbtTC);
			}
		}
		itemTC.setTag(getName(), nbtTL);
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList nbtTL = compound.hasKey(getName(), Constants.NBT.TAG_LIST) ? compound.getTagList(getName(), Constants.NBT.TAG_COMPOUND) : new NBTTagList();
		for (int i = 0; i < nbtTL.tagCount(); i++) {
			NBTTagCompound nbtTC = nbtTL.getCompoundTagAt(i);
			if (nbtTC != null) {
				int slot = nbtTC.getInteger(TAG_SLOT);
				ItemStack stack = new ItemStack(nbtTC);
				if (nbtTC.hasKey(TAG_COUNT)) {
					stack.setCount(nbtTC.getInteger(TAG_COUNT));
					setSizeForSlot(slot, nbtTC.getInteger(TAG_COUNT));
				}
				setInventorySlotContents(slot, stack);
			}
		}
	}

	public EntityPlayer getPlayer() {
		return player;
	}

}