package p455w0rd.danknull.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class SlotDankNull extends Slot {

	public SlotDankNull(final PlayerSlot slot, final EntityPlayer player, final int idx, final int x, final int y) {
		super(DankNullUtils.getNewDankNullInventory(slot, player), idx, x, y);
	}

	@Override
	public boolean isItemValid(final ItemStack itemStackIn) {
		return !(itemStackIn.getItem() instanceof ItemDankNull);
	}

	@Override
	public boolean getHasStack() {
		return !getStack().isEmpty();
	}

	@Override
	public ItemStack getStack() {
		return DankNullUtils.getStackInDankNullSlotWithSize(((InventoryDankNull) inventory).getDankNull(), getSlotIndex());
	}

	@Override
	public void putStack(ItemStack stack) {
		if (!stack.isEmpty() && stack.getCount() <= 0) {
			stack = ItemStack.EMPTY;
		}
		inventory.setInventorySlotContents(getSlotIndex(), stack);
		inventory.markDirty();
		onSlotChanged();
	}

	@Override
	public int getSlotStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public int getItemStackLimit(final ItemStack stack) {
		return getSlotStackLimit();
	}

	@Override
	public ItemStack decrStackSize(final int amount) {
		return inventory.decrStackSize(getSlotIndex(), amount);
	}

	@Override
	public boolean isHere(final IInventory inv, final int slotIn) {
		return inv == inventory && slotIn == getSlotIndex();
	}

	@Override
	public boolean canTakeStack(final EntityPlayer playerIn) {
		return true;
	}

	public int getX() {
		return xPos;
	}

	public int getY() {
		return yPos;
	}

}
