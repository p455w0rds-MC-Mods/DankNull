package p455w0rd.danknull.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import p455w0rd.danknull.DankNull;
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
	public Object getServerGuiElement(int id, EntityPlayer player, World worldIn, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		switch (GUIType.values()[id]) {
		case DANKNULL:
			return new ContainerDankNull(player, DankNullUtils.getNewDankNullInventory(player.getHeldItemMainhand()));
		default:
			break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World worldIn, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		switch (GUIType.values()[id]) {
		case DANKNULL:
			return new GuiDankNull(new ContainerDankNull(player, DankNullUtils.getNewDankNullInventory(player.getHeldItemMainhand())), player);
		default:
			break;
		}
		return null;
	}

	public static void launchGui(GUIType type, EntityPlayer playerIn, World worldIn, int x, int y, int z) {
		playerIn.openGui(DankNull.INSTANCE, type.ordinal(), worldIn, x, y, z);
	}

	public static enum GUIType {
			DANKNULL;
	}

}