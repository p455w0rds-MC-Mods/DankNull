package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class PacketSetSelectedItem implements IMessage {

	private int index;

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

	public PacketSetSelectedItem(int index) {
		this.index = index;
	}

	public static class Handler implements IMessageHandler<PacketSetSelectedItem, IMessage> {
		@Override
		public IMessage onMessage(PacketSetSelectedItem message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				EntityPlayerMP player = ctx.getServerHandler().player;
				ItemStack dankNull = ItemStack.EMPTY;
				if (player != null) {
					if (player.getHeldItemMainhand().getItem() == ModItems.DANK_NULL) {
						dankNull = player.getHeldItemMainhand();
					}
					else if (player.getHeldItemOffhand().getItem() == ModItems.DANK_NULL) {
						dankNull = player.getHeldItemOffhand();
					}

					InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(dankNull);
					DankNullUtils.setSelectedStackIndex(inv, message.index);
				}
			});
			return null;
		}
	}

}
