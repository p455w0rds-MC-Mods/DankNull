package p455w0rd.danknull.init;

import codechicken.lib.CodeChickenLib;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;

public class ModGlobals {

	public static final String MODID_PWLIB = "p455w0rdslib";

	public static final String MODID = "danknull";
	public static final String VERSION = "1.0.9";
	public static final String NAME = "/dank/null";
	public static final String SERVER_PROXY = "p455w0rd.danknull.proxy.CommonProxy";
	public static final String CLIENT_PROXY = "p455w0rd.danknull.proxy.ClientProxy";
	public static final String GUI_FACTORY = "p455w0rd.danknull.init.ModGuiFactory";
	public static final String CONFIG_FILE = "config/DankNull.cfg";
	public static final String DEPENDANCIES = "required-after:" + MODID_PWLIB + "[2.0.22,);" + CodeChickenLib.MOD_VERSION_DEP;

	public static boolean GUI_DANKNULL_ISOPEN = false;
	public static float TIME = 0.0F;

	public static class Rarities {
		private static final EnumRarity REDSTONE = EnumHelper.addRarity("dn:redstone", TextFormatting.RED, "Redstone");
		private static final EnumRarity LAPIS = EnumHelper.addRarity("dn:lapis", TextFormatting.BLUE, "Lapis");
		private static final EnumRarity IRON = EnumHelper.addRarity("dn:iron", TextFormatting.WHITE, "Iron");
		private static final EnumRarity GOLD = EnumHelper.addRarity("dn:gold", TextFormatting.YELLOW, "Gold");
		private static final EnumRarity DIAMOND = EnumHelper.addRarity("dn:diamond", TextFormatting.AQUA, "Diamond");
		private static final EnumRarity EMERALD = EnumHelper.addRarity("dn:emerald", TextFormatting.GREEN, "Emerald");

		private static final EnumRarity[] ARRAY = new EnumRarity[] {
				REDSTONE,
				LAPIS,
				IRON,
				GOLD,
				DIAMOND,
				EMERALD
		};

		public static EnumRarity getRarityFromMeta(int meta) {
			return meta >= ARRAY.length ? ARRAY[0] : ARRAY[meta];
		}

	}

}
