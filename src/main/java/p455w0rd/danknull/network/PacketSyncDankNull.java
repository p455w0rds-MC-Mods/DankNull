package p455w0rd.danknull.network;

import org.apache.commons.lang3.tuple.Pair;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Triple;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.EasyMappings;

/**
 * @author p455w0rd
 */
public class PacketSyncDankNull implements IMessage {

	private int[] stackSizes;
	private int slot;
	private int category;
	private ItemStack dankNull;
	private boolean isLocked;

	@Override
	public void fromBytes(final ByteBuf buf) {
		slot = buf.readInt();
		category = buf.readInt();
		dankNull = ByteBufUtils.readItemStack(buf);
		stackSizes = new int[buf.readInt()];
		for (int i = 0; i < stackSizes.length - 1; i++) {
			stackSizes[i] = buf.readInt();
		}
		isLocked = buf.readBoolean();
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		buf.writeInt(slot);
		buf.writeInt(category);
		ByteBufUtils.writeItemStack(buf, dankNull);
		final InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(dankNull);
		buf.writeInt(inv.getSizeInventory());
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			buf.writeInt(DankNullUtils.getNewDankNullInventory(dankNull).getSizeForSlot(i));
		}
		buf.writeBoolean(isLocked);
	}

	public PacketSyncDankNull() {
	}

	public PacketSyncDankNull(Triple<Integer, Integer, ItemStack> syncedDankNull) {
		this(syncedDankNull.getLeft(), syncedDankNull.getMiddle(), syncedDankNull.getRight());
	}

	public PacketSyncDankNull(int slot, int category, ItemStack stack) {
		this.slot = slot;
		dankNull = stack;
		this.category = category;
		isLocked = DankNullUtils.isCreativeDankNullLocked(stack);
	}

	public static class Handler implements IMessageHandler<PacketSyncDankNull, IMessage> {
		@Override
		public IMessage onMessage(final PacketSyncDankNull message, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				if (ctx.side == Side.CLIENT) {
					handleToClient(message, ctx);
				} else {
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
			DankNull.PROXY.setGuiInventory(DankNullUtils.getNewDankNullInventory(message.dankNull));
		}

		private void handle(final PacketSyncDankNull message, EntityPlayer player, Side side) {
			final ItemStack stack = message.dankNull;
			/*
			InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(stack);
			int[] sizes = message.stackSizes;
			for (int i = 0; i < sizes.length; i++) {
				inv.setSizeForSlot(i, sizes[i]);
			}
			*/
			PlayerSlot.EnumInvCategory cat = PlayerSlot.EnumInvCategory.fromIndex(message.category);

			if (cat == PlayerSlot.EnumInvCategory.MAIN) {
				player.inventory.setInventorySlotContents(message.slot, stack);
			} else if (cat == PlayerSlot.EnumInvCategory.ARMOR) {
				player.inventory.setInventorySlotContents(message.slot + player.inventory.getSizeInventory(), stack);
			} else if (cat == PlayerSlot.EnumInvCategory.OFF_HAND) {
				player.inventory.setInventorySlotContents(message.slot + player.inventory.getSizeInventory() + player.inventory.offHandInventory.size(), stack);
			}

			DankNullUtils.setLocked(stack, message.isLocked);
		}
	}

}