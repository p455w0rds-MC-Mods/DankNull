package p455w0rd.danknull.client.render;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;

/**
 * @author p455w0rd
 *
 */
public class RenderModel {
	public static void render(IBakedModel model, @Nonnull ItemStack stack) {
		render(model, -1, stack);
	}

	public static void render(IBakedModel model, int color) {
		render(model, color, ItemStack.EMPTY);
	}

	public static void render(IBakedModel model, int color, @Nonnull ItemStack stack) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
		for (EnumFacing enumfacing : EnumFacing.values()) {
			renderQuads(vertexbuffer, model.getQuads((IBlockState) null, enumfacing, 0L), color, stack);
		}
		renderQuads(vertexbuffer, model.getQuads((IBlockState) null, (EnumFacing) null, 0L), color, stack);
		tessellator.draw();
	}

	public static void renderQuads(VertexBuffer renderer, List<BakedQuad> quads, int color, ItemStack stack) {
		boolean flag = (color == -1) && (!stack.isEmpty());
		int i = 0;
		for (int j = quads.size(); i < j; i++) {
			BakedQuad bakedquad = quads.get(i);
			int k = color;
			if ((flag) && (bakedquad.hasTintIndex())) {
				ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
				k = itemColors.getColorFromItemstack(stack, bakedquad.getTintIndex());
				if (EntityRenderer.anaglyphEnable) {
					k = TextureUtil.anaglyphColor(k);
				}
				k |= 0xFF000000;
			}
			LightUtil.renderQuadColor(renderer, bakedquad, k);
		}
	}

	// =========

	public static void render(ModelPlayer model, int color, EntityPlayer player, float partialTicks, boolean multiPass) {
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		rm.renderEntityStatic(player, partialTicks, false);
		//rm.doRenderEntity(player, player.posX, player.posY, player.posZ, player.cameraYaw, partialTicks, multiPass);
	}

}
