package p455w0rd.danknull.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;

/**
 * @author p455w0rd
 */
public class ContainerDankNullItem extends ContainerDankNull {

    private final PlayerSlot playerSlot;
    private final IDankNullHandler handler;

    public ContainerDankNullItem(final EntityPlayer player, final PlayerSlot slot) {
        super(player);
        playerSlot = slot;
        final InventoryPlayer playerInv = player.inventory;
        final ItemStack dankNull = playerInv.getStackInSlot(slot.getCatIndex() == 2 ? 40 : slot.getSlotIndex());
        handler = dankNull.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
        init();
    }

    @Override
    public IDankNullHandler getHandler() {
        return handler;
    }

    @Override
    public ItemStack getDankNullStack() {
        return player.inventory.getStackInSlot(playerSlot.getSlotIndex());
    }

}