package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.EasyMappings;

/**
 * @author p455w0rd
 *
 */
public class PacketSyncDankNull implements IMessage {

	private int slot;
	private NBTTagCompound nbt;

	@Override
	public void fromBytes(final ByteBuf buf) {
		slot = buf.readInt();
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		buf.writeInt(slot);
		ByteBufUtils.writeTag(buf, nbt);
	}

	public PacketSyncDankNull() {
	}

	public PacketSyncDankNull(final int slot, final NBTTagCompound nbt) {
		this.slot = slot;
		this.nbt = nbt;
	}

	public static class Handler implements IMessageHandler<PacketSyncDankNull, IMessage> {
		@Override
		public IMessage onMessage(final PacketSyncDankNull message, final MessageContext ctx) {
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

		private void handleToServer(final PacketSyncDankNull message, final MessageContext ctx) {
			handle(message, ctx.getServerHandler().player, ctx.side);
		}

		private void handleToClient(final PacketSyncDankNull message, final MessageContext ctx) {
			handle(message, EasyMappings.player(), ctx.side);
		}

		private void handle(final PacketSyncDankNull message, final EntityPlayer player, final Side side) {
			final InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(player.inventory.getStackInSlot(message.slot));
			inv.deserializeNBT(message.nbt);
			inv.markDirty();
			/*
			InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(stack);
			int[] sizes = message.stackSizes;
			for (int i = 0; i < sizes.length; i++) {
				inv.setSizeForSlot(i, sizes[i]);
			}
			*/
			//player.inventory.setInventorySlotContents(message.slot, stack);
			//DankNullUtils.setLocked(stack, message.isLocked);
		}
	}

}