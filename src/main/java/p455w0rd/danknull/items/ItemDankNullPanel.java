package p455w0rd.danknull.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.api.IModelHolder;
import p455w0rd.danknull.client.render.DankNullPanelRenderer;
import p455w0rd.danknull.client.render.PModelRegistryHelper;
import p455w0rd.danknull.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings("deprecation")
public class ItemDankNullPanel extends Item implements IModelHolder {

	public ItemDankNullPanel() {
		setRegistryName("dank_null_panel");
		setUnlocalizedName("dank_null_panel");
		ForgeRegistries.ITEMS.register(this);
		setMaxDamage(0);
		setHasSubtypes(true);
		setMaxStackSize(1024);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(final ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab)) {
			for (int i = 0; i < 6; i++) {
				subItems.add(new ItemStack(this, 1, i));
			}
		}
	}

	@Override
	public boolean getHasSubtypes() {
		return true;
	}

	@Override
	public boolean isDamaged(final ItemStack stack) {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	@Override
	public boolean showDurabilityBar(final ItemStack stack) {
		return false;
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(final ItemStack stack) {
		return I18n.translateToLocal(stack.getItem().getUnlocalizedName() + "_" + stack.getItemDamage() + ".name").trim();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		for (int i = 0; i < 6; i++) {
			PModelRegistryHelper.registerMetaRenderer(this, DankNullPanelRenderer.getInstance(), i);
		}
	}

	@Override
	public EnumRarity getRarity(final ItemStack stack) {
		return ModGlobals.Rarities.getRarityFromMeta(stack.getItemDamage());
	}

}