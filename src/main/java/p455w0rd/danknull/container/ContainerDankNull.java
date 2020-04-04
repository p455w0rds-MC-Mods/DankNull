package p455w0rd.danknull.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.inventory.slot.SlotDankNullDock;
import p455w0rd.danknull.inventory.slot.SlotHotbar;
import p455w0rd.danknull.items.ItemDankNull;

/**
 * @author BrockWS
 */
public abstract class ContainerDankNull extends Container {

    protected final EntityPlayer player;

    public ContainerDankNull(final EntityPlayer player) {
        this.player = player;
    }

    protected void init() {
        final InventoryPlayer playerInv = player.inventory;
        final IDankNullHandler handler = getHandler();
        int lockedSlot = -1;
        final int numRows = handler.getTier().getNumRows();
        for (int i = 0; i < playerInv.getSizeInventory(); i++) {
            final ItemStack currStack = playerInv.getStackInSlot(i);
            if (!currStack.isEmpty() && currStack == getDankNullStack()) {
                lockedSlot = i;
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new SlotHotbar(playerInv, i, i * 20 + 9 + i, 90 + numRows - 1 + numRows * 20 + 6, lockedSlot == i));
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, j * 20 + 9 + j, 149 + numRows - 1 + i - (6 - numRows) * 20 + i * 20));
            }
        }
        for (int i = 0; i < handler.getTier().getNumRows(); i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(createDankNullSlot(handler, i, j));
            }
        }
    }

    public abstract IDankNullHandler getHandler();

    public abstract ItemStack getDankNullStack();

    protected boolean isDock() {
        return false;
    }

    private SlotDankNull createDankNullSlot(final IDankNullHandler handler, final int i, final int j) {
        return isDock() ? new SlotDankNullDock(handler, j + i * 9, j * 20 + 9 + j, 19 + i + i * 20) : new SlotDankNull(handler, j + i * 9, j * 20 + 9 + j, 19 + i + i * 20);
    }

    @Override
    public boolean canInteractWith(final EntityPlayer player) {
        return getHandler() != null;
    }

    @Override
    public Slot getSlot(final int slotId) {
        if (slotId < inventorySlots.size() && slotId >= 0) {
            return inventorySlots.get(slotId);
        }
        return null;
    }

    @Override
    public ItemStack slotClick(final int index, final int dragType, final ClickType clickType, final EntityPlayer player) {
        final Slot slot = getSlot(index);
        if (slot == null || index < 36 && clickType != ClickType.QUICK_MOVE || clickType == ClickType.CLONE) {
            return super.slotClick(index, dragType, clickType, player);
        }
        if (clickType == ClickType.QUICK_MOVE) {
            return transferStackInSlot(player, index);
        }
        final InventoryPlayer inventoryPlayer = player.inventory;
        final ItemStack heldStack = inventoryPlayer.getItemStack();
        if (slot instanceof SlotDankNull && clickType == ClickType.PICKUP) {
            final ItemStack slotStack = slot.getStack();
            if (ItemDankNull.isDankNull(slotStack)) {
                return ItemStack.EMPTY;
            }
            if (!heldStack.isEmpty()) { // Want to insert held stack into DankNull
                final ItemStack toAdd = heldStack.copy();
                if (dragType == 1) {
                    toAdd.setCount(1);
                }
                final ItemStack leftover = addStack(toAdd);
                if (dragType == 0) {
                    if (!leftover.isEmpty()) {
                        inventoryPlayer.setItemStack(leftover);
                    } else {
                        inventoryPlayer.setItemStack(ItemStack.EMPTY);
                    }
                } else if (dragType == 1 && leftover.isEmpty()) { // We right clicked and was able to add 1 to DankNull
                    heldStack.shrink(1);
                    inventoryPlayer.setItemStack(heldStack);
                }
                if (player instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) player).updateHeldItem();
                }
            } else if (!slotStack.isEmpty()) { // Want to take stack out of DankNull
                int amount = Math.min(slotStack.getCount(), slotStack.getMaxStackSize());
                if (dragType == 1) {
                    amount = Math.floorDiv(amount, 2);
                }
                final ItemStack newStack = slot.decrStackSize(amount);
                inventoryPlayer.setItemStack(newStack);
                if (player instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) player).updateHeldItem();
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack transferStackInSlot(final EntityPlayer player, final int index) {
        final Slot clickSlot = inventorySlots.get(index);
        if (clickSlot.getHasStack()) {
            if (!(clickSlot instanceof SlotDankNull)) { // Shift click from Player Inventory
                final ItemStack leftover = addStack(clickSlot.getStack());
                clickSlot.putStack(leftover);
                player.inventory.markDirty();
                return ItemStack.EMPTY;
            } else {
                final int slotIndex = clickSlot.getSlotIndex();
                final ItemStack slotStack = getHandler().extractItem(slotIndex, getHandler().getFullStackInSlot(slotIndex).getMaxStackSize(), true);
                if (!getHandler().getTier().isCreative()) {
                    final IItemHandler playerHandler = new PlayerMainInvWrapper(player.inventory);
                    final ItemStack notAdded = ItemHandlerHelper.insertItemStacked(playerHandler, slotStack, false);
                    if (notAdded.getCount() < slotStack.getCount()) {
                        getHandler().extractItemIngoreExtractionMode(slotIndex, slotStack.getCount() - notAdded.getCount(), false);
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack addStack(final ItemStack stack) {
        ItemStack leftover = stack.copy();
        final IDankNullHandler handler = getHandler();
        if (handler instanceof DankNullHandler) {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (handler.isItemValid(i, leftover)) {
                    leftover = handler.insertItem(i, leftover, false);
                    ((DankNullHandler) handler).updateSelectedSlot();
                }
            }
            for (int i = 0; i < handler.getSlots(); i++) {
                if (handler.getFullStackInSlot(i).isEmpty() && handler.isItemValid(i, leftover)) {
                    handler.setStackInSlot(i, leftover);
                    ((DankNullHandler) handler).updateSelectedSlot();
                    return ItemStack.EMPTY;
                }
            }
        }

        return leftover;
    }

}
