package p455w0rd.danknull.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.util.cap.CapabilityDankNull;
import p455w0rd.danknull.util.cap.IDankNullHandler;

/**
 * @author p455w0rd
 */
public class ContainerDankNull extends ContainerDankNullBase {

	private final PlayerSlot playerSlot;
	private IDankNullHandler handler;

	public ContainerDankNull(final EntityPlayer player, final PlayerSlot slot) {
		super(player);
		this.playerSlot = slot;
		InventoryPlayer playerInv = player.inventory;
		ItemStack dankNull = playerInv.getStackInSlot(slot.getSlotIndex());
		this.handler = dankNull.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
		this.init();
	}

	@Override
	public IDankNullHandler getHandler() {
		return this.handler;
	}

	@Override
	public ItemStack getDankNullStack() {
		return this.player.inventory.getStackInSlot(this.playerSlot.getSlotIndex());
	}
}