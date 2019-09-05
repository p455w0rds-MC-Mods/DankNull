package p455w0rd.danknull.integration;

import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.DefaultLightProvider;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rd.danknull.items.ItemDankNull;

/**
 * @author p455w0rd
 *
 */
public class Albedo {

	public static <T> T getTileCapability(final BlockPos pos, final ItemStack dankNull) {
		return elucent.albedo.Albedo.LIGHT_PROVIDER_CAPABILITY.cast(new DefaultLightProvider() {
			@Override
			public void gatherLights(final GatherLightsEvent event, final Entity context) {
				final DankNullTier tier = ItemDankNull.getTier(dankNull);
				if (tier != DankNullTier.NONE) {
					event.add(elucent.albedo.lighting.Light.builder().pos(pos).color(tier.getHexColor(true), true).radius(5.0f).build());
					event.add(elucent.albedo.lighting.Light.builder().pos(pos.down()).color(tier.getHexColor(true), true).radius(5.0f).build());
				}
			}
		});
	}

	public static <T> T getStackCapability(final ItemStack dankNull) {
		return elucent.albedo.Albedo.LIGHT_PROVIDER_CAPABILITY.cast(new DefaultLightProvider() {
			@Override
			public void gatherLights(final GatherLightsEvent event, final Entity entity) {
				final DankNullTier tier = ItemDankNull.getTier(dankNull);
				if (tier != DankNullTier.NONE) {
					event.add(elucent.albedo.lighting.Light.builder().pos(entity.posX, entity.posY - 0.5d, entity.posZ).color(tier.getHexColor(true), true).radius(3.0f).build());
				}
			}
		});
	}

}
