package p455w0rd.danknull.client.render;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;

import codechicken.lib.render.CCModelState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.model.ModelDankNullDock;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.GuiUtils;

/**
 * @author p455w0rd
 *
 */
public class TESRDankNullDock extends TileEntitySpecialRenderer<TileDankNullDock> implements IItemRenderer {

	public static final ModelDankNullDock MODEL = new ModelDankNullDock();

	@Override
	public void renderItem(@Nonnull ItemStack stack, TransformType transformType) {
		if (Block.getBlockFromItem(stack.getItem()) == ModBlocks.DANKNULL_DOCK) {
			GuiUtils.bindTexture(DankTextures.DOCK_TEXTURE);
			//GlStateManager.enableLighting();
			GlStateManager.rotate(180, 0, 0, 180);
			GlStateManager.translate(-0.5, -1.5, 0.5);
			MODEL.render(0.0625F);
			GlStateManager.translate(0.5, 1.5, -0.5);
			//GlStateManager.disableLighting();
			//GlStateManager.disableDepth();
			//GlStateManager.enableBlend();
			//GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			renderDankNull(stack);
		}
	}

	public void renderDankNull(@Nonnull ItemStack stack) {
		ItemStack dankNullStack = ItemStack.EMPTY;
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag")) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("BlockEntityTag");
			if (tag != null && tag.hasKey(TileDankNullDock.TAG_ITEMSTACK)) {
				dankNullStack = new ItemStack(tag.getCompoundTag(TileDankNullDock.TAG_ITEMSTACK));
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
				//GlStateManager.disableLighting();
				GlStateManager.disableBlend();
				GlStateManager.disableDepth();

				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public void render(TileDankNullDock te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);

		if (destroyStage >= 0) {
			bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		}
		else {
			GuiUtils.bindTexture(DankTextures.DOCK_TEXTURE);
		}
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);

		GlStateManager.translate(0.5F, -0.5F, 0.5F);

		if (destroyStage < 0) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		MODEL.render(0.0625F);

		GlStateManager.translate(-0.5F, 0.5F, -0.5F);

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if (destroyStage >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}

		ItemStack stack = te.getDankNull();
		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 0.45, z + 0.5);
			GlStateManager.scale(0.55D, 0.55D, 0.55D);
			//DankNullRenderer.getInstance().renderItem(stack, TransformType.GUI);
			Minecraft.getMinecraft().getItemRenderer().renderItem(EasyMappings.player(), stack, ItemCameraTransforms.TransformType.NONE);
			GlStateManager.translate(-x, -y, -z);
			GlStateManager.popMatrix();
		}
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
		return DankTextures.DANKNULL_DOCK_SPRITE;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}

	public static class DankNullDockTransforms {

		public static CCModelState block() {
			ImmutableMap.Builder<TransformType, TRSRTransformation> defaultBlockBuilder = ImmutableMap.builder();
			defaultBlockBuilder.put(TransformType.GUI, TransformUtils.create(0, 0, 0, 30, 225, 0, 0.625f));
			defaultBlockBuilder.put(TransformType.GROUND, TransformUtils.create(0, 3, 0, 0, 0, 0, 0.25f));
			defaultBlockBuilder.put(TransformType.FIXED, TransformUtils.create(0, 0, -5.1f, 90, 0, 0, -0.75f));
			defaultBlockBuilder.put(TransformType.THIRD_PERSON_RIGHT_HAND, TransformUtils.create(0f, 2.5f, 2.5f, 75, 45, 0, 0.375f));
			defaultBlockBuilder.put(TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.flipLeft(TransformUtils.create(0, 2.5f, 2.5F, 75, 45, 0, 0.375f)));
			defaultBlockBuilder.put(TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.create(0, 4f, 0, 0, 45, 2f, 0.4f));
			defaultBlockBuilder.put(TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.flipLeft(TransformUtils.create(0, 4f, 0, 0, 45, 2f, 0.4f)));//TransformUtils.create(0, 4f, 0, 0, 225, 0, 0.4f));
			return new CCModelState(defaultBlockBuilder.build());
		}

	}

	@Override
	public IModelState getTransforms() {
		return DankNullDockTransforms.block();
	}

}
