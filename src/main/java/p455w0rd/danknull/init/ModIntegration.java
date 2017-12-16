package p455w0rd.danknull.init;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import p455w0rd.danknull.integration.STG;
import p455w0rd.danknull.integration.TOP;
import p455w0rd.danknull.integration.WAILA;

/**
 * @author p455w0rd
 *
 */
public class ModIntegration {

	public static void preInit() {
		if (Mods.TOP.isLoaded()) {
			TOP.init();
		}
		else {
			ModLogger.info(Mods.TOP.getName() + " Integation: Disabled");
		}

	}

	public static void init() {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			if (Mods.WAILA.isLoaded()) {
				WAILA.init();
			}
			else {
				ModLogger.info("Waila Integation: Disabled");
			}
		}
		if (Mods.STG.isLoaded()) {
			STG.registerFakePlayer();
		}
	}

	public static enum Mods {
			TOP("theoneprobe", "The One Probe"), WAILA("waila", "WAILA"), JEI("jei", "JEI"),
			STG("stg", "SwingThroughGrass");

		private String modid, name;

		Mods(String modidIn, String nameIn) {
			modid = modidIn;
			name = nameIn;
		}

		public String getId() {
			return modid;
		}

		public String getName() {
			return name;
		}

		public boolean isLoaded() {
			return Loader.isModLoaded(getId());
		}
	}

}