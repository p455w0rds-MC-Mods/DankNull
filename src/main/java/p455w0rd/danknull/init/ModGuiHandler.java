package p455w0rd.danknull.init;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.gui.GuiDankNull;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class ModGuiHandler implements IGuiHandler {

	//private static PlayerSlot PLAYER_SLOT;

	public static void init() {
		ModLogger.info("Registering GUI Handler");
		NetworkRegistry.INSTANCE.registerGuiHandler(ModGlobals.MODID, new ModGuiHandler());
	}

	@Override
	public Object getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		switch (GUIType.VALUES[id]) {
		case DANKNULL:
			final PlayerSlot dankNull = DankNullUtils.getDankNullSlot(player);
			if (dankNull == null) {
				return null;
			}
			return new ContainerDankNull(player, dankNull);
		case DANKNULL_TE:
			final TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if (te instanceof TileDankNullDock) {
				final TileDankNullDock dankDock = (TileDankNullDock) te;
				if (!dankDock.getDankNull().isEmpty()) {
					return new ContainerDankNullDock(player, dankDock);
				}
			}
		default:
			break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		//final Container c = (Container) getServerGuiElement(id, player, world, x, y, z);
		//if (c != null) {
		switch (GUIType.VALUES[id]) {
		case DANKNULL_TE:
			final TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if (te instanceof TileDankNullDock) {
				final TileDankNullDock dankDock = (TileDankNullDock) te;
				if (!dankDock.getDankNull().isEmpty()) {
					return new GuiDankNull(new ContainerDankNullDock(player, dankDock));
				}
			}
		case DANKNULL:
			final PlayerSlot dankNull = DankNullUtils.getDankNullSlot(player);
			if (dankNull == null) {
				return null;
			}
			return new GuiDankNull(new ContainerDankNull(player, dankNull));
		default:
			break;
		}
		//}
		return null;
	}

	public static void launchGui(final GUIType type, final EntityPlayer player, final World world, final BlockPos pos, @Nullable final PlayerSlot playerSlot) {
		if (!world.isRemote) {
			/*if (playerSlot == null) {
				PLAYER_SLOT = null;
			}
			else {
				PLAYER_SLOT = playerSlot;
			}*/
			player.openGui(DankNull.INSTANCE, type.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		}
	}

	public static enum GUIType {

			DANKNULL, DANKNULL_TE;

		public static final GUIType[] VALUES = new GUIType[] {
				DANKNULL, DANKNULL_TE
		};

	}

}