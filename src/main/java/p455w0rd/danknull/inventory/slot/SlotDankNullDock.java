package p455w0rd.danknull.inventory.slot;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import p455w0rd.danknull.api.IDankNullHandler;

/**
 * @author p455w0rd
 *
 */
public class SlotDankNullDock extends SlotDankNull {

	private final int cachedIndex;

	public SlotDankNullDock(final IItemHandler handler, final int index, final int x, final int y) {
		super(handler, index, x, y);
		cachedIndex = index;
	}

	@Override
	public boolean isItemValid(@Nonnull final ItemStack stack) {
		if (stack.isEmpty() || !getItemHandler().isItemValid(cachedIndex, stack)) {
			return false;
		}

		final IItemHandler handler = getItemHandler();
		ItemStack remainder;
		if (handler instanceof IDankNullHandler) {
			final IDankNullHandler handlerModifiable = getDankNullHandler();
			final ItemStack currentStack = handlerModifiable.getFullStackInSlot(cachedIndex);

			handlerModifiable.setStackInSlot(cachedIndex, ItemStack.EMPTY);

			remainder = handlerModifiable.insertItem(cachedIndex, stack, true);

			handlerModifiable.setStackInSlot(cachedIndex, currentStack);
		}
		else {
			remainder = handler.insertItem(cachedIndex, stack, true);
		}
		return remainder.getCount() < stack.getCount();
	}

	/**
	 * Helper fnct to get the stack in the slot.
	 */
	@Override
	@Nonnull
	public ItemStack getStack() {
		return getDankNullHandler().getFullStackInSlot(cachedIndex);
	}

	public IDankNullHandler getDankNullHandler() {
		return (IDankNullHandler) getItemHandler();
	}

	// Override if your IItemHandler does not implement IItemHandlerModifiable
	/**
	 * Helper method to put a stack in the slot.
	 */
	@Override
	public void putStack(@Nonnull final ItemStack stack) {
		getDankNullHandler().setStackInSlot(cachedIndex, stack);
		onSlotChanged();
	}

	@Override
	public int getItemStackLimit(@Nonnull final ItemStack stack) {
		final ItemStack maxAdd = stack.copy();
		final int maxInput = stack.getMaxStackSize();
		maxAdd.setCount(maxInput);
		final ItemStack currentStack = getDankNullHandler().getFullStackInSlot(cachedIndex);
		getDankNullHandler().setStackInSlot(cachedIndex, ItemStack.EMPTY);
		final ItemStack remainder = getDankNullHandler().insertItem(cachedIndex, maxAdd, true);
		getDankNullHandler().setStackInSlot(cachedIndex, currentStack);
		return maxInput - remainder.getCount();
	}

	/**
	 * Return whether this slot's stack can be taken from this slot.
	 */
	@Override
	public boolean canTakeStack(final EntityPlayer playerIn) {
		return !getDankNullHandler().extractItemIngoreExtractionMode(cachedIndex, 1, true).isEmpty();
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
	 * stack.
	 */
	@Override
	@Nonnull
	public ItemStack decrStackSize(final int amount) {
		return getDankNullHandler().extractItemIngoreExtractionMode(cachedIndex, amount, false);
	}

}
