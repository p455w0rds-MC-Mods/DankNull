package p455w0rd.danknull.container;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
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
	private final EntityPlayer holdingPlayer;
	private int thisSlot = -1;

	public ContainerDankNull(EntityPlayer player, InventoryDankNull inv) {
		holdingPlayer = player;
		inventoryDankNull = inv;
		InventoryPlayer playerInv = player.inventory;
		ItemStack dankNull = inv.getDankNull();
		int lockedSlot = -1;
		int numRows = dankNull.getItemDamage() + 1;
		for (int i = 0; i < playerInv.getSizeInventory(); i++) {
			ItemStack currStack = playerInv.getStackInSlot(i);
			if (!currStack.isEmpty() && currStack == dankNull) {
				lockedSlot = i;
				thisSlot = i;
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new SlotHotbar(player.inventory, i, i * 20 + (9 + i), 90 + (numRows - 1) + (numRows * 20 + 6), lockedSlot == i));

		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, j * 20 + (9 + j), 149 + (numRows - 1) + i - (6 - numRows) * 20 + i * 20));
			}
		}
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new SlotDankNull(inventoryDankNull, j + i * 9, j * 20 + (9 + j), 19 + i + i * 20));
			}
		}
		markDirty();
	}

	public int getHeldSlot() {
		return thisSlot;
	}

	@Override
	public void addListener(IContainerListener listener) {
		if (listener instanceof EntityPlayerMP) {
			playerList.add((EntityPlayerMP) listener);
			//return;
		}
		super.addListener(listener);
	}

	public void markDirty() {
		for (EntityPlayerMP player : playerList) {
			player.sendContainerToPlayer(this);
		}

		if (FMLCommonHandler.instance().getSide().isServer()) {
			//for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {

			//}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public ItemStack getDankNull() {
		return getDankNullInventory().getDankNull();
	}

	public InventoryDankNull getDankNullInventory() {
		return inventoryDankNull;
	}

	private boolean addStack(ItemStack stack) {
		boolean ret = false;
		if (stack.getItem() == ModItems.DANK_NULL) {
			return false;
		}
		if (!DankNullUtils.isFiltered(getDankNullInventory(), stack).isEmpty()) {
			ret = DankNullUtils.addFilteredStackToDankNull(getDankNullInventory(), stack);
		}
		else if (getNextAvailableSlot() >= 0) {
			inventorySlots.get(getNextAvailableSlot()).putStack(stack);
			ret = true;
		}
		if (DankNullUtils.getSelectedStackIndex(getDankNullInventory()) == -1) {
			DankNullUtils.setSelectedIndexApplicable(getDankNullInventory());
		}
		return ret;
	}

	private int getNextAvailableSlot() {
		for (int i = 36; i < inventorySlots.size(); i++) {
			Slot s = inventorySlots.get(i);
			if ((s != null) && (s.getStack().isEmpty())) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot clickSlot = inventorySlots.get(index);
		if (clickSlot.getHasStack()) {
			if (!isDankNullSlot(clickSlot)) {
				if (getNextAvailableSlot() == -1 && !DankNullUtils.isFiltered(getDankNullInventory(), clickSlot.getStack()).isEmpty()) {
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
				ItemStack newStack = clickSlot.getStack().copy();
				int realMaxStackSize = newStack.getMaxStackSize();
				int currentStackSize = newStack.getCount();
				if (currentStackSize > realMaxStackSize) {
					newStack.setCount(realMaxStackSize);
					if (moveStackToInventory(newStack)) {
						DankNullUtils.decrDankNullStackSize(getDankNullInventory(), clickSlot.getStack(), realMaxStackSize);
					}
				}
				else {
					newStack.setCount(currentStackSize);
					if (moveStackToInventory(newStack)) {
						DankNullUtils.decrDankNullStackSize(getDankNullInventory(), clickSlot.getStack(), currentStackSize);
						clickSlot.putStack(ItemStack.EMPTY);
					}
					DankNullUtils.reArrangeStacks(getDankNullInventory());
				}
			}
		}
		//markDirty();
		return ItemStack.EMPTY;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	private boolean isDankNullSlot(Slot slot) {
		return slot instanceof SlotDankNull;
	}

	private boolean moveStackWithinInventory(ItemStack itemStackIn, int index) {
		if (isInHotbar(index)) {
			if (mergeItemStack(itemStackIn, 9, 37, false)) {
				return true;
			}
			for (int i = 9; i <= 36; i++) {
				Slot possiblyOpenSlot = inventorySlots.get(i);
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
				Slot possiblyOpenSlot = inventorySlots.get(i);
				if (!possiblyOpenSlot.getHasStack()) {
					possiblyOpenSlot.putStack(itemStackIn);
					inventorySlots.get(index).putStack(ItemStack.EMPTY);
					return true;
				}
			}
		}
		return false;
	}

	private boolean isInHotbar(int index) {
		return (index >= 0) && (index <= 8);
	}

	private boolean isInInventory(int index) {
		return (index >= 9) && (index <= 36);
	}

	protected boolean moveStackToInventory(ItemStack itemStackIn) {
		for (int i = 0; i <= 36; i++) {
			Slot possiblyOpenSlot = inventorySlots.get(i);
			if (!possiblyOpenSlot.getHasStack()) {
				possiblyOpenSlot.putStack(itemStackIn);
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack slotClick(int index, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		InventoryPlayer inventoryplayer = player.inventory;
		ItemStack heldStack = inventoryplayer.getItemStack();
		if (index < 0) {
			return super.slotClick(index, dragType, clickTypeIn, player);
		}
		Slot s = inventorySlots.get(index);
		if (s.getStack() == inventoryDankNull.getDankNull()) {
			return ItemStack.EMPTY;
		}
		if (isDankNullSlot(s)) {
			if (heldStack.getItem() == ModItems.DANK_NULL) {
				return ItemStack.EMPTY;
			}
			ItemStack thisStack = s.getStack();
			if (!thisStack.isEmpty() && (thisStack.getItem() == ModItems.DANK_NULL)) {
				return ItemStack.EMPTY;
			}
			if (!heldStack.isEmpty()) {
				if (addStack(heldStack)) {
					inventoryplayer.setItemStack(ItemStack.EMPTY);
					return heldStack;
				}
				else {
					if (thisStack.getCount() <= thisStack.getMaxStackSize()) {
						inventoryplayer.setItemStack(thisStack);
						s.putStack(heldStack);
					}
					return ItemStack.EMPTY;
				}
			}
			else {
				if (!thisStack.isEmpty() && clickTypeIn == ClickType.PICKUP) {
					int max = thisStack.getMaxStackSize();
					ItemStack newStack = thisStack.copy();

					if (thisStack.getCount() >= max) {
						newStack.setCount(max);
					}
					if (dragType == 1) {
						int returnSize = Math.min(newStack.getCount() / 2, newStack.getCount());
						if (getDankNullInventory() != null) {
							DankNullUtils.decrDankNullStackSize(getDankNullInventory(), thisStack, newStack.getCount() - returnSize);
							newStack.setCount(returnSize + ((newStack.getCount() % 2 == 0) ? 0 : 1));
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
					return newStack;
				}
			}
		}
		ItemStack ret = super.slotClick(index, dragType, clickTypeIn, player);
		return ret;

	}

}