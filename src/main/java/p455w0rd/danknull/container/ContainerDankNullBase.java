package p455w0rd.danknull.container;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.inventory.slot.*;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.network.PacketUpdateSlot;

/**
 * @author BrockWS
 */
public abstract class ContainerDankNullBase extends Container {

	protected final EntityPlayer player;

	public ContainerDankNullBase(final EntityPlayer player) {
		this.player = player;
	}

	protected void init() {
		final InventoryPlayer playerInv = player.inventory;
		final IDankNullHandler handler = getHandler();
		int lockedSlot = -1;
		final int numRows = handler.getTier().getNumRows();
		for (int i = 0; i < playerInv.getSizeInventory(); i++) {
			final ItemStack currStack = playerInv.getStackInSlot(i);
			if (!currStack.isEmpty() && currStack == getDankNullStack()) {
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
		for (int i = 0; i < handler.getTier().getNumRows(); i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(createDankNullSlot(handler, i, j));
			}
		}
	}

	public abstract IDankNullHandler getHandler();

	public abstract ItemStack getDankNullStack();

	protected boolean isDock() {
		return false;
	}

	private SlotDankNull createDankNullSlot(final IDankNullHandler handler, final int i, final int j) {
		return isDock() ? new SlotDankNullDock(handler, j + i * 9, j * 20 + 9 + j, 19 + i + i * 20) : new SlotDankNull(handler, j + i * 9, j * 20 + 9 + j, 19 + i + i * 20);
	}

	@Override
	public boolean canInteractWith(final EntityPlayer player) {
		return getHandler() != null;
	}

	@Override
	public Slot getSlot(final int slotId) {
		if (slotId < inventorySlots.size() && slotId >= 0) {
			return inventorySlots.get(slotId);
		}
		return null;
	}

	@Override
	public void detectAndSendChanges() {
		//I'll take this, thanks
		for (int i = 0; i < inventorySlots.size(); ++i) {
			final ItemStack slotStack = inventorySlots.get(i).getStack();
			ItemStack clientStack = inventoryItemStacks.get(i);
			if (!ItemStack.areItemStacksEqual(clientStack, slotStack)) {
				if (ItemStack.areItemStacksEqualUsingNBTShareTag(clientStack, slotStack)) {
					continue;
				}
				clientStack = slotStack.isEmpty() ? ItemStack.EMPTY : slotStack.copy();
				inventoryItemStacks.set(i, clientStack);
				for (final IContainerListener listener : listeners) {
					if (listener instanceof EntityPlayerMP) {
						ModNetworking.getInstance().sendTo(new PacketUpdateSlot(i, clientStack), (EntityPlayerMP) listener);
					}
				}
			}
		}
	}

	@Override
	public ItemStack slotClick(final int index, final int dragType, final ClickType clickType, final EntityPlayer player) {
		final Slot slot = getSlot(index);
		if (slot == null || index < 36 && clickType != ClickType.QUICK_MOVE || clickType == ClickType.CLONE) {
			return super.slotClick(index, dragType, clickType, player);
		}
		if (clickType == ClickType.QUICK_MOVE) {
			return transferStackInSlot(player, index);
		}
		final InventoryPlayer inventoryPlayer = player.inventory;
		final ItemStack heldStack = inventoryPlayer.getItemStack();
		if (slot instanceof SlotDankNull && clickType == ClickType.PICKUP) {
			final ItemStack slotStack = slot.getStack();
			if (ItemDankNull.isDankNull(slotStack)) {
				return ItemStack.EMPTY;
			}
			if (!heldStack.isEmpty()) { // Want to insert held stack into DankNull
				final ItemStack toAdd = heldStack.copy();
				if (dragType == 1) {
					toAdd.setCount(1);
				}

				final ItemStack leftover = addStack(toAdd);
				if (dragType == 0) {
					if (!leftover.isEmpty()) {
						inventoryPlayer.setItemStack(leftover);
					}
					else {
						inventoryPlayer.setItemStack(ItemStack.EMPTY);
					}
				}
				else if (dragType == 1 && leftover.isEmpty()) { // We right clicked and was able to add 1 to DankNull
					heldStack.shrink(1);
					inventoryPlayer.setItemStack(heldStack);
				}
				inventoryPlayer.markDirty(); // Probably not needed

				if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).updateHeldItem();
				}
			}
			else if (!slotStack.isEmpty()) { // Want to take stack out of DankNull
				int amount = Math.min(slotStack.getCount(), slotStack.getMaxStackSize());
				if (dragType == 1) {
					amount = Math.floorDiv(amount, 2);
				}
				final ItemStack newStack = slot.decrStackSize(amount);
				inventoryPlayer.setItemStack(newStack);
				inventoryPlayer.markDirty();
				if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).updateHeldItem();
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int index) {
		final Slot clickSlot = inventorySlots.get(index);
		if (clickSlot.getHasStack()) {
			if (!(clickSlot instanceof SlotDankNull)) { // Shift click from Player Inventory
				final ItemStack leftover = addStack(clickSlot.getStack());
				clickSlot.putStack(leftover);
				player.inventory.markDirty();
				return ItemStack.EMPTY;
			}
			else {
				final int slotIndex = clickSlot.getSlotIndex();
				final ItemStack slotStack = getHandler().extractItem(slotIndex, getHandler().getStackInSlot(slotIndex).getMaxStackSize(), true);
				if (!getHandler().getTier().isCreative()) {
					final IItemHandler playerHandler = new PlayerMainInvWrapper(player.inventory);
					final ItemStack notAdded = ItemHandlerHelper.insertItemStacked(playerHandler, slotStack, false);
					if (notAdded.getCount() < slotStack.getCount()) {
						getHandler().extractItem(slotIndex, slotStack.getCount() - notAdded.getCount(), false);
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

	private ItemStack addStack(final ItemStack stack) {
		ItemStack leftover = stack.copy();
		final IDankNullHandler handler = getHandler();
		for (int i = 0; i < handler.getSlots(); i++) {
			if (handler.isItemValid(i, leftover)) {
				leftover = handler.insertItem(i, leftover, false);
			}
		}
		for (int i = 0; i < handler.getSlots(); i++) {
			if (handler.getStackInSlot(i).isEmpty() && handler.isItemValid(i, leftover)) {
				handler.setStackInSlot(i, leftover);
				return ItemStack.EMPTY;
			}
		}
		return leftover;
	}

}
