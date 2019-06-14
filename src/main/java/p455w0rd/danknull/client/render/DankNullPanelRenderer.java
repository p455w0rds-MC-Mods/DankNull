package p455w0rd.danknull.client.render;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.items.ItemDankNullPanel;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.api.client.*;

/**
 * @author p455w0rd
 *
 */
public class DankNullPanelRenderer extends TileEntityItemStackRenderer implements ICustomItemRenderer {

	public ItemLayerWrapper model;
	public static TransformType transformType;
	private static final Map<Item, ICustomItemRenderer> CACHE = new HashMap<>();

	private DankNullPanelRenderer(@Nonnull final Item item) {
		registerRenderer(item, this);
	}

	private static void registerRenderer(final Item item, final ICustomItemRenderer instance) {
		CACHE.put(item, instance);
	}

	public static ICustomItemRenderer getRendererForItem(final Item item) {
		if (!CACHE.containsKey(item)) {
			new DankNullPanelRenderer(item);
		}
		return CACHE.get(item);
	}

	@Override
	public void renderByItem(final ItemStack stack, final float partialTicks) {
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemDankNullPanel)) {
			return;
		}
		if (model == null) {
			final IBakedModel baseModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
			if (baseModel == null) {
				return;
			}
			final ItemLayerWrapper wrapper = new ItemLayerWrapper(baseModel).setRenderer(this);
			final Item item = stack.getItem();
			if (item instanceof IModelHolder) {
				((IModelHolder) item).setWrappedModel(wrapper);
			}
			model = wrapper;
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableLighting();
		GlStateManager.enableAlpha();
		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		final float pbx = OpenGlHelper.lastBrightnessX;
		final float pby = OpenGlHelper.lastBrightnessY;
		if (stack.getItem().hasEffect(stack)) {
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		}
		RenderModel.render(model, stack);
		if (stack.hasEffect()) {
			final int meta = ((ItemDankNullPanel) stack.getItem()).getTier().ordinal();
			if (Options.superShine) {
				GlintEffectRenderer.apply2(model, DankNullUtils.getTier(stack).getHexColor(false));
			}
			else {
				GlintEffectRenderer.apply(model, meta);
			}
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, pbx, pby);
		}
		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
	}

	@Override
	public TransformType getTransformType() {
		return transformType;
	}

	@Override
	public void setTransformType(final TransformType type) {
		transformType = type;
	}

}