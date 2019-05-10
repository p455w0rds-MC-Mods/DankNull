package p455w0rd.danknull.client.render;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.model.ModelDankNullDock;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.init.ModGlobals.Textures;
import p455w0rdslib.api.client.ICustomItemRenderer;
import p455w0rdslib.api.client.ItemLayerWrapper;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.GuiUtils;

/**
 * @author p455w0rd
 *
 */
public class TESRDankNullDock extends TileEntitySpecialRenderer<TileDankNullDock> {

	public static final ModelDankNullDock MODEL = new ModelDankNullDock();

	public static void renderDankNull(@Nonnull final ItemStack stack) {
		ItemStack dankNullStack = ItemStack.EMPTY;
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT.BLOCKENTITYTAG)) {
			final NBTTagCompound tag = stack.getTagCompound().getCompoundTag(NBT.BLOCKENTITYTAG);
			if (tag != null && tag.hasKey(NBT.DOCKEDSTACK)) {
				dankNullStack = new ItemStack(tag.getCompoundTag(NBT.DOCKEDSTACK));
			}
			if (!dankNullStack.isEmpty()) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.5, -0.45, 0.5);
				GlStateManager.rotate(180.0F, 1.0F, 0F, 0F);
				GlStateManager.scale(0.55D, 0.55D, 0.55D);
				GlStateManager.enableDepth();
				GlStateManager.enableLighting();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				Minecraft.getMinecraft().getItemRenderer().renderItem(EasyMappings.player(), dankNullStack, ItemCameraTransforms.TransformType.NONE);
				GlStateManager.enableBlend();
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public void render(final TileDankNullDock te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);

		final ItemStack stack = te.getDankNull();
		/* nfi why this does nothing...
		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 0.45, z + 0.5);
			GlStateManager.scale(0.55D, 0.55D, 0.55D);
			Minecraft.getMinecraft().getItemRenderer().renderItem(EasyMappings.player(), stack, ItemCameraTransforms.TransformType.NONE);
			GlStateManager.translate(-x, -y, -z);
			GlStateManager.popMatrix();
		}
		*/
		if (destroyStage >= 0) {
			bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		}
		else {
			GuiUtils.bindTexture(Textures.DOCK_TEXTURE);
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);

		GlStateManager.translate(0.5F, -0.5F, 0.5F);

		if (destroyStage < 0) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		final float pbx = OpenGlHelper.lastBrightnessX;
		final float pby = OpenGlHelper.lastBrightnessY;
		//if (stack.getItem().hasEffect(stack)) {
		if (!te.getDankNull().isEmpty()) {
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		}
		MODEL.render();
		if (!te.getDankNull().isEmpty()) {
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, pbx, pby);
		}
		GlStateManager.translate(-0.5F, 0.5F, -0.5F);

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if (destroyStage >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}

		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 0.45, z + 0.5);
			GlStateManager.scale(0.55D, 0.55D, 0.55D);
			Minecraft.getMinecraft().getItemRenderer().renderItem(EasyMappings.player(), stack, ItemCameraTransforms.TransformType.NONE);
			GlStateManager.translate(-x, -y, -z);
			GlStateManager.popMatrix();
		}
	}

	public static class DankNullDockItemRenderer extends TileEntityItemStackRenderer implements ICustomItemRenderer {

		public ItemLayerWrapper model;
		public static TransformType transformType;
		private static final Map<Item, ICustomItemRenderer> CACHE = new HashMap<>();

		private DankNullDockItemRenderer(@Nonnull final Item item) {
			registerRenderer(item, this);
		}

		private static void registerRenderer(final Item item, final ICustomItemRenderer instance) {
			CACHE.put(item, instance);
		}

		public static ICustomItemRenderer getRendererForItem(final Item item) {
			if (!CACHE.containsKey(item)) {
				new DankNullDockItemRenderer(item);
			}
			return CACHE.get(item);
		}

		@Override
		public void renderByItem(@Nonnull final ItemStack stack, final float partialTicks) {
			if (Block.getBlockFromItem(stack.getItem()) == ModBlocks.DANKNULL_DOCK) {
				GuiUtils.bindTexture(Textures.DOCK_TEXTURE);
				GlStateManager.rotate(180, 0, 0, 180);
				GlStateManager.translate(-0.5, -1.5, 0.5);
				final float pbx = OpenGlHelper.lastBrightnessX;
				final float pby = OpenGlHelper.lastBrightnessY;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
				MODEL.render(0.0625F);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, pbx, pby);
				GlStateManager.translate(0.5, 1.5, -0.5);
				renderDankNull(stack);
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

}
