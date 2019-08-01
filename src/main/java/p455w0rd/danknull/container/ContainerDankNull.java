package p455w0rd.danknull.container;

import java.util.Objects;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.FMLCommonHandler;

import p455w0rd.danknull.init.ModLogger;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.inventory.slot.SlotHotbar;
import p455w0rd.danknull.network.PacketSyncDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 */
public class ContainerDankNull extends Container {

	private final PlayerSlot playerSlot;
	private final EntityPlayer player;

	public ContainerDankNull(final EntityPlayer player, final PlayerSlot slot) {
		playerSlot = slot;
		this.player = player;
		final InventoryPlayer playerInv = player.inventory;
		final ItemStack dankNull = playerInv.getStackInSlot(slot.getSlotIndex());
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
				addSlotToContainer(new SlotDankNull(playerSlot, player, j + i * 9, j * 20 + 9 + j, 19 + i + i * 20));
			}
		}
		/*if (tile.getWorld() != null) {
			tile.markDirty();
			final IBlockState s = tile.getWorld().getBlockState(tile.getPos());
			tile.getWorld().notifyBlockUpdate(tile.getPos(), s, s, 3);
		}*/
	}

	public PlayerSlot getPlayerSlot() {
		return playerSlot;
	}

	public ItemStack getDankNullInPlayerSlot() {
		return player.inventory.getStackInSlot(playerSlot.getSlotIndex());
	}

	@Override
	public boolean canInteractWith(final EntityPlayer playerIn) {
		return DankNullUtils.getNewDankNullInventory(getDankNullInPlayerSlot()).isValid();
	}

	private ItemStack addStack(final InventoryDankNull inventory, final ItemStack stack) {
		ItemStack leftover = ItemStack.EMPTY;
		if (DankNullUtils.isDankNull(stack)) {
			return stack;
		}
		if (DankNullUtils.isFiltered(inventory, stack)) { // AKA Already exists in DankNull
			leftover = DankNullUtils.addFilteredStackToDankNull(inventory, stack);
		} else if (DankNullUtils.getNextAvailableSlot(inventory) >= 0) {
			final int nextSlot = DankNullUtils.getNextAvailableSlot(inventory);
			inventory.setInventorySlotContents(nextSlot, stack);
			inventorySlots.get(36 + nextSlot).putStack(stack);
		}
		if (DankNullUtils.getSelectedStackIndex(inventory) == -1) {
			DankNullUtils.setSelectedIndexApplicable(inventory);
		}
		DankNullUtils.reArrangeStacks(inventory);
		return leftover;
	}

	private boolean isDankNullSlot(final Slot slot) {
		return slot instanceof SlotDankNull;
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
		for (int i = 0; i < 36; ++i) {
			ItemStack slotStack = this.inventorySlots.get(i).getStack();
			ItemStack clientStack = this.inventoryItemStacks.get(i);

			if (!ItemStack.areItemStacksEqual(clientStack, slotStack)) {
				if (ItemStack.areItemStacksEqualUsingNBTShareTag(clientStack, slotStack))
					continue;
				clientStack = slotStack.isEmpty() ? ItemStack.EMPTY : slotStack.copy();
				this.inventoryItemStacks.set(i, clientStack);
				for (IContainerListener listener : this.listeners) {
					listener.sendSlotContents(this, i, slotStack);
				}
			}
		}
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
		if (s == null || index < 36 && clickType != ClickType.QUICK_MOVE || clickType == ClickType.CLONE) {
			return super.slotClick(index, dragType, clickType, player);
		}
		final InventoryDankNull tmpInv = DankNullUtils.getNewDankNullInventory(getDankNullInPlayerSlot());
		Objects.requireNonNull(tmpInv, "Failed to create InventoryDankNull");
		if (clickType == ClickType.QUICK_MOVE) {
			return this.shiftClick(tmpInv, index, player);
		}
		final InventoryPlayer inventoryplayer = player.inventory;
		final ItemStack heldStack = inventoryplayer.getItemStack();
		if (s instanceof SlotDankNull && clickType == ClickType.PICKUP) {
			if (DankNullUtils.isDankNull(heldStack)) {
				return ItemStack.EMPTY;
			}
			final ItemStack thisStack = s.getStack();
			if (!thisStack.isEmpty() && DankNullUtils.isDankNull(thisStack)) {
				return ItemStack.EMPTY;
			}
			if (!heldStack.isEmpty()) { // Want to insert held stack into DankNull
				if (!DankNullUtils.canStackBeAdded(tmpInv, heldStack))
					return ItemStack.EMPTY;
				ItemStack toAdd = heldStack.copy();
				if (dragType == 1) {
					toAdd.setCount(1);
				}

				ItemStack leftover = this.addStack(tmpInv, toAdd);
				if (dragType == 0) {
					if (!leftover.isEmpty()) {
						inventoryplayer.setItemStack(leftover);
					} else {
						inventoryplayer.setItemStack(ItemStack.EMPTY);
					}
				} else if (dragType == 1 && leftover.isEmpty()) { // We right clicked and was able to add 1 to DankNull
					heldStack.shrink(1);
					inventoryplayer.setItemStack(heldStack);
				}
				tmpInv.markDirty();
				inventoryplayer.markDirty(); // Probably not needed

				if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).updateHeldItem();
					this.sync(tmpInv.getDankNull(), player);
				}
			}
			else if (!thisStack.isEmpty()) { // Want to take slot stack out of DankNull
				final int max = thisStack.getMaxStackSize();
				final ItemStack newStack = thisStack.copy();
				if (thisStack.getCount() >= max) {
					newStack.setCount(max);
				}
				if (dragType == 1) {
					final int returnSize = Math.min(newStack.getCount() / 2, newStack.getCount());
					DankNullUtils.decrDankNullStackSize(tmpInv, thisStack, returnSize);
					//else {
					//	returnSize = thisStack.getCount();
					//}
					newStack.setCount(returnSize + (newStack.getCount() % 2 == 0 ? 0 : 1));
				}
				else if (dragType == 0) {
					DankNullUtils.decrDankNullStackSize(tmpInv, thisStack, newStack.getCount());
					if (inventorySlots.get(index).getHasStack() && inventorySlots.get(index).getStack().getCount() <= 0) {
						inventorySlots.get(index).putStack(ItemStack.EMPTY);
					}
				}

				inventoryplayer.setItemStack(newStack);
				inventoryplayer.markDirty();
				DankNullUtils.reArrangeStacks(tmpInv);
				tmpInv.markDirty();

				if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).updateHeldItem();
					this.sync(tmpInv.getDankNull(), player);
				}
			}
		}
		return ItemStack.EMPTY;
	}

	private void sync(final ItemStack dankNull, final EntityPlayer player) {
		if (!(player instanceof EntityPlayerMP))
			throw new RuntimeException("Only call on server!");
		ModNetworking.getInstance().sendTo(new PacketSyncDankNull(playerSlot, dankNull), (EntityPlayerMP) player);
	}

	private ItemStack shiftClick(final InventoryDankNull inventory, final int index, final EntityPlayer player) {
		final Slot clickSlot = inventorySlots.get(index);
		if (clickSlot.getHasStack()) {
			if (!isDankNullSlot(clickSlot)) { // Shift click from Player Inventory
				ItemStack leftover = this.addStack(inventory, clickSlot.getStack());
				/*if (leftover.getCount() == clickSlot.getStack().getCount()) {
					leftover = moveStackWithinInventory(clickSlot.getStack(), index));
				}*/
				clickSlot.putStack(leftover);
				DankNullUtils.setSelectedIndexApplicable(inventory);
				inventory.markDirty();
				player.inventory.markDirty();
				if (player instanceof EntityPlayerMP) {
					this.sync(inventory.getDankNull(), player);
				}
				return ItemStack.EMPTY;
			}
			else {
				final ItemStack newStack = clickSlot.getStack().copy();
				final int realMaxStackSize = newStack.getMaxStackSize();
				final int currentStackSize = newStack.getCount();
				if (!DankNullUtils.isCreativeDankNull(inventory.getDankNull())) {
					if (currentStackSize > realMaxStackSize) {
						newStack.setCount(realMaxStackSize);
					}
					if (moveStackToInventory(newStack)) {
						DankNullUtils.decrDankNullStackSize(inventory, clickSlot.getStack(), realMaxStackSize);
					}
					if (player instanceof EntityPlayerMP) {
						this.sync(inventory.getDankNull(), player);
					}
				}
				else {
					newStack.setCount(DankNullUtils.isCreativeDankNull(inventory.getDankNull()) ? newStack.getMaxStackSize() : currentStackSize);
					if (moveStackToInventory(newStack) && !(player instanceof EntityPlayerMP)) {
						DankNullUtils.decrDankNullStackSize(inventory, clickSlot.getStack(), currentStackSize);
						if (DankNullUtils.isCreativeDankNull(inventory.getDankNull()) && !DankNullUtils.isCreativeDankNullLocked(inventory.getDankNull())) {
							clickSlot.putStack(ItemStack.EMPTY);
						}
					}

					if (player instanceof EntityPlayerMP) {
						player.inventory.setInventorySlotContents(index, newStack);
						player.inventory.markDirty();
					}
					DankNullUtils.reArrangeStacks(inventory);
					inventory.markDirty();
					this.sync(inventory.getDankNull(), player);
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