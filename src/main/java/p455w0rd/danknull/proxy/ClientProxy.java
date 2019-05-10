package p455w0rd.danknull.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.*;
import p455w0rd.danknull.init.*;
import p455w0rdslib.util.EasyMappings;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(final FMLPreInitializationEvent e) {
		super.preInit(e);
		ModItems.registerCustomRenderedItems();
		ModCreativeTab.init();
		ModKeyBindings.register();
	}

	@Override
	public void init(final FMLInitializationEvent e) {
		super.init(e);
	}

	@Override
	public void postInit(final FMLPostInitializationEvent e) {
		super.postInit(e);
		ModIntegration.postInit();
	}

	@Override
	public void serverStarting(final FMLServerStartingEvent e) {
		super.serverStarting(e);
	}

	@Override
	public EntityPlayer getPlayer() {
		return EasyMappings.player();
	}

	@Override
	public World getWorld() {
		return Minecraft.getMinecraft().world;
	}

	@Override
	public World getWorld(final int dimension) {
		return getWorld();
	}

	public GuiScreen getScreen() {
		return Minecraft.getMinecraft().currentScreen;
	}
}
