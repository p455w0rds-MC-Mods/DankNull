package p455w0rd.danknull.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;

/**
 * @author p455w0rd
 *
 */
public class DankNullSidedInvWrapper extends SidedInvWrapper {

	TileDankNullDock tile;

	public DankNullSidedInvWrapper(final TileDankNullDock inv, final EnumFacing side) {
		super(inv, side);
		tile = inv;
	}

	@Override
	@Nonnull
	public ItemStack insertItem(final int slot, @Nonnull ItemStack stack, final boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		final int slot1 = getSlot(inv, slot, side);

		if (slot1 == -1) {
			return stack;
		}

		final ItemStack stackInSlot = tile.getActualStackInSlot(slot1);

		int m;
		if (!stackInSlot.isEmpty()) {
			//if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot))) return stack;

			//if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) return stack;

			if (!inv.canInsertItem(slot1, stack, side) || !inv.isItemValidForSlot(slot1, stack)) {
				return stack;
			}

			m = getSlotLimit(slot) - stackInSlot.getCount();//Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

			if (stack.getCount() <= m) {
				if (!simulate) {
					final ItemStack copy = stack.copy();
					copy.grow(stackInSlot.getCount());
					setInventorySlotContents(slot1, copy);
				}

				return ItemStack.EMPTY;
			}
			else {
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate) {
					final ItemStack copy = stack.splitStack(m);
					copy.grow(stackInSlot.getCount());
					setInventorySlotContents(slot1, copy);
					return stack;
				}
				else {
					stack.shrink(m);
					return stack;
				}
			}
		}
		else {
			if (!inv.canInsertItem(slot1, stack, side) || !inv.isItemValidForSlot(slot1, stack)) {
				return stack;
			}

			m = getSlotLimit(slot);//Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
			if (m < stack.getCount()) {
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate) {
					setInventorySlotContents(slot1, stack.splitStack(m));
					return stack;
				}
				else {
					stack.shrink(m);
					return stack;
				}
			}
			else {
				if (!simulate) {
					setInventorySlotContents(slot1, stack);
				}
				return ItemStack.EMPTY;
			}
		}

	}

	private void setInventorySlotContents(final int slot, final ItemStack stack) {
		//inv.markDirty(); //Notify vanilla of updates, We change the handler to be responsible for this instead of the caller. So mimic vanilla behavior
		inv.setInventorySlotContents(slot, stack);
	}

}
