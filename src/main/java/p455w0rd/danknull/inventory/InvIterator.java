package p455w0rd.danknull.inventory;

import java.util.Iterator;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class InvIterator implements Iterator<ItemStack> {
	private final IInventory inventory;
	private final int size;
	private int counter = 0;

	public InvIterator(IInventory inventory) {
		this.inventory = inventory;
		size = this.inventory.getSizeInventory();
	}

	@Override
	public boolean hasNext() {
		return counter < size;
	}

	@Override
	public ItemStack next() {
		ItemStack result = inventory.getStackInSlot(counter);
		counter += 1;

		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
