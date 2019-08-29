package p455w0rd.danknull.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.util.cap.CapabilityDankNull;
import p455w0rd.danknull.util.cap.IDankNullHandler;

/**
 * @author p455w0rd
 */
public class ContainerDankNullDock extends ContainerDankNullBase {

	private final TileDankNullDock tile;
	private final IDankNullHandler handler;

	public ContainerDankNullDock(final EntityPlayer player, final TileDankNullDock tile) {
		super(player);
		this.tile = tile;
		this.handler = tile.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
		this.init();
	}

	@Override
	public IDankNullHandler getHandler() {
		return this.handler;
	}

	@Override
	public ItemStack getDankNullStack() {
		return this.tile.getDankNull();
	}
}