package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class PacketSetSelectedItem implements IMessage {
	private static int index;

	@Override
	public void fromBytes(ByteBuf buf) {
		index = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(index);
	}

	public PacketSetSelectedItem() {
	}

	public PacketSetSelectedItem(int newIndex) {
		index = newIndex;
	}

	public static class Handler implements IMessageHandler<PacketSetSelectedItem, IMessage> {
		@Override
		public IMessage onMessage(PacketSetSelectedItem message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				handle(message, ctx);
			});
			return null;
		}

		private void handle(PacketSetSelectedItem message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			if (player != null && player.getHeldItemMainhand() != null && DankNullUtils.isDankNull(player.getHeldItemMainhand())) {
				InventoryDankNull inventory = new InventoryDankNull(player.getHeldItemMainhand());
				DankNullUtils.setSelectedStackIndex(inventory, index, false);
			}
		}
	}

}
