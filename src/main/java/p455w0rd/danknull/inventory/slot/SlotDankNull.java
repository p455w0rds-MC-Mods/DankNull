package p455w0rd.danknull.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.items.ItemDankNull;

/**
 * @author p455w0rd
 *
 */
public class SlotDankNull extends Slot {

	//private Container myContainer = null;
	//protected String backgroundName = null;
	//protected ResourceLocation backgroundLocation = null;
	//protected Object backgroundMap;
	//public int slotNumber;

	public SlotDankNull(final InventoryDankNull inv, final int idx, final int x, final int y) {
		super(inv, idx, x, y);
	}

	@Override
	public boolean isItemValid(final ItemStack itemStackIn) {
		return !(itemStackIn.getItem() instanceof ItemDankNull);
	}

	/*
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getBackgroundLocation() {
		return backgroundLocation == null ? TextureMap.LOCATION_BLOCKS_TEXTURE : backgroundLocation;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setBackgroundLocation(final ResourceLocation texture) {
		backgroundLocation = texture;
	}
	
	@Override
	public void setBackgroundName(final String name) {
		backgroundName = name;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getBackgroundSprite() {
		final String name = getSlotTexture();
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
	*/
	@Override
	public boolean getHasStack() {
		return !getStack().isEmpty();
	}

	@Override
	public void putStack(final ItemStack stack) {
		inventory.setInventorySlotContents(getSlotIndex(), stack);
		onSlotChanged();
	}

	@Override
	public int getSlotStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public int getItemStackLimit(final ItemStack stack) {
		return getSlotStackLimit();
	}

	/*
	@Override
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return backgroundName;
	}
	*/
	@Override
	public ItemStack decrStackSize(final int amount) {
		return inventory.decrStackSize(getSlotIndex(), amount);
	}

	@Override
	public boolean isHere(final IInventory inv, final int slotIn) {
		return inv == inventory && slotIn == getSlotIndex();
	}

	@Override
	public boolean canTakeStack(final EntityPlayer playerIn) {
		return true;
	}

	public int getX() {
		return xPos;
	}

	public int getY() {
		return yPos;
	}
	/*
	public Container getContainer() {
		return myContainer;
	}

	public SlotDankNull setContainer(final Container myContainer) {
		this.myContainer = myContainer;
		return this;
	}
	*/
}
