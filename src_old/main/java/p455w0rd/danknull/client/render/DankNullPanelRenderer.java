package p455w0rd.danknull.client.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import codechicken.lib.model.ItemQuadBakery;
import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.model.bakedmodels.PerspectiveAwareBakedModel;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.IModelState;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.items.ItemDankNullPanel;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.RenderUtils;

/**
 * @author p455w0rd
 *
 */
public class DankNullPanelRenderer implements IItemRenderer {

	private static final DankNullPanelRenderer INSTANCE = new DankNullPanelRenderer();

	private static Map<String, IBakedModel> modelCache = new HashMap<>();

	private static PerspectiveProperties props = new PerspectiveProperties(TransformUtils.DEFAULT_ITEM, PerspectiveProperties.DEFAULT_ITEM);

	public static DankNullPanelRenderer getInstance() {
		return INSTANCE;
	}

	@Override
	public void renderItem(ItemStack stack, TransformType transformType) {
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemDankNullPanel)) {
			return;
		}

		IBakedModel model = getModel(stack);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableLighting();
		GlStateManager.enableBlend();
		GlStateManager.enableRescaleNormal();
		if (stack.isOnItemFrame()) {
			GlStateManager.scale(1.25D, 1.25D, 1.25D);
			GlStateManager.translate(-0.1D, -0.1D, -0.25D);
		}

		GlStateManager.pushMatrix();

		RenderModel.render(model, stack);
		if (stack.hasEffect()) {
			if (Options.superShine) {
				GlintEffectRenderer.apply2(model, DankNullUtils.getColor(stack.getItemDamage(), false));
			}
			else {
				GlintEffectRenderer.apply(model, stack.getItemDamage());
			}
		}

		GlStateManager.popMatrix();

		GlStateManager.disableRescaleNormal();

	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return new ArrayList<BakedQuad>();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}

	@Override
	public IModelState getTransforms() {
		return TransformUtils.DEFAULT_ITEM;
	}

	private String getKey(@Nonnull ItemStack stack) {
		return stack.getItem().getRegistryName().getResourcePath() + "" + stack.getItemDamage();
	}

	private IBakedModel getModel(@Nonnull ItemStack stack) {
		String key = getKey(stack);
		int meta = stack.getItemDamage();
		if (DankTextures.DANKNULL_PANELS == null) {
			DankTextures.getInstance().registerIcons(RenderUtils.getBlocksTextureMap());
		}
		if (!modelCache.containsKey(key)) {
			List<BakedQuad> quads = ItemQuadBakery.bakeItem(DankTextures.DANKNULL_PANELS[meta]);
			modelCache.put(key, new PerspectiveAwareBakedModel(quads, props));
		}
		return modelCache.get(key);
	}

	public static void initialize() {
		RenderUtils.getResourceManager().registerReloadListener(resourceManager -> modelCache.clear());
	}

}