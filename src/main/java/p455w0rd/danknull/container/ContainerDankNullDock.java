package p455w0rd.danknull.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;

/**
 * @author p455w0rd
 */
public class ContainerDankNullDock extends ContainerDankNullBase {

	private final TileDankNullDock tile;
	private final IDankNullHandler handler;

	public ContainerDankNullDock(final EntityPlayer player, final TileDankNullDock tile) {
		super(player);
		this.tile = tile;
		handler = tile.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
		init();
	}

	@Override
	protected boolean isDock() {
		return true;
	}

	@Override
	public IDankNullHandler getHandler() {
		return handler;
	}

	@Override
	public ItemStack getDankNullStack() {
		return tile.getDankNull();
	}
}