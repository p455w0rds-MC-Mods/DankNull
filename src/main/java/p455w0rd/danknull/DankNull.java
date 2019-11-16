package p455w0rd.danknull;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.proxy.CommonProxy;

@Mod(modid = ModGlobals.MODID, name = ModGlobals.NAME, version = ModGlobals.VERSION, dependencies = ModGlobals.DEPENDANCIES, guiFactory = ModGlobals.GUI_FACTORY, acceptedMinecraftVersions = "[1.12.2]", certificateFingerprint = "@FINGERPRINT@")
public class DankNull {

	static ClassLoader CLASS_LOADER_INSTANCE;

	@SidedProxy(clientSide = ModGlobals.CLIENT_PROXY, serverSide = ModGlobals.SERVER_PROXY)
	public static CommonProxy PROXY;

	@Mod.Instance(ModGlobals.MODID)
	public static DankNull INSTANCE;

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		INSTANCE = this;
		CLASS_LOADER_INSTANCE = this.getClass().getClassLoader();
		PROXY.preInit(event);
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event) {
		PROXY.init(event);
	}

	@Mod.EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
		PROXY.postInit(event);
	}

	@Mod.EventHandler
	public void serverStarting(final FMLServerStartingEvent event) {
		PROXY.serverStarting(event);
	}

	public static void resetClassLoader() {
		if (CLASS_LOADER_INSTANCE != null) {
			Thread.currentThread().setContextClassLoader(CLASS_LOADER_INSTANCE);
		}
	}

}
