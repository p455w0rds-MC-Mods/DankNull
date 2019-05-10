package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.init.ModGuiHandler.GUIType;

/**
 * @author p455w0rd
 *
 */
public class PacketOpenDankGui implements IMessage {

	@Override
	public void fromBytes(final ByteBuf buf) {
	}

	@Override
	public void toBytes(final ByteBuf buf) {
	}

	public static class Handler implements IMessageHandler<PacketOpenDankGui, IMessage> {
		@Override
		public IMessage onMessage(final PacketOpenDankGui message, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				final EntityPlayer player = ctx.getServerHandler().player;
				ModGuiHandler.launchGui(GUIType.DANKNULL, player, player.getEntityWorld(), player.getPosition());
			});
			return null;
		}
	}

}
