package p455w0rd.danknull.integration;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.integration.waila.WAILADankNullDockProvider;
import p455w0rdslib.LibGlobals.Mods;

/**
 * @author p455w0rd
 *
 */
public class WAILA {

	public static String toolTipEnclose = TextFormatting.BOLD + "" + TextFormatting.GREEN + "=====================";

	public static void init() {
        DankNull.LOGGER.info("Waila Integation: Enabled");
        FMLInterModComms.sendMessage(Mods.WAILA.getId(), "register", WAILA.class.getName() + ".callbackRegister");
	}

	public static void callbackRegister(final IWailaRegistrar registrar) {
		registrar.registerBodyProvider(new WAILADankNullDockProvider(), TileDankNullDock.class);
		registrar.registerStackProvider(new WAILADankNullDockProvider(), TileDankNullDock.class);
	}

}
