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

	public static final ItemDankNullHolder DANK_NULL_HOLDER = new ItemDankNullHolder();
	public static final ItemDankNull DANK_NULL = new ItemDankNull();
	public static final ItemDankNullPanel DANK_NULL_PANEL = new ItemDankNullPanel();

	public static void init() {
		long millis = System.currentTimeMillis() % 1000;
		ModLogger.info("Registering Items");
		ITEM_LIST.addAll(Arrays.asList(DANK_NULL, DANK_NULL_HOLDER, DANK_NULL_PANEL));
		ModLogger.info("Registering Items Complete In " + (int) ((System.currentTimeMillis() % 1000) - millis) + "ms");
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		ModLogger.info("Init adding item models");
		for (Item item : ITEM_LIST) {
			if (item != null && item instanceof IModelHolder) {
				((IModelHolder) item).initModel();
			}
			ModLogger.info("Registered Model for " + item.getItemStackDisplayName(new ItemStack(item)));
		}
		ModLogger.info("Finished adding item models");
	}

	public static NonNullList<Item> getList() {
		return ITEM_LIST;
	}
}
