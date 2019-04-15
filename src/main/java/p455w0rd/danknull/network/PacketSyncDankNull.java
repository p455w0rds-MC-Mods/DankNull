package p455w0rd.danknull.network;

import org.apache.commons.lang3.tuple.Pair;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.EasyMappings;

/**
 * @author p455w0rd
 *
 */
public class PacketSyncDankNull implements IMessage {

	private int[] stackSizes;
	private int slot;
	private ItemStack dankNull;
	private boolean isLocked;

	@Override
	public void fromBytes(ByteBuf buf) {
		slot = buf.readInt();
		dankNull = ByteBufUtils.readItemStack(buf);
		stackSizes = new int[buf.readInt()];
		for (int i = 0; i < stackSizes.length - 1; i++) {
			stackSizes[i] = buf.readInt();
		}
		isLocked = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(slot);
		ByteBufUtils.writeItemStack(buf, dankNull);
		InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(dankNull);
		buf.writeInt(inv.getSizeInventory());
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			buf.writeInt(DankNullUtils.getNewDankNullInventory(dankNull).getSizeForSlot(i));
		}
		buf.writeBoolean(isLocked);
	}

	public PacketSyncDankNull() {
	}

	public PacketSyncDankNull(Pair<Integer, ItemStack> syncedDankNull) {
		this(syncedDankNull.getLeft(), syncedDankNull.getRight());
	}

	public PacketSyncDankNull(int slot, ItemStack stack) {
		this.slot = slot;
		dankNull = stack;
		isLocked = DankNullUtils.isCreativeDankNullLocked(stack);
	}

	public static class Handler implements IMessageHandler<PacketSyncDankNull, IMessage> {
		@Override
		public IMessage onMessage(PacketSyncDankNull message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				if (ctx.side == Side.CLIENT) {
					handleToClient(message, ctx);
				}
				else {
					handleToServer(message, ctx);
				}
			});
			return null;
		}

		private void handleToServer(PacketSyncDankNull message, MessageContext ctx) {
			handle(message, ctx.getServerHandler().player, ctx.side);
		}

		private void handleToClient(PacketSyncDankNull message, MessageContext ctx) {
			handle(message, EasyMappings.player(), ctx.side);
		}

		private void handle(PacketSyncDankNull message, EntityPlayer player, Side side) {
			ItemStack stack = message.dankNull;
			/*
			InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(stack);
			int[] sizes = message.stackSizes;
			for (int i = 0; i < sizes.length; i++) {
				inv.setSizeForSlot(i, sizes[i]);
			}
			*/
			player.inventory.setInventorySlotContents(message.slot, stack);
			DankNullUtils.setLocked(stack, message.isLocked);
		}
	}

}