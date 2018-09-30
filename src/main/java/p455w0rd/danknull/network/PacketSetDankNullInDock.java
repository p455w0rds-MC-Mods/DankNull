package p455w0rd.danknull.network;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class PacketSetDankNullInDock implements IMessage {

	ItemStack dankNull;
	BlockPos pos;

	@Override
	public void fromBytes(ByteBuf buf) {
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		dankNull = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		ByteBufUtils.writeItemStack(buf, dankNull);
	}

	public PacketSetDankNullInDock() {
	}

	public PacketSetDankNullInDock(TileDankNullDock dockingStation, @Nonnull ItemStack dankNull) {
		pos = dockingStation.getPos();
		this.dankNull = dankNull;
	}

	public static class Handler implements IMessageHandler<PacketSetDankNullInDock, IMessage> {
		@Override
		public IMessage onMessage(PacketSetDankNullInDock message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				World world = DankNull.PROXY.getWorld();
				TileEntity te = world.getTileEntity(message.pos);
				if (te != null && te instanceof TileDankNullDock) {
					TileDankNullDock dankDock = (TileDankNullDock) te;
					InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(message.dankNull);
					dankDock.setInventory(inv);
					//world.updateComparatorOutputLevel(message.pos, te.getBlockType());
					dankDock.markDirty();
				}
			});
			return null;
		}
	}

}
