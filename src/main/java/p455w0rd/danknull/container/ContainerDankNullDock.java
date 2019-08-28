package p455w0rd.danknull.container;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.inventory.slot.SlotDankNullDock;
import p455w0rd.danknull.inventory.slot.SlotHotbar;
import p455w0rd.danknull.network.PacketChangeMode;
import p455w0rd.danknull.network.PacketUpdateSlot;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rd.danknull.util.cap.CapabilityDankNull;
import p455w0rd.danknull.util.cap.IDankNullHandler;

/**
 * @author p455w0rd
 */
public class ContainerDankNullDock extends Container {

	private final EntityPlayer player;
	private final TileDankNullDock tile;
	private final IDankNullHandler handler;

	public ContainerDankNullDock(final EntityPlayer player, final TileDankNullDock tile) {
		this.player = player;
		this.tile = tile;
		this.handler = tile.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
		final InventoryPlayer playerInv = player.inventory;
		final ItemStack dankNull = tile.getDankNull();
		int lockedSlot = -1;
		int numRows = DankNullUtils.getMeta(dankNull) + 1;
		if (DankNullUtils.isCreativeDankNull(dankNull)) {
			numRows--;
		}
		for (int i = 0; i < playerInv.getSizeInventory(); i++) {
			final ItemStack currStack = playerInv.getStackInSlot(i);
			if (!currStack.isEmpty() && currStack == dankNull) {
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
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new SlotDankNullDock(handler, j + i * 9, j * 20 + 9 + j, 19 + i + i * 20));
			}
		}
	}

	public TileDankNullDock getTile() {
		return tile;
	}

	@Override
	public boolean canInteractWith(final EntityPlayer player) {
		return this.handler != null;
	}

	public ItemStack getDankNull() {
		return getTile().getDankNull();
	}

	private ItemStack addStack(final ItemStack stack) {
		ItemStack leftover = stack.copy();
		for (int i = 0; i < this.handler.getSlots(); i++) {
			if (this.handler.isItemValid(i, leftover))
				leftover = this.handler.insertItem(i, leftover, false);
		}
		return leftover;
	}

	private boolean isInHotbar(final int index) {
		return index >= 0 && index <= 8;
	}

	private boolean isInInventory(final int index) {
		return index >= 9 && index <= 36;
	}

	@Override
	public void detectAndSendChanges() {
		//I'll take this, thanks
		for (int i = 0; i < this.inventorySlots.size(); ++i) {
			ItemStack slotStack = this.inventorySlots.get(i).getStack();
			ItemStack clientStack = this.inventoryItemStacks.get(i);

			if (!ItemStack.areItemStacksEqual(clientStack, slotStack)) {
				if (ItemStack.areItemStacksEqualUsingNBTShareTag(clientStack, slotStack))
					continue;
				clientStack = slotStack.isEmpty() ? ItemStack.EMPTY : slotStack.copy();
				this.inventoryItemStacks.set(i, clientStack);
				for (IContainerListener listener : this.listeners) {
					if (listener instanceof EntityPlayerMP)
						ModNetworking.getInstance().sendTo(new PacketUpdateSlot(i, clientStack), (EntityPlayerMP) listener);
				}
			}
		}
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
		final Slot slot = this.getSlot(index);
		if (slot == null || index < 36 && clickType != ClickType.QUICK_MOVE || clickType == ClickType.CLONE) {
			return super.slotClick(index, dragType, clickType, player);
		}
		if (clickType == ClickType.QUICK_MOVE) {
			return this.transferStackInSlot(player, index);
		}
		final InventoryPlayer inventoryPlayer = player.inventory;
		final ItemStack heldStack = inventoryPlayer.getItemStack();
		if (slot instanceof SlotDankNullDock && clickType == ClickType.PICKUP) {
			final ItemStack slotStack = slot.getStack();
			if (DankNullUtils.isDankNull(slotStack)) {
				return ItemStack.EMPTY;
			}
			if (!heldStack.isEmpty()) { // Want to insert held stack into DankNull
				ItemStack toAdd = heldStack.copy();
				if (dragType == 1) {
					toAdd.setCount(1);
				}

				ItemStack leftover = this.addStack(toAdd);
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
				inventoryPlayer.markDirty(); // Probably not needed

				if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).updateHeldItem();
				}
			} else if (!slotStack.isEmpty()) { // Want to take stack out of DankNull
				int amount = Math.min(slotStack.getCount(), slotStack.getMaxStackSize());
				if (dragType == 1)
					amount = Math.floorDiv(amount, 2);

				ItemStack newStack = slot.decrStackSize(amount);

				inventoryPlayer.setItemStack(newStack);
				inventoryPlayer.markDirty();

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
			if (!(clickSlot instanceof SlotDankNullDock)) { // Shift click from Player Inventory
				ItemStack leftover = this.addStack(clickSlot.getStack());
				//if (leftover.getCount() == clickSlot.getStack().getCount()) {
				//	leftover = moveStackWithinInventory(clickSlot.getStack(), index));
				//}
				clickSlot.putStack(leftover);
				player.inventory.markDirty();
				return ItemStack.EMPTY;
			}
			else {
				int slotIndex = clickSlot.getSlotIndex();
				ItemStack slotStack = this.handler.extractItem(slotIndex, this.handler.getStackInSlot(slotIndex).getMaxStackSize(), true);
				if (!DankNullUtils.isCreativeDankNull(getDankNull())) {
					IItemHandler playerHandler = new PlayerMainInvWrapper(player.inventory);
					ItemStack notAdded = ItemHandlerHelper.insertItemStacked(playerHandler, slotStack, false);
					if (notAdded.getCount() < slotStack.getCount())
						this.handler.extractItem(slotIndex, slotStack.getCount()-notAdded.getCount(), false);
				} else {
//					newStack.setCount(DankNullUtils.isCreativeDankNull(getDankNull()) ? newStack.getMaxStackSize() : currentStackSize);
//					//if (!(player instanceof EntityPlayerMP)) {
//					if (moveStackToInventory(newStack) && !(player instanceof EntityPlayerMP)) {
//						DankNullUtils.decrDankNullStackSize(inventory, clickSlot.getStack(), currentStackSize);
//						if (DankNullUtils.isCreativeDankNull(getDankNull()) && !DankNullUtils.isCreativeDankNullLocked(getDankNull())) {
//							clickSlot.putStack(ItemStack.EMPTY);
//						}
//					}
//					if (!(player instanceof EntityPlayerMP)) {
//						this.sync(getDankNull(),player);
//					}
//					if (player instanceof EntityPlayerMP) {
//						player.inventory.setInventorySlotContents(index, newStack);
//						player.inventory.markDirty();
//					}
//					DankNullUtils.reArrangeStacks(inventory);
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public void handleModeUpdate(PacketChangeMode.ChangeType changeType, int slot) {
		ItemStack slotStack = slot >= 0 && slot < this.handler.getSlots() ? this.handler.getStackInSlot(slot) : ItemStack.EMPTY;
		switch (changeType) {
			case SELECTED:
				this.handler.setSelected(slot);
				break;

			case LOCK:
				this.handler.setLocked(true);
				break;
			case UNLOCK:
				this.handler.setLocked(false);
				break;

			case ORE_ON:
				this.handler.setOre(slotStack, true);
				break;
			case ORE_OFF:
				this.handler.setOre(slotStack, false);
				break;

			case EXTRACT_KEEP_ALL:
				this.handler.setExtractionMode(slotStack, DankNullUtils.ItemExtractionMode.KEEP_ALL);
				break;
			case EXTRACT_KEEP_1:
				this.handler.setExtractionMode(slotStack, DankNullUtils.ItemExtractionMode.KEEP_1);
				break;
			case EXTRACT_KEEP_16:
				this.handler.setExtractionMode(slotStack, DankNullUtils.ItemExtractionMode.KEEP_16);
				break;
			case EXTRACT_KEEP_64:
				this.handler.setExtractionMode(slotStack, DankNullUtils.ItemExtractionMode.KEEP_64);
				break;
			case EXTRACT_KEEP_NONE:
				this.handler.setExtractionMode(slotStack, DankNullUtils.ItemExtractionMode.KEEP_NONE);
				break;

			case PLACE_KEEP_ALL:
				this.handler.setPlacementMode(slotStack, DankNullUtils.ItemPlacementMode.KEEP_ALL);
				break;
			case PLACE_KEEP_1:
				this.handler.setPlacementMode(slotStack, DankNullUtils.ItemPlacementMode.KEEP_1);
				break;
			case PLACE_KEEP_16:
				this.handler.setPlacementMode(slotStack, DankNullUtils.ItemPlacementMode.KEEP_16);
				break;
			case PLACE_KEEP_64:
				this.handler.setPlacementMode(slotStack, DankNullUtils.ItemPlacementMode.KEEP_64);
				break;
			case PLACE_KEEP_NONE:
				this.handler.setPlacementMode(slotStack, DankNullUtils.ItemPlacementMode.KEEP_NONE);
				break;
		}
	}

}