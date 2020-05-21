package p455w0rd.danknull.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;
import p455w0rd.danknull.items.ItemDankNull;

/**
 * @author p455w0rd
 */
public class ContainerDankNullItem extends ContainerDankNull {

    private final PlayerSlot playerSlot;
    private final IDankNullHandler handler;
    private final ItemStack dankNull;

    public ContainerDankNullItem(final EntityPlayer player, final PlayerSlot slot) {
        super(player);
        playerSlot = slot;
        final InventoryPlayer playerInv = player.inventory;
        dankNull = playerInv.getStackInSlot(slot.getCatIndex() == 2 ? 40 : slot.getSlotIndex());
        handler = dankNull.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
        init();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return playerSlot.getStackInSlot(player).getItem() instanceof ItemDankNull &&
            playerSlot.getStackInSlot(player) == dankNull &&
            super.canInteractWith(player);
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