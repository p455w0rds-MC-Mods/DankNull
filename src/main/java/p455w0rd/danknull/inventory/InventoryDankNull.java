package p455w0rd.danknull.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.ItemNBTUtils;

/**
 * @author p455w0rd
 */
public class InventoryDankNull implements IInventory {

	private final NonNullList<ItemStack> itemStacks;
	private int[] stackSizes;
	private EntityPlayer player;
	private PlayerSlot dankNullSlot = null;
	private String itemUUID = "";
	private ItemStack tileStack = null;

	public InventoryDankNull(final PlayerSlot dankNullSlot, final EntityPlayer player) {
		this.dankNullSlot = dankNullSlot;
		this.player = player;
		itemUUID = ItemNBTUtils.getString(getDankNull(), NBT.UUID);
		itemStacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
		stackSizes = new int[getSizeInventory()];
		loadInventory(getDNTag());
	}

	//This is for those few situations where you just dont have access to the player.
	public InventoryDankNull(final ItemStack stack) {
		tileStack = stack;
		itemUUID = ItemNBTUtils.getString(getDankNull(), NBT.UUID);
		itemStacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
		stackSizes = new int[getSizeInventory()];
		loadInventory(getDNTag());
	}

	@Override
	public int getSizeInventory() {
		int numRows = DankNullUtils.getMeta(getDankNull()) + 1;
		if (DankNullUtils.isCreativeDankNull(getDankNull())) {
			numRows--;
		}
		return numRows * 9;
	}

	@Override
	public ItemStack getStackInSlot(final int index) {
		if (DankNullUtils.isCreativeDankNull(getDankNull()) && index < getSizeInventory() && !itemStacks.get(index).isEmpty()) {
			final ItemStack tmp = itemStacks.get(index).copy();
			tmp.setCount(Integer.MAX_VALUE);
			return tmp;
		}
		return index < itemStacks.size() ? itemStacks.get(index) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(final int index, final int amount) {
		if (!getStackInSlot(index).isEmpty()) {
			if (DankNullUtils.isCreativeDankNull(getDankNull())) {
				final ItemStack tmp = getStackInSlot(index).copy();
				tmp.setCount(Integer.MAX_VALUE);
				return tmp;
			}
			if (getStackInSlot(index).getCount() <= amount) {
				final ItemStack itemstack = getStackInSlot(index);
				itemStacks.set(index, ItemStack.EMPTY);
				setSizeForSlot(index, 0);
				markDirty();
				return itemstack;
			}
			final ItemStack itemstack1 = getStackInSlot(index).splitStack(amount);
			setSizeForSlot(index, getSizeForSlot(index) - amount);
			if (getStackInSlot(index).getCount() == 0) {
				itemStacks.set(index, ItemStack.EMPTY);
			}
			markDirty();
			return itemstack1;
		}
		else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInventorySlotContents(final int index, final ItemStack itemStack) {
		itemStacks.set(index, itemStack);
		markDirty();
	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer var1) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack stack) {
		return !(stack.getItem() instanceof ItemDankNull);
	}

	@Override
	public String getName() {
		return NBT.DANKNULL_INVENTORY;
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
	public ItemStack removeStackFromSlot(final int index) {
		final ItemStack stack = getStackInSlot(index);
		if (!stack.isEmpty()) {
			itemStacks.set(index, ItemStack.EMPTY);
		}
		markDirty();
		return stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void openInventory(final EntityPlayer player) {
	}

	@Override
	public void closeInventory(final EntityPlayer player) {
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
		itemStacks.clear();
	}

	public DankNullTier getTier() {
		return DankNullUtils.getTier(getDankNull());
	}

	public NonNullList<ItemStack> getStacks() {
		return itemStacks;
	}

	public int getSizeForSlot(final int index) {
		return index >= 0 ? DankNullUtils.isCreativeDankNull(getDankNull()) ? Integer.MAX_VALUE : stackSizes[index] : 0;
	}

	public void setSizeForSlot(final int index, int size) {
		if (DankNullUtils.isCreativeDankNull(getDankNull())) {
			size = Integer.MAX_VALUE;
		}
		if (index >= stackSizes.length) {
			final int[] oldSizes = stackSizes;
			final int[] newSizes = new int[index - 1];
			for (final int i : oldSizes) {
				newSizes[i] = oldSizes[i];
			}
			stackSizes = newSizes;
		}
		stackSizes[index] = size < 0 ? 0 : size;
	}

	public long getMaxStackSize() {
		return getTier().getMaxStackSize();
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

	public ItemStack getDankNull() {
		if (tileStack != null) {
			return tileStack;
		}

		return dankNullSlot.getStackInSlot(getPlayer());
	}

	public int getPlayerSlotIndex() {
		return dankNullSlot.getSlotIndex();
	}

	public int getPlayerSlotCategoryIndex() { return dankNullSlot.getCatIndex(); }

	public NBTTagCompound getDNTag() {
		final ItemStack dn = getDankNull();
		if (!dn.hasTagCompound()) {
			dn.setTagCompound(saveInventory(new NBTTagCompound())); //This should not be needed but just in case...
		}
		return dn.getTagCompound();
	}

	@Override
	public void markDirty() {
		if (!isValid()) {
			return;
		}
		final ItemStack stack = getDankNull();
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound()); //Really not needed but better safe than sorry
		}
		saveInventory(stack.getTagCompound());
		if (dankNullSlot != null) {
			dankNullSlot.setStackInSlot(getPlayer(), stack);
		}
	}

	public NBTTagCompound saveInventory(final NBTTagCompound compound) {
		final NBTTagList nbtTL = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++) {
			if (!getStackInSlot(i).isEmpty()) {
				final NBTTagCompound nbtTC = new NBTTagCompound();
				nbtTC.setInteger(NBT.SLOT, i);
				nbtTC.setInteger(NBT.REALCOUNT, getStackInSlot(i).getCount() <= getTier().getMaxStackSize() ? getStackInSlot(i).getCount() : getTier().getMaxStackSize());
				getStackInSlot(i).writeToNBT(nbtTC);
				nbtTL.appendTag(nbtTC);
			}
		}
		if (itemUUID != null && !itemUUID.isEmpty()) {
			compound.setString(NBT.UUID, itemUUID);
		}
		compound.setTag(getName(), nbtTL);
		return compound;
	}

	public void loadInventory(final NBTTagCompound compound) {
		final NBTTagList nbtTL = compound.getTagList(getName(), Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbtTL.tagCount(); i++) {
			final NBTTagCompound nbtTC = nbtTL.getCompoundTagAt(i);
			final int slot = nbtTC.getInteger(NBT.SLOT);
			final ItemStack stack = new ItemStack(nbtTC);
			if (nbtTC.hasKey(NBT.REALCOUNT)) {
				stack.setCount(nbtTC.getInteger(NBT.REALCOUNT));
				setSizeForSlot(slot, nbtTC.getInteger(NBT.REALCOUNT));
			}
			itemStacks.set(slot, stack);
		}
		if (compound.hasKey(NBT.UUID, Constants.NBT.TAG_STRING)) {
			itemUUID = compound.getString(NBT.UUID);
		}
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public boolean isValid() {
		if (itemUUID.isEmpty()) {
			return false;
		}
		final String itemUUID = ItemNBTUtils.getString(getDankNull(), NBT.UUID);
		return itemUUID.equals(this.itemUUID);
	}

}