package p455w0rd.danknull.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.init.ModIntegration;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.init.ModNetworking;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		ModConfig.init();
		ModBlocks.init();
		ModItems.init();
		ModIntegration.preInit();
		ModNetworking.init();
	}

	public void init(FMLInitializationEvent e) {
		ModIntegration.init();
	}

	public void postInit(FMLPostInitializationEvent e) {
		ModGuiHandler.init();
	}

	public void serverStarting(FMLServerStartingEvent e) {

	}

	public EntityPlayer getPlayer() {
		return null;
	}

	public World getWorld() {
		return null;
	}

	public World getWorld(int dimension) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimension);
	}

	public void setGUIDankNull(ItemStack dankNull) {

	}

}
