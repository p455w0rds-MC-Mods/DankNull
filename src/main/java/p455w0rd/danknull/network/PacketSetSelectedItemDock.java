package p455w0rd.danknull.network;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class PacketSetSelectedItemDock implements IMessage {

	private int index;
	private BlockPos pos;

	@Override
	public void fromBytes(ByteBuf buf) {
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		index = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeInt(index);
	}

	public PacketSetSelectedItemDock() {
	}

	public PacketSetSelectedItemDock(@Nonnull int index, @Nonnull BlockPos pos) {
		this.index = index;
		this.pos = pos;
	}

	public static class Handler implements IMessageHandler<PacketSetSelectedItemDock, IMessage> {
		@Override
		public IMessage onMessage(PacketSetSelectedItemDock message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				EntityPlayerMP player = ctx.getServerHandler().player;
				World world = player.getEntityWorld();
				if (world != null && world.getTileEntity(message.pos) != null && world.getTileEntity(message.pos) instanceof TileDankNullDock) {
					TileDankNullDock dankDock = (TileDankNullDock) world.getTileEntity(message.pos);
					ItemStack dankNull = dankDock.getDankNull();
					if (!dankNull.isEmpty()) {
						//DankNullUtils.setSelectedStackIndex(DankNullUtils.getNewDankNullInventory(dankNull), message.index);
						//dankDock.setDankNull(dankNull);
					}
				}
			});
			return null;
		}
	}

}
