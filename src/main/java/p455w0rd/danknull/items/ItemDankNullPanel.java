package p455w0rd.danknull.items;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.api.IModelHolder;
import p455w0rd.danknull.client.render.DankNullPanelRenderer;
import p455w0rd.danknull.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class ItemDankNullPanel extends Item implements IModelHolder {

	private int meta = 0;

	public ItemDankNullPanel(int index) {
		setRegistryName("dank_null_panel_" + index);
		setUnlocalizedName("dank_null_panel_" + index);
		GameRegistry.register(this);
		meta = index;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.format(getUnlocalizedNameInefficiently(stack) + ".name").trim();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModelRegistryHelper.registerItemRenderer(this, DankNullPanelRenderer.getInstance());
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return ModGlobals.Rarities.getRarityFromMeta(meta);
	}

}