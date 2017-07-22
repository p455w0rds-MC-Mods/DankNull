package p455w0rd.danknull;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.proxy.CommonProxy;

@Mod(modid = ModGlobals.MODID, name = ModGlobals.NAME, version = ModGlobals.VERSION, dependencies = ModGlobals.DEPENDANCIES, guiFactory = ModGlobals.GUI_FACTORY, acceptedMinecraftVersions = "[1.12]")
public class DankNull {

	@SidedProxy(clientSide = ModGlobals.CLIENT_PROXY, serverSide = ModGlobals.SERVER_PROXY)
	public static CommonProxy PROXY;

	@Mod.Instance(ModGlobals.MODID)
	public static DankNull INSTANCE;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		PROXY.preInit(e);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		PROXY.init(e);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		PROXY.postInit(e);
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent e) {
		PROXY.serverStarting(e);
	}
}
