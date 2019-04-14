package p455w0rd.danknull;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModLogger;
import p455w0rd.danknull.proxy.CommonProxy;

@Mod(modid = ModGlobals.MODID, name = ModGlobals.NAME, version = ModGlobals.VERSION, dependencies = ModGlobals.DEPENDANCIES, guiFactory = ModGlobals.GUI_FACTORY, acceptedMinecraftVersions = "1.12", certificateFingerprint = "@FINGERPRINT@")
public class DankNull {

	@SidedProxy(clientSide = ModGlobals.CLIENT_PROXY, serverSide = ModGlobals.SERVER_PROXY)
	public static CommonProxy PROXY;

	@Instance(ModGlobals.MODID)
	public static DankNull INSTANCE;

	@EventHandler
	public void preInit(final FMLPreInitializationEvent e) {
		INSTANCE = this;
		PROXY.preInit(e);
	}

	@EventHandler
	public void init(final FMLInitializationEvent e) {
		PROXY.init(e);
	}

	@EventHandler
	public void postInit(final FMLPostInitializationEvent e) {
		PROXY.postInit(e);
	}

	@EventHandler
	public void serverStarting(final FMLServerStartingEvent e) {
		PROXY.serverStarting(e);
	}

	@EventHandler
	public void onFingerprintViolation(final FMLFingerprintViolationEvent event) {
		ModLogger.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
	}
}
