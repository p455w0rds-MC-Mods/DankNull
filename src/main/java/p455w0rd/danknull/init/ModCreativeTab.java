package p455w0rd.danknull.init;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;

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
	public ItemStack createIcon() {
		return new ItemStack(ModItems.CREATIVE_DANKNULL);
	}

	@Override
	public void displayAllRelevantItems(final NonNullList<ItemStack> items) {
		for (final Item item : ModItems.getItems()) {
			if (!(item instanceof ItemBlock)) {
				items.add(new ItemStack(item));
			}
		}
		for (final Block block : ModBlocks.getBlocks()) {
			items.add(new ItemStack(block));
		}
	}

}
