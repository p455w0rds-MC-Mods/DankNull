package p455w0rd.danknull.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.gui.GuiDankNull;
import p455w0rd.danknull.client.gui.GuiDankNullDock;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class ModGuiHandler implements IGuiHandler {

	private static int playerInvSlot = -1;

	public static void init() {
		ModLogger.info("Registering GUI Handler");
		NetworkRegistry.INSTANCE.registerGuiHandler(ModGlobals.MODID, new ModGuiHandler());
	}

	@Override
	public Object getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		switch (GUIType.VALUES[id]) {
		case DANKNULL:
			/*
			ItemStack dankNull = ItemStack.EMPTY;
			if (player.getHeldItemMainhand().getItem() == ModItems.DANK_NULL) {
				dankNull = player.getHeldItemMainhand();
			}
			else if (player.getHeldItemOffhand().getItem() == ModItems.DANK_NULL) {
				dankNull = player.getHeldItemOffhand();
			}
			if (dankNull.isEmpty()) {
				dankNull = DankNullUtils.getDankNull(player);
				if (dankNull.isEmpty()) {
					break;
				}
			}
			*/
			final ItemStack dn = DankNullUtils.getDankNullFromPlayerInvSlot(playerInvSlot, player);
			return new ContainerDankNull(player, playerInvSlot, DankNullUtils.getNewDankNullInventory(dn));
		case DANKNULL_TE:
			final TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if (te != null && te instanceof TileDankNullDock) {
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
		switch (GUIType.VALUES[id]) {
		case DANKNULL:
			/*
			ItemStack dankNull = ItemStack.EMPTY;
			if (player.getHeldItemMainhand().getItem() == ModItems.DANK_NULL) {
				dankNull = player.getHeldItemMainhand();
			}
			else if (player.getHeldItemOffhand().getItem() == ModItems.DANK_NULL) {
				dankNull = player.getHeldItemOffhand();
			}
			if (dankNull.isEmpty()) {
				dankNull = DankNullUtils.getDankNull(player);
				if (dankNull.isEmpty()) {
					break;
				}
			}
			*/
			final ItemStack dn = DankNullUtils.getDankNullFromPlayerInvSlot(playerInvSlot, player);
			final InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(dn);
			return new GuiDankNull(new ContainerDankNull(player, playerInvSlot, inv), player);
		case DANKNULL_TE:
			final TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if (te != null && te instanceof TileDankNullDock) {
				final TileDankNullDock dankDock = (TileDankNullDock) te;
				if (!dankDock.getDankNull().isEmpty()) {
					return new GuiDankNullDock(new ContainerDankNullDock(player, dankDock), player, dankDock);
				}
			}
		default:
			break;
		}
		return null;
	}

	public static void launchGui(final GUIType type, final EntityPlayer player, final World world) {
		playerInvSlot = player.inventory.currentItem;
		final BlockPos pos = player.getPosition();
		player.openGui(DankNull.INSTANCE, type.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static enum GUIType {

			DANKNULL, DANKNULL_TE;

		public static final GUIType[] VALUES = new GUIType[] {
				DANKNULL, DANKNULL_TE
		};

	}

}