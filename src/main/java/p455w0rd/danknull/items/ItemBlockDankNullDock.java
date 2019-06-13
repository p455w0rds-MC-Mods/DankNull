package p455w0rd.danknull.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.client.render.TESRDankNullDock.DankNullDockItemRenderer;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.integration.PwLib;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.api.client.*;
import p455w0rdslib.api.client.shader.IBlockLightEmitter;
import p455w0rdslib.capabilities.CapabilityLightEmitter;
import p455w0rdslib.integration.Albedo;
import p455w0rdslib.util.TextUtils;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings("deprecation")
public class ItemBlockDankNullDock extends ItemBlock implements IModelHolder, IBlockLightEmitter {

	ItemLayerWrapper wrappedModel;

	public ItemBlockDankNullDock() {
		super(ModBlocks.DANKNULL_DOCK);
		setRegistryName(ModBlocks.DANKNULL_DOCK.getRegistryName());
	}

	@Override
	public ICapabilityProvider initCapabilities(final ItemStack stack, final NBTTagCompound nbt) {
		return new ICapabilityProvider() {
			@Override
			public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
				return Albedo.albedoCapCheck(capability) || CapabilityLightEmitter.checkCap(capability);
			}

			@Override
			public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
				if (hasCapability(capability, facing)) {
					if (Albedo.albedoCapCheck(capability)) {
						return p455w0rd.danknull.integration.Albedo.getStackCapability(stack);
					}
					else if (CapabilityLightEmitter.checkCap(capability)) {
						return PwLib.getStackCapability(DankNullUtils.getDockedDankNull(stack));
					}
				}
				return null;
			}
		};
	}

	@Override
	public String getItemStackDisplayName(final ItemStack stack) {
		String name = TextUtils.translate(getUnlocalizedName() + ".name").trim();
		if (Options.callItDevNull) {
			name = name.replace("/dank/", "/dev/");
		}
		return name;
	}

	@Override
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
		return DankNullDockItemRenderer.getRendererForItem(this);
	}

	/*private int brightness = 0;
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
					lightStack = DankNullUtils.getDockedDankNull(stack);
					break;
				}
			}
		}
		else if (e instanceof EntityItem) {
			if (((EntityItem) e).getItem().getItem() == this) {
				lightStack = DankNullUtils.getDockedDankNull(((EntityItem) e).getItem());
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
			final DankNullTier tier = DankNullUtils.getTier(lightStack);
			final Vec3i c = RenderUtils.hexToRGB(tier.getHexColor(false));
			lights.add(Light.builder().pos(e).color(c.getX(), c.getY(), c.getZ(), (float) (brightness * 0.001)).radius(2.5f).intensity(5).build());
		}
		else {
			brightnessDir = false;
			brightness = 0;
			step = 0;
		}
	}*/

}
