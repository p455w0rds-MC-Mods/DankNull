package p455w0rd.danknull.inventory.slot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class SlotDankNull extends Slot {

	private ContainerDankNull myContainer = null;
	protected String backgroundName = null;
	protected ResourceLocation backgroundLocation = null;
	protected Object backgroundMap;
	public int slotNumber;

	public SlotDankNull(InventoryDankNull inv, int idx, int x, int y) {
		super(inv, idx, x, y);
	}

	@Override
	public ItemStack onTake(EntityPlayer thePlayer, ItemStack thisStack) {
		int max = thisStack.getMaxStackSize();
		ItemStack newStack = thisStack.copy();

		if (thisStack.getCount() >= max) {
			newStack.setCount(max);
		}
		InventoryDankNull inv = (InventoryDankNull) inventory;
		//if (dragType == 1) {
		int returnSize = Math.min(newStack.getCount() / 2, newStack.getCount());
		if (inv != null) {
			DankNullUtils.decrDankNullStackSize(inv, thisStack, newStack.getCount() - returnSize);
			newStack.setCount(returnSize + ((newStack.getCount() % 2 == 0) ? 0 : 1));
		}
		//}
		/*
		else if (dragType == 0) {
		if (getDankNullInventory() != null) {
			DankNullUtils.decrDankNullStackSize(getDankNullInventory(), thisStack, newStack.getCount());
			if (inventorySlots.get(index).getHasStack() && inventorySlots.get(index).getStack().getCount() <= 0) {
				inventorySlots.get(index).putStack(ItemStack.EMPTY);
			}
		}
		}
		*/
		onSlotChanged();
		return newStack;
	}

	@Override
	public boolean isItemValid(ItemStack itemStackIn) {
		return !(itemStackIn.getItem() instanceof ItemDankNull);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getBackgroundLocation() {
		return backgroundLocation == null ? TextureMap.LOCATION_BLOCKS_TEXTURE : backgroundLocation;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setBackgroundLocation(ResourceLocation texture) {
		backgroundLocation = texture;
	}

	@Override
	public void setBackgroundName(String name) {
		backgroundName = name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getBackgroundSprite() {
		String name = getSlotTexture();
		return name == null ? null : getBackgroundMap().getAtlasSprite(name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected TextureMap getBackgroundMap() {
		if (backgroundMap == null) {
			backgroundMap = Minecraft.getMinecraft().getTextureMapBlocks();
		}
		return (TextureMap) backgroundMap;
	}

	@Override
	public boolean getHasStack() {
		return !getStack().isEmpty();
	}

	@Override
	public void putStack(ItemStack stack) {
		inventory.setInventorySlotContents(getSlotIndex(), stack);
		if (inventory instanceof InventoryDankNull) {
			DankNullUtils.reArrangeStacks((InventoryDankNull) inventory);
		}
		onSlotChanged();
	}

	@Override
	public void onSlotChanged() {
		if (getContainer() != null) {
			getContainer().detectAndSendChanges();
		}
	}

	@Override
	public int getSlotStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return getSlotStackLimit();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return backgroundName;
	}

	@Override
	public ItemStack decrStackSize(int amount) {
		return inventory.decrStackSize(getSlotIndex(), amount);
	}

	@Override
	public boolean isHere(IInventory inv, int slotIn) {
		return (inv == inventory) && (slotIn == getSlotIndex());
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return true;
	}

	public int getX() {
		return xPos;
	}

	public int getY() {
		return yPos;
	}

	public ContainerDankNull getContainer() {
		return myContainer;
	}

	public void setContainer(ContainerDankNull myContainer) {
		this.myContainer = myContainer;
	}

}
