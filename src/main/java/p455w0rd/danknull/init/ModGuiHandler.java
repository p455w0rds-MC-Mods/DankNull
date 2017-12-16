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
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class ModGuiHandler implements IGuiHandler {

	public static void init() {
		ModLogger.info("Registering GUI Handler");
		NetworkRegistry.INSTANCE.registerGuiHandler(ModGlobals.MODID, new ModGuiHandler());
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch (GUIType.values()[id]) {
		case DANKNULL:
			ItemStack dankNull = ItemStack.EMPTY;
			if (player.getHeldItemMainhand().getItem() == ModItems.DANK_NULL) {
				dankNull = player.getHeldItemMainhand();
			}
			else if (player.getHeldItemOffhand().getItem() == ModItems.DANK_NULL) {
				dankNull = player.getHeldItemOffhand();
			}
			return new ContainerDankNull(player, DankNullUtils.getNewDankNullInventory(dankNull));
		case DANKNULL_TE:
			TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if (te != null && te instanceof TileDankNullDock) {
				TileDankNullDock dankDock = (TileDankNullDock) te;
				if (!dankDock.getStack().isEmpty()) {
					return new ContainerDankNull(player, dankDock);
				}
			}
		default:
			break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch (GUIType.values()[id]) {
		case DANKNULL:
			ItemStack dankNull = ItemStack.EMPTY;
			if (player.getHeldItemMainhand().getItem() == ModItems.DANK_NULL) {
				dankNull = player.getHeldItemMainhand();
			}
			else if (player.getHeldItemOffhand().getItem() == ModItems.DANK_NULL) {
				dankNull = player.getHeldItemOffhand();
			}
			return new GuiDankNull(new ContainerDankNull(player, DankNullUtils.getNewDankNullInventory(dankNull)), player);
		case DANKNULL_TE:
			TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if (te != null && te instanceof TileDankNullDock) {
				TileDankNullDock dankDock = (TileDankNullDock) te;
				if (!dankDock.getStack().isEmpty()) {
					return new GuiDankNull(new ContainerDankNull(player, dankDock), player, dankDock);
				}
			}
		default:
			break;
		}
		return null;
	}

	public static void launchGui(GUIType type, EntityPlayer playerIn, World worldIn, int x, int y, int z) {
		playerIn.openGui(DankNull.INSTANCE, type.ordinal(), worldIn, x, y, z);
	}

	public static enum GUIType {
			DANKNULL, DANKNULL_TE;
	}

}