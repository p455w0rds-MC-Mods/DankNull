package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.init.ModGuiHandler.GUIType;
import p455w0rd.danknull.inventory.PlayerSlot;

/**
 * @author p455w0rd
 *
 */
public class PacketOpenGui implements IMessage, IMessageHandler<PacketOpenGui, IMessage> {

	PlayerSlot slot;

	public PacketOpenGui() {
	}

	public PacketOpenGui(final PlayerSlot slot) {
		this.slot = slot;
	}

	@Override
	public IMessage onMessage(final PacketOpenGui message, final MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			final EntityPlayer player = ctx.getServerHandler().player;
			ModGuiHandler.launchGui(GUIType.DANKNULL, player, player.getEntityWorld(), player.getPosition(), message.slot);
		});
		return null;
	}

	@Override
	public void fromBytes(final ByteBuf buf) {
		slot = PlayerSlot.fromBuff(buf);
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		slot.toBuff(buf);
	}

}
