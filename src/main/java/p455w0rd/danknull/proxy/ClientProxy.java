package p455w0rd.danknull.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import p455w0rd.danknull.init.ModCreativeTab;
import p455w0rd.danknull.init.ModIntegration;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.init.ModKeyBindings;

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
    public World getWorld() {
        return Minecraft.getMinecraft().world;
    }
}
