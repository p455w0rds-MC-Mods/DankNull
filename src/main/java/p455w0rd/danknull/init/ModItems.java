package p455w0rd.danknull.init;

import java.util.Arrays;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.api.IModelHolder;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.items.ItemDankNullHolder;
import p455w0rd.danknull.items.ItemDankNullPanel;

/**
 * @author p455w0rd
 *
 */
public class ModItems {

	private static final NonNullList<Item> ITEM_LIST = NonNullList.<Item>create();

	public static final ItemDankNull DANK_NULL = new ItemDankNull();
	public static final ItemDankNullHolder DANK_NULL_HOLDER = new ItemDankNullHolder();
	public static final ItemDankNullPanel PANEL_REDSTONE = new ItemDankNullPanel(0);
	public static final ItemDankNullPanel PANEL_LAPIS = new ItemDankNullPanel(1);
	public static final ItemDankNullPanel PANEL_IRON = new ItemDankNullPanel(2);
	public static final ItemDankNullPanel PANEL_GOLD = new ItemDankNullPanel(3);
	public static final ItemDankNullPanel PANEL_DIAMOND = new ItemDankNullPanel(4);
	public static final ItemDankNullPanel PANEL_EMERALD = new ItemDankNullPanel(5);

	public static void init() {
		long millis = System.currentTimeMillis() % 1000;
		ModLogger.info("Registering Items");
		ITEM_LIST.addAll(Arrays.asList(DANK_NULL, DANK_NULL_HOLDER, PANEL_REDSTONE, PANEL_LAPIS, PANEL_IRON, PANEL_GOLD, PANEL_DIAMOND, PANEL_EMERALD));
		ModLogger.info("Registering Items Complete In " + (int) ((System.currentTimeMillis() % 1000) - millis) + "ms");
	}

	@SideOnly(Side.CLIENT)
	public static void preInitModels() {
		ModLogger.info("Init adding item models");
		for (Item item : ITEM_LIST) {
			if (item instanceof IModelHolder) {
				((IModelHolder) item).initModel();
			}
			//
			ModLogger.info("Registered Model for " + item.getItemStackDisplayName(new ItemStack(item)));
		}
		ModLogger.info("Finished adding item models");
	}

	public static NonNullList<Item> getList() {
		return ITEM_LIST;
	}
}
