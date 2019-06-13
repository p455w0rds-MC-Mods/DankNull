package p455w0rd.danknull;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.proxy.CommonProxy;

@Mod(modid = ModGlobals.MODID, name = ModGlobals.NAME, version = ModGlobals.VERSION, dependencies = ModGlobals.DEPENDANCIES, guiFactory = ModGlobals.GUI_FACTORY, acceptedMinecraftVersions = "[1.12.2]", certificateFingerprint = "@FINGERPRINT@")
public class DankNull {

	@SidedProxy(clientSide = ModGlobals.CLIENT_PROXY, serverSide = ModGlobals.SERVER_PROXY)
	public static CommonProxy PROXY;

	@Mod.Instance(ModGlobals.MODID)
	public static DankNull INSTANCE;

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent e) {
		INSTANCE = this;
		PROXY.preInit(e);
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent e) {
		PROXY.init(e);
	}

	@Mod.EventHandler
	public void postInit(final FMLPostInitializationEvent e) {
		PROXY.postInit(e);
	}

	@Mod.EventHandler
	public void serverStarting(final FMLServerStartingEvent e) {
		PROXY.serverStarting(e);
	}
}
