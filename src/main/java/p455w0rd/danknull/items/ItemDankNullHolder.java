package p455w0rd.danknull.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.api.IModelHolder;
import p455w0rd.danknull.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class ItemDankNullHolder extends Item implements IModelHolder {

	private final String name = "dank_null_holder";

	public ItemDankNullHolder() {
		setRegistryName(name);
		setUnlocalizedName(name);
		ForgeRegistries.ITEMS.register(this);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for (int i = 0; i < 6; i++) {
			ModelResourceLocation loc = new ModelResourceLocation(new ResourceLocation(ModGlobals.MODID, "dank_null_" + i), "inventory");
			ModelLoader.setCustomModelResourceLocation(this, i, loc);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (int i = 0; i < 6; i++) {
			subItems.add(new ItemStack(this, 1, i));
		}
	}
}