package p455w0rd.danknull.inventory.slot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.items.ItemDankNull;

/**
 * @author p455w0rd
 *
 */
public class DankNullSlot extends Slot {

	private Container myContainer = null;
	protected String backgroundName = null;
	protected ResourceLocation backgroundLocation = null;
	protected Object backgroundMap;
	public int slotNumber;

	public DankNullSlot(InventoryDankNull inv, int idx, int x, int y) {
		super(inv, idx, x, y);
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
		onSlotChanged();
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

	public Container getContainer() {
		return myContainer;
	}

	public void setContainer(Container myContainer) {
		this.myContainer = myContainer;
	}

}
