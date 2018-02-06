package p455w0rd.danknull.init;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * @author p455w0rd
 *
 */
public class ModConfig {

	public static Configuration CONFIG;

	public static final String CLIENT_CAT = "General";
	public static final String SERVER_CAT = "Server Rules";

	public static void init() {
		if (CONFIG == null) {
			CONFIG = new Configuration(new File(ModGlobals.CONFIG_FILE));
			MinecraftForge.EVENT_BUS.register(new ModConfig());
			CONFIG.load();
		}

		Options.callItDevNull = CONFIG.getBoolean("CallItDevNull", CLIENT_CAT, false, "Call it a /dev/null in-game ");
		Options.superShine = CONFIG.getBoolean("SuperShine", CLIENT_CAT, false, "Make items ultra shiny!");
		Options.creativeBlacklist = CONFIG.getString("CreativeBlacklist", SERVER_CAT, "", "A semicolon separated list of items that are not allowed to be placed into the creative /dank/null\nFormat: modid:name:meta (meta optional: modid:name is acceptable) - Example: minecraft:diamond;minecraft:coal:1").trim();
		Options.creativeWhitelist = CONFIG.getString("CreativeWhitelist", SERVER_CAT, "", "A semicolon separated list of items that are allowed to be placed into the creative /dank/null\nSame format as Blacklist and whitelist superceeds blacklist.\nIf whitelist is non-empty, then ONLY whitelisted items can be added to the Creative /dank/null").trim();
		if (CONFIG.hasChanged()) {
			CONFIG.save();
		}
	}

	@SubscribeEvent
	public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent e) {
		if (e.getModID().equals(ModGlobals.MODID)) {
			init();
		}
	}

	public static class Options {

		public static boolean callItDevNull = false;
		public static boolean superShine = false;
		public static String creativeBlacklist = "";
		public static String creativeWhitelist = "";
		public static String oreBlacklist = "";
		private static NonNullList<ItemStack> creativeItemBlacklist = null; // cache it..
		private static NonNullList<ItemStack> creativeItemWhitelist = null;

		public static NonNullList<ItemStack> getCreativeBlacklistedItems() throws Exception {
			if (creativeItemBlacklist == null) {
				creativeItemBlacklist = NonNullList.<ItemStack>create();
				if (!creativeBlacklist.isEmpty()) {
					List<String> itemStringList = Lists.newArrayList(creativeBlacklist.split(";"));
					for (String itemString : itemStringList) {
						String[] params = itemString.split(":");
						int numColons = params.length - 1;
						if (numColons > 2 || numColons <= 0) {
							throw new Exception(new Throwable("Invalid format for item blacklisting, check " + ModGlobals.CONFIG_FILE + " for an example"));
						}
						if (numColons == 1) { //no meta
							Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(params[0], params[1]));
							if (item == null) {
								ModLogger.warn("Item \"" + params[0] + ":" + params[1] + "\" not found");
							}
							else {
								creativeItemBlacklist.add(new ItemStack(item));
							}
						}
						else if (numColons == 2) {
							Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(params[0], params[1]));
							if (item == null) {
								ModLogger.warn("Item \"" + params[0] + ":" + params[1] + "\" not found");
							}
							else {
								int meta = -1;
								try {
									meta = Integer.parseInt(params[2]);
								}
								catch (NumberFormatException e) {
									meta = -1;
								}
								if (meta < 0) {
									ModLogger.warn("Invalid metadata for item \"" + params[0] + ":" + params[1] + "\" (" + params[2] + ")");
								}
								else {
									creativeItemWhitelist.add(new ItemStack(item, 1, meta));
								}
							}
						}
					}
				}
			}
			return creativeItemBlacklist;
		}

		public static NonNullList<ItemStack> getCreativeWhitelistedItems() throws Exception {
			if (creativeItemWhitelist == null) {
				creativeItemWhitelist = NonNullList.<ItemStack>create();
				if (!creativeWhitelist.isEmpty()) {
					List<String> itemStringList = Lists.newArrayList(creativeWhitelist.split(";"));
					for (String itemString : itemStringList) {
						String[] params = itemString.split(":");
						int numColons = params.length - 1;
						if (numColons > 2 || numColons <= 0) {
							throw new Exception(new Throwable("Invalid format for item whitelisting, check " + ModGlobals.CONFIG_FILE + " for an example"));
						}
						if (numColons == 1) { //no meta
							Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(params[0], params[1]));
							if (item == null) {
								ModLogger.warn("Item \"" + params[0] + ":" + params[1] + "\" not found");
							}
							else {
								creativeItemWhitelist.add(new ItemStack(item));
							}
						}
						else if (numColons == 2) {
							Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(params[0], params[1]));
							if (item == null) {
								ModLogger.warn("Item \"" + params[0] + ":" + params[1] + "\" not found");
							}
							else {
								int meta = -1;
								try {
									meta = Integer.parseInt(params[2]);
								}
								catch (NumberFormatException e) {
									meta = -1;
								}
								if (meta < 0) {
									ModLogger.warn("Invalid metadata for item \"" + params[0] + ":" + params[1] + "\" (" + params[2] + ")");
								}
								else {
									creativeItemWhitelist.add(new ItemStack(item, 1, meta));
								}
							}
						}
					}
				}
			}
			return creativeItemWhitelist;
		}
	}

}
