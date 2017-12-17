package p455w0rd.danknull.proxy;

import codechicken.lib.texture.TextureUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import p455w0rd.danknull.client.render.DankNullPanelRenderer;
import p455w0rd.danknull.client.render.DankTextures;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModCreativeTab;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.init.ModKeyBindings;
import p455w0rd.danknull.init.ModRendering;
import p455w0rdslib.util.EasyMappings;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		TextureUtils.addIconRegister(new DankTextures());
		ModBlocks.registerModels();
		ModItems.registerModels();
		DankNullPanelRenderer.initialize();
		ModCreativeTab.init();
		ModKeyBindings.register();
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		ModRendering.init();
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e) {
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
	public GuiScreen getScreen() {
		return Minecraft.getMinecraft().currentScreen;
	}

}
