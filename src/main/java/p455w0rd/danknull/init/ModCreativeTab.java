package p455w0rd.danknull.init;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.items.ItemDankNullHolder;

/**
 * @author p455w0rd
 *
 */
public class ModCreativeTab extends CreativeTabs {

	public static CreativeTabs TAB;

	public ModCreativeTab() {
		super(ModGlobals.MODID);
	}

	public static void init() {
		TAB = new ModCreativeTab();
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(ModItems.DANK_NULL, 1, 5);
	}

	@Override
	public void displayAllRelevantItems(NonNullList<ItemStack> items) {
		for (int i = 0; i < 6; i++) {
			items.add(new ItemStack(ModItems.DANK_NULL, 1, i));
		}
		for (Item item : ModItems.getList()) {
			if (!(item instanceof ItemDankNull) && !(item instanceof ItemDankNullHolder)) {
				items.add(new ItemStack(item));
			}
		}
		for (Block block : ModBlocks.getList()) {
			items.add(new ItemStack(block));
		}
	}

}
