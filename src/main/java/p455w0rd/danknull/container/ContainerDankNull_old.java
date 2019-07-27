package p455w0rd.danknull.container;

import java.util.List;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.slot.*;
import p455w0rd.danknull.network.PacketRequestInitialUpdate;
import p455w0rd.danknull.network.PacketSyncDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 */
public class ContainerDankNull_old extends Container {

	private final EntityPlayer player;
	InventoryDankNull inventoryDankNull;
	boolean dirty = false;

	public ContainerDankNull_old(final EntityPlayer player, final InventoryDankNull inv) {
		this.player = player;
		inventoryDankNull = inv;
		final InventoryPlayer playerInv = player.inventory;
		final ItemStack dankNull = inv.getDankNull();
		int lockedSlot = -1;
		final int numRows = DankNullUtils.getTier(dankNull).getNumRows();
		for (int i = 0; i < playerInv.getSizeInventory(); i++) {
			final ItemStack currStack = playerInv.getStackInSlot(i);
			if (!currStack.isEmpty() && currStack == dankNull) {
				lockedSlot = i;
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new SlotHotbar(player.inventory, i, i * 20 + 9 + i, 90 + numRows - 1 + numRows * 20 + 6, lockedSlot == i));

		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, j * 20 + 9 + j, 149 + numRows - 1 + i - (6 - numRows) * 20 + i * 20));
			}
		}
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < 9; j++) {
				//addSlotToContainer(new SlotDankNull(inventoryDankNull, j + i * 9, j * 20 + 9 + j, 19 + i + i * 20));
			}
		}
		if (!(player instanceof EntityPlayerMP)) {
			ModNetworking.getInstance().sendToServer(new PacketRequestInitialUpdate());
		}
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setAll(final List<ItemStack> list) {
		super.setAll(list);
		/*for (int i = 0; i < 36; ++i) {
			getSlot(i).putStack(list.get(i));
		}
		for (int i = 36; i < list.size(); ++i) {
			final ItemStack stack = getDankNullInventory().getStackInSlot(i - 36);
			final int realSize = getDankNullInventory().getSizeForSlot(i - 36);
			if (stack.getCount() != realSize) {
				stack.setCount(realSize);
			}
			inventoryItemStacks.set(i, stack.copy());
			getSlot(i).putStack(stack);
		}*/
	}

	@Override
	public boolean canInteractWith(final EntityPlayer playerIn) {
		return inventoryDankNull.isValid();
	}

	public ItemStack getDankNull() {
		return getDankNullInventory().getDankNull();
	}

	public InventoryDankNull getDankNullInventory() {
		return inventoryDankNull;
	}

	public void setDankNullInventory(final InventoryDankNull inv) {
		inventoryDankNull = inv;
	}

	public boolean addStack(final ItemStack stack) {
		boolean ret = false;
		if (DankNullUtils.isDankNull(stack)) {
			return false;
		}
		if (DankNullUtils.isFiltered(getDankNullInventory(), stack)) {
			ret = DankNullUtils.addFilteredStackToDankNull(getDankNullInventory(), stack);
		}
		else {
			if (ret = DankNullUtils.addFilteredStackToDankNull(getDankNullInventory(), stack)) {
				//noop
			}
			else if (DankNullUtils.getNextAvailableSlot(getDankNullInventory()) >= 0) {
				final int nextSlot = DankNullUtils.getNextAvailableSlot(getDankNullInventory());
				getDankNullInventory().setInventorySlotContents(nextSlot, stack);
				inventorySlots.get(36 + nextSlot).putStack(stack);
				ret = true;
			}
			if (DankNullUtils.getSelectedStackIndex(getDankNullInventory()) == -1) {
				DankNullUtils.setSelectedIndexApplicable(getDankNullInventory());
			}
		}
		DankNullUtils.reArrangeStacks(getDankNullInventory());
		return ret;
	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index) {
		return ItemStack.EMPTY;
	}

	private ItemStack shiftClick(final int index, final EntityPlayer player) {
		final Slot clickSlot = inventorySlots.get(index);
		if (clickSlot.getHasStack()) {
			if (!isDankNullSlot(clickSlot)) {
				if (!(player instanceof EntityPlayerMP)) {
					if (addStack(clickSlot.getStack())) {
						clickSlot.putStack(ItemStack.EMPTY);
						DankNullUtils.setSelectedIndexApplicable(getDankNullInventory());
						getDankNullInventory().markDirty();
					}
					else {
						moveStackWithinInventory(clickSlot.getStack(), index);
					}
					sync();
				}
				if (player instanceof EntityPlayerMP) {
					player.inventory.setInventorySlotContents(index, ItemStack.EMPTY);
					player.inventory.markDirty();
				}
				return ItemStack.EMPTY;
			}
			else {
				final ItemStack newStack = clickSlot.getStack().copy();
				final int realMaxStackSize = newStack.getMaxStackSize();
				final int currentStackSize = newStack.getCount();
				if (!DankNullUtils.isCreativeDankNull(getDankNull()) && currentStackSize > realMaxStackSize) {
					newStack.setCount(realMaxStackSize);
					if (moveStackToInventory(newStack) && !(player instanceof EntityPlayerMP)) {
						DankNullUtils.decrDankNullStackSize(getDankNullInventory(), clickSlot.getStack(), realMaxStackSize);
						sync();
					}
					if (player instanceof EntityPlayerMP) {
						player.inventory.setInventorySlotContents(index, newStack);
						player.inventory.markDirty();
						getDankNullInventory().markDirty();
					}
				}
				else {
					newStack.setCount(DankNullUtils.isCreativeDankNull(getDankNull()) ? newStack.getMaxStackSize() : currentStackSize);
					//if (!(player instanceof EntityPlayerMP)) {
					if (moveStackToInventory(newStack) && !(player instanceof EntityPlayerMP)) {
						DankNullUtils.decrDankNullStackSize(getDankNullInventory(), clickSlot.getStack(), currentStackSize);
						if (DankNullUtils.isCreativeDankNull(getDankNull()) && !DankNullUtils.isCreativeDankNullLocked(getDankNull())) {
							clickSlot.putStack(ItemStack.EMPTY);
						}
					}
					if (!(player instanceof EntityPlayerMP)) {
						sync();
					}
					if (player instanceof EntityPlayerMP) {
						player.inventory.setInventorySlotContents(index, newStack);
						player.inventory.markDirty();
					}
					DankNullUtils.reArrangeStacks(getDankNullInventory());
				}
			}
		}
		return ItemStack.EMPTY;
	}

	/*@Override
	public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index) {
		final Slot clickSlot = inventorySlots.get(index);
		if (clickSlot.getHasStack()) {
			if (!isDankNullSlot(clickSlot)) {
				if (DankNullUtils.getNextAvailableSlot(this) == -1 && DankNullUtils.isFiltered(getDankNullInventory(), clickSlot.getStack())) {
					if (addStack(clickSlot.getStack())) {
						clickSlot.putStack(ItemStack.EMPTY);
						playerIn.inventory.markDirty();
						DankNullUtils.setSelectedIndexApplicable(getDankNullInventory());
					}
					else if (!moveStackWithinInventory(clickSlot.getStack(), index)) {
						return ItemStack.EMPTY;
					}
					sync();
					return clickSlot.getStack();
				}
				else {
					if (addStack(clickSlot.getStack())) {
						clickSlot.putStack(ItemStack.EMPTY);
						playerIn.inventory.markDirty();
						DankNullUtils.setSelectedIndexApplicable(getDankNullInventory());
					}
					else if (!moveStackWithinInventory(clickSlot.getStack(), index)) {
						return ItemStack.EMPTY;
					}
					sync();
					return clickSlot.getStack();
				}
			}
			else {
				final ItemStack newStack = clickSlot.getStack().copy();
				final int realMaxStackSize = newStack.getMaxStackSize();
				final int currentStackSize = newStack.getCount();
				if (currentStackSize > realMaxStackSize && !DankNullUtils.isCreativeDankNull(getDankNull())) {
					newStack.setCount(realMaxStackSize);
					if (moveStackToInventory(newStack)) {
						DankNullUtils.decrDankNullStackSize(getDankNullInventory(), clickSlot.getStack(), realMaxStackSize);
					}
				}
				else {
					newStack.setCount(DankNullUtils.isCreativeDankNull(getDankNull()) ? newStack.getMaxStackSize() : currentStackSize);
					if (moveStackToInventory(newStack)) {
						DankNullUtils.decrDankNullStackSize(getDankNullInventory(), clickSlot.getStack(), currentStackSize);
						if (!DankNullUtils.isCreativeDankNullLocked(getDankNull())) {
							clickSlot.putStack(ItemStack.EMPTY);
						}
					}
					DankNullUtils.reArrangeStacks(getDankNullInventory());
				}
			}
		}
		sync();
		return ItemStack.EMPTY;
	}*/

	@Override
	public void detectAndSendChanges() {
		// nop
	}

	private boolean isDankNullSlot(final Slot slot) {
		return slot instanceof SlotDankNull;
	}

	private boolean moveStackWithinInventory(final ItemStack itemStackIn, final int index) {
		if (isInHotbar(index)) {
			if (mergeItemStack(itemStackIn, 9, 37, false)) {
				return true;
			}
			for (int i = 9; i <= 36; i++) {
				final Slot possiblyOpenSlot = inventorySlots.get(i);
				if (!possiblyOpenSlot.getHasStack()) {
					possiblyOpenSlot.putStack(itemStackIn);
					inventorySlots.get(index).putStack(ItemStack.EMPTY);
					return true;
				}
			}
		}
		else if (isInInventory(index)) {
			if (mergeItemStack(itemStackIn, 0, 9, false)) {
				return true;
			}
			for (int i = 0; i <= 8; i++) {
				final Slot possiblyOpenSlot = inventorySlots.get(i);
				if (!possiblyOpenSlot.getHasStack()) {
					possiblyOpenSlot.putStack(itemStackIn);
					inventorySlots.get(index).putStack(ItemStack.EMPTY);
					return true;
				}
			}
		}
		return false;
	}

	private boolean isInHotbar(final int index) {
		return index >= 0 && index <= 8;
	}

	private boolean isInInventory(final int index) {
		return index >= 9 && index <= 36;
	}

	protected boolean moveStackToInventory(final ItemStack itemStackIn) {
		for (int i = 0; i <= 36; i++) {
			final Slot possiblyOpenSlot = inventorySlots.get(i);
			if (!possiblyOpenSlot.getHasStack()) {
				possiblyOpenSlot.putStack(itemStackIn);
				return true;
			}
		}
		return false;
	}

	/*@Override
	public ItemStack slotClick(final int index, final int dragType, final ClickType clickTypeIn, final EntityPlayer player) {
		final InventoryPlayer inventoryplayer = player.inventory;
		final ItemStack heldStack = inventoryplayer.getItemStack();
		if (index < 0) {
			return super.slotClick(index, dragType, clickTypeIn, player);
		}
		final Slot s = inventorySlots.get(index);
		if (s.getStack() == inventoryDankNull.getDankNull()) {
			return ItemStack.EMPTY;
		}
		if (isDankNullSlot(s)) {
			if (DankNullUtils.isDankNull(heldStack)) {
				return ItemStack.EMPTY;
			}
			final ItemStack thisStack = s.getStack();
			if (!thisStack.isEmpty() && DankNullUtils.isDankNull(thisStack)) {
				return ItemStack.EMPTY;
			}
			if (!heldStack.isEmpty()) {
				if (addStack(heldStack)) {
					inventoryplayer.setItemStack(ItemStack.EMPTY);
					return heldStack;
				}
				else {
					if (DankNullUtils.isCreativeDankNull(getDankNull())) {
						return heldStack;
					}
					if (thisStack.getCount() <= thisStack.getMaxStackSize()) {
						inventoryplayer.setItemStack(thisStack);
						s.putStack(heldStack);
					}
					sync();
					return ItemStack.EMPTY;
				}
			}
			else {
				if (!thisStack.isEmpty() && clickTypeIn == ClickType.PICKUP) {
					final int max = thisStack.getMaxStackSize();
					final ItemStack newStack = thisStack.copy();
					if (thisStack.getCount() >= max) {
						newStack.setCount(max);
					}
					if (dragType == 1) {
						final int returnSize = Math.min(newStack.getCount() / 2, newStack.getCount());
						if (getDankNullInventory() != null) {
							DankNullUtils.decrDankNullStackSize(getDankNullInventory(), thisStack, newStack.getCount() - returnSize);
							newStack.setCount(returnSize + (newStack.getCount() % 2 == 0 ? 0 : 1));
						}
					}
					else if (dragType == 0) {
						if (getDankNullInventory() != null) {
							DankNullUtils.decrDankNullStackSize(getDankNullInventory(), thisStack, newStack.getCount());
							if (inventorySlots.get(index).getHasStack() && inventorySlots.get(index).getStack().getCount() <= 0) {
								inventorySlots.get(index).putStack(ItemStack.EMPTY);
							}
						}
					}
					inventoryplayer.setItemStack(newStack);
					inventoryplayer.markDirty();
					if (!(player instanceof EntityPlayerMP)) {
						DankNullUtils.reArrangeStacks(getDankNullInventory());
						getDankNullInventory().markDirty();
						sync();
					}
					return ItemStack.EMPTY;
				}
			}
		}
		super.slotClick(index, dragType, clickTypeIn, player);
		return ItemStack.EMPTY;
	}*/

	@Override
	public Slot getSlot(final int slotId) {
		if (slotId < inventorySlots.size() && slotId >= 0) {
			return inventorySlots.get(slotId);
		}
		return null;
	}

	@Override
	public ItemStack slotClick(final int index, final int dragType, final ClickType clickType, final EntityPlayer player) {
		final Slot s = getSlot(index);
		if (index < 36 && clickType != ClickType.QUICK_MOVE || clickType == ClickType.CLONE) {
			return super.slotClick(index, dragType, clickType, player);
		}
		if (clickType == ClickType.QUICK_MOVE) {
			shiftClick(index, player);
			return ItemStack.EMPTY;
		}
		final InventoryDankNull tmpInv = DankNullUtils.getNewDankNullInventory(getDankNull());
		final InventoryPlayer inventoryplayer = player.inventory;
		final ItemStack heldStack = inventoryplayer.getItemStack();
		if (s instanceof SlotDankNullDock && clickType == ClickType.PICKUP) {
			if (DankNullUtils.isDankNull(heldStack)) {
				return ItemStack.EMPTY;
			}
			final ItemStack thisStack = s.getStack();
			if (!thisStack.isEmpty() && DankNullUtils.isDankNull(thisStack)) {
				return ItemStack.EMPTY;
			}
			if (!heldStack.isEmpty()) {
				if (!(player instanceof EntityPlayerMP)) {
					if (addStack(heldStack)) {
						DankNullUtils.setSelectedIndexApplicable(tmpInv);
						tmpInv.markDirty();
					}
					sync();
				}
				if (player instanceof EntityPlayerMP) {
					player.inventory.setInventorySlotContents(index, ItemStack.EMPTY);
					player.inventory.markDirty();
				}
				inventoryplayer.setItemStack(ItemStack.EMPTY);
			}
			else if (!thisStack.isEmpty()) {

				final int max = thisStack.getMaxStackSize();
				final ItemStack newStack = thisStack.copy();
				if (thisStack.getCount() >= max) {
					newStack.setCount(max);
				}
				if (dragType == 1) {
					int returnSize = Math.min(newStack.getCount() / 2, newStack.getCount());
					if (tmpInv != null) {
						if (!(player instanceof EntityPlayerMP)) {

							DankNullUtils.decrDankNullStackSize(tmpInv, thisStack, returnSize);
						}
						else {
							returnSize = thisStack.getCount();
						}
						newStack.setCount(returnSize + (newStack.getCount() % 2 == 0 ? 0 : 1));
					}
				}
				else if (dragType == 0) {
					if (tmpInv != null) {
						if (!(player instanceof EntityPlayerMP)) {
							DankNullUtils.decrDankNullStackSize(tmpInv, thisStack, newStack.getCount());
						}
						if (inventorySlots.get(index).getHasStack() && inventorySlots.get(index).getStack().getCount() <= 0) {
							inventorySlots.get(index).putStack(ItemStack.EMPTY);
						}
					}
				}

				//if (!(player instanceof EntityPlayerMP)) {
				//DankNullUtils.decrDankNullStackSize(tmpInv, thisStack, realMaxStackSize);
				//}
				inventoryplayer.setItemStack(newStack);
				inventoryplayer.markDirty();
				if (!(player instanceof EntityPlayerMP)) {
					DankNullUtils.reArrangeStacks(tmpInv);
					tmpInv.markDirty();
					sync();
				}

			}
		}
		return ItemStack.EMPTY;
	}

	public void sync() {
		ModNetworking.getInstance().sendToServer(new PacketSyncDankNull(getDankNullInventory().getPlayerSlotIndex(), getDankNull()));
	}

}