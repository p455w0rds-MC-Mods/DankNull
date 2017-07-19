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
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.slot.DankNullSlot;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class ContainerDankNull extends Container {

	InventoryDankNull inventoryDankNull;
	private final Set<EntityPlayerMP> playerList = Sets.<EntityPlayerMP>newHashSet();

	public ContainerDankNull(EntityPlayer player, InventoryDankNull inv) {
		inventoryDankNull = inv;
		ItemStack dankNull = inv.getDankNull();
		int numRows = dankNull.getItemDamage() + 1;
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player.inventory, i, i * 20 + (9 + i), 90 + (numRows - 1) + (numRows * 20 + 6)));

		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, j * 20 + (9 + j), 149 + (numRows - 1) + i - (6 - numRows) * 20 + i * 20));
			}
		}
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new DankNullSlot(inventoryDankNull, j + i * 9, j * 20 + (9 + j), 19 + i + i * 20));
			}
		}
	}

	@Override
	public void addListener(IContainerListener listener) {
		if (!(listener instanceof EntityPlayerMP)) {
			super.addListener(listener);
			return;
		}
		playerList.add((EntityPlayerMP) listener);
	}

	public void markDirty() {
		for (EntityPlayerMP player : playerList) {
			player.sendContainerToPlayer(this);
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
		if (DankNullUtils.isFiltered(getDankNullInventory(), stack) != null) {
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
				if (getNextAvailableSlot() == -1 && DankNullUtils.isFiltered(getDankNullInventory(), clickSlot.getStack()) != null) {
					if (!moveStackWithinInventory(clickSlot.getStack(), index)) {
						return null;
					}
					//getDankNullInventory().serializeNBT();
					return clickSlot.getStack();
				}
				if (addStack(clickSlot.getStack())) {
					clickSlot.putStack(ItemStack.EMPTY);
					playerIn.inventory.markDirty();
					//DankNullUtils.setSelectedStackIndex(dankNullStack, 0);
					//inventoryDankNull.markDirty();
					//DankNullUtils.reArrangeStacks(getDankNull());
					//arrangeSlots();
					DankNullUtils.setSelectedIndexApplicable(getDankNullInventory());
					//writeToNBT();
					//getDankNullInventory().serializeNBT();
				}
			}
			else {
				ItemStack newStack = clickSlot.getStack().copy();
				int realMaxStackSize = newStack.getMaxStackSize();
				int currentStackSize = newStack.getCount();
				//newStack.getTagCompound().removeTag("p455w0rd.StackSize");
				//if (newStack.getTagCompound().hasNoTags()) {
				//	newStack.setTagCompound(null);
				//}
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
					//arrangeSlots();
					DankNullUtils.reArrangeStacks(getDankNullInventory());
					//DankNullUtils.setSelectedIndexApplicable(getDankNull());
					//writeToNBT();
				}
			}
		}
		//markDirty();
		return ItemStack.EMPTY;
	}

	@Override
	public void detectAndSendChanges() {
		//super.detectAndSendChanges();
		if (FMLCommonHandler.instance().getSide().isServer()) {
			for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
				//ModNetworking.INSTANCE.sendTo(new PacketSyncDankNull((NBTTagCompound) getDankNullInventory().serializeNBT()), player);
			}
		}
	}

	private boolean isDankNullSlot(Slot slot) {
		return slot instanceof DankNullSlot;
	}

	private boolean moveStackWithinInventory(ItemStack itemStackIn, int index) {
		if (isInHotbar(index)) {
			for (int i = 9; i <= 36; i++) {
				Slot possiblyOpenSlot = inventorySlots.get(i);
				if (!possiblyOpenSlot.getHasStack()) {
					possiblyOpenSlot.putStack(itemStackIn);
					inventorySlots.get(index).putStack(null);
					return true;
				}
			}
		}
		else if (isInInventory(index)) {
			for (int i = 0; i <= 8; i++) {
				Slot possiblyOpenSlot = inventorySlots.get(i);
				if (!possiblyOpenSlot.getHasStack()) {
					possiblyOpenSlot.putStack(itemStackIn);
					inventorySlots.get(index).putStack(null);
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
		if ((index == -999) || (index == -1)) {
			if (!inventoryplayer.getItemStack().isEmpty()) {
				if (dragType == 0) {
					player.dropItem(inventoryplayer.getItemStack(), true);
					inventoryplayer.setItemStack(ItemStack.EMPTY);
				}
				if (dragType == 1) {
					player.dropItem(inventoryplayer.getItemStack().splitStack(1), true);
					if (inventoryplayer.getItemStack().getCount() == 0) {
						inventoryplayer.setItemStack(ItemStack.EMPTY);
					}
				}
			}
			//detectAndSendChanges();
			//markDirty();
			return heldStack;
		}
		Slot s = inventorySlots.get(index);
		if (isDankNullSlot(s)) {
			ItemStack thisStack = s.getStack();
			if ((!thisStack.isEmpty()) && ((thisStack.getItem() instanceof ItemDankNull))) {
				return null;
			}
			if (index == -1) {
				markDirty();
				return heldStack;
			}
			if (!heldStack.isEmpty()) {
				if (addStack(heldStack)) {
					inventoryplayer.setItemStack(ItemStack.EMPTY);
					//detectAndSendChanges();
					//markDirty();
					return heldStack;
				}
			}
			else {
				if (!thisStack.isEmpty() && clickTypeIn == ClickType.PICKUP) {
					//System.out.println(dragType + " " + clickTypeIn);
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
					//getDankNullInventory().markDirty();
					//detectAndSendChanges();
					//markDirty();
					DankNullUtils.reArrangeStacks(getDankNullInventory());
					return newStack;
				}
			}
		}
		//else if ((index != -1) && (index != -999)) {
		else if (s.getStack() != null) {
			if (s.getStack().getItem() instanceof ItemDankNull) {
				return ItemStack.EMPTY;
			}
		}

		//}
		ItemStack ret = super.slotClick(index, dragType, clickTypeIn, player);
		//markDirty();
		return ret;

	}

}