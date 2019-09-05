package p455w0rd.danknull.client.render;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;

/**
 * @author p455w0rd
 *
 */
public class RenderModel {

	public static void render(final IBakedModel model, @Nonnull final ItemStack stack) {
		render(model, -1, stack);
	}

	public static void render(final IBakedModel model, final int color) {
		render(model, color, ItemStack.EMPTY);
	}

	public static void render(final IBakedModel model, final int color, @Nonnull final ItemStack stack) {
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
		for (final EnumFacing enumfacing : EnumFacing.VALUES) {
			renderQuads(vertexbuffer, model.getQuads((IBlockState) null, enumfacing, 0L), color, stack);
		}
		renderQuads(vertexbuffer, model.getQuads((IBlockState) null, (EnumFacing) null, 0L), color, stack);
		tessellator.draw();
	}

	public static void renderQuads(final BufferBuilder renderer, final List<BakedQuad> quads, final int color, final ItemStack stack) {
		final boolean flag = color == -1 && !stack.isEmpty();
		int i = 0;
		for (final int j = quads.size(); i < j; i++) {
			final BakedQuad bakedquad = quads.get(i);
			int k = color;
			if (flag && bakedquad.hasTintIndex()) {
				final ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
				k = itemColors.colorMultiplier(stack, bakedquad.getTintIndex());
				if (EntityRenderer.anaglyphEnable) {
					k = TextureUtil.anaglyphColor(k);
				}
				k |= 0xFF000000;
			}
			LightUtil.renderQuadColor(renderer, bakedquad, k);
		}
	}

	// =========

	/*private static void render(final ModelPlayer model, final int color, final EntityPlayer player, final float partialTicks, final boolean multiPass) {
		Minecraft.getMinecraft().getRenderManager().renderEntityStatic(player, partialTicks, false);
		//rm.doRenderEntity(player, player.posX, player.posY, player.posZ, player.cameraYaw, partialTicks, multiPass);
	}*/

}
