package p455w0rd.danknull.network;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class PacketEmptyDock implements IMessage {

	private BlockPos pos;

	public PacketEmptyDock() {
	}

	public PacketEmptyDock(@Nonnull BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		pos = new BlockPos(x, y, z);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}

	public static class Handler implements IMessageHandler<PacketEmptyDock, IMessage> {
		@Override
		public IMessage onMessage(PacketEmptyDock message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				EntityPlayer player = DankNull.PROXY.getPlayer();
				if (player.openContainer instanceof ContainerDankNull) {
					player.closeScreen();
				}
				World world = player.getEntityWorld();
				TileEntity te = world.getTileEntity(message.pos);
				if (te instanceof TileDankNullDock) {
					DankNullUtils.emptyDankNullDock((TileDankNullDock) te);
					te.markDirty();
				}
			});
			return null;
		}
	}

}
