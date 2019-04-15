package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.init.ModGuiHandler.GUIType;

/**
 * @author p455w0rd
 *
 */
public class PacketOpenDankGui implements IMessage {

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

	public static class Handler implements IMessageHandler<PacketOpenDankGui, IMessage> {
		@Override
		public IMessage onMessage(PacketOpenDankGui message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				EntityPlayer player = ctx.getServerHandler().player;
				ModGuiHandler.launchGui(GUIType.DANKNULL, player, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
			});
			return null;
		}
	}

}
