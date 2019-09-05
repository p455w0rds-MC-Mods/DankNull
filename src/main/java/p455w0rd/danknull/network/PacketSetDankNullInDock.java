package p455w0rd.danknull.network;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;

/**
 * @author p455w0rd
 *
 */
public class PacketSetDankNullInDock implements IMessage {

	ItemStack dankNull;
	BlockPos pos;

	@Override
	public void fromBytes(final ByteBuf buf) {
		final int x = buf.readInt();
		final int y = buf.readInt();
		final int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		dankNull = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		ByteBufUtils.writeItemStack(buf, dankNull);
	}

	public PacketSetDankNullInDock() {
	}

	public PacketSetDankNullInDock(final TileDankNullDock dockingStation, @Nonnull final ItemStack dankNull) {
		pos = dockingStation.getPos();
		this.dankNull = dankNull;
	}

	public static class Handler implements IMessageHandler<PacketSetDankNullInDock, IMessage> {
		@Override
		public IMessage onMessage(final PacketSetDankNullInDock message, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				final World world = DankNull.PROXY.getWorld();
				final TileEntity te = world.getTileEntity(message.pos);
				if (te != null && te instanceof TileDankNullDock) {
					final TileDankNullDock dankDock = (TileDankNullDock) te;
					dankDock.setDankNull(message.dankNull);
				}
			});
			return null;
		}
	}

}
