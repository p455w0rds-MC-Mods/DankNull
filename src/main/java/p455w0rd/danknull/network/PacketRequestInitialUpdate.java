package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import p455w0rd.danknull.container.ContainerDankNull;

/**
 * @author p455w0rd
 *
 */
public class PacketRequestInitialUpdate implements IMessage {

	public PacketRequestInitialUpdate() {
	}

	@Override
	public void fromBytes(final ByteBuf buf) {
	}

	@Override
	public void toBytes(final ByteBuf buf) {
	}

	public static class Handler implements IMessageHandler<PacketRequestInitialUpdate, IMessage> {
		@Override
		public IMessage onMessage(final PacketRequestInitialUpdate message, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				final Container c = ctx.getServerHandler().player.openContainer;
				if (c instanceof ContainerDankNull) {
					//((ContainerDankNull) c).sync();
				}
			});
			return null;
		}
	}

}
