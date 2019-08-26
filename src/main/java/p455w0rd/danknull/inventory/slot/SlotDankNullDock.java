package p455w0rd.danknull.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class SlotDankNullDock extends Slot {

	private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
	final ContainerDankNullDock c;

	public SlotDankNullDock(final ContainerDankNullDock c, final int index, final int xPosition, final int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.c = c;
	}

	public TileDankNullDock getTile() {
		return c.getTile();
	}

	public ItemStack getDankNull() {
		return getTile().getDankNull();
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
		return getTile().getActualStackInSlot(getSlotIndex());
	}

	@Override
	public void putStack(final ItemStack stack) {
		final InventoryDankNull tmpInv = DankNullUtils.getNewDankNullInventory(getDankNull());
		tmpInv.setInventorySlotContents(getSlotIndex(), stack);
		tmpInv.markDirty();
		onSlotChanged();
	}

	@Override
	public void onSlotChanged() {
		//getTile().markDirty();
	}

	@Override
	public int getSlotStackLimit() {
		return DankNullUtils.getTier(c.getDankNull()).getMaxStackSize();
	}

	@Override
	public int getItemStackLimit(final ItemStack stack) {
		return getSlotStackLimit();
	}

	@Override
	public ItemStack decrStackSize(final int amount) {
		final InventoryDankNull tmpInv = DankNullUtils.getNewDankNullInventory(getDankNull());
		final ItemStack ret = tmpInv.decrStackSize(getSlotIndex(), amount);
		tmpInv.markDirty();
		return ret;
	}

	@Override
	public boolean isHere(final IInventory inv, final int slotIn) {
		return slotIn == getSlotIndex();
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
