package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import p455w0rd.danknull.container.ContainerDankNullItem;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.inventory.slot.SlotDankNull;

/**
 * @author p455w0rd
 *
 */
public class PacketMouseWheel implements IMessage {

	int[] values;

	public PacketMouseWheel() {

	}

    @Override
	public void fromBytes(final ByteBuf buf) {
		values = new int[] {
				buf.readInt(), buf.readInt()
		};
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		buf.writeInt(values[0]);
		buf.writeInt(values[1]);
	}

	public static class Handler implements IMessageHandler<PacketMouseWheel, IMessage> {
		@Override
		public IMessage onMessage(final PacketMouseWheel message, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				final EntityPlayer player = ctx.getServerHandler().player;
				if (player.openContainer instanceof ContainerDankNullItem || player.openContainer instanceof ContainerDankNullDock) {
					final Slot s = player.openContainer.inventorySlots.get(36 + message.values[1]);
					if (s instanceof SlotDankNull) {
						if (message.values[0] == 0) { //add
							final ItemStack mouseStack = player.inventory.getItemStack();
							final ItemStack slotStack = s.getStack();
							final ItemStack tmpStack = slotStack.copy();
							tmpStack.setCount(mouseStack.getCount() + 1);
							player.inventory.setItemStack(tmpStack);
							player.inventory.markDirty();
						}
						else { //subtract
							final ItemStack mouseStack = player.inventory.getItemStack();
							if (!mouseStack.isEmpty()) {
								final ItemStack tmpStack = mouseStack.copy();
								tmpStack.setCount(1);
								final ItemStack tmpStack2 = mouseStack.copy();
								tmpStack2.shrink(1);
								player.inventory.setItemStack(tmpStack2);
							}
						}
					}
				}
			});
			return null;
		}
	}

}