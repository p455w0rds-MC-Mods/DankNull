package p455w0rd.danknull.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.client.render.DankNullPanelRenderer;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.api.client.*;
import p455w0rdslib.api.client.shader.IColoredLightEmitter;
import p455w0rdslib.api.client.shader.Light;
import p455w0rdslib.util.RenderUtils;
import p455w0rdslib.util.TextUtils;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings("deprecation")
public class ItemDankNullPanel extends Item implements IModelHolder, IColoredLightEmitter {

	@SideOnly(Side.CLIENT)
	ItemLayerWrapper wrappedModel;
	DankNullTier tier;

	public ItemDankNullPanel(final DankNullTier tier) {
		this.tier = tier;
		setRegistryName(tier.getDankNullPanelRegistryName());
		setUnlocalizedName(tier.getUnlocalizedNameForPanel());
		setMaxDamage(0);
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

	private int brightness = 0;
	private boolean brightnessDir = false;
	private int step = 0;

	@Override
	public void emitLight(final List<Light> lights, final Entity e) {
		if (!Options.enabledColoredLightShaderSupport) {
			return;
		}
		ItemStack lightStack = ItemStack.EMPTY;
		if (e instanceof EntityPlayer) {
			for (final ItemStack stack : ((EntityPlayer) e).getHeldEquipment()) {
				if (stack.getItem() == this) {
					lightStack = stack.copy();
					break;
				}
			}
		}
		else if (e instanceof EntityItem) {
			if (((EntityItem) e).getItem().getItem() == this) {
				lightStack = ((EntityItem) e).getItem();
			}
		}
		if (!lightStack.isEmpty() && lightStack.hasEffect()) {
			if (brightnessDir) {
				brightness++;
				if (brightness > DankNullUtils.STEPS_MOST[step]) {
					brightnessDir = !brightnessDir;
					step++;
					if (step > 4) {
						step = 0;
					}
				}
			}
			else {
				brightness--;
				if (brightness < DankNullUtils.STEPS_LEAST[step]) {
					brightnessDir = !brightnessDir;
					step++;
					if (step > 4) {
						step = 0;
					}
				}
			}
			final Vec3i c = RenderUtils.hexToRGB(tier.getHexColor(false));
			lights.add(Light.builder().pos(e).color(c.getX(), c.getY(), c.getZ(), (float) (brightness * 0.001)).radius(2.5f).intensity(5).build());
		}
		else {
			brightnessDir = false;
			brightness = 0;
			step = 0;
		}
	}

}