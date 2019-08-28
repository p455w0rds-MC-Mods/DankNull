package p455w0rd.danknull.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.*;
import p455w0rd.danknull.init.*;
import p455w0rd.danknull.util.cap.CapabilityDankNull;

public class CommonProxy {

	public void preInit(final FMLPreInitializationEvent e) {
		ModDataFixing.registerWalkers();
		ModConfig.init();
		ModIntegration.preInit();
		ModNetworking.init();
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

	public World getWorld(final int dimension) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimension);
	}
}
