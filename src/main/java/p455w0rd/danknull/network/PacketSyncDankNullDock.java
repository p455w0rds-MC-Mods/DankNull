package p455w0rd.danknull.network;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.EasyMappings;

/**
 * @author p455w0rd
 *
 */
public class PacketSyncDankNullDock implements IMessage {

	private BlockPos dockPos;
	private ItemStack dankNull;
	private int[] stackSizes;

	@Override
	public void fromBytes(ByteBuf buf) {
		dockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		dankNull = ByteBufUtils.readItemStack(buf);
		stackSizes = new int[buf.readInt()];
		for (int i = 0; i < stackSizes.length - 1; i++) {
			stackSizes[i] = buf.readInt();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dockPos.getX());
		buf.writeInt(dockPos.getY());
		buf.writeInt(dockPos.getZ());
		ByteBufUtils.writeItemStack(buf, dankNull);
		InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(dankNull);
		buf.writeInt(inv.getSizeInventory());
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			buf.writeInt(DankNullUtils.getNewDankNullInventory(dankNull).getSizeForSlot(i));
		}
	}

	public PacketSyncDankNullDock() {
	}

	public PacketSyncDankNullDock(@Nonnull TileDankNullDock dockingStation, ItemStack dankNull) {
		dockPos = dockingStation.getPos();
		this.dankNull = dankNull;
	}

	public static class Handler implements IMessageHandler<PacketSyncDankNullDock, IMessage> {
		@Override
		public IMessage onMessage(PacketSyncDankNullDock message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				handle(message, ctx.side == Side.SERVER ? ctx.getServerHandler().player : EasyMappings.player(), ctx.side);
			});
			return null;
		}

		private void handle(PacketSyncDankNullDock message, EntityPlayer player, Side side) {
			World world = player.getEntityWorld();
			if (world != null && world.getTileEntity(message.dockPos) != null && world.getTileEntity(message.dockPos) instanceof TileDankNullDock) {
				TileDankNullDock dankDock = (TileDankNullDock) world.getTileEntity(message.dockPos);
				ItemStack stack = message.dankNull;
				InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(stack);
				int[] sizes = message.stackSizes;
				for (int i = 0; i < sizes.length; i++) {
					inv.setSizeForSlot(i, sizes[i]);
				}
				//dankDock.setDankNull(stack);
				/*
				if (side.isClient() && player.openContainer != null && player.openContainer instanceof ContainerDankNullDock) {
					ContainerDankNullDock c = (ContainerDankNullDock) player.openContainer;
					c.setDankNullInventory(inv);
					for (int i = 0; i < sizes.length; i++) {
						SlotDankNull s = c.getDankNullSlots().get(i);
						if (!s.getHasStack()) {
							s.getStack().setCount(message.stackSizes[i]);
						}
					}
				}
				*/
			}
		}
	}

}