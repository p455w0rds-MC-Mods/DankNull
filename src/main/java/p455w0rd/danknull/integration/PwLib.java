package p455w0rd.danknull.integration;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import p455w0rd.danknull.blocks.BlockDankNullDock;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.items.ItemDankNullPanel;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.api.client.shader.Light;
import p455w0rdslib.capabilities.CapabilityLightEmitter;
import p455w0rdslib.capabilities.CapabilityLightEmitter.StackLightEmitter;
import p455w0rdslib.capabilities.CapabilityLightEmitter.TileLightEmitter;
import p455w0rdslib.handlers.BrightnessHandler;
import p455w0rdslib.util.RenderUtils;

/**
 * @author p455w0rd
 *
 */
public class PwLib {

	public static <T> T getStackCapability(final ItemStack stack) {
		return CapabilityLightEmitter.LIGHT_EMITTER_CAPABILITY.cast(

				new StackLightEmitter(stack) {

					@Override
					public List<Light> emitLight(final List<Light> lights, final Entity entity) {
						if (Options.enableColoredLightShaderSupport && ItemDankNullPanel.isDankNullPanel(stack) || ItemDankNull.isDankNull(stack)) {
							final ItemStack lightStack = stack.copy();
							if (!lightStack.isEmpty() && DankNullUtils.getTier(lightStack) != DankNullTier.NONE) {
								final Vec3i c = RenderUtils.hexToRGB(DankNullUtils.getTier(lightStack).getHexColor(false));
								lights.add(Light.builder().pos(entity).color(c.getX(), c.getY(), c.getZ(), BrightnessHandler.getBrightness(entity).value() * 0.001f).radius(2f).intensity(2.5f).build());
							}
							else {
								BrightnessHandler.getBrightness(entity).reset();
							}
						}
						return lights;
					}
				});
	}

	public static <T> T getTileCapability(final TileEntity tile) {
		return CapabilityLightEmitter.LIGHT_EMITTER_CAPABILITY.cast(

				new TileLightEmitter(tile) {

					@Override
					public List<Light> emitLight(final List<Light> lights, final TileEntity tile) {
						if (Options.enableColoredLightShaderSupport && BlockDankNullDock.isDankNullDock(tile)) {
							final ItemStack lightStack = BlockDankNullDock.getDockedDankNull(tile);
							if (!lightStack.isEmpty() && DankNullUtils.getTier(lightStack) != DankNullTier.NONE) {
								final Vec3i c = RenderUtils.hexToRGB(DankNullUtils.getTier(lightStack).getHexColor(false));
								lights.add(Light.builder().pos(tile.getPos()).color(c.getX(), c.getY(), c.getZ(), (float) (BrightnessHandler.getBrightness(tile).value() * 0.001)).radius(2f).intensity(5).build());
							}
							else {
								BrightnessHandler.getBrightness(tile).reset();
							}
						}
						return lights;
					}
				});
	}

	public static boolean checkCap(final Capability<?> capability) {
		return CapabilityLightEmitter.checkCap(capability) && Options.enableColoredLightShaderSupport;
	}

}
