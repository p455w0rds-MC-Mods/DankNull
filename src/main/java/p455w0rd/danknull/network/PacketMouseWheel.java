package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.inventory.slot.SlotDankNullDock;

/**
 * @author p455w0rd
 *
 */
public class PacketMouseWheel implements IMessage {

	int[] values;

	public PacketMouseWheel() {

	}

	public PacketMouseWheel(final int wheelDir, final int slot) {
		values = new int[] {
				wheelDir == -1 ? 0 : 1, slot
		};
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
				if (player.openContainer instanceof ContainerDankNull || player.openContainer instanceof ContainerDankNullDock) {
					final ItemStack dankNull = player.openContainer instanceof ContainerDankNull ? ((ContainerDankNull) player.openContainer).getDankNull() : ((ContainerDankNullDock) player.openContainer).getDankNull();
					final Slot s = player.openContainer.inventorySlots.get(36 + message.values[1]);
					if (s instanceof SlotDankNull || s instanceof SlotDankNullDock) {
						if (message.values[0] == 0) { //add
							final ItemStack mouseStack = player.inventory.getItemStack();
							final ItemStack slotStack = s.getStack();
							final ItemStack tmpStack = slotStack.copy();
							tmpStack.setCount(mouseStack.getCount() + 1);
							player.inventory.setItemStack(tmpStack);
							//DankNullUtils.decrDankNullStackSize(DankNullUtils.getNewDankNullInventory(dankNull), slotStack, 1);
							player.inventory.markDirty();
						}
						else { //subtract
							final ItemStack mouseStack = player.inventory.getItemStack();
							if (!mouseStack.isEmpty()) {
								final ItemStack tmpStack = mouseStack.copy();
								tmpStack.setCount(1);
								if (s instanceof SlotDankNullDock) {
									//((ContainerDankNullDock) player.openContainer).addStack(DankNullUtils.getNewDankNullInventory(dankNull), tmpStack);
								}
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