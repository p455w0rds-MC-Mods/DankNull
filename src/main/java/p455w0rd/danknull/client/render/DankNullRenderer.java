package p455w0rd.danknull.client.render;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraftforge.client.ForgeHooksClient;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.items.ItemDankNullPanel;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.api.client.*;
import p455w0rdslib.util.EasyMappings;

/**
 * @author p455w0rd
 *
 */
public class DankNullRenderer extends TileEntityItemStackRenderer implements ICustomItemRenderer {

	public ItemLayerWrapper model;
	public static TransformType transformType;
	boolean isGUI = false;
	private static final Map<Item, DankNullRenderer> CACHE = new HashMap<>();

	private DankNullRenderer(@Nonnull final Item item) {
		registerRenderer(item, this);
	}

	private static void registerRenderer(final Item item, final DankNullRenderer instance) {
		CACHE.put(item, instance);
	}

	public static DankNullRenderer getRendererForItem(final Item item) {
		if (!CACHE.containsKey(item)) {
			new DankNullRenderer(item);
		}
		return CACHE.get(item);
	}

	@Override
	public void renderByItem(final ItemStack item, final float partialTicks) {
		if (item.getItem() instanceof ItemDankNull) {
			final RenderManager rm = Minecraft.getMinecraft().getRenderManager();
			if (rm == null) {
				return;
			}
			if (model == null) {
				final IBakedModel baseModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(item);
				if (baseModel == null) {
					return;
				}
				final ItemLayerWrapper wrapper = new ItemLayerWrapper(baseModel).setRenderer(this);
				final Item it = item.getItem();
				if (it instanceof IModelHolder) {
					((IModelHolder) it).setWrappedModel(wrapper);
				}
				model = wrapper;
			}

			final GameSettings options = rm.options;
			if (options == null) {
				return;
			}
			final int view = options.thirdPersonView;
			IDankNullHandler dankNullHandler = item.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
			final int index = dankNullHandler.getSelected();
			final ItemStack containedStack = index > -1 ? dankNullHandler.getStackInSlot(index) : ItemStack.EMPTY;

			final float pbx = OpenGlHelper.lastBrightnessX;
			final float pby = OpenGlHelper.lastBrightnessY;
			if (getTransformType() == TransformType.FIRST_PERSON_LEFT_HAND || getTransformType() == TransformType.FIRST_PERSON_RIGHT_HAND) {
				if (item.hasEffect()) {
					//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
				}
				renderItem(item, model);
				if (item.hasEffect()) {
					//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, pbx, pby);
				}
			}
			if (!containedStack.isEmpty()) {
				IBakedModel containedItemModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(containedStack);
				GlStateManager.pushMatrix();
				if (containedStack.getItem() instanceof ItemBlock && !(Block.getBlockFromItem(containedStack.getItem()) instanceof BlockTorch)) {
					GlStateManager.scale(0.4D, 0.4D, 0.4D);
					if (containedItemModel.isBuiltInRenderer()) {
						if (view > 0 || !isStackInHand(item)) {
							GlStateManager.scale(1.1D, 1.1D, 1.1D);
							GlStateManager.translate(1.25D, 1.4D, 1.25D);
						}
						else {
							GlStateManager.translate(1.25D, 2.0D, 1.25D);
						}
					}
					else if (view > 0 || !isStackInHand(item)) {
						GlStateManager.translate(0.75D, 0.9D, 0.75D);
					}
					else {
						GlStateManager.translate(0.75D, 1.5D, 0.75D);
					}
				}
				else {
					GlStateManager.scale(0.5D, 0.5D, 0.5D);
					if (containedItemModel.isBuiltInRenderer()) {
						if (view > 0 || !isStackInHand(item)) {
							if (containedStack.getItem() instanceof ItemSkull) {
								if (containedStack.getItemDamage() == 5) {
									GlStateManager.scale(0.65D, 0.65D, 0.65D);
									GlStateManager.translate(1.5D, 3.0D, 1.5D);
								}
								else {
									GlStateManager.translate(0.75D, 2.25D, 1.1D);
								}
							}
							else {
								GlStateManager.scale(1.1D, 1.1D, 1.1D);
								GlStateManager.translate(0.95D, 1.4D, 0.9D);
							}
						}
						else if (containedStack.getItem() instanceof ItemSkull) {
							if (containedStack.getItemDamage() == 5) {
								GlStateManager.scale(0.65D, 0.65D, 0.65D);
								GlStateManager.translate(1.5D, 3.0D, 1.5D);
							}
							else {
								GlStateManager.translate(0.75D, 2.25D, 1.1D);
							}
						}
						else {
							GlStateManager.translate(0.75D, 2.0D, 1.0D);
						}
					}
					else if (view > 0 || !isStackInHand(item)) {
						GlStateManager.translate(0.5D, 0.9D, 0.5D);
					}
					else {
						GlStateManager.translate(0.5D, 1.5D, 0.5D);
					}
				}
				if (item.isOnItemFrame()) {
					GlStateManager.scale(1.25D, 1.25D, 1.25D);
					GlStateManager.translate(-0.2D, -0.2D, -0.5D);
				}
				if (containedItemModel.isBuiltInRenderer()) {
					if (containedStack.getItem() == Item.getItemFromBlock(ModBlocks.DANKNULL_DOCK)) {
						GlStateManager.translate(0.0D, 1.0D, 0.0D);
						GlStateManager.rotate(ModGlobals.TIME, 1.0F, ModGlobals.TIME, 1.0F);
					}
					else if (containedStack.getItem() instanceof ItemDankNullPanel) {
						GlStateManager.rotate(ModGlobals.TIME, 1.0F, ModGlobals.TIME, 1.0F);
					}
					else if (containedStack.getItem() == Items.BANNER) {
						GlStateManager.rotate(ModGlobals.TIME, 1.0F, ModGlobals.TIME, 1.0F);
					}
					else {
						GlStateManager.translate(-0.1D, 0.0D, -0.1D);
						GlStateManager.rotate(ModGlobals.TIME, 1.0F, ModGlobals.TIME, 1.0F);
					}
				}
				else {
					GlStateManager.rotate(ModGlobals.TIME, 1.0F, 1.0F, 1.0F);
				}

				containedItemModel = ForgeHooksClient.handleCameraTransforms(containedItemModel, ItemCameraTransforms.TransformType.NONE, false);
				final String[] registryName = containedStack.getItem().getRegistryName().toString().split(":");
				final String modID = registryName[0];
				if (modID.equalsIgnoreCase("danknull") || modID.equalsIgnoreCase("minecraft")) {
					if (containedStack.getItem() instanceof ItemBucket || containedStack.getItem() instanceof ItemBucketMilk) {
						renderItem(containedStack, Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(containedStack, EasyMappings.player().getEntityWorld(), EasyMappings.player()));
					}
					else {
						renderItem(containedStack, containedItemModel);
						GlStateManager.enableBlend();
					}
				}
				else {
					renderItem(containedStack, Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(containedStack, EasyMappings.player().getEntityWorld(), EasyMappings.player()));
				}
				GlStateManager.popMatrix();

			}
			if (item.hasEffect()) {
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
			}
			renderItem(item, model);

			if (item.hasEffect()) {
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, pbx, pby);
			}
			Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, true);
		}
	}

	private boolean isStackInHand(final ItemStack itemStackIn) {
		final EntityPlayer player = EasyMappings.player();
		if (player.getHeldItemMainhand() == itemStackIn || player.getHeldItemOffhand() == itemStackIn) {
			return true;
		}
		return false;
	}

	private static void renderItem(final ItemStack stack, final IBakedModel model) {
		renderItem(stack, model, false);
	}

	private static void renderItem(final ItemStack stack, final IBakedModel model, final boolean disableGlint) {
		if (!stack.isEmpty() && model != null) {
			if (model.isBuiltInRenderer() && !(stack.getItem() instanceof ItemDankNull)) {
				Minecraft.getMinecraft().getItemRenderer().renderItem(EasyMappings.player(), stack, ItemCameraTransforms.TransformType.NONE);
			}
			else {
				RenderModel.render(model, stack);
				if (stack.hasEffect() && !disableGlint) {
					if (stack.getItem() instanceof ItemDankNull) {
						final int meta = ((ItemDankNull) stack.getItem()).getTier().ordinal();
						if (!Options.superShine) {
							GlintEffectRenderer.apply(model, meta);
						}
						else {
							GlintEffectRenderer.apply2(model, DankNullUtils.getTier(stack).getHexColor(false));
						}
					}
					else {
						GlintEffectRenderer.apply(model, -1);
					}
				}
			}
		}
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
