package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class PacketSetSelectedItem implements IMessage {

	private int index;

	@Override
	public void fromBytes(final ByteBuf buf) {
		index = buf.readInt();

	}

	@Override
	public void toBytes(final ByteBuf buf) {
		buf.writeInt(index);
	}

	public PacketSetSelectedItem() {
	}

	public PacketSetSelectedItem(final int index) {
		this.index = index;
	}

	public static class Handler implements IMessageHandler<PacketSetSelectedItem, IMessage> {
		@Override
		public IMessage onMessage(final PacketSetSelectedItem message, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				final EntityPlayerMP player = ctx.getServerHandler().player;
				ItemStack dankNull = ItemStack.EMPTY;
				/*
				if (Minecraft.getMinecraft().currentScreen instanceof GuiDankNull) {
					final GuiDankNull gui = (GuiDankNull) Minecraft.getMinecraft().currentScreen;
					if (gui.isTile()) {
						final TileDankNullDock tile = ((ContainerDankNullDock) gui.inventorySlots).getTile();
						dankNull = tile.getDankNull();
						final InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(dankNull);
						DankNullUtils.setSelectedStackIndex(inv, message.index);
						inv.markDirty();
						tile.setDankNull(inv.getDankNull());
						//final NBTTagCompound nbt = new NBTTagCompound();
						//nbt.setTag(NBT.DOCKEDSTACK, inv.getDankNull().writeToNBT(new NBTTagCompound()));
						//tile.readFromNBT(nbt);
					}
				}*/
				if (player != null) {
					if (DankNullUtils.isDankNull(player.getHeldItemMainhand())) {
						dankNull = player.getHeldItemMainhand();
					}
					else if (DankNullUtils.isDankNull(player.getHeldItemOffhand())) {
						dankNull = player.getHeldItemOffhand();
					}

					final InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(dankNull);
					DankNullUtils.setSelectedStackIndex(inv, message.index);
				}
			});
			return null;
		}
	}

}
