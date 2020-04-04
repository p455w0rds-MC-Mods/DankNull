package p455w0rd.danknull.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.*;
import p455w0rd.danknull.init.*;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;

public class CommonProxy {

	public void preInit(final FMLPreInitializationEvent e) {
		ModDataFixing.registerWalkers();
		ModConfig.load();
		ModIntegration.preInit();
		ModNetworking.registerMessages();
		CapabilityDankNull.register();
	}

	public void init(final FMLInitializationEvent e) {
		ModDataFixing.registerFixes();
		ModIntegration.init();
	}

	public void postInit(final FMLPostInitializationEvent e) {
		ModGuiHandler.init();
	}

	public void serverStarting(final FMLServerStartingEvent e) {

	}

	public EntityPlayer getPlayer() {
		return null;
	}

	public World getWorld() {
		return null;
	}
}
