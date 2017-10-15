package p455w0rd.danknull.init;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author p455w0rd
 *
 */
public class ModConfig {

	public static Configuration CONFIG;

	public static final String CLIENT_CAT = "General";

	public static void init() {
		if (CONFIG == null) {
			CONFIG = new Configuration(new File(ModGlobals.CONFIG_FILE));
			MinecraftForge.EVENT_BUS.register(new ModConfig());
			CONFIG.load();
		}

		//Options.callItDevNull = CONFIG.getBoolean("CallItDevNull", CLIENT_CAT, false, "Call it a /dev/null in-game ");
		Options.superShine = CONFIG.getBoolean("SuperShine", CLIENT_CAT, false, "Make items ultra shiny!");

		if (CONFIG.hasChanged()) {
			CONFIG.save();
		}
	}

	@SubscribeEvent
	public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent e) {
		if (e.getModID().equals(ModGlobals.MODID)) {
			init();
		}
	}

	public static class Options {

		//public static boolean callItDevNull = false;
		public static boolean superShine = false;

	}

}
