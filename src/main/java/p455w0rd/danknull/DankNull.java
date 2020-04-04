package p455w0rd.danknull;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.proxy.CommonProxy;

@Mod(modid = ModGlobals.MODID, name = ModGlobals.NAME, version = ModGlobals.VERSION, dependencies = ModGlobals.DEPENDANCIES, guiFactory = ModGlobals.GUI_FACTORY, acceptedMinecraftVersions = "[1.12.2]", certificateFingerprint = "@FINGERPRINT@")
public class DankNull {

    @SidedProxy(clientSide = ModGlobals.CLIENT_PROXY, serverSide = ModGlobals.SERVER_PROXY)
    public static CommonProxy PROXY;

    @Mod.Instance(ModGlobals.MODID)
    public static DankNull INSTANCE;

    public static Logger LOGGER = LogManager.getLogger(ModGlobals.NAME);

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        PROXY.preInit(event);
        OreDictionary.registerOre("railBed", new ItemStack(Blocks.LOG, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("railBed", new ItemStack(Blocks.BEDROCK));
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

}
