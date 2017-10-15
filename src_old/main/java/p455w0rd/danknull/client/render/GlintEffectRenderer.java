package p455w0rd.danknull.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

/**
 * @author p455w0rd
 *
 */
public class GlintEffectRenderer {

	public static void apply(IBakedModel model, int damage) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(SourceFactor.SRC_COLOR, DestFactor.ONE);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("minecraft", "textures/misc/enchanted_item_glint.png"));
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f = Minecraft.getSystemTime() % 3000L / 3000.0F / 8.0F;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
		switch (damage) {
		case 0:
			RenderModel.render(model, -10092544);
			break;
		case 1:
			RenderModel.render(model, -16777114);
			break;
		case 2:
			RenderModel.render(model, -10066330);
			break;
		case 3:
			RenderModel.render(model, -10066432);
			break;
		case 4:
			RenderModel.render(model, -12097946);
			break;
		case 5:
			RenderModel.render(model, -16751104);
			break;
		case 6:
			RenderModel.render(model, 0xFFFF0000);
			break;
		case 7:
			RenderModel.render(model, 0xFF0000FF);
			break;
		case -1:
		default:
			RenderModel.render(model, -8372020);
		}
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}

	public static void apply2(IBakedModel model, int color) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(SourceFactor.SRC_COLOR, DestFactor.ONE);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("minecraft", "textures/misc/enchanted_item_glint.png"));
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f = Minecraft.getSystemTime() % 3000L / 3000.0F / 8.0F;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
		RenderModel.render(model, color);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}

}
