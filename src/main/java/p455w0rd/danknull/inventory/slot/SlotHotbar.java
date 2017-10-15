package p455w0rd.danknull.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * @author p455w0rd
 *
 */
public class SlotHotbar extends Slot {

	boolean locked = false;

	public SlotHotbar(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		this(inventoryIn, index, xPosition, yPosition, false);
	}

	public SlotHotbar(IInventory inventoryIn, int index, int xPosition, int yPosition, boolean shouldLock) {
		super(inventoryIn, index, xPosition, yPosition);
		locked = shouldLock;
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return !locked;
	}

}
