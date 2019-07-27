package p455w0rd.danknull.container;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.slot.SlotDankNullDock;
import p455w0rd.danknull.inventory.slot.SlotHotbar;
import p455w0rd.danknull.network.PacketSyncDankNullDock;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 */
public class ContainerDankNullDock extends Container {

	private final TileDankNullDock tile;

	public ContainerDankNullDock(final EntityPlayer player, final TileDankNullDock tile) {
		this.tile = tile;
		final InventoryPlayer playerInv = player.inventory;
		final ItemStack dankNull = tile.getDankNull();
		int lockedSlot = -1;
		int numRows = DankNullUtils.getMeta(dankNull) + 1;
		if (DankNullUtils.isCreativeDankNull(dankNull)) {
			numRows--;
		}
		for (int i = 0; i < playerInv.getSizeInventory(); i++) {
			final ItemStack currStack = playerInv.getStackInSlot(i);
			if (!currStack.isEmpty() && currStack == dankNull) {
				lockedSlot = i;
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new SlotHotbar(playerInv, i, i * 20 + 9 + i, 90 + numRows - 1 + numRows * 20 + 6, lockedSlot == i));

		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, j * 20 + 9 + j, 149 + numRows - 1 + i - (6 - numRows) * 20 + i * 20));
			}
		}
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new SlotDankNullDock(this, j + i * 9, j * 20 + 9 + j, 19 + i + i * 20));
			}
		}
		if (tile.getWorld() != null) {
			tile.markDirty();
			final IBlockState s = tile.getWorld().getBlockState(tile.getPos());
			tile.getWorld().notifyBlockUpdate(tile.getPos(), s, s, 3);
		}
	}

	public TileDankNullDock getTile() {
		return tile;
	}

	@Override
	public boolean canInteractWith(final EntityPlayer playerIn) {
		return DankNullUtils.getNewDankNullInventory(getDankNull()).isValid();
	}

	public ItemStack getDankNull() {
		return getTile().getDankNull();
	}

	public boolean addStack(final InventoryDankNull inventory, final ItemStack stack) {
		boolean ret = false;
		if (DankNullUtils.isDankNull(stack)) {
			return false;
		}
		if (DankNullUtils.isFiltered(inventory, stack)) {
			ret = DankNullUtils.addFilteredStackToDankNull(inventory, stack);
		}
		else {
			if (ret = DankNullUtils.addFilteredStackToDankNull(inventory, stack)) {
				//noop
			}
			else if (DankNullUtils.getNextAvailableSlot(inventory) >= 0) {
				final int nextSlot = DankNullUtils.getNextAvailableSlot(inventory);
				inventory.setInventorySlotContents(nextSlot, stack);
				inventorySlots.get(36 + nextSlot).putStack(stack);
				ret = true;
			}
			if (DankNullUtils.getSelectedStackIndex(inventory) == -1) {
				DankNullUtils.setSelectedIndexApplicable(inventory);
			}
		}
		DankNullUtils.reArrangeStacks(inventory);
		return ret;
	}

	private boolean isDankNullSlot(final Slot slot) {
		return slot instanceof SlotDankNullDock;
	}

	private boolean isInHotbar(final int index) {
		return index >= 0 && index <= 8;
	}

	private boolean isInInventory(final int index) {
		return index >= 9 && index <= 36;
	}

	@Override
	public void detectAndSendChanges() {
		//I'll take this, thanks
	}

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
			final InventoryDankNull tmpInv = DankNullUtils.getNewDankNullInventory(getDankNull());
			shiftClick(tmpInv, index, player);
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
					if (addStack(tmpInv, heldStack)) {
						DankNullUtils.setSelectedIndexApplicable(tmpInv);
						tmpInv.markDirty();
					}
					sync(getDankNull());
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
					final int returnSize = Math.min(newStack.getCount() / 2, newStack.getCount());
					if (tmpInv != null) {
						if (!(player instanceof EntityPlayerMP)) {

							DankNullUtils.decrDankNullStackSize(tmpInv, thisStack, returnSize);
						}
						//else {
						//returnSize = thisStack.getCount();
						//}
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
					sync(getDankNull());
				}

			}
		}
		return ItemStack.EMPTY;
	}

	private void sync(final ItemStack dankNull) {
		ModNetworking.getInstance().sendToServer(new PacketSyncDankNullDock(getTile(), dankNull));
	}

	private ItemStack shiftClick(final InventoryDankNull inventory, final int index, final EntityPlayer player) {
		final Slot clickSlot = inventorySlots.get(index);
		if (clickSlot.getHasStack()) {
			if (!isDankNullSlot(clickSlot)) {
				if (!(player instanceof EntityPlayerMP)) {
					if (addStack(inventory, clickSlot.getStack())) {
						clickSlot.putStack(ItemStack.EMPTY);
						DankNullUtils.setSelectedIndexApplicable(inventory);
						inventory.markDirty();
					}
					else {
						moveStackWithinInventory(clickSlot.getStack(), index);
					}
					sync(getDankNull());
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
						DankNullUtils.decrDankNullStackSize(inventory, clickSlot.getStack(), realMaxStackSize);
						sync(inventory.getDankNull());
					}
					if (player instanceof EntityPlayerMP) {
						player.inventory.setInventorySlotContents(index, newStack);
						player.inventory.markDirty();
						getTile().markDirty();
					}
				}
				else {
					newStack.setCount(DankNullUtils.isCreativeDankNull(getDankNull()) ? newStack.getMaxStackSize() : currentStackSize);
					//if (!(player instanceof EntityPlayerMP)) {
					if (moveStackToInventory(newStack) && !(player instanceof EntityPlayerMP)) {
						DankNullUtils.decrDankNullStackSize(inventory, clickSlot.getStack(), currentStackSize);
						if (DankNullUtils.isCreativeDankNull(getDankNull()) && !DankNullUtils.isCreativeDankNullLocked(getDankNull())) {
							clickSlot.putStack(ItemStack.EMPTY);
						}
					}
					if (!(player instanceof EntityPlayerMP)) {
						sync(getDankNull());
					}
					if (player instanceof EntityPlayerMP) {
						player.inventory.setInventorySlotContents(index, newStack);
						player.inventory.markDirty();
					}
					DankNullUtils.reArrangeStacks(inventory);

				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index) {
		return ItemStack.EMPTY;
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

	private boolean moveStackToInventory(final ItemStack itemStackIn) {
		for (int i = 0; i < 36; i++) {
			final Slot possiblyOpenSlot = inventorySlots.get(i);
			if (!possiblyOpenSlot.getHasStack()) {
				possiblyOpenSlot.putStack(itemStackIn);
				return true;
			}
		}
		return false;
	}

}