package p455w0rd.danknull.init;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.network.PacketConfigSync;
import p455w0rd.danknull.util.NonNullListSerializable;
import p455w0rd.danknull.util.WeakHashMapSerializable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.common.config.Configuration.CATEGORY_CLIENT;

/**
 * @author p455w0rd
 */
public class ModConfig {

    public static final String SERVER_CAT = "Server Rules";
    public static final boolean DEBUG_RESET = false;
    public static final String NAME_CREATIVE_BLACKLIST = "CreativeBlacklist";
    public static final String NAME_CREATIVE_WHITELIST = "CreativeWhitelist";
    public static final String NAME_OREDICT_BLACKLIST = "OreDictBlacklist";
    public static final String NAME_OREDICT_WHITELIST = "OreDictWhitelist";
    public static final String NAME_DISABLE_OREDICT = "DisableOreDictMode";
    public static final String NAME_ALLOW_DOCK_INSERTION = "AllowDockInsertion";
    public static final String NAME_CALL_IT_DEVNULL = "CallItDevNull";
    public static final String NAME_SUPERSHINE = "SuperShine";
    public static final String NAME_ONLY_CYCLE_BLOCKS = "onlyCycleBlocks";
    private static Configuration CONFIG = null;

    private static Configuration config() {
        if (CONFIG == null) {
            CONFIG = new Configuration(new File("config/DankNull.cfg"));
        }
        return CONFIG;
    }

    public static Configuration getInstance() {
        return config();
    }

    public static void load() {
        if (DEBUG_RESET) {
            config().getConfigFile().delete();
        }
        sync();
    }

    public static void sync() {
        Options.callItDevNull = config().getBoolean(NAME_CALL_IT_DEVNULL, CATEGORY_CLIENT, false, "Call it a /dev/null in-game (Requested by TheMattaBase)");
        Options.superShine = config().getBoolean(NAME_SUPERSHINE, CATEGORY_CLIENT, false, "Make items ultra shiny!");
        Options.skipNonBlocksOnCycle = config().getBoolean(NAME_ONLY_CYCLE_BLOCKS, CATEGORY_CLIENT, false, "When cycling selected item with /dank/null in-hand, should it try to only cycle blocks?");
        Options.creativeBlacklist = config().getString(NAME_CREATIVE_BLACKLIST, SERVER_CAT, "", "A semicolon separated list of items that are not allowed to be placed into the creative /dank/null\nFormat: modid:name:meta (meta optional: modid:name is acceptable) - Example: minecraft:diamond;minecraft:coal:1").trim();
        Options.creativeWhitelist = config().getString(NAME_CREATIVE_WHITELIST, SERVER_CAT, "", "A semicolon separated list of items that are allowed to be placed into the creative /dank/null\nSame format as Blacklist and whitelist superceeds blacklist.\nIf whitelist is non-empty, then ONLY whitelisted items can be added to the Creative /dank/null").trim();
        Options.oreBlacklist = config().getString(NAME_OREDICT_BLACKLIST, SERVER_CAT, "itemSkull", "A semicolon separated list of Ore Dictionary entries (strings) which WILL NOT be allowed to be used with /dank/null's Ore Dictionary functionality.");
        Options.oreWhitelist = config().getString(NAME_OREDICT_WHITELIST, SERVER_CAT, "", "A semicolon separated list of Ore Dictionary entries (strings) which WILL BE allowed to be used with /dank/null's Ore Dictionary functionality. Whitelist superceeds blacklist.\nIf whitelist is non-empty, then ONLY Ore Dictionary items matching the entries will\nbe able to take advantage of /dank/null's Ore Dictionary functionality.");
        Options.disableOreDictMode = config().getBoolean(NAME_DISABLE_OREDICT, SERVER_CAT, false, "If set to true, then Ore Dictionary Mode will not be available (overrides Ore Dictionary White/Black lists)");
        Options.showHUD = config().getBoolean("showHUD", CATEGORY_CLIENT, true, "Show the /dank/null HUD overlay?");
        Options.allowDockInserting = config().getBoolean(NAME_ALLOW_DOCK_INSERTION, SERVER_CAT, true, "If true, you will be able to pipe items into the /dank/null Docking Station");
        if (config().hasChanged()) {
            config().save();
        }
    }

    @SideOnly(Side.CLIENT)
    public static List<IConfigElement> getClientConfigElements() {
        return new ConfigElement(getInstance().getCategory(CATEGORY_CLIENT)).getChildElements();
    }

    @SideOnly(Side.SERVER)
    public static void sendConfigsToClient(final EntityPlayerMP player) {
        final WeakHashMapSerializable<String, Object> map = new WeakHashMapSerializable<>();
        map.put(ModConfig.NAME_CREATIVE_BLACKLIST, Options.creativeBlacklist);
        map.put(ModConfig.NAME_CREATIVE_WHITELIST, Options.creativeWhitelist);
        map.put(ModConfig.NAME_OREDICT_BLACKLIST, Options.oreBlacklist);
        map.put(ModConfig.NAME_OREDICT_WHITELIST, Options.oreWhitelist);
        map.put(ModConfig.NAME_DISABLE_OREDICT, Options.disableOreDictMode);
        ModNetworking.getInstance().sendTo(new PacketConfigSync(map), player);
    }

    public static boolean isOreDictBlacklistEnabled() {
        return !Options.getOreBlacklist().isEmpty() && !isOreDictWhitelistEnabled();
    }

    public static boolean isOreDictWhitelistEnabled() {
        return !Options.getOreWhitelist().isEmpty();
    }

    public static boolean isOreBlacklisted(final String oreName) {
        for (final String currentOre : Options.getOreBlacklist()) {
            if (currentOre.equals(oreName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOreWhitelisted(final String oreName) {
        for (final String currentOre : Options.getOreWhitelist()) {
            if (currentOre.equals(oreName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidOre(final String oreName) {
        boolean isValid = true;
        if (isOreConfigEnabled()) {
            isValid = isOreDictWhitelistEnabled() && isOreWhitelisted(oreName);
            if (!isValid) {
                isValid = isOreDictBlacklistEnabled() && !isOreBlacklisted(oreName);
            }
        }
        return isValid;
    }

    public static boolean isOreConfigEnabled() {
        return !Options.disableOreDictMode && isOreDictBlacklistEnabled() || isOreDictWhitelistEnabled();
    }

    public static boolean isItemOreDictBlacklisted(final ItemStack stack) {
        if (isOreConfigEnabled() && isOreDictBlacklistEnabled()) {
            for (final int id : OreDictionary.getOreIDs(stack)) {
                final String currentOreName = OreDictionary.getOreName(id);
                if (isValidOre(currentOreName)) {
                    for (final String oreName : Options.getOreBlacklist()) {
                        if (isValidOre(oreName) && oreName.equals(currentOreName)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isItemOreDictWhitelisted(final ItemStack stack) {
        if (isOreConfigEnabled() && isOreDictWhitelistEnabled()) {
            for (final int id : OreDictionary.getOreIDs(stack)) {
                final String currentOreName = OreDictionary.getOreName(id);
                if (isValidOre(currentOreName)) {
                    for (final String oreName : Options.getOreWhitelist()) {
                        if (isValidOre(oreName) && oreName.equals(OreDictionary.getOreName(id))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static class Options {

        public static boolean callItDevNull = false;
        public static boolean superShine = false;
        public static String creativeBlacklist = "";
        public static String creativeWhitelist = "";
        public static String oreBlacklist = "";
        public static String oreWhitelist = "";
        public static boolean showHUD = true;
        public static boolean disableOreDictMode = false;
        public static boolean allowDockInserting = true;
        public static boolean skipNonBlocksOnCycle = false;
        private static NonNullListSerializable<ItemStack> creativeItemBlacklist;
        private static NonNullListSerializable<ItemStack> creativeItemWhitelist;
        private static ArrayList<String> oreStringBlacklist = Lists.newArrayList();
        private static ArrayList<String> oreStringWhitelist = Lists.newArrayList();

        public static List<String> getOreBlacklist() {
            String[] tmpList = null;
            if (oreStringBlacklist.isEmpty() && !oreBlacklist.isEmpty() && getOreWhitelist().isEmpty()) {
                tmpList = oreBlacklist.split(";");
            }
            if (tmpList != null) {
                for (final String string : tmpList) {
                    if (OreDictionary.doesOreNameExist(string)) {
                        oreStringBlacklist.add(string);
                    }
                }
            }
            return oreStringBlacklist;
        }

        public static List<String> getOreWhitelist() {
            String[] tmpList = null;
            if (oreStringWhitelist.isEmpty() && !oreWhitelist.isEmpty()) {
                tmpList = oreWhitelist.split(";");
            }
            if (tmpList != null) {
                for (final String string : tmpList) {
                    if (OreDictionary.doesOreNameExist(string)) {
                        oreStringWhitelist.add(string);
                    }
                }
            }
            return oreStringWhitelist;
        }

        public static NonNullListSerializable<ItemStack> getCreativeBlacklistedItems() throws Exception {
            if (creativeItemBlacklist == null && getCreativeWhitelistedItems().isEmpty()) {
                creativeItemBlacklist = (NonNullListSerializable<ItemStack>) NonNullListSerializable.<ItemStack>create();
                if (!creativeBlacklist.isEmpty()) {
                    final List<String> itemStringList = Lists.newArrayList(creativeBlacklist.split(";"));
                    for (final String itemString : itemStringList) {
                        final String[] params = itemString.split(":");
                        final int numColons = params.length - 1;
                        if (numColons > 2 || numColons <= 0) {
                            throw new Exception(new Throwable("Invalid format for item blacklisting, check " + config().getConfigFile() + " for an example"));
                        }
                        if (numColons == 1) { //no meta
                            final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(params[0], params[1]));
                            if (item == null) {
                                DankNull.LOGGER.warn("Item \"" + params[0] + ":" + params[1] + "\" not found");
                            } else {
                                creativeItemBlacklist.add(new ItemStack(item));
                            }
                        } else if (numColons == 2) {
                            final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(params[0], params[1]));
                            if (item == null) {
                                DankNull.LOGGER.warn("Item \"" + params[0] + ":" + params[1] + "\" not found");
                            } else {
                                int meta = -1;
                                try {
                                    meta = Integer.parseInt(params[2]);
                                } catch (final NumberFormatException e) {
                                    meta = -1;
                                }
                                if (meta < 0) {
                                    DankNull.LOGGER.warn("Invalid metadata for item \"" + params[0] + ":" + params[1] + "\" (" + params[2] + ")");
                                } else {
                                    creativeItemWhitelist.add(new ItemStack(item, 1, meta));
                                }
                            }
                        }
                    }
                }
            }
            return creativeItemBlacklist;
        }

        public static NonNullListSerializable<ItemStack> getCreativeWhitelistedItems() throws Exception {
            if (creativeItemWhitelist == null) {
                creativeItemWhitelist = (NonNullListSerializable<ItemStack>) NonNullListSerializable.<ItemStack>create();
                if (!creativeWhitelist.isEmpty()) {
                    final List<String> itemStringList = Lists.newArrayList(creativeWhitelist.split(";"));
                    for (final String itemString : itemStringList) {
                        final String[] params = itemString.split(":");
                        final int numColons = params.length - 1;
                        if (numColons > 2 || numColons <= 0) {
                            throw new Exception(new Throwable("Invalid format for item whitelisting, check " + config().getConfigFile() + " for an example"));
                        }
                        if (numColons == 1) { //no meta
                            final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(params[0], params[1]));
                            if (item == null) {
                                DankNull.LOGGER.warn("Item \"" + params[0] + ":" + params[1] + "\" not found");
                            } else {
                                creativeItemWhitelist.add(new ItemStack(item));
                            }
                        } else if (numColons == 2) {
                            final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(params[0], params[1]));
                            if (item == null) {
                                DankNull.LOGGER.warn("Item \"" + params[0] + ":" + params[1] + "\" not found");
                            } else {
                                int meta = -1;
                                try {
                                    meta = Integer.parseInt(params[2]);
                                } catch (final NumberFormatException e) {
                                    meta = -1;
                                }
                                if (meta < 0) {
                                    DankNull.LOGGER.warn("Invalid metadata for item \"" + params[0] + ":" + params[1] + "\" (" + params[2] + ")");
                                } else {
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
