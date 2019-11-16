package p455w0rd.danknull.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.client.render.DankNullPanelRenderer;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rdslib.api.client.*;
import p455w0rdslib.util.TextUtils;

/**
 * @author p455w0rd
 *
 */
public class ItemDankNullPanel extends Item implements IModelHolder {

	@SideOnly(Side.CLIENT)
	ItemLayerWrapper wrappedModel;
	DankNullTier tier;

	public ItemDankNullPanel(final DankNullTier tier) {
		this.tier = tier;
		setRegistryName(tier.getDankNullPanelRegistryName());
		setUnlocalizedName(tier.getUnlocalizedNameForPanel());
		setMaxDamage(0);
	}

	public static boolean isDankNullPanel(final ItemStack stack) {
		return stack.getItem() instanceof ItemDankNullPanel;
	}

	public DankNullTier getTier() {
		return tier;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(final ItemStack stack) {
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
		return TextUtils.translate(stack.getItem().getUnlocalizedName() + ".name").trim();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, getModelResource(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemLayerWrapper getWrappedModel() {
		return wrappedModel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setWrappedModel(final ItemLayerWrapper wrappedModel) {
		this.wrappedModel = wrappedModel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldUseInternalTEISR() {
		return true;
	}

	@Override
	public ICustomItemRenderer getRenderer() {
		return DankNullPanelRenderer.getRendererForItem(this);
	}

	@Override
	public IRarity getForgeRarity(final ItemStack stack) {
		return tier.getRarity();
	}

}