package p455w0rd.danknull.network;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.EasyMappings;

/**
 * @author p455w0rd
 *
 */
public class PacketSyncDankNull implements IMessage {

	private NonNullList<ItemStack> itemStacks;
	private int[] stackSizes;

	@Override
	public void fromBytes(ByteBuf buf) {
		int i = buf.readShort();
		itemStacks = NonNullList.<ItemStack>withSize(i, ItemStack.EMPTY);
		stackSizes = new int[i];
		for (int j = 0; j < i; ++j) {
			itemStacks.set(j, ByteBufUtils.readItemStack(buf));
		}
		for (int j = 0; j < i; ++j) {
			stackSizes[j] = buf.readInt();
		}

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(itemStacks.size());
		for (ItemStack itemstack : itemStacks) {
			ByteBufUtils.writeItemStack(buf, itemstack);
		}
		for (int i = 0; i < itemStacks.size(); i++) {
			buf.writeInt(stackSizes[i]);
		}
	}

	public PacketSyncDankNull() {
	}

	public PacketSyncDankNull(@Nonnull InventoryDankNull inv) {
		itemStacks = NonNullList.<ItemStack>withSize(inv.getStacks().size(), ItemStack.EMPTY);
		stackSizes = new int[inv.getStacks().size()];
		for (int i = 0; i < itemStacks.size(); i++) {
			ItemStack itemstack = inv.getStacks().get(i);
			itemStacks.set(i, itemstack.copy());
			stackSizes[i] = itemstack.getCount();
		}
	}

	public static class Handler implements IMessageHandler<PacketSyncDankNull, IMessage> {
		@Override
		public IMessage onMessage(PacketSyncDankNull message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				handle(message, ctx);
			});
			return null;
		}

		private void handle(PacketSyncDankNull message, MessageContext ctx) {
			EntityPlayer player = EasyMappings.player();
			if (player != null && !player.getHeldItemMainhand().isEmpty() && DankNullUtils.isDankNull(player.getHeldItemMainhand()) && player.openContainer instanceof ContainerDankNull) {
				ContainerDankNull container = (ContainerDankNull) player.openContainer;
				InventoryDankNull inv = container.getDankNullInventory();
				for (int i = 0; i < message.itemStacks.size(); i++) {
					message.itemStacks.get(i).setCount(message.stackSizes[i]);
					//inv.setInventorySlotContents(i, message.itemStacks.get(i));
					container.putStackInSlot(i + 36, message.itemStacks.get(i));
				}
			}
		}
	}

}