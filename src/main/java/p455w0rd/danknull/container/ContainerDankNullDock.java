package p455w0rd.danknull.container;

import java.util.ArrayList;
import java.util.List;
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
import net.minecraftforge.fml.relauncher.Side;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.inventory.slot.SlotHotbar;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class ContainerDankNullDock extends Container {

	InventoryDankNull inventoryDankNull;
	private final Set<EntityPlayerMP> playerList = Sets.<EntityPlayerMP>newHashSet();
	private List<SlotDankNull> dankNullSlots = new ArrayList<SlotDankNull>();
	private TileDankNullDock tile;
	Side side;

	public ContainerDankNullDock(EntityPlayer player, TileDankNullDock tile) {
		side = player.getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER;
		this.tile = tile;
		inventoryDankNull = tile.getInventory();
		InventoryPlayer playerInv = player.inventory;
		ItemStack dankNull = tile.getDankNull();
		int lockedSlot = -1;
		int numRows = dankNull.getItemDamage() + 1;
		if (DankNullUtils.isCreativeDankNull(dankNull)) {
			numRows--;
		}
		for (int i = 0; i < playerInv.getSizeInventory(); i++) {
			ItemStack currStack = playerInv.getStackInSlot(i);
			if (!currStack.isEmpty() && currStack == dankNull) {
				lockedSlot = i;
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
				SlotDankNull s = new SlotDankNull(inventoryDankNull, j + i * 9, j * 20 + (9 + j), 19 + i + i * 20);
				addSlotToContainer(s);
				dankNullSlots.add(s);
			}
		}
	}

	public TileDankNullDock getDankNullTile() {
		return tile;
	}

	@Override
	public void addListener(IContainerListener listener) {
		if (listener instanceof EntityPlayerMP) {
			EntityPlayerMP l = (EntityPlayerMP) listener;
			if (!playerList.contains(l)) {
				playerList.add(l);
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public List<SlotDankNull> getDankNullSlots() {
		return dankNullSlots;
	}

	public ItemStack getDankNull() {
		return getDankNullInventory().getDankNull();
	}

	public InventoryDankNull getDankNullInventory() {
		return inventoryDankNull;
	}

	public void setDankNullInventory(InventoryDankNull inv) {
		inventoryDankNull = inv;
	}

	private boolean addStack(ItemStack stack) {
		boolean ret = false;
		if (stack.getItem() == ModItems.DANK_NULL) {
			return false;
		}
		if (DankNullUtils.isFiltered(getDankNullInventory(), stack)) {
			ret = DankNullUtils.addFilteredStackToDankNull(getDankNullInventory(), stack);
		}
		else if (DankNullUtils.getNextAvailableSlot(this) >= 0) {
			ret = DankNullUtils.addUnfiliteredFilteredStackToDankNull(this, stack);
		}
		if (DankNullUtils.getSelectedStackIndex(getDankNullInventory()) == -1) {
			DankNullUtils.setSelectedIndexApplicable(getDankNullInventory());
		}
		DankNullUtils.reArrangeStacks(getDankNullInventory());
		return ret;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot clickSlot = inventorySlots.get(index);
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
				ItemStack newStack = clickSlot.getStack().copy();
				int realMaxStackSize = newStack.getMaxStackSize();
				int currentStackSize = newStack.getCount();
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
		if (side.isServer()) {
			for (EntityPlayerMP player : playerList) {
				//ModNetworking.getInstance().sendTo(new PacketSyncDankNullDock(getDankNullTile(), getDankNullInventory().getDankNull()), player);
			}
			return;
		}
		//ModNetworking.getInstance().sendToServer(new PacketSyncDankNullDock(getDankNullTile(), getDankNullInventory().getDankNull()));
		//super.detectAndSendChanges();

		/*
		for (EntityPlayerMP player : playerList) {
			for (int i = 0; i < inventorySlots.size(); ++i) {
				ItemStack itemstack = inventorySlots.get(i).getStack();
				ItemStack itemstack1 = inventoryItemStacks.get(i);
				if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
					boolean clientStackChanged = !ItemStack.areItemStacksEqualUsingNBTShareTag(itemstack1, itemstack);
					itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
					inventoryItemStacks.set(i, itemstack1);
					if (clientStackChanged) {
						player.sendSlotContents(this, i, itemstack1);
					}
				}
			}
		}
		*/
		//
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
			//sync();
			return super.slotClick(index, dragType, clickTypeIn, player);
		}
		Slot s = inventorySlots.get(index);
		if (s.getStack() == inventoryDankNull.getDankNull()) {
			//sync();
			return ItemStack.EMPTY;
		}
		if (isDankNullSlot(s)) {
			if (heldStack.getItem() == ModItems.DANK_NULL) {
				//sync();
				return ItemStack.EMPTY;
			}
			ItemStack thisStack = s.getStack();
			if (!thisStack.isEmpty() && (thisStack.getItem() == ModItems.DANK_NULL)) {
				detectAndSendChanges();
				return ItemStack.EMPTY;
			}
			if (!heldStack.isEmpty()) {
				if (addStack(heldStack)) {
					inventoryplayer.setItemStack(ItemStack.EMPTY);
					detectAndSendChanges();
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
					detectAndSendChanges();
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
					detectAndSendChanges();
					return ItemStack.EMPTY;
				}
			}
		}
		super.slotClick(index, dragType, clickTypeIn, player);
		return ItemStack.EMPTY;

	}

}
