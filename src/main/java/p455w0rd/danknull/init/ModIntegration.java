package p455w0rd.danknull.init;

import net.minecraftforge.fml.common.FMLCommonHandler;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.integration.ItemScroller;
import p455w0rd.danknull.integration.TOP;
import p455w0rd.danknull.integration.WAILA;
import p455w0rdslib.LibGlobals.Mods;

/**
 * @author p455w0rd
 */
public class ModIntegration {

    public static void preInit() {
        if (Mods.TOP.isLoaded()) {
            TOP.init();
        } else {
            DankNull.LOGGER.info(Mods.TOP.getName() + " Integation: Disabled");
        }
    }

    public static void init() {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            if (Mods.WAILA.isLoaded()) {
                WAILA.init();
            } else {
                DankNull.LOGGER.info("Waila Integation: Disabled");
            }
        }
    }

    public static void postInit() {
        if (Mods.ITEMSCROLLER.isLoaded()) {
            ItemScroller.blackListSlots();
        }
    }

}