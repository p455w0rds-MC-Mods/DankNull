package p455w0rd.danknull.container;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.inventory.slot.SlotHotbar;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class ContainerDankNull extends Container {

	InventoryDankNull inventoryDankNull;
	private final Set<EntityPlayerMP> playerList = Sets.<EntityPlayerMP>newHashSet();
	private final EntityPlayer player;
	private final int playerInvSlot;

	public ContainerDankNull(final EntityPlayer player, final int slot, final InventoryDankNull inv) {
		inventoryDankNull = inv;
		this.player = player;
		playerInvSlot = slot;
		final InventoryPlayer playerInv = player.inventory;
		final ItemStack dankNull = inv.getDankNull();
		int lockedSlot = -1;
		int numRows = dankNull.getItemDamage() + 1;
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
			addSlotToContainer(new SlotHotbar(player.inventory, i, i * 20 + 9 + i, 90 + numRows - 1 + numRows * 20 + 6, lockedSlot == i));

		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, j * 20 + 9 + j, 149 + numRows - 1 + i - (6 - numRows) * 20 + i * 20));
			}
		}
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new SlotDankNull(inventoryDankNull, j + i * 9, j * 20 + 9 + j, 19 + i + i * 20));
			}
		}
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public int getPlayerInvSlot() {
		return playerInvSlot;
	}

	/*
	@Override
	public void addListener(IContainerListener listener) {
		if (listener instanceof EntityPlayerMP) {
			EntityPlayerMP l = (EntityPlayerMP) listener;
			if (!playerList.contains(l)) {
				playerList.add(l);
			}
		}
		//sync();
	}
	*/

	@Override
	public boolean canInteractWith(final EntityPlayer playerIn) {
		return true;
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

	private boolean addStack(final ItemStack stack) {
		boolean ret = false;
		if (stack.getItem() == ModItems.DANK_NULL) {
			return false;
		}
		if (DankNullUtils.isFiltered(getDankNullInventory(), stack)) {
			ret = DankNullUtils.addFilteredStackToDankNull(getDankNullInventory(), stack);
		}
		else if (DankNullUtils.getNextAvailableSlot(this) >= 0) {
			ret = DankNullUtils.addUnfiliteredStackToDankNull(this, stack);
		}
		if (DankNullUtils.getSelectedStackIndex(getDankNullInventory()) == -1) {
			DankNullUtils.setSelectedIndexApplicable(getDankNullInventory());
		}
		DankNullUtils.reArrangeStacks(getDankNullInventory());
		return ret;
	}

	@Override
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

		return ItemStack.EMPTY;
	}

	@Override
	public void detectAndSendChanges() {
		for (int i = 0; i < inventorySlots.size(); ++i) {
			final ItemStack itemstack = inventorySlots.get(i).getStack();
			final ItemStack itemstack1 = inventoryItemStacks.get(i);
			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
				final boolean clientStackChanged = !ItemStack.areItemStacksEqualUsingNBTShareTag(itemstack1, itemstack);
				if (clientStackChanged) {
					//sync();
				}
			}
		}
		//super.detectAndSendChanges();

		//TODO remembher to redisable
		for (final EntityPlayerMP player : playerList) {
			for (int i = 0; i < inventorySlots.size(); ++i) {
				final ItemStack itemstack = inventorySlots.get(i).getStack();
				ItemStack itemstack1 = inventoryItemStacks.get(i);
				if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
					final boolean clientStackChanged = !ItemStack.areItemStacksEqualUsingNBTShareTag(itemstack1, itemstack);
					itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
					inventoryItemStacks.set(i, itemstack1);
					if (clientStackChanged) {
						player.sendSlotContents(this, i, itemstack1);
					}
				}
			}
		}

		//
	}

	public void sync() {
		if (FMLCommonHandler.instance().getSide().isServer()) {
			for (final EntityPlayerMP player : playerList) {
				//ModNetworking.getInstance().sendTo(new PacketSyncDankNullDock(getDankNullInventory()), player);
			}
		}
		else {
			//ModNetworking.getInstance().sendToServer(new PacketSyncDankNull(getPlayerInvSlot(), getDankNullInventory().getDankNull()));
			//ModNetworking.getInstance().sendToServer(new PacketSyncDankNull(getDankNullInventory()));
		}
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

	@Override
	public ItemStack slotClick(final int index, final int dragType, final ClickType clickTypeIn, final EntityPlayer player) {
		final InventoryPlayer inventoryplayer = player.inventory;
		final ItemStack heldStack = inventoryplayer.getItemStack();
		if (index < 0) {
			//sync();
			return super.slotClick(index, dragType, clickTypeIn, player);
		}
		final Slot s = inventorySlots.get(index);
		if (s.getStack() == inventoryDankNull.getDankNull()) {
			//sync();
			return ItemStack.EMPTY;
		}
		if (isDankNullSlot(s)) {
			if (heldStack.getItem() == ModItems.DANK_NULL) {
				//sync();
				return ItemStack.EMPTY;
			}
			final ItemStack thisStack = s.getStack();
			if (!thisStack.isEmpty() && thisStack.getItem() == ModItems.DANK_NULL) {
				//sync();
				return ItemStack.EMPTY;
			}
			if (!heldStack.isEmpty()) {
				if (addStack(heldStack)) {
					inventoryplayer.setItemStack(ItemStack.EMPTY);
					sync();
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
					DankNullUtils.reArrangeStacks(getDankNullInventory());
					sync();
					return ItemStack.EMPTY;
				}
			}
		}
		super.slotClick(index, dragType, clickTypeIn, player);
		if (player instanceof EntityPlayerMP) {
			sync();
		}
		return ItemStack.EMPTY;

	}

}